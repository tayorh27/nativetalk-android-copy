/*
 * Copyright (c) 2010-2019 Belledonne Communications SARL.
 *
 * This file is part of linphone-android
 * (see https://www.linphone.org).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.linphone.assistant;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.AppConfig;
import org.linphone.DataStore.AppData;
import org.linphone.DataStore.Client;
import org.linphone.LinphoneContext;
import org.linphone.LinphoneManager;

import com.hbb20.CountryCodePicker;
import com.nk.ntalk.R;
import org.linphone.activities.LinphoneGenericActivity;
import org.linphone.core.AccountCreator;
import org.linphone.core.Core;
import org.linphone.core.DialPlan;
import org.linphone.core.Factory;
import org.linphone.core.ProxyConfig;
import org.linphone.core.tools.Log;
import org.linphone.dialer.DialerActivity;
import org.linphone.settings.LinphonePreferences;
import org.linphone.utils.Utils;

public abstract class AssistantActivity extends LinphoneGenericActivity
        implements CountryPicker.CountryPickedListener {
//    protected ImageView mBack;
    private AlertDialog mCountryPickerDialog;

    private CountryPicker mCountryPicker;

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

//        View statusBar = findViewById(R.id.status);
//        if (getResources().getBoolean(R.bool.assistant_hide_status_bar)) {
//            statusBar.setVisibility(View.GONE);
//        }
//
//        View topBar = findViewById(R.id.top_bar);
//        if (getResources().getBoolean(R.bool.assistant_hide_top_bar)) {
//            topBar.setVisibility(View.GONE);
//        }

//        mBack = findViewById(R.id.back);
//        mBack.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        finish();
//                    }
//                });
    }

    @Override
    protected void onDestroy() {
//        mBack = null;
        mCountryPickerDialog = null;
        mCountryPicker = null;

        super.onDestroy();
    }

    @Override
    public void onCountryClicked(DialPlan dialPlan) {
        if (mCountryPickerDialog != null) {
            mCountryPickerDialog.dismiss();
            mCountryPickerDialog = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (!mBack.isEnabled())
//                return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    public AccountCreator getAccountCreator() {
        return LinphoneManager.getInstance().getAccountCreator();
    }

    private void reloadAccountCreatorConfig(String path) {
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.loadConfigFromXml(path);
            AccountCreator accountCreator = getAccountCreator();
            accountCreator.reset();
            accountCreator.setLanguage(Locale.getDefault().getLanguage());
        }
    }

    void reloadDefaultAccountCreatorConfig() {
        Log.i("[Assistant] Reloading configuration with default");
        reloadAccountCreatorConfig(LinphonePreferences.instance().getDefaultDynamicConfigFile());
    }

    void reloadLinphoneAccountCreatorConfig() {
        Log.i("[Assistant] Reloading configuration with specifics");
        reloadAccountCreatorConfig(LinphonePreferences.instance().getLinphoneDynamicConfigFile());
    }

    void createProxyConfigAndLeaveAssistant() {
        createProxyConfigAndLeaveAssistant(false);
    }

    void createProxyConfigAndLeaveAssistant(boolean isGenericAccount) {
        Core core = LinphoneManager.getCore();
        boolean useLinphoneDefaultValues =
                getString(R.string.default_domain).equals(getAccountCreator().getDomain());

        if (isGenericAccount) {
            if (useLinphoneDefaultValues) {
                Log.i(
                        "[Assistant] Default domain found for generic connection, reloading configuration");
                core.loadConfigFromXml(
                        LinphonePreferences.instance().getLinphoneDynamicConfigFile());
            } else {
                Log.i("[Assistant] Third party domain found, keeping default values");
            }
        }

        ProxyConfig proxyConfig = getAccountCreator().createProxyConfig();

        if (isGenericAccount) {
            if (useLinphoneDefaultValues) {
                // Restore default values
                Log.i("[Assistant] Restoring default assistant configuration");
                core.loadConfigFromXml(
                        LinphonePreferences.instance().getDefaultDynamicConfigFile());
            } else {
                // If this isn't a sip.linphone.org account, disable push notifications and enable
                // service notification, otherwise incoming calls won't work (most probably)
                if (proxyConfig != null) {
                    proxyConfig.setPushNotificationAllowed(false);
                }
                Log.w(
                        "[Assistant] Unknown domain used, push probably won't work, enable service mode");
                LinphonePreferences.instance().setServiceNotificationVisibility(true);
                LinphoneContext.instance().getNotificationManager().startForeground();
            }
        }
        LinphonePreferences.instance()
                .setPushNotificationEnabled(!isGenericAccount || useLinphoneDefaultValues);

        if (proxyConfig == null) {
            Log.e("[Assistant] Account creator couldn't create proxy config");
            new Utils(this).error("Account creator couldn't create proxy config");
            // TODO: display error message
        } else {
            if (proxyConfig.getDialPrefix() == null) {
                DialPlan dialPlan = getDialPlanForCurrentCountry();
                if (dialPlan != null) {
                    proxyConfig.setDialPrefix(dialPlan.getCountryCallingCode());
                }
            }

            LinphonePreferences.instance().firstLaunchSuccessful();
            AccountCreator accountCreator = getAccountCreator();
            new GetClientDetails(accountCreator.getUsername(), accountCreator.getPassword()).execute();
            goToLinphoneActivity();
        }
    }

    void goToLinphoneActivity() {
        boolean needsEchoCalibration =
                LinphoneManager.getCore().isEchoCancellerCalibrationRequired();
        boolean echoCalibrationDone =
                LinphonePreferences.instance().isEchoCancellationCalibrationDone();
        Log.i(
                "[Assistant] Echo cancellation calibration required ? "
                        + needsEchoCalibration
                        + ", already done ? "
                        + echoCalibrationDone);

        Intent intent;
        if (needsEchoCalibration && !echoCalibrationDone) {
            intent = new Intent(this, EchoCancellerCalibrationAssistantActivity.class);
        } else {
            /*boolean openH264 = LinphonePreferences.instance().isOpenH264CodecDownloadEnabled();
            boolean codecFound =
                    LinphoneManager.getInstance().getOpenH264DownloadHelper().isCodecFound();
            boolean abiSupported =
                    Version.getCpuAbis().contains("armeabi-v7a")
                            && !Version.getCpuAbis().contains("x86");
            boolean androidVersionOk = Version.sdkStrictlyBelow(Build.VERSION_CODES.M);

            if (openH264 && abiSupported && androidVersionOk && !codecFound) {
                intent = new Intent(this, OpenH264DownloadAssistantActivity.class);
            } else {*/
            intent = new Intent(this, DialerActivity.class);
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            // }
        }
        startActivity(intent);
    }

    void showPhoneNumberDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.phone_number_info_title))
                .setMessage(
                        getString(R.string.phone_number_link_info_content)
                                + "\n"
                                + getString(
                                        R.string.phone_number_link_info_content_already_account))
                .show();
    }

    void showAccountAlreadyExistsDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.account_already_exist))
                .setMessage(getString(R.string.assistant_phone_number_unavailable))
                .show();
    }

    void showGenericErrorDialog(AccountCreator.Status status) {
        String message;

        switch (status) {
                // TODO handle other possible status
            case PhoneNumberInvalid:
                message = getString(R.string.phone_number_invalid);
                break;
            case WrongActivationCode:
                message = getString(R.string.activation_code_invalid);
                break;
            case PhoneNumberOverused:
                message = getString(R.string.phone_number_overuse);
                break;
            case AccountNotExist:
                message = getString(R.string.account_doesnt_exist);
                break;
            default:
                message = getString(R.string.error_unknown);
                break;
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error))
                .setMessage(message)
                .show();
    }

    void showCountryPickerDialog() {
        if (mCountryPicker == null) {
            mCountryPicker = new CountryPicker(this, this);
        }
        mCountryPickerDialog =
                new AlertDialog.Builder(this).setView(mCountryPicker.getView()).show();
    }

    DialPlan getDialPlanForCurrentCountry() {
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String countryIso = tm.getNetworkCountryIso();
            return getDialPlanFromCountryCode(countryIso);
        } catch (Exception e) {
            Log.e("[Assistant] " + e);
        }
        return null;
    }

    String getDevicePhoneNumber() {
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            return tm.getLine1Number();
        } catch (Exception e) {
            Log.e("[Assistant] " + e);
        }
        return null;
    }

    DialPlan getDialPlanFromPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) return null;

        for (DialPlan c : Factory.instance().getDialPlans()) {
            if (prefix.equalsIgnoreCase(c.getCountryCallingCode())) return c;
        }
        return null;
    }

    private DialPlan getDialPlanFromCountryCode(String countryCode) {
        if (countryCode == null || countryCode.isEmpty()) return null;

        for (DialPlan c : Factory.instance().getDialPlans()) {
            if (countryCode.equalsIgnoreCase(c.getIsoCountryCode())) return c;
        }
        return null;
    }

    int arePhoneNumberAndPrefixOk(EditText prefixEditText, EditText phoneNumberEditText) {
        String prefix = prefixEditText.getText().toString();
        if (prefix.startsWith("+")) {
            prefix = prefix.substring(1);
        }

        String phoneNumber = phoneNumberEditText.getText().toString();
        return getAccountCreator().setPhoneNumber(phoneNumber, prefix);
    }

    String getErrorFromPhoneNumberStatus(int status) {
        AccountCreator.PhoneNumberStatus phoneNumberStatus =
                AccountCreator.PhoneNumberStatus.fromInt(status);
        switch (phoneNumberStatus) {
            case InvalidCountryCode:
                return getString(R.string.country_code_invalid);
            case TooShort:
                return getString(R.string.phone_number_too_short);
            case TooLong:
                return getString(R.string.phone_number_too_long);
            case Invalid:
                return getString(R.string.phone_number_invalid);
        }
        return null;
    }

    String getErrorFromUsernameStatus(AccountCreator.UsernameStatus status) {
        switch (status) {
            case Invalid:
                return getString(R.string.username_invalid_size);
            case InvalidCharacters:
                return getString(R.string.invalid_characters);
            case TooLong:
                return getString(R.string.username_too_long);
            case TooShort:
                return getString(R.string.username_too_short);
        }
        return null;
    }

    private class GetClientDetails extends AsyncTask<Void, Void, Void> {

        String username, password;
        AppData data;
        Utils utils;

        GetClientDetails(String username, String password) {
            this.username = username;
            this.password = password;
            data = new AppData(AssistantActivity.this);
            utils = new Utils(AssistantActivity.this);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String url = AppConfig.API_URL + "/VS.WebAPI.Admin/json/syncreply/admin.client.login";
            JSONObject object = new JSONObject();
            try {
                object.put("login", username);
                object.put("password", password);
                String res = utils.MakeHttpsApiCalls(url, object.toString());
                android.util.Log.e("BGres", res);
                //android.util.Log.e("res", res);
                JSONObject jsonObject = new JSONObject(res);
                data.SaveClientDetails(jsonObject.getInt("idClient"), jsonObject.getInt("clientType"));
                //data.SaveClientInformation(new Client(jsonObject.getString("firstName"), jsonObject.getString("lastName"), jsonObject.getString("eMail")));
                data.SaveTariff(21);//10jsonObject.getInt("tariffId"));
                android.util.Log.e("currencyLOgin1", "run: "+jsonObject.getInt("tariffId"));


                String url2 = AppConfig.API_URL + "/VS.WebAPI.Admin/json/syncreply/admin.client.personal.get";
                JSONObject object2 = new JSONObject();
                object2.put("idClient", jsonObject.getInt("idClient"));
                object2.put("clientType", jsonObject.getInt("clientType"));
                String res2 = utils.MakeHttpsApiCalls(url2, object2.toString());
                JSONObject jsonObject2 = new JSONObject(res2);
                data.SaveClientInformation(new Client(
                        (jsonObject2.getJSONObject("personal").isNull("firstName")) ? "" : jsonObject2.getJSONObject("personal").getString("firstName"),
                        (jsonObject2.getJSONObject("personal").isNull("lastName")) ? "" : jsonObject2.getJSONObject("personal").getString("lastName"),
                        (jsonObject2.getJSONObject("personal").isNull("eMail")) ? "" : jsonObject2.getJSONObject("personal").getString("eMail"),
                        (jsonObject2.getJSONObject("personal").isNull("city")) ? "" : jsonObject2.getJSONObject("personal").getString("city"),
                        (jsonObject2.getJSONObject("personal").isNull("taxId")) ? "" : jsonObject2.getJSONObject("personal").getString("taxId"),
                        (jsonObject2.getJSONObject("personal").isNull("phoneNumber")) ? "" : jsonObject2.getJSONObject("personal").getString("phoneNumber"),
                        (jsonObject2.getJSONObject("personal").isNull("address")) ? "" : jsonObject2.getJSONObject("personal").getString("address"),
                        (jsonObject2.getJSONObject("personal").isNull("state")) ? "" : jsonObject2.getJSONObject("personal").getString("state"),
                        (jsonObject2.getJSONObject("personal").isNull("country")) ? "" : jsonObject2.getJSONObject("personal").getString("country"),
                        (jsonObject2.getJSONObject("personal").isNull("zip")) ? "" : jsonObject2.getJSONObject("personal").getString("zip")));
                android.util.Log.e("currencyLOgin2", "run: "+jsonObject2.getJSONObject("personal").getString("eMail"));
            } catch (JSONException e) {
                android.util.Log.e("currencyLOgin3", "doInBackground: " +e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                android.util.Log.e("currencyLOgin4", "doInBackground: " +e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }
}
