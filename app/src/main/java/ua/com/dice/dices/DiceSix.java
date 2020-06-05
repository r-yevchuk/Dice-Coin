package ua.com.dice.dices;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.io.Serializable;

import ua.com.dice.R;
import ua.com.dice.SettingsActivity;


public class DiceSix extends AbstractDice implements Serializable {
    private final ImageView ivDice;
    private final Activity activity;
    private int random, prevRandom;
    private final ImageButton btDrop;

    DiceSix(Activity activity) {
        super(activity);
        this.activity = activity;
        btDrop = activity.findViewById(R.id.bt_drop);

        ivDice = new ImageView(activity);
        ivDice.setImageResource(R.drawable.dice1);
        ivDice.setOnClickListener(dropAllListener);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f); //MATCH
        int margin = dpToPx(SettingsActivity.DICES_MARGINS[NewDice.dices.size()]);
        ivDice.setLayoutParams(ivParams);
        ivDice.setPadding(margin, margin, margin, margin);

        dicesFrame.addView(ivDice);
    }

    @Override
    public void drop() {
        NewDice.totalScores = 0;
        ivDice.setOnClickListener(null);
        btDrop.setEnabled(false);

        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();
        Handler handler = new Handler(thread.getLooper());

        // Dices turns
        int k = SettingsActivity.PREFERENCES_ANIMATION ? 6 : 2;

        for (int i = 1; i < k; i++) {
                final int finalI = i;
                final int finalK = k;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final int angle = rangedRandom(-180, 180);
                            random = rangedRandom(1, 6);

                        final int imgID = context.getResources().getIdentifier("dice" + random, "drawable", "ua.com.dice");
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                ivDice.setRotation(angle);
                                ivDice.setImageResource(imgID);
                            }
                        });

                        if (finalI == finalK - 1) {
                            ivDice.setOnClickListener(dropAllListener);
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    btDrop.setEnabled(true);
                                }
                            });
                            NewDice.updateScores(activity, random);
                        }
                    }
                }, 100 * i); //120
                vibrateStatic(context,100 * i);
            }
    }

    @NonNull
    public String toString(){
        return "DiceSix";
    }
}
