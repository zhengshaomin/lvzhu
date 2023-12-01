package Min.app.plus;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import Min.app.plus.bmob.Apply;
import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;

public class ApplyActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView boluo,apply_tv;
    private TextView pay,rmb;
    private EditText alipay_account,alipay_name;
    private TextView pay10,pay30,pay50,pay80,pay100,pay200;
    private LinearLayout alipay,wpay;
    private String user_id,name;
    private int boluob,boluo_quantity=0;
    private int select = 0; //表示单选对话框初始时选中哪一项
    private int[] quantity={1000,3000,5000,8000,10000,20000};
    private String[] reports = {1000+"菠萝", 3000+"菠萝",5000+"菠萝",8000+"菠萝",10000+"菠萝",20000+"菠萝"};
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            getuser();
            withdrawal_dialog();
        }};
    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apply);
        toolbar = findViewById(R.id.toolbar);
        boluo=findViewById(R.id.boluo);
        pay = findViewById(R.id.pay);
        rmb = findViewById(R.id.rmb);


        alipay_account=findViewById(R.id.alipay_account);
        alipay_name=findViewById(R.id.alipay_name);
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        }.start();
        toolbar.setTitle("提现");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        apply_tv=findViewById(R.id.apply_tv);

        apply_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(ApplyActivity.this);
                dialog.setTitle("请选择提现数量");
                dialog.setCancelable(false);
                dialog.setSingleChoiceItems(reports, select, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        select = which;
                        //System.out.println("选择: " + items[select]);
                    }
                });
                dialog.setPositiveButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                });
                dialog.setNegativeButton("确认",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {
                        if (select != -1)
                        {

                            boluo_quantity=quantity[select];
                            rmb.setText(boluo_quantity/100+"");
                            apply_tv.setText(reports[select]);
                            //txv1.setText("你选择了："+report[select]);
                        }
                    }
                });
                dialog.show();
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(boluo_quantity==0) {
                    Toast.makeText(ApplyActivity.this, "请选择提现数量", Toast.LENGTH_SHORT).show();
                }else if(alipay_name.getText().length()<2){
                    Toast.makeText(ApplyActivity.this, "请输入正确的姓名", Toast.LENGTH_SHORT).show();
                }else if(alipay_account.getText().toString().length()<11) {
                    Toast.makeText(ApplyActivity.this, "请输入正确的支付宝账号", Toast.LENGTH_SHORT).show();
                }else if(boluob-boluo_quantity<0){
                    Toast.makeText(ApplyActivity.this, "余额不足", Toast.LENGTH_SHORT).show();
                }else {
                    updateboluo();

                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_alipay, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.withddrawal_record:

                //提现记录表
                Intent intent4 = new Intent(ApplyActivity.this, ApplyList.class);
                startActivity(intent4);
                break;
        }
        return super.onOptionsItemSelected(item);
    }public void withdrawal_dialog(){
        AlertDialog.Builder a = new AlertDialog.Builder(ApplyActivity.this);
        a.setIcon(R.drawable.diablos);//图标
        a.setCancelable(false);//点击界面其他地方弹窗不会消失
        a.setTitle("提示");//标题
        a.setMessage("100菠萝=1元，以此换算。\n提现处理时间一般不超过24小时，若24h后仍未到账请记得联系客服。");//弹窗内容
//        a.setPositiveButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface p1, int p2) {
//                //getdownload();
//            }
//        });
        a.setNegativeButton("朕知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface p1, int p2) {
            }
        });
        a.show();
    }
    public void getuser() {
        BmobQuery<_User> bq1 = new BmobQuery<_User>();
        bq1.getObject((String) BmobUser.getObjectByKey("objectId"), new QueryListener<_User>() {
            @Override
            public void done(_User object, BmobException e) {
                if (e == null) {
                    user_id = object.getObjectId();
                    boluob = Integer.parseInt(object.getBoluocoin());
                    boluo.setText(boluob + "");
                }else {
                    Toasty.error(ApplyActivity.this, "用户信息加载失败", Toast.LENGTH_SHORT, true).show();
                }
            }
        });

    }public void updateboluo() {
        //提现更新数量
        int coin=boluob - boluo_quantity;
        _User p2 = new _User();
        p2.setBoluocoin(coin+"");
        p2.update(user_id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    boluo.setText(boluob - boluo_quantity + "");

                    addapply();
                } else {
                    Toasty.error(ApplyActivity.this, "出现错误", Toast.LENGTH_SHORT, true).show();
                }
            }

        });
    }public void addapply(){
        Apply p2 = new Apply();
        p2.setUser(BmobUser.getCurrentUser(_User.class));
        p2.setAlipayname(alipay_name.getText().toString());//姓名
        p2.setAlipay(alipay_account.getText().toString());//支付宝账号
        p2.setBoluo(boluo_quantity);
        p2.setRmb(boluo_quantity/100);
        p2.setState("审核中");
        p2.save(new SaveListener<String>() {
            @Override
            public void done(String objectId,BmobException e) {
                if(e==null){
                    Toasty.success(ApplyActivity.this, "提现成功，请等待审核", Toast.LENGTH_SHORT, true).show();
                    getuser();
                }else{
                    Toasty.error(ApplyActivity.this, "出现错误", Toast.LENGTH_SHORT, true).show();
                }
            }
        });

    }}
