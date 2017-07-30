package com.example.harbin.nova;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;
import org.json.JSONException;
import org.webrtc.MediaStream;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import webrtcclient.WebRtcClient;
import webrtcclient.PeerConnectionParameters;

import java.util.List;

public class RtcActivity extends Activity implements MyService.ServiceListener {
    private MyService myService;
    private boolean bound = false;
    private boolean isCallingBool = false;

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
    //private WebRtcClient client;
    private String mSocketAddress;
    private String callerId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent newintent = new Intent(this, MyService.class);
        bindService(newintent, serviceConnection, Context.BIND_AUTO_CREATE);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(
                LayoutParams.FLAG_FULLSCREEN
                        | LayoutParams.FLAG_KEEP_SCREEN_ON
                        | LayoutParams.FLAG_DISMISS_KEYGUARD
                        | LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.video_main); //here0
//        mSocketAddress = "http://" + getResources().getString(R.string.host);
//        mSocketAddress += (":" + getResources().getString(R.string.port) + "/");

        vsv = (GLSurfaceView) findViewById(R.id.glview_call);
        vsv.setPreserveEGLContextOnPause(true);
        vsv.setKeepScreenOn(true);
        VideoRendererGui.setView(vsv, new Runnable() {
            @Override
            public void run() {
                init();
                // startCam();
            }
        });

        // local and remote render
        remoteRender = VideoRendererGui.create(
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false);
        localRender = VideoRendererGui.create(
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, scalingType, true);

        final Intent intent = getIntent();
//        final String action = intent.getAction();
//
//        if (Intent.ACTION_VIEW.equals(action)) {
//            final List<String> segments = intent.getData().getPathSegments();
//            callerId = segments.get(0);
//        }
        Bundle extras = intent.getExtras();
        String isCalling= extras.getString("isCalling");
        String doctorId= extras.getString("doctorId");
        if (isCalling.equals("yes")){
            isCallingBool=true;
//            myService.CallDoctor();
        }
    }

    private void init() {
//        Point displaySize = new Point();
//        getWindowManager().getDefaultDisplay().getSize(displaySize);
//        PeerConnectionParameters params = new PeerConnectionParameters(
//                true, false, displaySize.x, displaySize.y, 30, 1, VIDEO_CODEC_VP9, true, 1, AUDIO_CODEC_OPUS, true);
//
//        client = new WebRtcClient(this, mSocketAddress, params, VideoRendererGui.getEGLContext());
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // bind to Service
//        Intent intent = new Intent(this, MyService.class);
//        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//    }

    @Override
    public void onStop() {
        super.onStop();
        // Unbind from service
        if (bound) {
            myService.setServiceListener(null); // unregister
            unbindService(serviceConnection);
            bound = false;
        }
    }

    /** Callbacks for service binding, passed to bindService() */
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getService();
            bound = true;
            myService.setServiceListener(RtcActivity.this); // register
            if (isCallingBool){
                myService.CallDoctor();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        //call service then webrtc!!!
//        vsv.onPause();
//        if(client != null) {
//            client.onPause();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        vsv.onResume();
//        if(client != null) {
//            client.onResume();
//        }
    }

    @Override
    public void onDestroy() {
//        if(client != null) {
//            client.onDestroy();
//        }
        super.onDestroy();
    }

//    @Override
//    public void onCallReady(String callId) {
//        if (callerId != null) {
//            try {
//                answer(callerId);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } else {
//            // call(callId);
//            startCam();
//        }
//    }
//
//    public void answer(String callerId) throws JSONException {
//        client.sendMessage(callerId, "init", null);
//        startCam();
//    }

//    public void call(String callId) {
//        Intent msg = new Intent(Intent.ACTION_SEND);
//        msg.putExtra(Intent.EXTRA_TEXT, mSocketAddress + callId);
//        msg.setType("text/plain");
//        startActivityForResult(Intent.createChooser(msg, "Call someone :"), VIDEO_CALL_SENT);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //not used anywhere
//        if (requestCode == VIDEO_CALL_SENT) {
//            startCam();
//        }
//    }

//    public void startCam() {
//        // Camera settings
//        client.start("fuzzy_2");
//    }

//    @Override
//    public void onStatusChanged(final String newStatus) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), newStatus, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
    @Override
    public void onActivityLocalStream(MediaStream localStream) {
        localStream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                scalingType);
    }

    @Override
    public void onActivityAddRemoteStream(MediaStream remoteStream, int endPoint) {
        remoteStream.videoTracks.get(0).addRenderer(new VideoRenderer(remoteRender));
        VideoRendererGui.update(remoteRender,
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType);
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED,
                LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED,
                scalingType);
    }

    @Override
    public void onActivityRemoveRemoteStream(int endPoint) {
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                scalingType);
    }

}
