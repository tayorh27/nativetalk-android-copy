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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hbb20.CountryCodePicker;
import com.nk.ntalk.R;

import org.linphone.DataStore.AppData;
import org.linphone.settings.LinphonePreferences;

public class MenuAssistantActivity extends AssistantActivity implements View.OnClickListener {

    private Button createAccount, logLinphoneAccount, logGenericAccount, remoteProvisioning;
    AppData data;
    CountryCodePicker ccp;
    Button btnContinue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.assistant_menu);

        createAccount = (Button) findViewById(R.id.create_account);
//        status = view.findViewById(R.id.status);
        //ccp = view.findViewById(R.id.ccpp);
        //btnContinue = view.findViewById(R.id.Bcontinue);
        createAccount.setOnClickListener(this);
//        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
//            @Override
//            public void onCountrySelected() {
//                Log.e("selected", ccp.getSelectedCountryNameCode());
//                Log.e("selected", ccp.getSelectedCountryName());
//                Log.e("selected", ccp.getSelectedCountryCode());
//            }
//        });

        logLinphoneAccount = (Button) findViewById(R.id.login_linphone);
        if (getResources().getBoolean(R.bool.hide_linphone_accounts_in_assistant)) {
            logLinphoneAccount.setVisibility(View.GONE);
        } else {
            logLinphoneAccount.setOnClickListener(this);
        }

        logGenericAccount = (Button) findViewById(R.id.login_generic);
        if (getResources().getBoolean(R.bool.hide_generic_accounts_in_assistant)) {
            logGenericAccount.setVisibility(View.GONE);
        } else {
            logGenericAccount.setOnClickListener(this);
        }

        remoteProvisioning = (Button) findViewById(R.id.remote_provisioning);
        if (getResources().getBoolean(R.bool.hide_remote_provisioning_in_assistant)) {
            remoteProvisioning.setVisibility(View.GONE);
        } else {
            remoteProvisioning.setOnClickListener(this);
        }
        data = new AppData(MenuAssistantActivity.this);

        TextView accountCreation = findViewById(R.id.account_creation);
        accountCreation.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent;
                        if (getResources().getBoolean(R.bool.isTablet)
                                || !getResources().getBoolean(R.bool.use_phone_number_validation)) {
                            intent =
                                    new Intent(
                                            MenuAssistantActivity.this,
                                            EmailAccountCreationAssistantActivity.class);
                        } else {
                            intent =
                                    new Intent(
                                            MenuAssistantActivity.this,
                                            PhoneAccountCreationAssistantActivity.class);
                        }
                        startActivity(intent);
                    }
                });

        TextView accountConnection = findViewById(R.id.account_connection);
        accountConnection.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(
                                new Intent(
                                        MenuAssistantActivity.this,
                                        AccountConnectionAssistantActivity.class));
                    }
                });
        if (getResources().getBoolean(R.bool.hide_linphone_accounts_in_assistant)) {
            accountConnection.setVisibility(View.GONE);
        }

        TextView genericConnection = findViewById(R.id.generic_connection);
        genericConnection.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(
                                new Intent(
                                        MenuAssistantActivity.this,
                                        GenericConnectionAssistantActivity.class));
                    }
                });
        if (getResources().getBoolean(R.bool.hide_generic_accounts_in_assistant)) {
            genericConnection.setVisibility(View.GONE);
        }

        TextView remoteConfiguration = findViewById(R.id.remote_configuration);
        remoteConfiguration.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(
                                new Intent(
                                        MenuAssistantActivity.this,
                                        RemoteConfigurationAssistantActivity.class));
                    }
                });
        if (getResources().getBoolean(R.bool.hide_remote_provisioning_in_assistant)) {
            remoteConfiguration.setVisibility(View.GONE);
        }

        if (getResources().getBoolean(R.bool.assistant_use_linphone_login_as_first_fragment)) {
            startActivity(
                    new Intent(
                            MenuAssistantActivity.this, AccountConnectionAssistantActivity.class));
            finish();
        } else if (getResources()
                .getBoolean(R.bool.assistant_use_generic_login_as_first_fragment)) {
            startActivity(
                    new Intent(
                            MenuAssistantActivity.this, GenericConnectionAssistantActivity.class));
            finish();
        } else if (getResources()
                .getBoolean(R.bool.assistant_use_create_linphone_account_as_first_fragment)) {
            startActivity(
                    new Intent(
                            MenuAssistantActivity.this,
                            PhoneAccountCreationAssistantActivity.class));
            finish();
        }

        setUpTermsAndPrivacyLinks();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (getResources()
//                .getBoolean(R.bool.forbid_to_leave_assistant_before_account_configuration)) {
//            mBack.setEnabled(false);
//        }
//
//        mBack.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        LinphonePreferences.instance().firstLaunchSuccessful();
//                        goToLinphoneActivity();
//                    }
//                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getResources()
                    .getBoolean(R.bool.forbid_to_leave_assistant_before_account_configuration)) {
                // Do nothing
                return true;
            } else {
                LinphonePreferences.instance().firstLaunchSuccessful();
                goToLinphoneActivity();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setUpTermsAndPrivacyLinks() {
        String terms = getString(R.string.assistant_general_terms);
        String privacy = getString(R.string.assistant_privacy_policy);

        String label = getString(R.string.assistant_read_and_agree_terms, terms, privacy);
        Spannable spannable = new SpannableString(label);

        Matcher termsMatcher = Pattern.compile(terms).matcher(label);
        if (termsMatcher.find()) {
            ClickableSpan clickableSpan =
                    new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            Intent browserIntent =
                                    new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(
                                                    getString(
                                                            R.string
                                                                    .assistant_general_terms_link)));
                            startActivity(browserIntent);
                        }
                    };
            spannable.setSpan(
                    clickableSpan,
                    termsMatcher.start(0),
                    termsMatcher.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        Matcher privacyMatcher = Pattern.compile(privacy).matcher(label);
        if (privacyMatcher.find()) {
            ClickableSpan clickableSpan =
                    new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            Intent browserIntent =
                                    new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(
                                                    getString(
                                                            R.string
                                                                    .assistant_privacy_policy_link)));
                            startActivity(browserIntent);
                        }
                    };
            spannable.setSpan(
                    clickableSpan,
                    privacyMatcher.start(0),
                    privacyMatcher.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        TextView termsAndPrivacy = findViewById(R.id.terms_and_privacy);
        final CheckBox termsAndPrivacyCheckBox = findViewById(R.id.terms_and_privacy_checkbox);

        termsAndPrivacy.setText(spannable);
        termsAndPrivacy.setMovementMethod(new LinkMovementMethod());
        if (LinphonePreferences.instance().getReadAndAgreeTermsAndPrivacy()) {
            termsAndPrivacyCheckBox.setEnabled(false);
            termsAndPrivacyCheckBox.setChecked(true);
        } else {
            final TextView accountCreation = findViewById(R.id.account_creation);
            final TextView accountConnection = findViewById(R.id.account_connection);
            final TextView genericConnection = findViewById(R.id.generic_connection);
            final TextView remoteConfiguration = findViewById(R.id.remote_configuration);
            accountCreation.setEnabled(false);
            accountConnection.setEnabled(false);
            genericConnection.setEnabled(false);
            remoteConfiguration.setEnabled(false);

            termsAndPrivacyCheckBox.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                LinphonePreferences.instance().setReadAndAgreeTermsAndPrivacy(true);
                                termsAndPrivacyCheckBox.setEnabled(false);
                                accountCreation.setEnabled(true);
                                accountConnection.setEnabled(true);
                                genericConnection.setEnabled(true);
                                remoteConfiguration.setEnabled(true);
                            }
                        }
                    });
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.login_generic) {
            data.SaveTariff(21);
            startActivity(
                    new Intent(
                            MenuAssistantActivity.this, GenericConnectionAssistantActivity.class));
//            AssistantActivity().displayLoginGeneric();
        } else if (id == R.id.login_linphone) {
//            AssistantActivity.instance().displayLoginLinphone(null, null);
        } else if (id == R.id.create_account) {
            //DisplayCounty();
//            data.SaveTariff(10);
            data.SaveTariff(21);
            startActivity(
                    new Intent(
                            MenuAssistantActivity.this, RegisterAssistantActivity.class));
//            AssistantActivity.instance().displayRegisterPage();
//            AssistantActivity.instance().displayWebview();
        }
        else if (id == R.id.remote_provisioning) {
//            AssistantActivity.instance().displayRemoteProvisioning();
        }
    }
}
