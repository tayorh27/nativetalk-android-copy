package org.linphone;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.nk.ntalk.R;

import co.paystack.android.PaystackSdk;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MyApplication extends Application {

    private static MyApplication sInstance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("avenir_light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        PaystackSdk.initialize(this);
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

}
