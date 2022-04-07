package org.linphone.support;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nk.ntalk.R;

import org.linphone.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class ContactSupportActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView ivBack;
    RelativeLayout rCall, rEmail;
    EditText editMsg;
    Button btnSend;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_support);

        utils = new Utils(this);
        ivBack = findViewById(R.id.back);
        rCall = findViewById(R.id.relative_call);
        rEmail = findViewById(R.id.relative_email);
        editMsg = findViewById(R.id.assistant_message);
        btnSend = findViewById(R.id.assistant_send);

        ivBack.setOnClickListener(this);
        rCall.setOnClickListener(this);
        rEmail.setOnClickListener(this);
        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.back) {
            onBackPressed();
        }
        if(id == R.id.relative_call) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.putExtra(Intent.EXTRA_PHONE_NUMBER, "+234 803 567 0547");
//            startActivity(intent);
        }
        if(id == R.id.relative_email) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.putExtra(Intent.EXTRA_EMAIL, "hello@nativetalk.io");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Contact support from NativeTalk App");
//            startActivity(intent);
        }
        if(id == R.id.assistant_send) {
            String comment = editMsg.getText().toString();
            if(TextUtils.isEmpty(comment)) {
                return;
            }
//            utils.displayDialog("Sending message...");
            Toast.makeText(ContactSupportActivity.this, "Sending message...", Toast.LENGTH_SHORT).show();
            Map<String, Object> mail = new HashMap<>();
            Map<String, Object> msg = new HashMap<>();
            msg.put("html", "<p>"+comment+"</p>");
            msg.put("subject", "Contact support from NativeTalk App");
            mail.put("to", "hello@nativetalk.io");
            mail.put("message", msg);
            FirebaseFirestore.getInstance().collection("mail").add(mail).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()) {
                        startActivity(new Intent(ContactSupportActivity.this, SuccessMailSentActivity.class));
                    }else {
                        Toast.makeText(ContactSupportActivity.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}