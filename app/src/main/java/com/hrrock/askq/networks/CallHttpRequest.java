package com.hrrock.askq.networks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.widget.ProgressBar;

import com.dx.dxloadingbutton.lib.LoadingButton;
import com.hrrock.askq.utils.MultipartUtility;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hp-u on 11/28/2017.
 */

public class CallHttpRequest extends AsyncTask<String, Void, String> {
    private File destination;
    @SuppressLint("StaticFieldLeak")
    private Context ctx;
    @SuppressLint("StaticFieldLeak")
    private LoadingButton loginButton;
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;
    private String requestFrom;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @SuppressLint("StaticFieldLeak")
    private Activity activityRef;
    // private static final String LOGIN_ACTIVITY = "LoginActivity";
    private static final String SIGNUP_ACTIVITY = "SignupActivity";
    private static final String PROFILE_ACTIVITY = "ProfileActivity";
    private static final String EDIT_PROFILE_ACTIVITY = "EditProfileActivity";
    private static final String MOMENT_ACTIVITY = "MomentActivity";
    private static final String CREATE_DEAL_ACTIVITY = "CreateDealActivity";
    private static final String SPLASH_SCREEN_ACTIVITY = "SplashScreenActivity";
    private static final String USER_PREFERENCE = "userinfo";
    @SuppressLint("StaticFieldLeak")
    private AVLoadingIndicatorView dpUpdateProgress;
    @SuppressLint("StaticFieldLeak")
    private CircleImageView dp;
    @SuppressLint("StaticFieldLeak")
    private FloatingActionButton dpFab;

    public CallHttpRequest(Context ctx, LoadingButton loginButton, String requestFrom, Activity activityRef) {
        this.ctx = ctx;
        this.loginButton = loginButton;
        this.requestFrom = requestFrom;
        this.activityRef = activityRef;
    }

    public CallHttpRequest(Context ctx, LoadingButton loginButton, String requestFrom) {
        this.ctx = ctx;
        this.loginButton = loginButton;
        this.requestFrom = requestFrom;
    }

    public CallHttpRequest(Context ctx, String requestFrom, Activity activityRef, ProgressBar progressBar) {
        this.ctx = ctx;
        this.requestFrom = requestFrom;
        this.activityRef = activityRef;
        this.progressBar = progressBar;
    }

    public CallHttpRequest(Context ctx, File destination, String requestFrom) {
        this.ctx = ctx;
        this.destination = destination;
        this.requestFrom = requestFrom;
    }

    public CallHttpRequest(Context ctx, File destination, String requestFrom, AVLoadingIndicatorView dpUpdateProgress, CircleImageView dp, FloatingActionButton dpFab) {
        this.ctx = ctx;
        this.destination = destination;
        this.requestFrom = requestFrom;
        this.dpUpdateProgress = dpUpdateProgress;
        this.dp = dp;
        this.dpFab = dpFab;
        preferences = ctx.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(String... url) {
        String res;
        if(Objects.equals(requestFrom,SIGNUP_ACTIVITY)){
            res = fileUpload(url[0], destination);
        }else{
            res=callRequest(url[0]);
        }

        return res;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    private String callRequest(String Url) {
        try {
            URL url = new URL(Url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            DataInputStream in = new DataInputStream(con.getInputStream());

            StringBuilder output = new StringBuilder();
            String str;
            while ((str = in.readLine()) != null) {
                output.append(str);
            }
            in.close();
            return (output.toString());
        } catch (Exception e) {
        }
        return (null);
    }

    private String fileUpload(String requestURL, File uploadFile) {
        String charset = "UTF-8";
        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
            multipart.addFilePart("image", uploadFile);
            List<String> response = multipart.finish();
            System.out.println("SERVER REPLIED:");
            for (String line : response) {
                System.out.println(line);
            }
            return response.get(0);
        } catch (IOException ex) {
            return ("Fail");
        }
    }
}




