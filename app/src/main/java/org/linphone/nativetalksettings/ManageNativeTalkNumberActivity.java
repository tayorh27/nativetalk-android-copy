package org.linphone.nativetalksettings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;

import com.nk.ntalk.R;

public class ManageNativeTalkNumberActivity extends AppCompatActivity {

    Spinner periodSpin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_native_talk_number);

        ImageView ivBack = findViewById(R.id.back);
        periodSpin = findViewById(R.id.SpinnerPeroid);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}