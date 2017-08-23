package com.example.harbin.nova;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class PatientHistoryActivity extends AppCompatActivity {

    private WebView webView;



    private static SharedPreferences pres;
    private static SharedPreferences.Editor editor;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        pres = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pres.edit();


        String url = pres.getString("hospitalUrl", null);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl("https://www.myuofmhealth.org/MyChart-PRD/");
        webView.loadUrl(url);
        //https://www.myuofmhealth.org/MyChart-PRD/
        //patient history url
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_normal, menu);
//        ActionBar actionBar = getActionBar();
//        actionBar.setTitle("Check Symptoms");
//        menu.findItem(R.id.menu_logout).setEnabled(false);

//        updateMenuTitles();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
//            case R.id.menu_logout:
//                mFirebaseUser = null;
//                mFirebaseAuth.signOut();
//                mDb.execSQL("delete from " + ReminderContract.ReminderlistEntry.TABLE_NAME);
//                finish();
//                startActivity(getIntent());
//                break;
//            case R.id.menu_signIn:
//                startActivity(new Intent(this, LoginActivity.class));
//                break;
//            case R.id.menu_signUp:
//                startActivity(new Intent(this, SignupActivity.class));
//                break;
            case R.id.menu_home:
                startActivity(new Intent(this, MainActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}


