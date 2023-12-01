package Min.app.plus;

import static Min.app.plus.application.App.userInfo;
import static Min.app.plus.utils.DialogUtils.dialoagnoticeshow;
import static Min.app.plus.utils.DialogUtils.dialogdownloadshow;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import Min.app.plus.bmob.Notice;
import Min.app.plus.utils.Download;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author daboluo
 */
public class StartActivity extends AppCompatActivity
{
    Animation topAnim,bottomAnim;
    ImageView app_icon;
    private String title,content,promise,cancel,url;
    private boolean visible=false,type=false;
    private ProgressDialog prodialog;//进度条弹窗
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            getnotice();
        }};
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        topAnim=AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        app_icon=findViewById(R.id.app_icon);
        app_icon.setAnimation(topAnim);



        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        }.start();

        CountDownTimer timer=new CountDownTimer(3000, 10)
        {
            public void onTick(long millisUntilFinished)
            {
                //tv_time.setText(millisUntilFinished/1000+"秒");
            }
            public void onFinish()
            {
                //tv_time.setVisibility(View.GONE);
                if (visible==true){
                    dialoagnoticeshow(StartActivity.this,type,title,content,url,promise,cancel);
                }else {
                    getuser();
                }
            }
        };
        timer.start();

    }
    public void getuser() {
        //是否缓存有登陆数据,缓存时间为一年
        if (userInfo == null) {
            startActivity(new Intent(StartActivity.this, LoginActivity.class));
            StartActivity.this.finish();
        } else {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            StartActivity.this.finish();
        }

    }
public void getnotice() {
    BmobQuery<Notice> bmobQuery = new BmobQuery<Notice>();
    bmobQuery.getObject("c511a22b08", new QueryListener<Notice>() {
        @Override//ezEaJJJK
        public void done(Notice object, BmobException e) {
            if (e == null) {
                title = object.getTitle();
                content = object.getContent();
                promise = object.getPromise();
                cancel = object.getCancel();
                url = object.getUrl();
                visible=object.getVisiable();
                type=object.getType();
            }else {
                //当bmob出现错误时，调用api
                getjson();
            }
        }
    });

    }
    public void getjson(){
    new Thread(new Runnable() {
        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("https://www.fastmock.site/mock/70a15ff6f4350bbd7094641813449c37/app/api").build();
            try {
                Response response = client.newCall(request).execute();//发送请求
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    //String data=json.optString("data",null);
                    //
                    title=json.optString("title",null);
                    content=json.optString("content",null);
                    promise=json.optString("promise",null);
                    cancel=json.optString("cancel",null);
                    visible=json.optBoolean("visible",false);
                    type=json.optBoolean("type",false);
                    url=json.optString("url",null);


                    //Toast.makeText(Startup.this, visible, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }).start();

}

}