package com.chris.massagehat;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chris.massagehat.ble.BluetoothLeService;

public class LearnMoreActivity extends Activity {

    private BluetoothLeService bleService = null;
    private String deviceAddress = "";

    TextView amount = null;
    Button button =  null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_more);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        deviceAddress = intent.getStringExtra(MainActivity.ADDRESS_KEY);
        amount = findViewById(R.id.amount);
        button = findViewById(R.id.getamount_button);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getAmount();
                    }
                }
        );
        if (!deviceAddress.equals("")) {
            bleService = new BluetoothLeService(this);
            if (bleService.connect(deviceAddress)) {
                getAmount();
            } else {
                amount.setText("Connection Failed, try again");
            }
        } else {
            amount.setText("connect to find out");
        }
    }

    private void getAmount() {
        if (bleService != null && bleService.getConnectionState() == BluetoothLeService.STATE_CONNECTED) {
            final BluetoothGattCharacteristic characteristic = bleService.getCharacteristic(Constants.SERVICE_NAME, Constants.CHARACTERISTIC_READ1);
            if (characteristic != null) {
                bleService.readCharacteristic(characteristic);
                while (bleService.isReading()) {
                    // wait
                }
                int val = characteristic.getValue()[0];
                amount.setText("Amount of Massage Points: " + val);
            }
        } else {
            amount.setText("connect to find out");
        }
    }
}
