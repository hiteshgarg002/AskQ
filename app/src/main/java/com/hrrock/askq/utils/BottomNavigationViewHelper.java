package com.hrrock.askq.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;

import com.hrrock.askq.R;
import com.hrrock.askq.activities.AnswerActivity;
import com.hrrock.askq.activities.HomeActivity;
import com.hrrock.askq.activities.NotificationsActivity;
import com.hrrock.askq.activities.ProfileActivity;
import com.hrrock.askq.activities.UiDesignActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Created by hp-u on 2/18/2018.
 */

public class BottomNavigationViewHelper {

    public static void setUpBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx) {
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.setTextVisibility(false);
      //  bottomNavigationViewEx.setItemIconTintList(null);
      //  bottomNavigationViewEx.setItemTextColor(null);
    }

    public static void enableBottomNavigationView(Context ctx, Activity activity, BottomNavigationViewEx bottomNavigationViewEx) {
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.item_home:
                  //  item.setIcon(R.drawable.ic_action_home_black);
                    if (!(activity instanceof HomeActivity)) {
                        ctx.startActivity(new Intent(ctx, HomeActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        activity.overridePendingTransition(0, 0);
                        //  Bungee.fade(ctx);
                        activity.finish();
                    }
                    break;

                case R.id.item_answer:
                    if (!(activity instanceof AnswerActivity)) {
                        ctx.startActivity(new Intent(ctx, AnswerActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        activity.overridePendingTransition(0, 0);
                        // Bungee.fade(ctx);
                        activity.finish();
                    }
                    break;

                case R.id.item_ui_design:
                    if (!(activity instanceof UiDesignActivity)) {
                        ctx.startActivity(new Intent(ctx, UiDesignActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        activity.overridePendingTransition(0, 0);
                        //   Bungee.fade(ctx);
                        activity.finish();
                    }
                    break;

                case R.id.item_notifications:
                    if (!(activity instanceof NotificationsActivity)) {
                        ctx.startActivity(new Intent(ctx, NotificationsActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        activity.overridePendingTransition(0, 0);
                        // Bungee.fade(ctx);
                        activity.finish();
                    }
                    break;

                case R.id.item_profile:
                    if (!(activity instanceof ProfileActivity)) {
                        ctx.startActivity(new Intent(ctx, ProfileActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        activity.overridePendingTransition(0, 0);
                        // Bungee.fade(ctx);
                        activity.finish();
                    }
                    break;
            }

            return true;
        });
    }
}
