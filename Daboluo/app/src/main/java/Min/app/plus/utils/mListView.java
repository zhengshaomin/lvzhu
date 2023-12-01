package Min.app.plus.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @author daboluo
 */
public class mListView extends ListView
{
    public mListView(Context context) {
        super(context);
    }
    public mListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public mListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int newHeight=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, newHeight);
    }
}
