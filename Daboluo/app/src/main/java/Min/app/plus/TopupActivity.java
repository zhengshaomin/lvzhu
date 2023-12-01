package Min.app.plus;

import static Min.app.plus.utils.AlipayOrederInfo.addPay;
import static Min.app.plus.utils.QueryBasisInfoUtils.getuserinfo;
import static Min.app.plus.utils.QueryBasisInfoUtils.userboluocoin;
import static Min.app.plus.utils.QueryBasisInfoUtils.usermobilePhoneNumber;
import static Min.app.plus.utils.QueryBasisInfoUtils.userobjectid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import Min.app.plus.alipay.AliPayResultStatus;
import Min.app.plus.alipay.PayResult;
import Min.app.plus.bmob.Alipay;
import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;

import com.alipay.sdk.app.PayTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 充值页面*/
public class TopupActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView boluo;
    private TextView pay,rmb;
    private TextView pay10,pay30,pay50,pay80,pay100,pay200;
    private TextView alipays;
    private LinearLayout alipay;
    private String user_me_id,type=null,name;
    private int quantity=0;
    private AlertDialog dialog2;
    private String orderInfo;

    private LinearLayout layout1,layout2,layout3,layout4,layout5,layout6;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 10001:
                    PayResult payResult = new PayResult((String) msg.obj);
                    String rs = payResult.getResultStatus();
                    String r = payResult.getResult();
                    switch (rs) {
                        case AliPayResultStatus.PAY_SUCCESS:
                            updataboluo();
                            saveorderinfo();
                            Toasty.success(TopupActivity.this, "支付成功", Toast.LENGTH_SHORT,true).show();
                            //通知接口支付成功
                            //getPresenter().alipayVerify(new VerifyBody(InfoUtils.getUserId(), rs, r, result.getExtraParam()));
                            break;
                        case AliPayResultStatus.PAY_PROCESSING:
                        case AliPayResultStatus.PAY_UNKNOWN:
                            //支付可能成功，要接口去查询,可暂时判断为成功或者支付结果未知提示用户
                            Toasty.warning(TopupActivity.this, "支付结果未知，请联系客服", Toast.LENGTH_SHORT,true).show();
                            //getPresenter().alipayVerify(new VerifyBody(InfoUtils.getUserId(), rs, r, result.getExtraParam()));
                            break;
                        default:
                            Toasty.error(TopupActivity.this, "支付失败", Toast.LENGTH_SHORT,true).show();
                            //通知接口支付失败，取消订单
                            //getPresenter().orderCancel(new CancelBody(result.getExtraParam()));
                    }
                    break;
            }
        }
    };
    private String TAG="TopupActivity";
    private String numericDate;
    private int substr =Integer.parseInt(usermobilePhoneNumber.substring(0, 5));//手机号后六位
    private int SerialNumber =substr*10000;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topup);

        initUi();//初始化布局
        initListener();//初始化事件

        if(getorderinfo()==0){
            Log.d(TAG,"未存值");
            saveorderinfo();
        }else {
            Log.d(TAG,"有值"+getorderinfo());
        }

        // 获取当前日期
        Date currentDate = new Date();
        // 创建一个格式化器，将日期格式化为纯数字
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        // 将日期格式化为纯数字字符串
        numericDate = dateFormat.format(currentDate);

    }
    //加载布局
    private void initUi() {
        toolbar = findViewById(R.id.toolbar);
        boluo = findViewById(R.id.boluo);
        pay = findViewById(R.id.pay);
        rmb = findViewById(R.id.rmb);
        pay10 = findViewById(R.id.pay10);
        pay30 = findViewById(R.id.pay30);
        pay50 = findViewById(R.id.pay50);
        pay80 = findViewById(R.id.pay80);
        pay100 = findViewById(R.id.pay100);
        pay200 = findViewById(R.id.pay200);
        alipay = findViewById(R.id.alipay);
        alipays = findViewById(R.id.alipays);
        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        layout4 = findViewById(R.id.layout4);
        layout5 = findViewById(R.id.layout5);
        layout6 = findViewById(R.id.layout6);

    }
    //初始化事件
    private void initListener(){
        toolbar.setTitle("充值");
        boluo.setText(userboluocoin+"");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = 10;
                rmb.setText(quantity+"");
                pay10.setTextColor(getResources().getColor(R.color.fen));
                pay30.setTextColor(getResources().getColor(R.color.black));
                pay50.setTextColor(getResources().getColor(R.color.black));
                pay80.setTextColor(getResources().getColor(R.color.black));
                pay100.setTextColor(getResources().getColor(R.color.black));
                pay200.setTextColor(getResources().getColor(R.color.black));

            }
        });
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = 30;
                rmb.setText(quantity+"");
                pay10.setTextColor(getResources().getColor(R.color.black));
                pay30.setTextColor(getResources().getColor(R.color.fen));
                pay50.setTextColor(getResources().getColor(R.color.black));
                pay80.setTextColor(getResources().getColor(R.color.black));
                pay100.setTextColor(getResources().getColor(R.color.black));
                pay200.setTextColor(getResources().getColor(R.color.black));
            }
        });
        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = 50;
                rmb.setText(quantity+"");
                pay10.setTextColor(getResources().getColor(R.color.black));
                pay30.setTextColor(getResources().getColor(R.color.black));
                pay50.setTextColor(getResources().getColor(R.color.fen));
                pay80.setTextColor(getResources().getColor(R.color.black));
                pay100.setTextColor(getResources().getColor(R.color.black));
                pay200.setTextColor(getResources().getColor(R.color.black));
            }
        });
        layout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = 80;
                rmb.setText(quantity+"");
                pay10.setTextColor(getResources().getColor(R.color.black));
                pay30.setTextColor(getResources().getColor(R.color.black));
                pay50.setTextColor(getResources().getColor(R.color.black));
                pay80.setTextColor(getResources().getColor(R.color.fen));
                pay100.setTextColor(getResources().getColor(R.color.black));
                pay200.setTextColor(getResources().getColor(R.color.black));
            }
        });
        layout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = 100;
                rmb.setText(quantity+"");
                pay10.setTextColor(getResources().getColor(R.color.black));
                pay30.setTextColor(getResources().getColor(R.color.black));
                pay50.setTextColor(getResources().getColor(R.color.black));
                pay80.setTextColor(getResources().getColor(R.color.black));
                pay100.setTextColor(getResources().getColor(R.color.fen));
                pay200.setTextColor(getResources().getColor(R.color.black));
            }
        });
        layout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = 200;
                rmb.setText(quantity+"");
                pay10.setTextColor(getResources().getColor(R.color.black));
                pay30.setTextColor(getResources().getColor(R.color.black));
                pay50.setTextColor(getResources().getColor(R.color.black));
                pay80.setTextColor(getResources().getColor(R.color.black));
                pay100.setTextColor(getResources().getColor(R.color.black));
                pay200.setTextColor(getResources().getColor(R.color.fen));
            }
        });
        alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "a";
                alipays.setTextColor(getResources().getColor(R.color.fen));
                //wpays.setTextColor(getResources().getColor(R.color.black));
            }
        });
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity == 0) {
                    Toasty.warning(TopupActivity.this, "请选择充值数量", Toast.LENGTH_SHORT,true).show();
                } else if (type == null) {
                    Toasty.warning(TopupActivity.this, "请选择充值方式", Toast.LENGTH_SHORT,true).show();
                } else {
//                    if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
//                        //showAlert(this, getString(R.string.error_missing_appid_rsa_private));
//                        return;
//                    }
                    getdata();

                }
            }
        });

//        View view = LayoutInflater.from(TopupActivity.this).inflate(R.layout.dialog_loading, null, false);
//        dialog2 = new AlertDialog.Builder(TopupActivity.this).setView(view).create();
//        dialog2.setCancelable(false);
//        dialog2.show();

    }
    //获取支付宝密钥
    private void getdata(){

        BmobQuery<Alipay> bmobQuery = new BmobQuery<Alipay>();
        bmobQuery.getObject("91b55d683d", new QueryListener<Alipay>() {
            @Override
            public void done(Alipay object, BmobException e) {
                if (e == null) {
                   try {
                       orderInfo = addPay(numericDate+getorderinfo(),quantity,"用户"+usermobilePhoneNumber+"充值"+quantity*100+"菠萝","UICK_MSECURITY_PA",object.getPrivateKey(),object.getAlipayPublicKey(),object.getServerUrl(),object.getSignType(),object.getAppid());
                       alipay(orderInfo);
                   }catch (Exception exception){

                   }

                }else {

                }
            }
        });
    }
    //调用支付
    private void alipay(String info){
        try {
            Runnable payRunnable = new Runnable() {

                @Override
                public void run() {
                    PayTask alipay = new PayTask(TopupActivity.this);
                    //支付宝demo是用payV2的，用pay简单点
                    //用户在商户app内部点击付款，是否需要一个loading做为在钱包唤起之前的过渡，这个值设置为true
                    String result = alipay.pay(info, true);
                    Message msg = new Message();
                    msg.what = 10001;
                    msg.obj = result;
                    handler.sendMessage(msg);
                }
            };
            // 必须异步调用
            Thread payThread = new Thread(payRunnable);
            payThread.start();
        } catch (Exception e) {
        }
    }
    //更新用户菠萝币数据
    private void updataboluo(){
        int b=userboluocoin+quantity*100;
        _User p2 = new _User();
        p2.setBoluocoin(b+"");
        p2.update(userobjectid, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    getuserinfo();//更新全部信息
                    boluo.setText(b+"");
                    Toasty.success(TopupActivity.this, "充值成功", Toast.LENGTH_SHORT,true).show();
                }else{
                    Toasty.error(TopupActivity.this, "充值失败", Toast.LENGTH_SHORT,true).show();
                }
            }

        });
    }
    //保存值
    private void saveorderinfo(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        //保存插页广告展示值
        SerialNumber = sharedPreferences.getInt("SerialNumber", 0);
        SerialNumber = SerialNumber + 1;
        editor.putInt("SerialNumber", SerialNumber);
        editor.apply();
    }
    //取值
    private int getorderinfo(){
        SharedPreferences sharedPreferences =getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("SerialNumber", 0);
    }
}
