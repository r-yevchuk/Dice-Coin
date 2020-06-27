package ua.com.dice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.preference.PreferenceManager;
import ua.com.dice.base.AbstractItem;
import ua.com.dice.base.DialogBase;

import java.util.Arrays;

class NewArrow extends AbstractItem {
    private final ImageView ivArrow;
    private final Activity activity;


    NewArrow(Activity activity) {
        super(activity);
        this.activity = activity;
        initAdditionalMenu();
        // Arrow layout params
        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        int arrowMargin = dpToPx(40);
        rp.setMargins(arrowMargin, arrowMargin, arrowMargin, arrowMargin);
        rp.addRule(RelativeLayout.CENTER_IN_PARENT);

        ivArrow = new ImageView(activity);
        ivArrow.setImageResource(R.drawable.arrow);
        ivArrow.setOnClickListener(listener);
        // set View and Layout params into container
        setView(ivArrow, rp);
    }

    @Override
    public void drop() {
        int randomNumber = rangedRandom(0, SettingsActivity.PREFERENCES_ARROW_DIRECTIONS); //36 72
        int animDuration;
        int vibrationDuration;

        // Check if animation enabled
        if (SettingsActivity.PREFERENCES_ANIMATION) {
            animDuration = 500;
            vibrationDuration = 500;
            MainActivity.sound.play(E_SOUND.ARROW1, context);
        } else {
            animDuration = 0;
            vibrationDuration = 100;
            MainActivity.sound.play(E_SOUND.ARROW2, context);
        }

        int toDegrees = randomNumber * (360 / SettingsActivity.PREFERENCES_ARROW_DIRECTIONS) + 360;
        RotateAnimation anim = new RotateAnimation(0, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(animDuration);
        anim.setFillAfter(true);
        ivArrow.startAnimation(anim);

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
        final String[] arrowDirections = {"4", "8", "12"};

        AlertDialog.Builder alertDialog = DialogBase.getBuilder(context);
        alertDialog.setTitle("Number of arrow directions:")
                .setCancelable(false)
                .setSingleChoiceItems(arrowDirections,
                        Arrays.asList(arrowDirections).indexOf("" + SettingsActivity.PREFERENCES_ARROW_DIRECTIONS),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SettingsActivity.PREFERENCES_ARROW_DIRECTIONS = Integer.parseInt(arrowDirections[which]);
                    }
                })
                .setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("arrowDirections", SettingsActivity.PREFERENCES_ARROW_DIRECTIONS);
                        editor.apply();
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }
}
