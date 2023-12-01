package Min.app.plus.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 * 作者：daboluo on 2023/9/17 13:32
 * Email:daboluo719@gmail.com
 */
public class AnimationTools {
    public static void scale(View v) {
        ScaleAnimation anim = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f,

                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,

                0.5f);

        anim.setDuration(300);

        v.startAnimation(anim);

    }

}

