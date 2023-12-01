package Min.app.plus;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Min.app.plus.bmob.Carmi;
import Min.app.plus.bmob.FreeCarmi;
import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;

/**
 * @author daboluo
 */
public class Account extends AppCompatActivity {

    private Toolbar toolbar;
    private Button cash;
    private String user_me_id,username,carmi_id,carmi_ed;
    private int user_boluo,carmi_boluo,withdraw_boluo,alipay;
    private TextView topup,withdraw,account_boluo;
    private AlertDialog dialog2,dialog3,dialog4;
    private android.app.AlertDialog dialog_topup,dialog_withdraw;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            getuser();

        }};

    private int use=0;//未使用
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("我的账户");
        account_boluo = findViewById(R.id.account_boluo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        topup = findViewById(R.id.topup);
        withdraw = findViewById(R.id.withddraw);
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        }.start();
        topup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Account.this, TopupActivity.class);
                startActivity(intent);
            }
        });
        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Account.this, ApplyActivity.class);
                startActivity(intent);
            }
        });
    }
    public void dialogs(){
        AlertDialog.Builder a=new AlertDialog.Builder(Account.this);
        //a.setIcon(R.drawable.ic_launcher);//图标
        a.setTitle("提示");//标题
        a.setCancelable(false);
        a.setMessage("若购买失败，或未到账，请联系客服。");//弹窗内容
        a.setPositiveButton("取消", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface p1, int p2)
            {
            }
        });
        a.setNegativeButton("确认",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface p1, int p2)
            {
                Intent intent = new Intent(Account.this,WebActivity.class);
                intent.putExtra("title", "旅助充值");
                intent.putExtra("url", "https://www.fakabang.com/links/D71396B6");
                startActivity(intent);
            }
        });
        a.show();
    }public void getuser(){
            BmobQuery<_User> bq1 = new BmobQuery<_User>();
            bq1.getObject((String) BmobUser.getObjectByKey("objectId"), new QueryListener<_User>() {
                @Override
                public void done(_User object, BmobException e) {
                    if (e == null) {
                        user_me_id = object.getObjectId();
                        user_boluo = Integer.parseInt(object.getBoluocoin());
                        account_boluo.setText(user_boluo + "");
                    }else {
                        Toasty.error(Account.this, "获取用户信息失败", Toast.LENGTH_SHORT,true).show();
                    }
                }
            });






    }}
