package org.linphone.nativetalksettings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nk.ntalk.R;

public class BuyNumberMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_number_menu);

        ImageView ivBack = findViewById(R.id.back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        RelativeLayout relativeLayoutBuy = findViewById(R.id.rrBuy);
        relativeLayoutBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(
                                BuyNumberMenuActivity.this, BuyNativeTalkNumberActivity.class));
            }
        });

        RelativeLayout relativeLayoutMange = findViewById(R.id.rrManage);
        relativeLayoutMange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(
                                BuyNumberMenuActivity.this, ManageNativeTalkNumberActivity.class));
            }
        });
    }
}