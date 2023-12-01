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
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
public class Mgoods extends AppCompatActivity {
    private Toolbar toolbar;
    private String goods_id,me_id,user_id,reply_userid,puser_id;
    private ViewTreeObserver view;
    private EditText p_password;
    private Button delete;
    private ImageView photo;
    private TextView t_price,t_title,t_content,t_time,p_price;
    private int select = 0,paypassword; //表示单选对话框初始时选中哪一项
    private double account,price;
    private String[] reports = {"色情低俗", "攻击谩骂","血腥暴力","政治敏感","诈骗信息","其他行为"};

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            //getuser();

        }};
    private AlertDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mgoods);

        Bmob.initialize(this, "6d233c0993a3ab132ba5e11b0942960b");

       /* Intent intent = getIntent();
        goods_id = intent.getStringExtra("goods_id");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("详情");
        setSupportActionBar(toolbar);
        t_price = findViewById(R.id.t_price);
        t_title=findViewById(R.id.t_title);
        t_content = findViewById(R.id.t_content);
        photo=findViewById(R.id.photo);
        delete = findViewById(R.id.delete);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder a = new AlertDialog.Builder(Mgoods.this);
                a.setTitle("提示");//标题
                a.setMessage("下架商品后将不会展示给用户");//弹窗内容
                a.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface p1, int p2) {
                        //取消
                    }
                });
                a.setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface p1, int p2) {

                        getdelete();
                    }
                });
                a.show();
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
                    //paypassword = object.getPaypassword();
                    account = object.getAccount();
                    getGoods();
                }
            }
        });
    }
    public void getdelete(){
        Goods goods=new Goods();
        goods.setObjectId(goods_id);
        goods.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    Snackbar.make(toolbar, "下架成功", Snackbar.LENGTH_SHORT).show();
                }else{
                    Snackbar.make(toolbar, "出现错误", Snackbar.LENGTH_SHORT).show();
                }
            }

        });

    }
    public void getGoods() {
        BmobQuery<Goods> query = new BmobQuery<>();
        query.include("author,merchants");
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
        query.getObject(goods_id, new QueryListener<Goods>() {
            @Override
            public void done(Goods object, BmobException e) {
                if (e == null) {
                    //帖子内容
                    Glide.with(Mgoods.this).load(object.getPhoto().getUrl()).crossFade(200).into(photo);
                    puser_id=object.getAuthor().getObjectId();
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




    };};*/
    }}
