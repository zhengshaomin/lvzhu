package Min.app.plus;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author daboluo
 */
public class GoodsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String goods_id,me_id,user_id,merchants,puser_id;
    private ViewTreeObserver view;
    private EditText p_password;
    private Button t_buy,p_sure;
    private ImageView photo;
    private TextView t_price,t_title,t_content,t_time,p_price;
    private int select = 0,paypassword; //表示单选对话框初始时选中哪一项
    private double account,price;
    private String[] reports = {"色情低俗", "攻击谩骂","血腥暴力","政治敏感","诈骗信息","其他行为"};

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
//            getuser();

        }};
    private AlertDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trade);

        Bmob.initialize(this, "6d233c0993a3ab132ba5e11b0942960b");
/*
        Intent intent = getIntent();
        goods_id = intent.getStringExtra("goods_id");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("详情");
        setSupportActionBar(toolbar);
        t_price = findViewById(R.id.t_price);
        t_title=findViewById(R.id.t_title);
        t_content = findViewById(R.id.t_content);
        photo=findViewById(R.id.photo);
        t_buy = findViewById(R.id.t_buy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        t_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(GoodsActivity.this).inflate(R.layout.pay, null, false);
                p_price=view.findViewById(R.id.p_price);
                p_password=view.findViewById(R.id.p_password);
                p_sure=view.findViewById(R.id.p_sure);
                p_price.setText("支付"+price+"元");
                p_sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (p_password.getText().toString().length()<1){
                            Snackbar.make(toolbar, "请输入密码", Snackbar.LENGTH_SHORT).show();
                        }else if(Integer.parseInt(p_password.getText().toString())==paypassword){
                            //密码正确
                            if(account-price>=0){
                                double i=account-price;
                                _User p2 = new _User();
                                p2.setAccount((double)Math.round(i*100)/100);
                                p2.update(user_id, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        dialog.dismiss();
                                        if(e==null){
                                            getorder();
                                        }else{
                                            Snackbar.make(toolbar, "支付失败", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }

                                });
                            }else {
                                dialog.dismiss();
                                Snackbar.make(toolbar, "余额不足", Snackbar.LENGTH_SHORT).show();
                            }

                        }else {
                            Snackbar.make(toolbar, "密码错误", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog = new AlertDialog.Builder(GoodsActivity.this).setView(view).create();
                dialog.setCancelable(false);
                dialog.show();
            }
        });
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        }.start();
    }
    public void getuser() {
        BmobQuery<_User> bq1 = new BmobQuery<_User>();
        bq1.getObject((String) BmobUser.getObjectByKey("objectId"), new QueryListener<_User>() {
            @Override
            public void done(_User object, BmobException e) {
                if (e == null) {
                    user_id = object.getObjectId();
                   // paypassword = object.getPaypassword();
                    account = object.getAccount();
                    getGoods();
                }
            }
        });
    }
    public void getorder(){
        Order order = new Order();
        order.setState("已付款");
        Goods goods = new Goods();
        goods.setObjectId(goods_id);
        _User m=new _User();
        m.setObjectId(merchants);
        order.setGoods(goods);
        order.setMerchants(m);
        //添加一对一关联，用户关联帖子
        order.setPrice((double)Math.round(price*100)/100);
        order.setAuthor(BmobUser.getCurrentUser(_User.class));//作者
        order.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Snackbar.make(toolbar, "购买成功", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(toolbar, "出现错误", Snackbar.LENGTH_SHORT).show();
                }
            }});
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_post, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.send:

                AlertDialog.Builder dialog = new AlertDialog.Builder(GoodsActivity.this);
                dialog.setTitle("请选择举报理由");
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
                            _User author = _User.getCurrentUser(_User.class);
                            Posts post = new Posts();
                            post.setObjectId(goods_id);
                            final Report report = new Report();
                            report.setReport_post(post);
                            report.setAuthor(author);
                            report.setReport_content(reports[select]);
                            report.save(new SaveListener<String>() {

                                @Override
                                public void done(String p1, BmobException e) {
                                    if (e == null) {
                                        Snackbar.make(toolbar, "举报成功", Snackbar.LENGTH_SHORT).show();

                                    } else {
                                        Snackbar.make(toolbar, "举报失败", Snackbar.LENGTH_SHORT).show();
                                    }
                                    // TODO: Implement this method
                                }
                            });
                            //txv1.setText("你选择了："+report[select]);
                        }
                    }
                });

                dialog.show();
                break;

        }
        return super.onOptionsItemSelected(item);

    }
    public void getGoods() {
        BmobQuery<Goods> query = new BmobQuery<>();
        query.include("author");
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
        query.getObject(goods_id, new QueryListener<Goods>() {
            @Override
            public void done(Goods object, BmobException e) {
                if (e == null) {
                    //帖子内容
                    Glide.with(GoodsActivity.this).load(object.getPhoto().getUrl()).crossFade(200).into(photo);
                    merchants=object.getAuthor().getObjectId();
                    price=object.getPrice();
                    t_price.setText("￥"+object.getPrice());
                    //t_time.setText(object.getCreatedAt());
                    t_title.setText(object.getTitle());
                    t_content.setText(object.getContent());
                } else {
                    Snackbar.make(toolbar, "数据加载失败", Snackbar.LENGTH_SHORT).show();
                }
            }
        });




 */

    };};
