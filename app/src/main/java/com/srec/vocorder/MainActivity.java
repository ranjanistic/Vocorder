package com.srec.vocorder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Snapshot;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.transform.OutputKeys;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private MediaRecorder mdr;
    private static final int REQUEST_CODE_PERMISSIONS = 0;
    private String outFile;
    private String[] permissions =  {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageButton startrec = findViewById(R.id.startrecbutt);
        final Button stoprec = findViewById(R.id.stopRecButt);
        stoprec.setEnabled(false);
        stoprec.setBackgroundColor(getColor(R.color.colorAccent));
        startrec.setEnabled(true);
        outFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/vocorderAudioFile.3gp";
        ActivityCompat.requestPermissions(MainActivity.this, permissions, REQUEST_CODE_PERMISSIONS);

        startrec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(permissionsAccepted(permissions)){
                    startVoRec();
                    startrec.setEnabled(false);
                    stoprec.setEnabled(true);
                    stoprec.setBackgroundColor(getColor(R.color.activeColor));
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Permissions Required")
                            .setIcon(R.drawable.ic_launcher_foreground)
                            .setMessage("Audio permission is required to access your device mic.\nStorage permission is required to save recordings on your device")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
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
                startrec.setEnabled(true);
                stoprec.setEnabled(false);
                stoprec.setBackgroundColor(getColor(R.color.colorAccent));
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
            mdr.prepare();;
        } catch (IOException e){
            Log.e(TAG, "startVoRec: " + e.toString());
        }
        mdr.start();

    }

    private void stopVoRec(){
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
