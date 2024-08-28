package com.xyvcard.test;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.xyvcard.push.XyPushManager;
import com.xyvcard.push.utils.BadgeUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        XyPushManager.getInstance().addOnRefreshPushTokenListener((token, deviceType) -> {
            Log.d(TAG, "onRefreshToken: " + token + "-" + deviceType);
        });

        XyPushManager.getInstance().registerPush();

        findViewById(R.id.open_notify_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BadgeUtils.openNotificationSetting(MainActivity.this);
            }
        });
    }


}