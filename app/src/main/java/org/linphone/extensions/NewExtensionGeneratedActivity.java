package org.linphone.extensions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nk.ntalk.R;

import org.linphone.assistant.GenericConnectionAssistantActivity;
import org.linphone.assistant.SuccessAssistantActivity;
import org.linphone.dialer.DialerActivity;

public class NewExtensionGeneratedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_extension_generated);

        Button btnContinue = findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(
                                NewExtensionGeneratedActivity.this, DialerActivity.class));
            }
        });
    }
}