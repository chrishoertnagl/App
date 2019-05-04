package com.chris.massagehat;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chris.massagehat.ble.BluetoothLeService;

import static java.lang.Thread.sleep;

public class MassageActivity extends Activity {

    private BluetoothLeService bleService;
    private String deviceAddress = "";

    private Thread thread = null;
    private long startTime = 0;
    private int strength = 0;
    private int program = 0;
    private boolean on = false;
    private boolean programRunning = false;
    private boolean stopProgram = false;

    Button offButton = null;
    private boolean offBtnClicked = true;
    private int buttonColor = R.drawable.mybtnback;
    private int buttonClickedColor = R.drawable.mybtnbackclicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massage);

        Intent intent = getIntent();
        deviceAddress = intent.getStringExtra(MainActivity.ADDRESS_KEY);
        bleService = new BluetoothLeService(this);
        bleService.connect(deviceAddress);

        Button relaxButton = findViewById(R.id.relax_button);
        relaxButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!offBtnClicked) {
                            startProgram(Constants.RELAX);
                        }
                    }
                }
        );

        Button actionButton = findViewById(R.id.action_button);
        actionButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!offBtnClicked) {
                            startProgram(Constants.ACTION);
                        }
                    }
                }
        );

        Button wakeupButton = findViewById(R.id.wakeup_button);
        wakeupButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!offBtnClicked) {
                            startProgram(Constants.WAKEUP);
                        }
                    }
                }
        );

        Button crazyButton = findViewById(R.id.crazy_button);
        crazyButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!offBtnClicked) {
                            startProgram(Constants.CRAZY);
                        }
                    }
                }
        );



        offButton = findViewById(R.id.off_button);
        final Button strongButton = findViewById(R.id.strong_button);
        final Button mediumButton = findViewById(R.id.medium_button);
        final Button weakButton = findViewById(R.id.weak_button);

        offButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        offButton.setBackground(getResources().getDrawable(buttonClickedColor));
                        strongButton.setBackground(getResources().getDrawable(buttonColor));
                        mediumButton.setBackground(getResources().getDrawable(buttonColor));
                        weakButton.setBackground(getResources().getDrawable(buttonColor));
                        offBtnClicked = true;
                        strength = 0;
                        stopProgram = true;
                        sendOff();
                    }
                }
        );


        strongButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        offButton.setBackground(getResources().getDrawable(buttonColor));
                        strongButton.setBackground(getResources().getDrawable(buttonClickedColor));
                        mediumButton.setBackground(getResources().getDrawable(buttonColor));
                        weakButton.setBackground(getResources().getDrawable(buttonColor));
                        offBtnClicked = false;
                        strength = 3;
                    }
                }
        );


        mediumButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        offButton.setBackground(getResources().getDrawable(buttonColor));
                        strongButton.setBackground(getResources().getDrawable(buttonColor));
                        mediumButton.setBackground(getResources().getDrawable(buttonClickedColor));
                        weakButton.setBackground(getResources().getDrawable(buttonColor));
                        offBtnClicked = false;
                        strength = 2;
                    }
                }
        );


        weakButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        offButton.setBackground(getResources().getDrawable(buttonColor));
                        strongButton.setBackground(getResources().getDrawable(buttonColor));
                        mediumButton.setBackground(getResources().getDrawable(buttonColor));
                        weakButton.setBackground(getResources().getDrawable(buttonClickedColor));
                        offBtnClicked = false;
                        strength = 1;
                    }
                }
        );
    }

    private void startProgram (int program) {
        if (!programRunning) {
            programRunning = true;
            stopProgram = false;
            startTime = System.currentTimeMillis();
            this.program = program;
            thread = new Thread(runnable);
            thread.start();
        } else {
            Toast.makeText(this,
                    "Program already running pls wait",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            switch (program) {
                case Constants.RELAX:
                    relaxProgram();
                    break;
                case Constants.ACTION:
                    actionProgram();
                    break;
                case Constants.WAKEUP:
                    wakeupProgram();
                    break;
                case Constants.CRAZY:
                    crazyProgram();
                    break;

            }
            programRunning = false;
        }
    };

    private void relaxProgram() {
        setMotors(true,true,true,true);
        while (System.currentTimeMillis() - startTime <= 10000) {
            if (stopProgram) {return;}
        }
        sendOff();
    }

    private void actionProgram() {
        while (System.currentTimeMillis() - startTime <= 10000) {
            if (stopProgram) {return;}
            if (on) {
                sendOff();
                on = false;
            } else {
                setMotors(true,true,true,true);
                on = true;
            }
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        sendOff();
    }

    private void wakeupProgram() {
        int i = 1;
        while (System.currentTimeMillis() - startTime <= 10000) {
            if (stopProgram) {return;}
            if ( i == 1) {
                setMotors(true,false,false,false);
                i++;
            } else if (i == 2) {
                setMotors(false,true,false,false);
                i++;
            } else if (i == 3) {
                setMotors(false,false,true,false);
                i++;
            } else if (i == 4) {
                setMotors(false,false,false,true);
                i = 1;
            }
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        sendOff();
    }

    private void crazyProgram() {
        if (stopProgram) {return;}
        while (System.currentTimeMillis() - startTime <= 10000) {
            if (on) {
                setMotors(true,false,true,false);
                on = false;
            } else {
                setMotors(false,true,false,true);
                on = true;
            }
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        sendOff();
    }

    private void setMotors(boolean m1, boolean m2, boolean m3, boolean m4) {
        int value = 0;
        int fs = 0x00;
        int sc = 0x00;
        int tr = 0x00;
        int fo = 0x00;
        if (strength == 1) {
            if (m1) fs = 0x77;
            if (m2) sc = 0x77;
            if (m3) tr = 0x77;
            if (m4) fo = 0x77;
        } else if (strength == 2) {
            if (m1) fs = 0xaa;
            if (m2) sc = 0xaa;
            if (m3) tr = 0xaa;
            if (m4) fo = 0xaa;
        } else if (strength == 3) {
            if (m1) fs = 0xff;
            if (m2) sc = 0xff;
            if (m3) tr = 0xff;
            if (m4) fo = 0xff;
        }
        value = (fs << 24)|(sc << 16)|(tr << 8)|fo;

        if (bleService != null && bleService.getConnectionState() == BluetoothLeService.STATE_CONNECTED) {
            BluetoothGattCharacteristic characteristic = bleService.getCharacteristic(Constants.SERVICE_NAME, Constants.CHARACTERISTIC_ID);

            if (characteristic != null) {
                characteristic.setValue(value, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
                bleService.writeCharacteristic(characteristic);
            }else {
                Log.e(Constants.TAG, "Could not write to BLE device");
            }
        } else {
            Log.e(Constants.TAG, "Could not write to BLE device");
        }
    }

    private void sendOff() {
        if (bleService != null && bleService.getConnectionState() == BluetoothLeService.STATE_CONNECTED) {
            BluetoothGattCharacteristic characteristic = bleService.getCharacteristic(Constants.SERVICE_NAME, Constants.CHARACTERISTIC_ID);
            int value = Constants.OFF;
            if (characteristic != null) {
                characteristic.setValue(value, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
                bleService.writeCharacteristic(characteristic);
            }else {
                Log.e(Constants.TAG, "Could not write to BLE device");
            }
        } else {
            Log.e(Constants.TAG, "Could not write to BLE device");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        offButton.performClick();
        bleService.cleanUp();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        offButton.performClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bleService.connect(deviceAddress);
    }
}
