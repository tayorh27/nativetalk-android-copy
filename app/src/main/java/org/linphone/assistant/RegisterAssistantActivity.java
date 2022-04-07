package org.linphone.assistant;

        import android.app.Fragment;
        import android.content.Intent;
        import android.os.Bundle;
        import android.text.Editable;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.RadioGroup;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.gson.JsonObject;
        import com.nk.ntalk.R;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;
        import org.linphone.AppConfig;
        import org.linphone.DataStore.AppData;
        import org.linphone.utils.Utils;
//        import org.linphone.core.LinphoneAddress;
        import org.linphone.models.Plans;

        import java.io.IOException;

        import androidx.annotation.NonNull;

public class RegisterAssistantActivity extends AssistantActivity implements View.OnClickListener {

    Utils utils;
    private EditText login, fullname, password, email;
    private Button apply;
    AppData data;
    private ImageView btnBack;
    private TextView btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        utils = new Utils(RegisterAssistantActivity.this);
        data = new AppData(RegisterAssistantActivity.this);

        btnBack =  findViewById(R.id.create_account_back);
        btnSignIn =  findViewById(R.id.sign_in_btn);

        login = (EditText) findViewById(R.id.assistant_username);
        fullname = (EditText) findViewById(R.id.assistant_full_name);
        password = (EditText) findViewById(R.id.assistant_password);
        email = (EditText) findViewById(R.id.assistant_email_address);
        apply = (Button) findViewById(R.id.assistant_reg);
//        apply.setEnabled(false);
        apply.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.create_account_back) {
            onBackPressed();
        }

        if(id == R.id.sign_in_btn) {
            startActivity(
                    new Intent(
                            RegisterAssistantActivity.this, GenericConnectionAssistantActivity.class));
        }

        if (id == R.id.assistant_reg) {
            if (login.getText() == null || login.length() == 0 || password.getText() == null || password.length() == 0 || fullname.getText() == null || fullname.length() == 0 || email.getText() == null || email.length() == 0) {
                Toast.makeText(RegisterAssistantActivity.this, getString(R.string.first_launch_no_login_password_register), Toast.LENGTH_LONG).show();
                return;
            }
//            Toast.makeText(getActivity(), "Please wait...", Toast.LENGTH_SHORT).show();
            utils.displayDialog("Please wait...");
            String lastName = (fullname.getText().toString().split(" ").length > 1) ? fullname.getText().toString().split(" ")[1] : "";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("login", login.getText().toString());
                jsonObject.put("password", password.getText().toString());
                jsonObject.put("webPassword", password.getText().toString());
                jsonObject.put("address", "");
                jsonObject.put("zipCode", "");
                jsonObject.put("city", "");
                jsonObject.put("taxID", "");
                jsonObject.put("eMail", email.getText().toString());
                jsonObject.put("phoneNumber", login.getText().toString());
                jsonObject.put("mobileNumber", login.getText().toString());
                jsonObject.put("tariffId", 21);
                jsonObject.put("accountState", 0);
                jsonObject.put("generateInvoice", false);
                jsonObject.put("sendInvoice", false);
                jsonObject.put("country", "Nigeria");
                jsonObject.put("state", "");
                jsonObject.put("firstName", fullname.getText().toString().split(" ")[0]);
                jsonObject.put("lastName", lastName);
                jsonObject.put("callsLimit", 0);
                jsonObject.put("postPaid", false);
                jsonObject.put("postPaidCredit", 0);
                jsonObject.put("postPaidDescription", "");
                jsonObject.put("resellerId", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = AppConfig.API_URL + "/VS.WebAPI.Admin/json/syncreply/admin.retail.create";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String res = utils.MakeHttpsApiCalls(url, jsonObject.toString());
//                        Log.e("TAGd", "run: "+res );
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Log.e("planResponse", res);
                                utils.dismissDialog();
                                try {
                                    JSONObject object = new JSONObject(res);
                                    if(object.has("responseStatus")) {
                                        JSONObject errorObj = object.getJSONObject("responseStatus");
                                        String message = errorObj.getString("message");
                                        Toast.makeText(RegisterAssistantActivity.this, "Registration failed. Please try again.", Toast.LENGTH_LONG).show();
                                        utils.error(message);
                                        return;
                                    }
                                    Toast.makeText(RegisterAssistantActivity.this, "Registration successful. Please login.", Toast.LENGTH_LONG).show();
                                    data.SaveTariff(21);
                                    startActivity(
                                            new Intent(
                                                    RegisterAssistantActivity.this, SuccessAssistantActivity.class));
//                                    AssistantActivity.instance().displaySuccessRegisterPage();
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
}

