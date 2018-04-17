package com.example.juggernaut.notificationble;

import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.juggernaut.notificationble.BluetoothUtils.BluetoothInitialize;

public class NotificationListener extends NotificationListenerService {
    private static final String TAG = "Notification";
    private byte value=0x00;
    private String ApplicationName;
    Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        BluetoothInitialize.setContext(NotificationListener.this);
        BluetoothInitialize.BluetoothInitializeHelper();
    }
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!(sbn.getTag()==null)) {
            Log.v(TAG,sbn.getKey());
            sendNotification(sbn.getPackageName());
        }
    }
    public void sendNotification(String applicationName) {
        switch (applicationName) {
            case "com.whatsapp":
                BluetoothInitialize.setValue((byte) 0x00);
                break;
            case "com.google.android.dialer":
                BluetoothInitialize.setValue((byte) 0x01);
                break;
            case "com.google.android.apps.messaging":
                BluetoothInitialize.setValue((byte) 0x02);
                break;
            default:
                BluetoothInitialize.setValue((byte) 0x05);
                break;
        }
    }
}