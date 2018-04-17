package com.example.juggernaut.notificationble.BluetoothUtils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;


public class BluetoothInitialize {
    private static BluetoothManager bluetoothManager;
    private static BluetoothAdapter bluetoothAdapter;
    private static BluetoothLeScanner bluetoothLeScanner;
    private static BluetoothGatt bluetoothGatt;
    private static BluetoothDevice device;
    private static BluetoothGattCharacteristic bluetoothGattCharacteristic;

    private static Context context;
    private static final String TAG = "BluetoothInitialize";

    private static int value =0;
    private static final UUID SERVICE_ID = UUID.fromString("cb83de01-1e97-4054-8805-e5320971b54c");
    private static final UUID CHARACTERSTIC_ID = UUID.fromString("f20edf69-0760-4af8-bcc1-e2f9790ef719");

    public static void BluetoothInitializeHelper() {
        bluetoothManager = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"Scanning Started");
                bluetoothLeScanner.startScan(scanCallback);
            }
        });
    }
    public static ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            device = result.getDevice();
            if (device.getName().equals("Module")) {
                bluetoothGatt = device.connectGatt(BluetoothInitialize.context, false, bluetoothGattCallback);
                Toast.makeText(BluetoothInitialize.context, "Module found", Toast.LENGTH_SHORT).show();
                stopScanning();
            }
        }
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };
    public static void stopScanning(){
        Log.i(TAG, "Stopped scanning");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                bluetoothLeScanner.stopScan(scanCallback);
            }
        });
    }
    public static BluetoothGattCallback bluetoothGattCallback =new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                Log.i(TAG, "onConnectionStateChange() - STATE_CONNECTED");
                boolean discoverServicesOk = gatt.discoverServices();
                Log.i(TAG,Boolean.toString(discoverServicesOk));
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                Log.i(TAG, "onConnectionStateChange() - STATE_DISCONNECTED");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            BluetoothInitialize.bluetoothGatt = gatt;
            BluetoothInitialize.bluetoothGattCharacteristic=gatt.getService(SERVICE_ID).getCharacteristic(CHARACTERSTIC_ID);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG,"Characteristic write");
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.i(TAG,String.valueOf(BluetoothInitialize.value));
            characteristic.setValue(new byte[] {(byte)BluetoothInitialize.value});
            gatt.writeCharacteristic(characteristic);
        }
    };
    public static void setContext(Context context) {
        BluetoothInitialize.context = context;
    }
    public static void setValue(int value) {
        BluetoothInitialize.value = value;
        bluetoothGattCallback.onCharacteristicWrite(bluetoothGatt,bluetoothGattCharacteristic,value);
    }

}
