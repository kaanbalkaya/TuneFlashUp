package com.kaanbalkaya.tuneflashup;

import android.hardware.Camera;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.Context;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.pm.PackageManager;




public class MainActivity extends AppCompatActivity {

    private Visualizer visualizer;
    private FlashThread th = null;
    private boolean flashFlag = false;
    private Camera camera;
    Camera.Parameters params;
    Button flashButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        visualizer = new Visualizer(0);
        visualizer.setEnabled(true);



        if (!getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            //todo: flashla ilgili kontrol mesajları düzenle.
            temizle();
            onDestroy();
            return;
        }

        flashButton = (Button) findViewById(R.id.flashButton);

        if(savedInstanceState!=null){
            th=(FlashThread) savedInstanceState.getSerializable("th");
            flashFlag=savedInstanceState.getBoolean("flashFlag");
            flashButton.setText(savedInstanceState.getCharSequence("flashButton"));
        }

    }

    public void onClick(View v) {
        if (flashFlag) {
            flashFlag = false;
            flashButton.setText("On");
            temizle();
        } else {
            flashFlag = true;
            flashButton.setText("Off");
            //wl.acquire();
            th = new FlashThread(visualizer);
            th.start();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable("th", th);
        outState.putBoolean("flashFlag", flashFlag);
        outState.putCharSequence("flashButton", flashButton.getText());

    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        temizle();
        super.onBackPressed();


    }

    /*@Override
    protected void onRestart() {
        onResume();
        super.onRestart();
    }*/


    @Override
    protected void onDestroy() {
        temizle();
        super.onDestroy();
    }

    public void temizle() {
        if (th != null) {
            th.interrupt();
        }
    }



}
