/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jibo.base.search;

import java.io.File;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;

/**
 *
 * @author Administrator
 */
public class StatusCheckUtil {

    public static boolean isSDAvailable;
    public static int mediaUpdating = 0;
    public static boolean mediaScanning;
    public static boolean isSDCardAvailable() {
        isSDAvailable = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        return isSDAvailable;
    }
}
