/*
 * Copyright (C) 2023 Rising OS Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.util.custom;

import android.app.AlertDialog;
import android.app.IActivityManager;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;

import com.android.internal.R;
import com.android.internal.statusbar.IStatusBarService;

import java.lang.ref.WeakReference;

public class SystemRebootUtils {

    private static final int REBOOT_TIMEOUT = 1000;

    public static void showSystemRebootDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.system_reboot_title)
                .setMessage(R.string.system_reboot_message)
                .setPositiveButton(R.string.reboot_title, (dialog, id) -> {
                    Handler handler = new Handler();
                    handler.postDelayed(() -> rebootSystem(context), REBOOT_TIMEOUT);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public static void rebootSystem(Context context) {
        new RebootSystemTask(context).execute();
    }

    private static class RebootSystemTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> mContext;

        RebootSystemTask(Context context) {
            mContext = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                IStatusBarService mBarService = IStatusBarService.Stub.asInterface(
                        ServiceManager.getService(Context.STATUS_BAR_SERVICE));
                if (mBarService != null) {
                    try {
                        Thread.sleep(REBOOT_TIMEOUT);
                        mBarService.reboot(false);
                    } catch (RemoteException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
