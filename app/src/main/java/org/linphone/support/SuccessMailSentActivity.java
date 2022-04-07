package org.linphone.support;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nk.ntalk.R;

import org.linphone.dialer.DialerActivity;
import org.linphone.extensions.NewExtensionGeneratedActivity;

public class SuccessMailSentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_mail_sent);

        Button btnContinue = findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(
                                SuccessMailSentActivity.this, DialerActivity.class));
            }
        });
    }
}