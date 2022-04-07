package org.linphone.popups;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.nk.ntalk.R;

import org.linphone.DataStore.AppData;
import org.linphone.LinphoneManager;
import org.linphone.assistant.MenuAssistantActivity;
import org.linphone.core.Core;

public class LogoutPopupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_logout_popup);

        Button cancelBtn = findViewById(R.id.BtnNo);
        Button logoutBtn = findViewById(R.id.BtnYes);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
            }
        });
    }

    private void Logout() {
        Core core = LinphoneManager.getCore();
        if (core != null) {
            new AppData(this).LogOut();
            FirebaseAuth.getInstance().signOut();
            core.setDefaultProxyConfig(null);
            core.clearAllAuthInfo();
            core.clearProxyConfig();
            startActivity(
                    new Intent()
                            .setClass(
                                    LogoutPopupActivity.this,
                                    MenuAssistantActivity.class));
            finish();
        }
    }
}
