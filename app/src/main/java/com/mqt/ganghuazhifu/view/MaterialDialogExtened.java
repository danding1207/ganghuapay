package com.mqt.ganghuazhifu.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mqt.ganghuazhifu.utils.ScreenManager;

/**
 * Created by danding1207 on 17/11/29.
 */

public class MaterialDialogExtened extends MaterialDialog {

    protected MaterialDialogExtened(BuilderExtened builder) {
        super(builder);
    }

    public static class BuilderExtened extends Builder {

        public BuilderExtened(@NonNull Activity context) {

            super(context);
        }

    }

}
