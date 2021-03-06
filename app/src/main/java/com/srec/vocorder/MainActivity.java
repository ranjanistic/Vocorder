package com.srec.vocorder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import java.util.Calendar;


import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private MediaRecorder mdr;
    private static final int REQUEST_CODE_PERMISSIONS = 0;
    private String outFile;
    long timeWhenStopped = 0;
    long current;

    Animation animView, animHide, animRot, animARot, textFade;
    Boolean isOpen = false, stopFlag = true;
    private String[] permissions =  {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Chronometer chronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView subtext = findViewById(R.id.subText);
        final Chronometer timeView = findViewById(R.id.chronometerTimer);
        final ImageButton startrec = findViewById(R.id.startrecbutt);
        final ImageButton stoprec = findViewById(R.id.stopRecButt);
        final ImageView animImg = findViewById(R.id.animimage);
        current=System.currentTimeMillis();
        startrec.setEnabled(true);
        chronometer = (Chronometer) findViewById(R.id.chronometerTimer);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault());

        //change "yyyyMMdd_HHmmss as per requirement

        String currentDateandTime = sdf.format(new java.util.Date());
        String fileName = currentDateandTime;
        String ext= ".mp3";
        String name= fileName.concat(ext);
        outFile = Environment.getExternalStorageDirectory().getAbsolutePath()+name;
        animView = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        animHide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        animRot = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clk_inf);
        animARot = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_aclk_inf);
        textFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadetexton);
        startrec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(permissionsAccepted(permissions)){
                    subtext.startAnimation(textFade);
                    subtext.setText(getResources().getText(R.string.whileRecord));
                    timeView.startAnimation(animView);
                    stopFlag = false;
                    if(isOpen){
                     //   stoprec.startAnimation(animHide);
                        stoprec.startAnimation(animARot);
                        stoprec.setClickable(false);
                        isOpen = false;
                    } else {
                       // stoprec.startAnimation(animView);
                        stoprec.startAnimation(animRot);
             //           stoprec.startAnimation(animRot);
                        stoprec.setClickable(true);
                        isOpen = true;
                    }

                    Snackbar.make(view, Html.fromHtml("<font color=\"#aaaa00\">Recording started.</font>"), 5000)
                            .setAction("OKAY", null)
                            .setActionTextColor(Color.WHITE)
                            .show();
                    startVoRec();
                    startrec.setEnabled(false);


                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.dialogTheme);
                    builder.setTitle("Permissions Required")
                            .setIcon(R.drawable.ic_openmic)
                            .setMessage("\nAudio permission is required to access your device mic.\n\nStorage permission is required to save recordings on your device.")
                            .setNegativeButton(R.string.denytext, null)
                            .setPositiveButton(R.string.accepText, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ActivityCompat.requestPermissions(MainActivity.this,permissions, REQUEST_CODE_PERMISSIONS);
                                }
                            });
                    builder.create().show();
                }
            }
        });

        stoprec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopVoRec();
                subtext.startAnimation(textFade);
                subtext.setText(getResources().getText(R.string.startRecord));
                startrec.setEnabled(true);
                //stoprec.startAnimation(animHide);
                stoprec.startAnimation(animARot);
                timeView.startAnimation(animHide);
                stoprec.setClickable(false);
                timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime(); chronometer.stop();

                Snackbar.make(view, Html.fromHtml("<font color=\"#00aaaa\">Recording stopped, saved as 'AudioFile.mp3'.</font>"), 5000)
                        .setAction("OKAY", null)
                        .setActionTextColor(Color.YELLOW)
                        .show();
                isOpen = false;
                stopFlag = true;
            }
        });
    }

    private void startVoRec(){
        mdr = new MediaRecorder();
        mdr.setAudioSource(MediaRecorder.AudioSource.MIC);
        mdr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mdr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mdr.setOutputFile(outFile);
        try{
            mdr.prepare();
        } catch (IOException e){
            Log.e(TAG, "startVoRec: " + e.toString());
        }
        mdr.start();
        chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        chronometer.setBase(SystemClock.elapsedRealtime()); timeWhenStopped = 0;

        chronometer.start();

    }

    private void stopVoRec(){chronometer.stop();
        if(mdr != null){
            mdr.stop();
            mdr.release();
            mdr = null;
        }
    }

    private boolean permissionsAccepted(String[] permissions){
        for(String permit : permissions){
            if(ActivityCompat.checkSelfPermission(this, permit)!= PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}