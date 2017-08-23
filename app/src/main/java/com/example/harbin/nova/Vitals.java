package com.example.harbin.nova;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.harbin.nova.data.BP3L;
import com.example.harbin.nova.data.BP5;
import com.example.harbin.nova.login.LoginActivity;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;

/**
 * Activity for scan and connect available iHealth devices.
 */
public class Vitals extends Activity {
    private int callbackId;
    private Class selectedDeviceClass;
    private String selectedDeviceType;
    private ProgressDialog progressDialog;
    private final static int BLUETOOTH_ENABLED = 1;
    private final static int DEVICE = 2;
    private boolean bluetoothReady = false;
    private static final int LOGIN_REQUEST = 3;
    private Button login;


    /*
     * Example creditentials
     */
    private String userName = "liu01234345555@jiuan.com";
    private String clientId = "2a8387e3f4e94407a3a767a72dfd52ea";
    private String clientSecret = "fd5e845c47944a818bc511fb7edb0a77";
//    private String userName = "haibinzh@umich.edu";
//    private String clientId = "d8e98b5c5a2a4325ab0c0bf7a83ef172";
//    private String clientSecret = "f085bd903f184eab93deb11abd385f33";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitals);

        login = (Button) findViewById(R.id.login);

        progressDialog = new ProgressDialog(Vitals.this);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                iHealthDevicesManager.getInstance().stopDiscovery();
            }
        });

        checkBluetooth();


        iHealthDevicesManager.getInstance().init(this);
        callbackId = iHealthDevicesManager.getInstance().registerClientCallback(iHealthDevicesCallback);
        iHealthDevicesManager.getInstance().sdkUserInAuthor(Vitals.this, userName, clientId, clientSecret, callbackId);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iHealthDevicesManager.getInstance().stopDiscovery();
        iHealthDevicesManager.getInstance().unRegisterClientCallback(callbackId);
        iHealthDevicesManager.getInstance().destroy();
    }


    private iHealthDevicesCallback iHealthDevicesCallback = new iHealthDevicesCallback() {

        @Override
        public void onScanDevice(String mac, String deviceType) {
            if (selectedDeviceType.equals(deviceType)) {
                iHealthDevicesManager.getInstance().stopDiscovery();
                iHealthDevicesManager.getInstance().connectDevice(userName, mac);
            }
        }

        @Override
        public void onDeviceConnectionStateChange(String mac, String deviceType, int status) {
            if (selectedDeviceType.equals(deviceType) && status==1) {
                Intent intent = new Intent();
                intent.putExtra("mac", mac);
                intent.setClass(Vitals.this, selectedDeviceClass);
                startActivityForResult(intent, DEVICE);

            }
        }

        @Override
        public void onUserStatus(String username, int userStatus) {}

        @Override
        public void onDeviceNotify(String mac, String deviceType, String action, String message) {}

        @Override
        public void onScanFinish() {
            progressDialog.dismiss();
        }
    };


    public void discoveryBP5(View v) {
        discover(iHealthDevicesManager.TYPE_BP5, BP5.class, iHealthDevicesManager.DISCOVERY_BP5);
    }


    public void discoveryBP3l(View v) {
        discover(iHealthDevicesManager.TYPE_BP3L, BP3L.class, iHealthDevicesManager.DISCOVERY_BP3L);
    }


    private void checkBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device does not support Bluetooth
            progressDialog.setMessage("Your device does not support Bluetooth!");
            progressDialog.show();
        } else if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth is not enable
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,BLUETOOTH_ENABLED);
        } else {
            bluetoothReady = true;
        }
    }

    private void discover(String type, Class className, int discoverType) {
        if(bluetoothReady) {
            progressDialog.setMessage("Discovery...");
            progressDialog.show();
            selectedDeviceType = type;
            selectedDeviceClass = className;
            iHealthDevicesManager.getInstance().startDiscovery(discoverType);
        } else {
            Toast.makeText(this,"Bluetooth is not ready!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        switch(requestCode) {
            case BLUETOOTH_ENABLED:
                // Make sure the request was successful
                bluetoothReady = (resultCode == RESULT_OK);
                break;
            case DEVICE:
                if(resultCode==RESULT_CANCELED) {
                    Toast.makeText(this,"Connection with device lost!",Toast.LENGTH_SHORT).show();
                }
                break;
            case LOGIN_REQUEST:
                if(resultCode == RESULT_OK) {
                    setLogoutView();
                }
        }
    }

    private void setLoginView() {
        login.setText("Login");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Vitals.this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST);
            }
        });
    }

    private void setLogoutView() {
        login.setText("Logout");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoginView();
            }
        });
    }
}