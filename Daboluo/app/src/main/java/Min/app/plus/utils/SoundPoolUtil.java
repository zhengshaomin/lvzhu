package Min.app.plus.utils;

import static android.os.Build.VERSION_CODES.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.SoundPool;
import android.util.Log;

import Min.app.plus.R;

/**
 * 作者：daboluo on 2023/8/23 14:50
 * Email:daboluo719@gmail.com
 */
public class SoundPoolUtil {
    public static SoundPoolUtil soundPoolUtil;
    public static SoundPool soundPool;

    //单例模式
    public static SoundPoolUtil getInstance(Context context) {
        if (soundPoolUtil == null)
            soundPoolUtil = new SoundPoolUtil(context);
        return soundPoolUtil;
    }

    @SuppressLint("NewApi")//这里初始化SoundPool的方法是安卓5.0以后提供的新方式
    private SoundPoolUtil(Context context) {
//        soundPool = new SoundPool(3, AudioManager.STREAM_SYSTEM, 0);
        soundPool = new SoundPool.Builder().build();
        //加载音频文件
        soundPool.load(context, Min.app.plus.R.raw.a, 1);

    }

    public static void play(int number) {
        Log.d("tag", "number " + number);
        //播放音频
        soundPool.play(number, 1, 1, 0, 0, 1);
    }
}
