package com.chris.massagehat.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.UUID;

public class BluetoothLeService {
    private static final String TAG = BluetoothLeService.class.getSimpleName();

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    Boolean reading = false;

    private Context mContext;

    /**
     * Creates a new <code>BluetoothLeService</code> and initializes Bluetooth.
     * @param context current app context.
     */
    public BluetoothLeService(Context context) {
        mContext = context;

        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
        }
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionState = STATE_CONNECTED;
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            if (!(status == BluetoothGatt.GATT_SUCCESS)) {
                Log.e(TAG, "Writing characteristic failed");
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            if (!(status == BluetoothGatt.GATT_SUCCESS)) {
                Log.e(TAG, "Reading characteristic failed");
            }
            reading = false;
        }
    };

    /**
     * Connects to the given BLE device.
     * @return Status of the connection.
     */
    public boolean connect(String address) {
        if (mConnectionState == STATE_CONNECTED)
            return true;

        if (mBluetoothAdapter == null || address.equals("")) {
            Log.w(TAG, "Device not initialized");
            return false;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnect from gatt service and close connection
     */
    public void cleanUp() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        Log.i(TAG, "Disconnecting gatt server");
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();

        mConnectionState = STATE_DISCONNECTED;

        mBluetoothGatt = null;
    }


    /**
     * Get write characteristic
     *
     * @param serviceID Bluetooth service uuid string
     * @param characteristicsID Characteristics uuid string
     * @return Characteristic
     */
    public BluetoothGattCharacteristic getCharacteristic(String serviceID, String characteristicsID) {
        if (mBluetoothGatt == null
                || serviceID.isEmpty()
                || characteristicsID.isEmpty()) return null;

        BluetoothGattService service = mBluetoothGatt.
                getService(UUID.fromString(serviceID));
        if (service != null) {
            return service.getCharacteristic(UUID.fromString(characteristicsID));
        } else {
            return null;
        }

    }

    /**
     * Writes a characteristic to the gatt server. Skips if gatt not available.
     * @param characteristic the characteristic
     */
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothGatt == null || mConnectionState != STATE_CONNECTED) {
            Log.w(TAG, "Gatt not available");
            return;
        }

        if (!mBluetoothGatt.writeCharacteristic(characteristic)) {
            Log.e(TAG, "Initializing writing characteristic failed");
        } else {
            Log.i(TAG, "Writing characteristic: " + characteristic.getUuid());
            Log.i(TAG, "Data: " + Arrays.toString(characteristic.getValue()));
        }
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothGatt == null || mConnectionState != STATE_CONNECTED) {
            Log.w(TAG, "Gatt not available");
            return;
        }

        if (!mBluetoothGatt.readCharacteristic(characteristic)) {
            Log.e(TAG, "Initializing reading characteristic failed");
        } else {
            reading = true;
            Log.i(TAG, "Reading characteristic: " + characteristic.getUuid());
            Log.i(TAG, "Data: " + Arrays.toString(characteristic.getValue()));
        }
    }

    public boolean isReading() {
        return this.reading;
    }

    public int getConnectionState() {
        return mConnectionState;
    }
}


