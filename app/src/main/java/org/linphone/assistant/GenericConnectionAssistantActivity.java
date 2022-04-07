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
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.AppConfig;
import org.linphone.DataStore.AppData;
import org.linphone.LinphoneManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.nk.ntalk.R;
import org.linphone.core.AccountCreator;
import org.linphone.core.Core;
import org.linphone.core.TransportType;
import org.linphone.core.tools.Log;
import org.linphone.utils.Utils;

import java.io.IOException;

public class GenericConnectionAssistantActivity extends AssistantActivity implements TextWatcher, View.OnClickListener {
    private TextView mLogin;
    private EditText mUsername, mPassword, mDomain, mDisplayName;
    private RadioGroup mTransport;
    private ImageView btnBack;
    private TextView btnForgotPassword, btnCreateAccount;

    Utils utils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.assistant_generic_connection);
        setContentView(R.layout.activity_login);

        utils = new Utils(GenericConnectionAssistantActivity.this);

        btnBack = findViewById(R.id.login_back);
        btnForgotPassword =  findViewById(R.id.forgot_password_btn);
        btnCreateAccount = findViewById(R.id.create_acct_btn);

        btnBack.setOnClickListener(this);
        btnForgotPassword.setOnClickListener(this);
        btnCreateAccount.setOnClickListener(this);

        mLogin = findViewById(R.id.assistant_login);
        mLogin.setEnabled(false);
        mLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AppData(GenericConnectionAssistantActivity.this).SavePassword(mPassword.getText().toString());
                        utils.displayDialog("Please wait...");
                        verifyLogin();
//                        configureAccount();
                    }
                });

        mUsername = findViewById(R.id.assistant_username);
        mUsername.addTextChangedListener(this);
        mDisplayName = findViewById(R.id.assistant_display_name);
        mDisplayName.addTextChangedListener(this);
        mPassword = findViewById(R.id.assistant_password);
        mPassword.addTextChangedListener(this);
        mDomain = findViewById(R.id.assistant_domain);
        mDomain.addTextChangedListener(this);
        mTransport = findViewById(R.id.assistant_transports);
    }

    void signUserIn() {
        android.util.Log.e("Gen", "signUserIn: " );
        String email = mUsername.getText().toString().toLowerCase()+"@nativetalk.com.ng";
        String pwd = mPassword.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    new AppData(GenericConnectionAssistantActivity.this).SaveUID(task.getResult().getUser().getUid());
                }
                utils.dismissDialog();
                configureAccount();
//                AssistantActivity.instance().genericLogIn(login.getText().toString(), password.getText().toString(), null, domain.getText().toString(), transport)
            }
        });
    }

    private void verifyLogin() {
        String url = AppConfig.API_URL + "/VS.WebAPI.Admin/json/syncreply/admin.client.login";
        JSONObject object = new JSONObject();
        try {
            object.put("login", mUsername.getText().toString());
            object.put("password", mPassword.getText().toString());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final String res = utils.MakeHttpsApiCalls(url, object.toString());
                        Log.e("login", "run: " + res);
//                        if(getActivity() != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject jsonObject = new JSONObject(res);
                                    if(jsonObject.has("responseStatus")) {
                                        utils.dismissDialog();
                                        utils.error("Invalid email address or password");
                                        return;
                                    }
                                    String email = mUsername.getText().toString().toLowerCase()+"@nativetalk.com.ng";
                                    String pwd = mPassword.getText().toString();
                                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()) {
                                                android.util.Log.e("Gen", "createUserIn: " );
                                                new AppData(GenericConnectionAssistantActivity.this).SaveUID(task.getResult().getUser().getUid());
                                                utils.dismissDialog();
                                                configureAccount();
//                                                AssistantActivity.instance().genericLogIn(login.getText().toString(), password.getText().toString(), null, domain.getText().toString(), transport);
                                            }else {
                                                signUserIn();
                                            }
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (JSONException e) {
            android.util.Log.e("currencyLOgin3", "doInBackground: " +e.getMessage());
            e.printStackTrace();
        }
    }

    private void configureAccount() {
        Core core = LinphoneManager.getCore();
        if (core != null) {
            Log.i("[Generic Connection Assistant] Reloading configuration with default");
            reloadDefaultAccountCreatorConfig();
        }

        AccountCreator accountCreator = getAccountCreator();
        accountCreator.setUsername(mUsername.getText().toString());
        accountCreator.setDomain(mDomain.getText().toString());
        accountCreator.setPassword(mPassword.getText().toString());
        accountCreator.setDisplayName(mDisplayName.getText().toString());

        switch (mTransport.getCheckedRadioButtonId()) {
            case R.id.transport_udp:
                accountCreator.setTransport(TransportType.Udp);
                break;
            case R.id.transport_tcp:
                accountCreator.setTransport(TransportType.Tcp);
                break;
            case R.id.transport_tls:
                accountCreator.setTransport(TransportType.Tls);
                break;
        }

        createProxyConfigAndLeaveAssistant(true);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mLogin.setEnabled(
                !mUsername.getText().toString().isEmpty()
                        && !mDomain.getText().toString().isEmpty() && !mPassword.getText().toString().isEmpty());
    }

    @Override
    public void afterTextChanged(Editable s) {}

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.login_back) {
            onBackPressed();
        }

        if(id == R.id.forgot_password_btn) {
//            AssistantActivity.instance().displayResetPasswordPage();
        }

        if(id == R.id.create_acct_btn) {
            startActivity(
                    new Intent(
                            GenericConnectionAssistantActivity.this, RegisterAssistantActivity.class));
        }
    }
}
