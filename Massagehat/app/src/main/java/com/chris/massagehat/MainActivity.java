package com.chris.massagehat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chris.massagehat.ble.BluetoothConnectionActivity;

import static com.chris.massagehat.ble.BluetoothConnectionActivity.BT_ERROR;
import static com.chris.massagehat.ble.BluetoothConnectionActivity.BT_PERMISSION_NOT_GRANTED;
import static com.chris.massagehat.ble.BluetoothConnectionActivity.REQUEST_NO_DEVICE_FOUND;
import static com.chris.massagehat.ble.BluetoothConnectionActivity.REQUEST_SCAN_BLE;

public class MainActivity extends AppCompatActivity {

    public static final String ADDRESS_KEY = "adresskey";
    private static final int REQUEST_GPS_PERMISSIONS = 16;

    private boolean bleSetup = false;
    private boolean permissionGranted = false;

    private String deviceAddress = "";

    Button button = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermissions();

        Button learnButton = findViewById(R.id.lernButton);
        learnButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoLearnmore();
                    }
                }
        );

        button = findViewById(R.id.connectButton);
        if (bleSetup) {
            button.setText(R.string.start_text);
        } else {
            button.setText(R.string.connect_text);
        }
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bleSetup) {
                            startMassage();
                        } else {
                            setupBluetooth();
                        }
                    }
                }
        );
    }

    private void getPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_GPS_PERMISSIONS);
        } else {
            permissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == REQUEST_GPS_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true;
            } else {
                Toast.makeText(this,
                        "You need the permission to use the App",
                        Toast.LENGTH_LONG)
                        .show();
                finish();
            }
        }
    }

    private void setupBluetooth() {
        if (permissionGranted) {
            if (!bleSetup) {
                Intent startBleIntent = new Intent(this, BluetoothConnectionActivity.class);
                startBleIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startBleIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivityForResult(startBleIntent, REQUEST_SCAN_BLE);
            }
        } else {
            getPermissions();
        }
    }

    private void startMassage() {
        Intent intent = new Intent(this, MassageActivity.class);
        intent.putExtra(ADDRESS_KEY, deviceAddress);
        startActivity(intent);
    }

    private void gotoLearnmore() {
        Intent intent = new Intent(this, LearnMoreActivity.class);
        intent.putExtra(ADDRESS_KEY, deviceAddress);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case REQUEST_SCAN_BLE:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        deviceAddress = ((String) extras.get(BluetoothConnectionActivity.EXTRA_DEVICE_ADDR));
                        bleSetup = true;

                        Toast.makeText(this,
                                "Found device",
                                Toast.LENGTH_LONG)
                                .show();

                        button.setText(R.string.start_text);
                    } else {
                        Toast.makeText(this,
                                "No address for Bluetooth device passed",
                                Toast.LENGTH_LONG)
                                .show();
                    }

                }

                break;
            case BluetoothConnectionActivity.BLE_NOT_SUPPORTED:
                Toast.makeText(this,
                        "BLE is not supported on this device.",
                        Toast.LENGTH_LONG)
                        .show();
                break;
            case REQUEST_NO_DEVICE_FOUND:
                Toast.makeText(this,
                        "The requested device could not be found",
                        Toast.LENGTH_LONG)
                        .show();

                break;
            case BT_PERMISSION_NOT_GRANTED:
                Toast.makeText(this,
                        "The permission to use Bluetooth is not granted",
                        Toast.LENGTH_LONG)
                        .show();
                break;
            case BT_ERROR:
                Toast.makeText(this,
                        "An error occurred while trying to build up a BT connection",
                        Toast.LENGTH_LONG)
                        .show();
                break;
            default:
                Log.e(Constants.TAG, "Activity ended due to an unknown reason");
                break;
        }
    }
}
