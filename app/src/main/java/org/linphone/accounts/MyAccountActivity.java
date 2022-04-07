package org.linphone.accounts;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.nk.ntalk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.AppConfig;
import org.linphone.DataStore.AppData;
import org.linphone.LinphoneManager;
import org.linphone.assistant.MenuAssistantActivity;
import org.linphone.core.Core;
import org.linphone.core.ProxyConfig;
import org.linphone.popups.LogoutPopupActivity;
import org.linphone.popups.SharePopupActivity;
import org.linphone.utils.LinphoneUtils;
import org.linphone.utils.Utils;

import java.io.IOException;
import java.util.Objects;

public class MyAccountActivity extends AppCompatActivity implements View.OnClickListener {

    TextView balance;
    Button addF, viewAcc;
    TextView editProfile, changePass, shareNumber, logOutBtn;
    TextView tvU, tvD;
    ImageView ivBack;
    Utils utils;
    AppData data;
    ProxyConfig proxy = Objects.requireNonNull(LinphoneManager.getCore()).getDefaultProxyConfig();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account);

        utils = new Utils(this);
        data = new AppData(this);

        balance = findViewById(R.id.tvBalance);
        addF = findViewById(R.id.BtnFunds);
        editProfile = findViewById(R.id.BtnEditProfile);
        changePass = findViewById(R.id.BtnChangePassword);
        viewAcc = findViewById(R.id.BtnView);
        ivBack = findViewById(R.id.back);
        shareNumber = findViewById(R.id.BtnShare);
        logOutBtn = findViewById(R.id.BtnLogout);

        tvU = findViewById(R.id.tvUsername);
        tvD = findViewById(R.id.tvDomain);

        addF.setOnClickListener(this);
        editProfile.setOnClickListener(this);
        changePass.setOnClickListener(this);
        viewAcc.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        shareNumber.setOnClickListener(this);
        logOutBtn.setOnClickListener(this);

        if(proxy != null){
            String username = LinphoneUtils.getAddressDisplayName(proxy.getIdentityAddress());
            String domain = proxy.getIdentityAddress().asStringUriOnly();
            tvU.setText(username);
            tvD.setText(domain);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.BtnFunds) {
            if (proxy == null) {
                new Utils(this).error("Account not configured yet!");
                return;
            }
            startActivity(new Intent(this, BuyCreditActivity.class));
        }
        if(id == R.id.BtnLogout) {
            startActivity(new Intent(this, LogoutPopupActivity.class));
        }
        if (id == R.id.BtnEditProfile) {
            startActivity(new Intent(this, EditProfileActivity.class));
        }
        if (id == R.id.BtnChangePassword) {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        }
        if (id == R.id.BtnView) {
//            LinphoneActivity.instance().displayOnlineAccount(AppConfig.NATIVETALK_API_URL+"/VUP");
        }
        if (id == R.id.back) {
            onBackPressed();
        }
        if (id == R.id.BtnShare) {
//            GetPlanBalance();
            startActivity(new Intent(this, SharePopupActivity.class));
        }
    }

    private void GetPlanBalance() {
        utils.displayDialog("Checking balance...");
        String url = AppConfig.API_URL + "/VS.WebAPI.Admin/json/syncreply/admin.client.bundle.state.get";
        int[] details = data.getSaveClientDetails();
        JSONObject jsonObject = new JSONObject();
        try {
            //jsonObject.put("planId", data.getPlanId());
            jsonObject.put("clientId", details[0]);
            jsonObject.put("clientType", details[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String res = utils.MakeHttpsApiCalls(url, jsonObject.toString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            try {
                                JSONObject object = new JSONObject(res);
                                if (!object.isNull("responseStatus")) {
                                    utils.error(object.getJSONObject("responseStatus").getString("message"));
                                    return;
                                }
                                //String planName = object.getString("planName");
                                int accountState = object.getJSONArray("data").getJSONObject(0).getInt("accountState");
                                String bundleName = object.getJSONArray("data").getJSONObject(0).getString("bundleName");
                                utils.error("Bundle Name: " + bundleName + "\nAccount Balance: " + accountState);
                            } catch (JSONException e) {
                                utils.error("You don't have any plan.");
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    utils.dismissDialog();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void GetAccountBalance() {
        Log.e("testing", "onStart: here");
        String username = LinphoneUtils.getAddressDisplayName(proxy.getIdentityAddress());
        Utils utils = new Utils(this);
        String url = AppConfig.API_URL + "/VSServices/Export.ashx?f=GetClientBalance&pin=" + username;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String res = utils.GetNormalApiCalls(url);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String bal = res;
                            //Log.e("currency", "run: "+data.getTariff());
//                                if (data.getTariff() == 10) {
//                                    balance.setText("$" + res);
//                                } else {
                            if(bal.startsWith("<!")) {
                                bal = "0.00";
                            }
                            balance.setText("₦" + bal);
//                                }
                        }
                    });
                } catch (IOException e) {
                    Log.e("testing", "onStart: here"+e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        GetAccountBalance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetAccountBalance();
    }
}
