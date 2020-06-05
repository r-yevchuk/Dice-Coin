package ua.com.dice;

import android.app.Activity;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import ua.com.dice.base.AbstractItem;

class NewCoin extends AbstractItem {
    private final ImageView ivCoin;
    private final int coinWidth;
    private final int newCoinWidth;
    private final ImageButton btDrop;
    private int randomValue;

    NewCoin(Activity activity) {
        super(activity);
        btDrop = activity.findViewById(R.id.bt_drop);
        ivCoin = new ImageView(context);
        // Container layout params
        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //MATCH
        rp.addRule(RelativeLayout.CENTER_IN_PARENT);
        // Coin image layout params
        int coinSize = dpToPx(250);
        RelativeLayout.LayoutParams coinParams = new RelativeLayout.LayoutParams(coinSize, coinSize);

        ivCoin.setLayoutParams(coinParams);
        ivCoin.setImageResource(R.drawable.coin0);
        ivCoin.setScaleType(ImageView.ScaleType.FIT_XY);
        ivCoin.setOnClickListener(listener);
        // Set View and Layout params into container
        setView(ivCoin, rp);

        coinWidth = ivCoin.getLayoutParams().width;
        newCoinWidth = dpToPx(150);
    }

    @Override
    public void drop() {
        randomValue = rangedRandom(0, 1);
        // How many turns get coin
        final int coinTurns;
        if (SettingsActivity.PREFERENCES_ANIMATION) {
            coinTurns = 11;
            MainActivity.sound.play(E_SOUND.COIN1, context);
        } else {
            coinTurns = 4;
            MainActivity.sound.play(E_SOUND.COIN2, context);
        }

        Handler handler = new Handler();
        for (int i = 1; i < coinTurns; i++) {
            final int finalI = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int imgID = context.getResources().getIdentifier("coin" + randomValue, "drawable", "ua.com.dice");
                    ivCoin.setImageResource(imgID);
                    ivCoin.getLayoutParams().width = newCoinWidth;
                    ivCoin.setOnClickListener(null);
                    btDrop.setEnabled(false);
                    ivCoin.requestLayout();
                    // change coin width, illusion of animation
                    if (finalI % 2 == 0) {
                        ivCoin.getLayoutParams().width = coinWidth;
                    }
                    // turn coin on another side
                    if (randomValue == 0)
                        randomValue = 1;
                    else randomValue = 0;
                    //coin animation end
                    if (finalI == coinTurns - 1) {
                        ivCoin.getLayoutParams().width = coinWidth;
                        ivCoin.setOnClickListener(listener);
                        btDrop.setEnabled(true);
                        ivCoin.requestLayout();
                    }
                }
            }, 100 * i);
            vibrate(100 * i);
        }
    }
}
