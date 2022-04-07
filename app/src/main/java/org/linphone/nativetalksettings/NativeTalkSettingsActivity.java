package org.linphone.nativetalksettings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nk.ntalk.R;

import org.linphone.dialer.DialerActivity;
import org.linphone.extensions.ChangeExtensionsActivity;
import org.linphone.extensions.NewExtensionGeneratedActivity;

public class NativeTalkSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_talk_settings);

        RelativeLayout relativeLayout = findViewById(R.id.rNative);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(
                                NativeTalkSettingsActivity.this, BuyNumberMenuActivity.class));
            }
        });

        ImageView ivBack = findViewById(R.id.back);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}