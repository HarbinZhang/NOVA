package com.example.harbin.nova;

import android.app.Activity;
import android.os.Binder;
import android.os.Handler;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONException;
import org.webrtc.MediaStream;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import java.util.List;

import webrtcclient.PeerConnectionParameters;
import webrtcclient.WebRtcClient;


public class MyService extends Service implements WebRtcClient.RtcListener{

    private final static int VIDEO_CALL_SENT = 666;
    private static final String VIDEO_CODEC_VP9 = "VP9";
    private static final String AUDIO_CODEC_OPUS = "opus";
    // Local preview screen position before call is connected.
    private static final int LOCAL_X_CONNECTING = 0;
    private static final int LOCAL_Y_CONNECTING = 0;
    private static final int LOCAL_WIDTH_CONNECTING = 100;
    private static final int LOCAL_HEIGHT_CONNECTING = 100;
    // Local preview screen position after call is connected.
    private static final int LOCAL_X_CONNECTED = 72;
    private static final int LOCAL_Y_CONNECTED = 72;
    private static final int LOCAL_WIDTH_CONNECTED = 25;
    private static final int LOCAL_HEIGHT_CONNECTED = 25;
    // Remote video screen position
    private static final int REMOTE_X = 0;
    private static final int REMOTE_Y = 0;
    private static final int REMOTE_WIDTH = 100;
    private static final int REMOTE_HEIGHT = 100;
    private VideoRendererGui.ScalingType scalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL;
    private GLSurfaceView vsv;
    private VideoRenderer.Callbacks localRender;
    private VideoRenderer.Callbacks remoteRender;
    public WebRtcClient client;
    private String mSocketAddress;
    public String doctorId, uidString;
    Handler handler;
    private ServiceListener mServiceListener;
    boolean localStreamOn=false;

    public interface ServiceListener{
        void onActivityLocalStream(MediaStream localStream);

        void onActivityAddRemoteStream(MediaStream remoteStream, int endPoint);

        void onActivityRemoveRemoteStream(int endPoint);
    }

    private final IBinder binder = new LocalBinder();
    // Class used for the client Binder.
    public class LocalBinder extends Binder {
        MyService getService() {
            // Return this instance of MyService so clients can call public methods
            return MyService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    public boolean onUnbind (Intent intent){
        this.stopSelf();
        return true;
    }


    public void setServiceListener(ServiceListener callbacks) {
        mServiceListener = callbacks;
    }

    public void CallDoctor() {
        try {
            answer(doctorId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Ready to receive call", Toast.LENGTH_LONG).show();

        Point displaySize = new Point();
        WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        window.getDefaultDisplay().getSize(displaySize);
        PeerConnectionParameters params = new PeerConnectionParameters(
                true, false, displaySize.x, displaySize.y, 30, 1, VIDEO_CODEC_VP9, true, 1, AUDIO_CODEC_OPUS, true);

        Bundle extras = intent.getExtras();
        if (extras != null) {
            uidString= extras.getString("uid");
            doctorId= extras.getString("doctorId");
        }
        client = new WebRtcClient(this, mSocketAddress, params, VideoRendererGui.getEGLContext(), uidString,doctorId);

        final SharedPreferences prefs = getSharedPreferences("NOVA_data", MODE_PRIVATE);
        String firstName=prefs.getString("firstname", "not");
        String lastName=prefs.getString("lastname", "available");
        String name=firstName+" "+lastName;
        client.SendreadyToStream(name);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        if(client != null) {
            client.onDestroy();
        }
        localStreamOn=false;
        super.onDestroy();
        System.gc();
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    @Override
    public void onCreate() {
        mSocketAddress = "http://" + getResources().getString(R.string.host);
        mSocketAddress += (":" + getResources().getString(R.string.port) + "/");

        handler = new Handler();
    }

    @Override
    public void TakeCall(String doctorId) {

        Intent activityIntent = new Intent(this, RtcActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityIntent.putExtra("isCalling", "no");
        startActivity(activityIntent);
    }

    public void answer(String callerId) throws JSONException {
        client.setIsCalling(true);
        client.sendInvite(callerId);
        startCam();
    }

    public void startCam() {
        client.start();
    }

    @Override
    public void onStatusChanged(final String newStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), newStatus, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLocalStream(MediaStream localStream) {
        while(true) {
            if(localStreamOn){
                break;
            }
            if (mServiceListener != null) {
                mServiceListener.onActivityLocalStream(localStream);
                localStreamOn=true;
                break;
            }
        }
    }

    @Override
    public void onAddRemoteStream(MediaStream remoteStream, int endPoint) {
        mServiceListener.onActivityAddRemoteStream(remoteStream, endPoint);
    }
    //
    @Override
    public void onRemoveRemoteStream(int endPoint) {
        mServiceListener.onActivityRemoveRemoteStream(endPoint);
    }

}
