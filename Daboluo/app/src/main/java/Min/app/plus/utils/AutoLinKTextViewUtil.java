package Min.app.plus.utils;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import Min.app.plus.WebActivity;

/**
 * 作者：daboluo on 2023/9/21 22:49
 * Email:daboluo719@gmail.com
 */
//网页链接拦截
public class AutoLinKTextViewUtil {

    private volatile static AutoLinKTextViewUtil autoLinKTextViewUtil;


    private AutoLinKTextViewUtil(){};

    public static AutoLinKTextViewUtil getInstance(){
        if(autoLinKTextViewUtil == null){
            synchronized (AutoLinKTextViewUtil.class){
                if(autoLinKTextViewUtil == null){
                    autoLinKTextViewUtil = new AutoLinKTextViewUtil();
                }
            }
        }
        return autoLinKTextViewUtil;
    }

    public void interceptHyperLink(TextView textView) {
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = textView.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable spannable = (Spannable) textView.getText();
            URLSpan[] urlSpans = spannable.getSpans(0, end, URLSpan.class);
            if (urlSpans.length == 0) {
                return;
            }

            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
            // 循环遍历并拦截 所有http://开头的链接
            for (URLSpan uri : urlSpans) {
                String url = uri.getURL();
                if (url.indexOf("http://") == 0||url.indexOf("https://") == 0) {
                    CustomUrlSpan customUrlSpan = new CustomUrlSpan(textView.getContext(), url,
                            new CustomUrlSpan.OnClickInterface() {
                                @Override
                                public void onClick(View widget, String url, Context context) {//处理链接地址的点击事情
                                    if(!TextUtils.isEmpty(url)){
                                        //Toast.makeText(widget.getContext(), url, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(widget.getContext(), WebActivity.class);
                                        intent.putExtra("url", url);
                                        intent.putExtra("title","其它网页");
                                        widget.getContext().startActivity(intent);
                                    }
                                }
                            });
                    spannableStringBuilder.setSpan(customUrlSpan, spannable.getSpanStart(uri),
                            spannable.getSpanEnd(uri), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
            textView.setText(spannableStringBuilder);
        }
    }
}
