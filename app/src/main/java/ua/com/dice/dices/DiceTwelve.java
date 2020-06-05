package ua.com.dice.dices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.Serializable;

import ua.com.dice.R;
import ua.com.dice.SettingsActivity;

import static ua.com.dice.dices.NewDice.dices;

public class DiceTwelve extends AbstractDice implements Serializable {
    private ImageView ivDice;
    private TextView tvDice;
    private int random, prevRandom;
    private ImageButton btDrop;
    private Activity activity;

    DiceTwelve(Activity activity) {
        super(activity);
        this.activity = activity;
        btDrop = activity.findViewById(R.id.bt_drop);

        ivDice = new ImageView(activity);
        ivDice.setImageResource(R.drawable.dice_twelve);
        ivDice.setOnClickListener(dropAllListener);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f); //MATCH
        int margin = dpToPx(SettingsActivity.DICES_TWELVE_MARGINS[dices.size()]);
        //ivParams.setMargins(margin, margin, margin, margin);
        ivDice.setLayoutParams(ivParams);
        ivDice.setPadding(margin, margin, margin, margin);
        dicesFrame.addView(ivDice);

        tvDice = new TextView(activity);
        tvDice.setGravity(Gravity.CENTER);
        tvDice.setTextSize(dpToPx(SettingsActivity.TEXT_SIZE[dices.size()]));
        tvDice.setText(String.valueOf(1));
        tvDice.setLayoutParams(ivParams);
        tvDice.setPadding(margin, margin, (margin + dpToPx(8)), margin);
        tvDice.setTextColor(Color.BLUE);
        //tvDice.setPadding(0, 0, dpToPx(8), 0);
        dicesFrame.addView(tvDice);
    }

    @Override
    public void drop() {
        NewDice.totalScores = 0;
        ivDice.setOnClickListener(null);
        btDrop.setEnabled(false);

        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();
        Handler handler = new Handler(thread.getLooper());

        // dices turns
        int k = SettingsActivity.PREFERENCES_ANIMATION ? 6 : 2;

        for (int i = 1; i < k; i++) {
            final int finalI = i;
            final int finalK = k;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final int angle = rangedRandom(-180, 180);
                    random = rangedRandom(1, 12);
                    // prevent drop same numbers
                    if (prevRandom == random)
                        random = rangedRandom(1, 12);
                    prevRandom = random;

                    activity.runOnUiThread(new Runnable() {
                        @SuppressLint("DefaultLocale")
                        public void run() {
                            ivDice.setRotation(angle);
                            tvDice.setRotation(angle);
                            if (random == 6){
                                tvDice.setText(String.format("%d.", random));
                            } else {
                                tvDice.setText(String.valueOf(random));
                            }
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
        return "DiceTwelve";
    }
}
