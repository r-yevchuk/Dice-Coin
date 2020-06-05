package ua.com.dice;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.SparseIntArray;

public class Sound {
    private SoundPool soundPool;
    private SparseIntArray soundPoolMap;
    private boolean load;

    Sound(Context context){
         initSounds(context);
    }

    private void initSounds(Context context){
        load = false;
        // Create SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .build();
        } else {
            soundPool = new SoundPool(100, AudioManager.STREAM_MUSIC, 0);
        }
        soundPoolMap = new SparseIntArray();

        // Load sounds
        loadSfx(R.raw.dice1, E_SOUND.DICE1.ordinal(), context);
        loadSfx(R.raw.dice2, E_SOUND.DICE2.ordinal(), context);
        loadSfx(R.raw.coin, E_SOUND.COIN1.ordinal(), context);
        loadSfx(R.raw.coin2, E_SOUND.COIN2.ordinal(), context);
        loadSfx(R.raw.arrow, E_SOUND.ARROW1.ordinal(), context);
        loadSfx(R.raw.arrow2, E_SOUND.ARROW2.ordinal(), context);
        loadSfx(R.raw.numbers, E_SOUND.NUMBERS.ordinal(), context);


        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                load = true;
            }
        });
    }

    private void loadSfx(int raw, int id, Context context) {
        soundPoolMap.put(id, soundPool.load(context, raw, id));
    }

    // Play sound or init and play
    public void play(final E_SOUND sound, Context context) {
        if (SettingsActivity.PREFERENCES_SOUND ) {
            // Check if sound loaded, if no init again
            if (load) {
                int id = sound.ordinal();
                soundPool.play(soundPoolMap.get(id), 1, 1, 1, 0, 1f);
            } else
                initSounds(context);
        }
    }
}


