package Min.app.plus.utils;


import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author daboluo
 */
public class Download
{
    ondown dow;
    String ruest;
    int length=0,len=0;

    public static interface ondown
    {
        public void downing(int len, int oklen);
        public void downok(String ruest);
    }
    public void setondown(ondown dow)
    {
        this.dow = dow;
    }
    public void dowmFile(final String URL, final String path)
    {
        new Thread()
        {
            public void run()
            {
                String url=URL;
                try
                {
                    File ff=new File(path);
                    if (!ff.getParentFile().exists())
                    {
                        ff.getParentFile().mkdirs();
                    }
                    String rgx="(?<==)(.|\n)+?(?=&|$)";
                    Matcher su= Pattern.compile(rgx).matcher(url);
                    while (su.find())
                    {
                        String o=su.group();
                        url = url.replace(o, URLEncoder.encode(o));
                    }
                    String[] one={"{","}"};
                    for (String two:one)
                    {
                        url = url.replace(two, URLEncoder.encode(two));
                    }
                    java.net.URL UR=new URL(url);
                    URLConnection op=UR.openConnection();
                    InputStream input=op.getInputStream();
                    byte[] b=new byte[1024 * 1024];
                    int le=0;
                    length = op.getContentLength();
                    len = 0;
                    FileOutputStream hj=new FileOutputStream(path);
                    while ((le = input.read(b)) != -1)
                    {
                        len += le;
                        if (dow != null)
                        {
                            Message me=new Message();
                            me.what = 0;
                            hh.sendMessage(me);
                        }
                        hj.write(b, 0, le);
                    }
                    input.close();
                    hj.flush();
                    hj.close();
                    ruest = "下载完成";
                }
                catch (Exception e)
                {
                    ruest = e.toString();
                }
                Message me=new Message();
                me.what = 1;
                hh.sendMessage(me);
            }
        }.start();
    }
    Handler hh=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0:
                    dow.downing(length, len);
                    break;
                case 1:
                    dow.downok(ruest);
                    break;
            }
        }
    };
}



