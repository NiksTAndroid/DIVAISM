package com.prometteur.divaism.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import com.prometteur.divaism.R;


public class LoadingDialog {

    public static Dialog getLoadingDialog(Context context) {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.loader_layout);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
      //  dialog.getWindow().setBackgroundDrawable(new ColorDrawableResource(R.color.transparent));
        return dialog;
    }
}