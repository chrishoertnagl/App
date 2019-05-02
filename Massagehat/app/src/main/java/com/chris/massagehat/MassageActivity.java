package com.chris.massagehat;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.chris.massagehat.ble.BluetoothLeService;

public class MassageActivity extends Activity {

    private BluetoothLeService mBleService;
    private String mDeviceAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massage);

        Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(MainActivity.ADRESS_KEY);
        mBleService = new BluetoothLeService(this);
        mBleService.connect(mDeviceAddress);

        Button onButton = findViewById(R.id.strong_button);
        onButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendOn();
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


    private void sendOn() {
        if (mBleService != null && mBleService.getConnectionState() == BluetoothLeService.STATE_CONNECTED) {
            BluetoothGattCharacteristic characteristic = mBleService.getCharacteristic(Constants.SERVICE_NAME, Constants.CHARACTERISTIC_ID);
            int value = Constants.ON;

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
