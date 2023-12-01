package Min.app.plus;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

/**
 * @author daboluo
 */
public class GlideLoading extends ImageLoader
{
    @Override
    public void displayImage(Context p1, Object p2, ImageView p3)
    {
        // TODO: Implement this method
        Glide.with(p1).load((String) p2).into(p3);
    }
}