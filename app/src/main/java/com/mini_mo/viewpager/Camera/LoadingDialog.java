package com.mini_mo.viewpager.Camera;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.mini_mo.viewpager.R;

/**
 * Created by userForGame on 2018-05-01.
 */

public class LoadingDialog {

    public AppCompatDialog progressDialog;

    /***********************************
     *  로딩화면 띄우기
     *******************************/
    public void progressON(Activity activity, String message) {

        if (activity == null || activity.isFinishing()) {
            return;
        }


        if (progressDialog != null && progressDialog.isShowing()) {
            progressSET(message);
        } else {

            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.camera_dialog);
            progressDialog.show();
        }

        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);

        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget( img_loading_frame );
        Glide.with( activity ).load(R.drawable.loading1 ).into(imageViewTarget);

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }


    }

    public void progressSET(String message) {

        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }

    }

    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
