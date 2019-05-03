package com.chris.massagehat;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.chris.massagehat.ble.BluetoothLeService;

import static java.lang.Thread.sleep;

public class MassageActivity extends Activity {

    private BluetoothLeService mBleService;
    private String mDeviceAddress = "";

    final Handler handler = new Handler();
    private long startTime = 0;
    private Boolean on = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massage);

        Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(MainActivity.ADRESS_KEY);
        mBleService = new BluetoothLeService(this);
        mBleService.connect(mDeviceAddress);

        Button relaxButton = findViewById(R.id.relax_button);
        relaxButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        relax();
                    }
                }
        );

        Button actionButton = findViewById(R.id.action_button);
        actionButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        action();
                    }
                }
        );

        Button offButton = findViewById(R.id.off_button);
        offButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendOff();
                    }
                }
        );
    }

    Runnable action = new Runnable() {
        @Override
        public void run() {
            while (System.currentTimeMillis() - startTime <= 10000) {
                if (on) {
                    sendOff();
                    on = false;
                } else {
                    sendOn();
                    on = true;
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void action() {
        startTime = System.currentTimeMillis();
        handler.post(action);
    }

    Runnable relax = new Runnable() {
        @Override
        public void run() {
            sendOn();
            while (System.currentTimeMillis() - startTime <= 10000) {
                // do Nothing
            }
            sendOff();
        }
    };

    private void relax() {
        startTime = System.currentTimeMillis();
        handler.post(relax);
    }

    private int createValue() {
        int fs = 0xff;
        int sc = 0x11;
        int tr = 0xaa;
        int fo = 0x00;
        int res = (fs << 24)|(sc << 16)|(tr << 8)|fo;
        return res;
    }

    private void sendOn() {
        if (mBleService != null && mBleService.getConnectionState() == BluetoothLeService.STATE_CONNECTED) {
            BluetoothGattCharacteristic characteristic = mBleService.getCharacteristic(Constants.SERVICE_NAME, Constants.CHARACTERISTIC_ID);
            int value = createValue();

            characteristic.setValue(value, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
            mBleService.writeCharacteristic(characteristic);
        }
    }

    private void sendOff() {
        if (mBleService != null && mBleService.getConnectionState() == BluetoothLeService.STATE_CONNECTED) {
            BluetoothGattCharacteristic characteristic = mBleService.getCharacteristic(Constants.SERVICE_NAME, Constants.CHARACTERISTIC_ID);
            int value = Constants.OFF;

            characteristic.setValue(value, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
            mBleService.writeCharacteristic(characteristic);
        }
    }

}
