package ua.com.dice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.preference.PreferenceManager;
import ua.com.dice.base.AbstractItem;
import ua.com.dice.base.DialogBase;

class NewYesOrNo extends AbstractItem {
    private final TextView tvYesOrNo;
    private final ColorStateList oldColor;
    private Activity activity;
    private int randomValue;

    NewYesOrNo(Activity activity) {
        super(activity);
        this.activity = activity;
        initAdditionalMenu();

        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //MATCH
        rp.addRule(RelativeLayout.CENTER_IN_PARENT);

        tvYesOrNo = new TextView(context);
        tvYesOrNo.setText(context.getString(R.string.text_yes));
        tvYesOrNo.setTextSize(160); //was 180
        tvYesOrNo.setGravity(Gravity.CENTER);
        tvYesOrNo.setOnClickListener(listener);
        oldColor = tvYesOrNo.getTextColors();

        setView(tvYesOrNo, rp);
    }

    @Override
    public void drop() {
        int vibrationDuration = 100;
        int colorChangeDelay = 250;
        // animate text if need
        if (SettingsActivity.PREFERENCES_ANIMATION) {
            Animation anim = new ScaleAnimation(
                    0f, 1f, // Start and end values for the X axis scaling
                    0f, 1f, // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
            anim.setFillAfter(true); // Needed to keep the result of the animation
            anim.setDuration(500);
            tvYesOrNo.startAnimation(anim);
            vibrationDuration = 500;
            colorChangeDelay = 500;
        }

        if (SettingsActivity.PREFERENCES_ADVANCED_YES_OR_NO) {
            randomValue = rangedRandom(0, 5);
        } else {
            randomValue = rangedRandom(0, 1);
        }

        if (randomValue != 0 && randomValue != 1) {
            tvYesOrNo.setTextSize(90);
        } else
            tvYesOrNo.setTextSize(160);

        Answers[] answers = Answers.values();
        tvYesOrNo.setText(String.format("%s", answers[randomValue]));

        MainActivity.sound.play(E_SOUND.NUMBERS, context);

        // Change TextView color with delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (randomValue == 0)
                    tvYesOrNo.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                else if (randomValue == 1)
                    tvYesOrNo.setTextColor(context.getResources().getColor(R.color.brown));
            }
        }, colorChangeDelay);

        tvYesOrNo.setTextColor(oldColor);
        vibrate(vibrationDuration);
    }

    private void initAdditionalMenu() {
        Button btAdditionalMenu = activity.findViewById(R.id.bt_range);
        btAdditionalMenu.setVisibility(View.VISIBLE);
        btAdditionalMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        // Old devices support (appCompat themes not work on 4.2 bug?)
        AlertDialog.Builder alertDialog = DialogBase.getBuilder(context);
        alertDialog.setMessage("Generator Appearance")
                .setCancelable(false)
                .setPositiveButton("Simple",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SettingsActivity.PREFERENCES_ADVANCED_YES_OR_NO = false;
                                dialog.cancel();
                            }
                        })
                .setNeutralButton("Advanced",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SettingsActivity.PREFERENCES_ADVANCED_YES_OR_NO = true;
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("advancedYesOrNo", SettingsActivity.PREFERENCES_ADVANCED_YES_OR_NO);
        editor.apply();
        alertDialog.show();
    }

    private enum Answers {
        YES,
        NO,
        MAYBE,
        LATER,
        SOON,
        NEVER,
    }
}
