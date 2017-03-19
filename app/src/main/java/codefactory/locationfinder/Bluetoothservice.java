package codefactory.locationfinder;

import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import android.os.Handler;

import static android.content.ContentValues.TAG;

/**
 * Created by Manibalan Baskaran on 19/03/2017.
 */

public class Bluetoothservice {

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice mdevice;

    public String getNewlocation() {
        return newlocation;
    }


    private String newlocation;

    public Bluetoothservice(Context context) {

        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        GetDevices();
    }


    private void GetDevices() {
        Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();

        if (deviceSet.size() > 0) {
            for (BluetoothDevice device : deviceSet) {
                if (device.getName().equals(R.string.devicename)) {
                    mdevice = device;
                }
            }
        }
        ConnectThread connectThread = new ConnectThread(mdevice);
        connectThread.start();
    }


    class ConnectThread extends Thread {
        private BluetoothSocket socket;
        private final BluetoothDevice device;

        public ConnectThread(BluetoothDevice device) throws NullPointerException {
            this.device = device;
            BluetoothSocket tmp = null;

            if(device != null) {
                try {
                    tmp = this.device.createRfcommSocketToServiceRecord(UUID.randomUUID());
                } catch (IOException exp) {
                    Log.e("Error", exp.toString());
                }

                socket = tmp;
            }
        }

        public void run() {
            try {
                socket.connect();
            } catch (IOException exp) {
                Log.e("ERROR", exp.toString());
                try {
                    socket.close();
                } catch (IOException exp1) {
                }
                return;
            }

            ConnectedThread connectedThread = new ConnectedThread(socket);
            connectedThread.start();
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException exp) {
            }
        }
    }

    class ConnectedThread extends Thread {
        private Handler mHandler;
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int len =0;
            ByteArrayOutputStream stream = new ByteArrayOutputStream(); // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    mmInStream.read(mmBuffer);
                    while ((len = mmInStream.read(mmBuffer)) != -1) {
                        stream.write(mmBuffer, 0, len);
                    }
                    // Send the obtained bytes to the UI activity.
                    newlocation = new String(stream.toByteArray(), "UTF-8");
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }
    }
}


