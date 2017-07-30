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
    private WebRtcClient client;
    private String mSocketAddress;
    private String doctorId;
    Handler handler;
    private ServiceListener mServiceListener;

    public interface ServiceListener{
//        void onCallReady(String callId);
//
//        void onStatusChanged(String newStatus);

        void onActivityLocalStream(MediaStream localStream);

        void onActivityAddRemoteStream(MediaStream remoteStream, int endPoint);

        void onActivityRemoveRemoteStream(int endPoint);
    }


//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
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
//
    public void setServiceListener(ServiceListener callbacks) {
        mServiceListener = callbacks;
//        startCam();
    }

    public void CallDoctor() {
//        startCam();
        try {
            answer(doctorId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        Point displaySize = new Point();
        WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //getWindowManager().getDefaultDisplay().getSize(displaySize);
        window.getDefaultDisplay().getSize(displaySize);
        PeerConnectionParameters params = new PeerConnectionParameters(
                true, false, displaySize.x, displaySize.y, 30, 1, VIDEO_CODEC_VP9, true, 1, AUDIO_CODEC_OPUS, true);

        Bundle extras = intent.getExtras();
        String uidString= extras.getString("uid");
        doctorId= extras.getString("doctorId");
        client = new WebRtcClient(this, mSocketAddress, params, VideoRendererGui.getEGLContext(), uidString);

        final SharedPreferences prefs = getSharedPreferences("NOVA_data", MODE_PRIVATE);
        String firstName=prefs.getString("firstname", "not");
        String lastName=prefs.getString("lastname", "available");
        String name=firstName+" "+lastName;
        client.SendreadyToStream(name);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        if(client != null) {
            client.onDestroy();
        }
        super.onDestroy();
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    @Override
    public void onCreate() {
    //public void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().addFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//        //setContentView(R.layout.video_main); //here0
        mSocketAddress = "http://" + getResources().getString(R.string.host);
        mSocketAddress += (":" + getResources().getString(R.string.port) + "/");

        handler = new Handler();

//        vsv = (GLSurfaceView) findViewById(R.id.glview_call);
//        vsv.setPreserveEGLContextOnPause(true);
//        vsv.setKeepScreenOn(true);
//        VideoRendererGui.setView(vsv, new Runnable() {
//            @Override
//            public void run() {
//                init();
//                // startCam();
//            }
//        });

//        // local and remote render
//        remoteRender = VideoRendererGui.create(
//                REMOTE_X, REMOTE_Y,
//                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false);
//        localRender = VideoRendererGui.create(
//                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
//                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, scalingType, true);

//        final Intent intent = getIntent();
//        final String action = intent.getAction();
//
//        if (Intent.ACTION_VIEW.equals(action)) {
//            final List<String> segments = intent.getData().getPathSegments();
//            callerId = segments.get(0);
//        }
    }

//    @Override
//    public void onCallReady(String callId) {
//        if (callerId != null) {
//            try {
//                answer(callerId);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
////        else {
//            // call(callId);
////            startCam();
////        }
//    }

    @Override
    public void TakeCall(String doctorId) {

        Intent activityIntent = new Intent(this, RtcActivity.class);//service.getBaseContext()
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityIntent.putExtra("isCalling", "no");
        startActivity(activityIntent);
    }

    public void answer(String callerId) throws JSONException {
//        client.sendMessage(callerId, "init", null);
        client.sendInvite(callerId);
        client.setIsCalling(true);
        startCam();
    }

//    public void call(String callId) {
//        Intent msg = new Intent(Intent.ACTION_SEND);
//        msg.putExtra(Intent.EXTRA_TEXT, mSocketAddress + callId);
//        msg.setType("text/plain");
//        startActivityForResult(Intent.createChooser(msg, "Call someone :"), VIDEO_CALL_SENT);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //not used anywhere
//        if (requestCode == VIDEO_CALL_SENT) {
//            startCam();
//        }
//    }

    public void startCam() {
        // Camera settings
        //client.start("fuzzy_2");

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
        mServiceListener.onActivityLocalStream(localStream);
//        localStream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));
//        VideoRendererGui.update(localRender,
//                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
//                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
//                scalingType);
    }

    @Override
    public void onAddRemoteStream(MediaStream remoteStream, int endPoint) {
        mServiceListener.onActivityAddRemoteStream(remoteStream, endPoint);
//        remoteStream.videoTracks.get(0).addRenderer(new VideoRenderer(remoteRender));
//        VideoRendererGui.update(remoteRender,
//                REMOTE_X, REMOTE_Y,
//                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType);
//        VideoRendererGui.update(localRender,
//                LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED,
//                LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED,
//                scalingType);
    }
//
    @Override
    public void onRemoveRemoteStream(int endPoint) {
        mServiceListener.onActivityRemoveRemoteStream(endPoint);
//        VideoRendererGui.update(localRender,
//                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
//                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
//                scalingType);
    }

}
