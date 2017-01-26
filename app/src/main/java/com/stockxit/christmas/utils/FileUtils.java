package com.stockxit.christmas.utils;

import android.os.Environment;
import android.text.format.DateFormat;

import java.io.File;

/**
 * Created by thomasstockx on 26/01/2017.
 */

public class FileUtils {
    public static File generateImageFile () {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/christmas_tree_"
                + DateFormat.format("yyyy-MM-dd-kk-mm-ss", System.currentTimeMillis())
                + ".png");
    }
}
