package Min.app.plus;

import static Min.app.plus.utils.DialogUtils.dialogloadingdismiss;
import static Min.app.plus.utils.DialogUtils.dialogloadingshow;
import static Min.app.plus.utils.QueryBasisInfoUtils.getstoreinfo;
import static Min.app.plus.utils.QueryBasisInfoUtils.getuserinfo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import Min.app.plus.bmob._User;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import es.dmoral.toasty.Toasty;

/**
 * @author daboluo
 */
public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText telephone,smscode;
    private TextView sendcode;
    private Button qq,login;
    private AlertDialog dialog,dialog2;
    private int times=0;
    private CheckBox checkbox;
    private TextView instructions;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("登录");
        telephone = findViewById(R.id.telephone);
        smscode = findViewById(R.id.smscode);
        sendcode = findViewById(R.id.sendcode);
        login = findViewById(R.id.login);

        checkbox=findViewById(R.id.checkbox);
        instructions=findViewById(R.id.instructions);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        agreement("用户协议",getString(R.string.UserAgreement));
        instructions.setText(getClickableSpan());
        //设置超链接可点击
        instructions.setMovementMethod(LinkMovementMethod.getInstance());

        sendcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (times == 0) {

                    if(checkbox.isChecked()==false) {
                        //抖一抖
                        Toasty.warning(LoginActivity.this, "请勾选同意用户协议", Toast.LENGTH_SHORT, true).show();
                    }else if (telephone.getText().toString().length() != 11) {
                        //手机号码格式错误
                        Toasty.warning(LoginActivity.this, "请输入正确手机号", Toast.LENGTH_SHORT, true).show();
                    } else {
                        BmobSMS.requestSMSCode(telephone.getText().toString(), "1", new QueryListener<Integer>() {
                            @Override
                            public void done(Integer smsId, BmobException e) {
                                if (e == null) {
                                    time();
                                    Toasty.success(LoginActivity.this, "验证码发送成功", Toast.LENGTH_SHORT, true).show();
                                    //Snackbar.make(toolbar, "短信验证码发送成功", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Toasty.error(LoginActivity.this, "验证码发送失败", Toast.LENGTH_SHORT, true).show();
                                }
                            }
                        });

                    }

                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (smscode.getText().toString().length() >= 6) {

                    dialogloadingshow(LoginActivity.this);

                    _User user = new _User();
                    //设置手机号码（必填）
                    user.setMobilePhoneNumber(telephone.getText().toString());
                    //设置用户名，如果没有传用户名，则默认为手机号码
                    user.setUsername(telephone.getText().toString());
                    //设置用户密码
                    user.setBoluocoin("0");
                    user.setSignature("因为个性所以没有签名");
                    user.setPassword("123456");
                    //设置额外信息：此处为年龄
                    user.signOrLogin(smscode.getText().toString(), new SaveListener<_User>() {

                        @Override
                        public void done(_User user,BmobException e) {
                            if (e == null) {
                                getuserinfo();//查询登陆信息
                                getstoreinfo();//查询店铺信息
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                LoginActivity.this.finish();
                            } else {
                                Toasty.error(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    });

                }

            }
        });
requestMyPermissions();
    }

    private void requestMyPermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
           // Log.d(TAG, "requestMyPermissions: 有写SD权限");
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
           // Log.d(TAG, "requestMyPermissions: 有读SD权限");
        }
    }
    public void time(){
        times=1;
        CountDownTimer timer=new CountDownTimer(60000, 10)//1000为1秒
        {
            public void onTick(long millisUntilFinished)
            {
                sendcode.setText(millisUntilFinished/1000+"s后重新发送");//倒计时过程
            }
            public void onFinish()
            {
                //时间完成后的事件
                times=0;
                sendcode.setText("发送验证码");
            }
        };
        timer.start();

    }
    private void agreement(String title,String content) {

        AlertDialog.Builder a = new AlertDialog.Builder(LoginActivity.this);
        a.setIcon(R.drawable.diablos);//图标
        a.setCancelable(false);//点击界面其他地方弹窗不会消失
        a.setTitle(title);//标题
        a.setMessage(content);//弹窗内容
        a.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface p1, int p2) {
                checkbox.setChecked(false);
            }
        });
        a.setPositiveButton("我已知晓并同意该协议", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkbox.setChecked(true);
            }
        });
        a.show();


    }
    /**
     * 获取可点击的SpannableString
     * @return
     */
    private SpannableString getClickableSpan() {
        SpannableString spannableString = new SpannableString("我已阅读并同意用户协议、隐私政策。");
        //设置下划线文字
        spannableString.setSpan(new UnderlineSpan(), 7, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                agreement("用户协议",getString(R.string.UserAgreement));
            }
        }, 7, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的前景色
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.fen)), 7, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //设置下划线文字
        spannableString.setSpan(new UnderlineSpan(), 12, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                agreement("隐私政策",getString(R.string.PrivacyPolicy));
            }
        }, 12, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的前景色
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.fen)), 12, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
