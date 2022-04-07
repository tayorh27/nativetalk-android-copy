package org.linphone.accounts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.nk.ntalk.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.AppConfig;
import org.linphone.DataStore.AppData;
import org.linphone.DataStore.Client;
import org.linphone.LinphoneManager;
import org.linphone.assistant.GenericConnectionAssistantActivity;
import org.linphone.core.AccountCreator;
import org.linphone.core.ProxyConfig;
import org.linphone.utils.LinphoneUtils;
import org.linphone.utils.Utils;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText cPassword, nPassword;
    Button btnSave;

    Utils utils;
    AppData data;
    ProxyConfig proxy = Objects.requireNonNull(LinphoneManager.getCore()).getDefaultProxyConfig();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        utils = new Utils(this);
        data = new AppData(this);

        cPassword = findViewById(R.id.assistant_cpassword);
        nPassword = findViewById(R.id.assistant_password);
        btnSave = findViewById(R.id.assistant_save_password);

        ImageView ivBack = findViewById(R.id.back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentPassword = data.getPassword();//accountCreator.getPassword();

                String getCP = cPassword.getText().toString();
                String getNP = nPassword.getText().toString();

                Log.e("TAGCHANG", "onClick: "+currentPassword );
                Log.e("TAGCHANGCP", "onClick: "+getCP );

                if(!getCP.contentEquals(currentPassword)) {
                    utils.error("Current password is incorrect");
                    return;
                }

                if(getNP.length() < 8) {
                    utils.error("New password must have at least 8 characters");
                    return;
                }

                UpdatedNewPassword(getCP, getNP);

//                Matcher matcher = new PatternMatcher("");

//                if(getNP.contains("")) {
//                    utils.error("New password must have at least 8 characters");
//                    return;
//                }


            }
        });
    }

    private void UpdatedNewPassword(String oldPassword, String password) {
        utils.displayDialog("Updating password...");

        String url = AppConfig.API_URL + "/VS.WebAPI.Admin/json/syncreply/admin.client.password.set";
        int[] details = data.getSaveClientDetails();
        JSONObject object = new JSONObject();
        try {
            object.put("clientId", details[0]);
            object.put("clientType", details[1]);
            object.put("password", password);
            object.put("webPassword", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String res = utils.MakeHttpsApiCalls(url, object.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                JSONObject object = new JSONObject(res);
                                if(object.has("responseStatus")) {
                                    utils.dismissDialog();
                                    JSONObject errorObj = object.getJSONObject("responseStatus");
                                    String message = errorObj.getString("message");
                                    Toast.makeText(ChangePasswordActivity.this, "Password change failed. Please try again.", Toast.LENGTH_LONG).show();
                                    utils.error(message);
                                    return;
                                }
                                data.SavePassword(password);
                                String mUsername = LinphoneUtils.getAddressDisplayName(proxy.getIdentityAddress());
                                String email = mUsername.toLowerCase()+"@nativetalk.com.ng";
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, oldPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                utils.dismissDialog();
                                                startActivity(
                                                        new Intent(
                                                                ChangePasswordActivity.this, SuccessPasswordChangeActivity.class));
                                            }
                                        });
                                    }
                                });
                            } catch (JSONException e) {
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
}