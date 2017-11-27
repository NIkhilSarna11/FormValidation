package com.example.nikhil.formvalidation;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by nikhil on 25/11/17.
 */

public class ProgressDialoge extends AppCompatActivity {
    ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void showStorageProgressDialog(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(caption);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}
