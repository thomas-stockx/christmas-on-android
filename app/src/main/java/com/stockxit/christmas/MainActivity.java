package com.stockxit.christmas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;
import com.stockxit.christmas.utils.FileUtils;
import com.stockxit.christmas.utils.IntentUtils;
import com.stockxit.christmas.views.DrawView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements ColorPickerDialogListener {
    private static final String TAG = "MainActivity";

    public DrawView mDrawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawView = (DrawView) findViewById(R.id.draw_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog.newBuilder().show((MainActivity) view.getContext());
            }
        });

        FloatingActionButton fabShare = (FloatingActionButton) findViewById(R.id.fab_share);
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImage();
            }
        });

        FloatingActionButton fabSave = (FloatingActionButton) findViewById(R.id.fab_save);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImageToDisk();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
        mDrawView.addCircle(color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        // don't do anything
    }


    /** TODO: Merge SaveImageToDisk and ShareImage methods, or at least make them share code */
    public void saveImageToDisk() {
        if (isStoragePermissionGranted() == false)
            return;

        mDrawView.setDrawingCacheEnabled(true);
        Bitmap b = mDrawView.getDrawingCache();
        FileOutputStream fos = null;

        File targetPath = FileUtils.generateImageFile();

        try {
            fos = new FileOutputStream(targetPath);

            b.compress(Bitmap.CompressFormat.PNG, 95, fos);

            IntentUtils.openImageIntent(this, targetPath);
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Something went wrong with saving the image to disk", Toast.LENGTH_SHORT);
        }


    }

    public void shareImage() {
        if (isStoragePermissionGranted() == false)
            return;

        mDrawView.setDrawingCacheEnabled(true);
        Bitmap b = mDrawView.getDrawingCache();
        FileOutputStream fos = null;

        File targetPath = FileUtils.generateImageFile();

        try {
            fos = new FileOutputStream(targetPath);

            b.compress(Bitmap.CompressFormat.PNG, 95, fos);

            IntentUtils.shareImageIntent(this, targetPath);
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Something went wrong with saving the image to disk", Toast.LENGTH_SHORT);
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
