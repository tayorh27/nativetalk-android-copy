package org.linphone.accounts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nk.ntalk.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.AppConfig;
import org.linphone.DataStore.AppData;
import org.linphone.DataStore.Client;
import org.linphone.LinphoneManager;
import org.linphone.assistant.RegisterAssistantActivity;
import org.linphone.assistant.SuccessAssistantActivity;
import org.linphone.core.ProxyConfig;
import org.linphone.utils.LinphoneUtils;
import org.linphone.utils.Utils;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {

    Utils utils;
    AppData data;
    EditText editUsername, editFirstName, editLastName, editEmail, editAddress, editCountry, editState, editCity, editPostal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        utils = new Utils(this);
        data = new AppData(this);

        TextView btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        TextView btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateProfile();
            }
        });

        editUsername = findViewById(R.id.assistant_username);
        editFirstName = findViewById(R.id.assistant_first_name);
        editLastName = findViewById(R.id.assistant_last_name);
        editEmail = findViewById(R.id.assistant_email_address);
        editAddress = findViewById(R.id.assistant_address);
        editCountry = findViewById(R.id.assistant_country);
        editState = findViewById(R.id.assistant_state);
        editCity = findViewById(R.id.assistant_city);
        editPostal = findViewById(R.id.assistant_postal_code);
    }

    private void UpdateProfile() {
        if (editFirstName.getText() == null || editFirstName.length() == 0 || editLastName.getText() == null || editLastName.length() == 0 || editUsername.getText() == null || editUsername.length() == 0 || editEmail.getText() == null || editEmail.length() == 0) {
//            Toast.makeText(EditProfileActivity.this, "Please ", Toast.LENGTH_LONG).show();
            utils.error("Make sure you fill your first name and last name.");
            return;
        }
        utils.displayDialog("Updating profile data...");

        String url = AppConfig.API_URL + "/VS.WebAPI.Admin/json/syncreply/admin.client.personal.update";
        int[] details = data.getSaveClientDetails();

        JSONObject objectM = new JSONObject();
        JSONObject object = new JSONObject();
        try {
            object.put("idClient", details[0]);
            object.put("clientType", details[1]);
//            data.getClientInformation().setTaxId(customer_code_naira);
//            data.SaveClientInformation(data.getClientInformation());
            objectM.put("firstName", editFirstName.getText().toString());
            objectM.put("eMail", editEmail.getText().toString().toLowerCase());
            objectM.put("lastName", editLastName.getText().toString());
            objectM.put("country", editCountry.getText().toString());
            objectM.put("state", editState.getText().toString());
            objectM.put("address", editAddress.getText().toString());
            objectM.put("city", editCity.getText().toString());
            objectM.put("zip", editPostal.getText().toString());
            objectM.put("taxId", data.getClientInformation().getTaxId());
            objectM.put("phoneNumber", data.getClientInformation().getPhoneNumber());
            objectM.put("mobileNumber", data.getClientInformation().getPhoneNumber());
            object.put("personal", objectM);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String res = utils.MakeHttpsApiCalls(url, object.toString());
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
                                    Toast.makeText(EditProfileActivity.this, "Profile update failed. Please try again.", Toast.LENGTH_LONG).show();
                                    utils.error(message);
                                    return;
                                }
                                Client client = data.getClientInformation();
                                client.setFirstName(editFirstName.getText().toString());
                                client.setLastName(editLastName.getText().toString());
                                client.setEmail(editEmail.getText().toString());
                                client.setCountry(editCountry.getText().toString());
                                client.setState(editState.getText().toString());
                                client.setAddress(editAddress.getText().toString());
                                client.setCity(editCity.getText().toString());
                                client.setPostalCode(editPostal.getText().toString());
                                data.SaveClientInformation(client);
                                Toast.makeText(EditProfileActivity.this, "Profile update successful.", Toast.LENGTH_LONG).show();
                                data.SaveTariff(21);
                                onBackPressed();
//                                startActivity(
//                                        new Intent(
//                                                EditProfileActivity.this, SuccessAssistantActivity.class));
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

    @Override
    protected void onStart() {
        super.onStart();

        ProxyConfig proxy = LinphoneManager.getCore().getDefaultProxyConfig();
        if(proxy != null){
            String username = LinphoneUtils.getAddressDisplayName(proxy.getIdentityAddress());
            editUsername.setText(username);
        }

        Client client = data.getClientInformation();
//        editUsername.setText(client.getPhoneNumber());
        editFirstName.setText(client.getFirstName());
        editLastName.setText(client.getLastName());
        editEmail.setText(client.getEmail());
        editAddress.setText(client.getAddress());
        editCountry.setText(client.getCountry());
        editState.setText(client.getState());
        editCity.setText(client.getCity());
        editPostal.setText(client.getPostalCode());
    }
}