package Min.app.plus;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import Min.app.plus.bmob.Order;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.GlideActivity;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author daboluo
 */
public class MorderActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String order_id,order_author_id,user_me_id,post_author_id,post_content,recipient_user_id=null,order_state;
    private ViewTreeObserver view;
    private ImageView headportrait,photo;
    private TextView username,time,title,content,take_order,boluos;
    private boolean like=false,anonymous=true,type=true;
    private int boluo;
    private int select = 0,recipient_user_boluo; //表示单选对话框初始时选中哪一项
    private String[] reports = {"色情低俗", "攻击谩骂","血腥暴力","政治敏感","诈骗信息","其他行为"};

    private LinearLayout answers;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            getorder();
            getuser();//查询是否实名

        }};
    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);

        Bmob.initialize(this, "6d233c0993a3ab132ba5e11b0942960b");


        Intent intent = getIntent();
        order_id = intent.getStringExtra("order_id");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("详情");
        setSupportActionBar(toolbar);
        headportrait = findViewById(R.id.headportrait);
        username = findViewById(R.id.username);
        time = findViewById(R.id.time);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        photo = findViewById(R.id.photo);
        take_order = findViewById(R.id.take_order);

        boluos=findViewById(R.id.boluos);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
        headportrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post_author_id.equals(user_me_id)) {
                    Intent intent = new Intent(MorderActivity.this, MuserActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MorderActivity.this, UserActivity.class);
                    intent.putExtra("user_id", post_author_id);
                    startActivity(intent);
                }

            }
        });

        View view = LayoutInflater.from(MorderActivity.this).inflate(R.layout.dialog_loading, null, false);
        dialog = new AlertDialog.Builder(MorderActivity.this).setView(view).create();
        dialog.setCancelable(false);
        dialog.show();

    }public void getuser(){
        BmobQuery<_User> bq1 = new BmobQuery<_User>();
        bq1.getObject((String) BmobUser.getObjectByKey("objectId"), new QueryListener<_User>() {
            @Override
            public void done(_User object, BmobException e) {
                if (e == null) {
                    user_me_id = object.getObjectId();//
                } else {
                    // Snackbar.make(navigationview, "数据加载失败", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_morder, menu);
            return super.onCreateOptionsMenu(menu);
        }
        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
//                case R.id.edit:
//                    //
//                    break;
//                case R.id.delete:
//                    //
//                    break;
                case R.id.recipient:
                    if(recipient_user_id==null){
                        Toast.makeText(MorderActivity.this, "暂无人接单", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(MorderActivity.this, UserActivity.class);
                        intent.putExtra("user_id", recipient_user_id);
                        startActivity(intent);
                    }

                    break;
                case R.id.accomplish:

                    if(order_state.equals("进行中")){
                        //状态为进行中，才可以进行下一步操作进行确认
                        AlertDialog.Builder a=new AlertDialog.Builder(MorderActivity.this);
                        a.setIcon(R.drawable.diablosicon);//图标
                        a.setTitle("提示");//标题
                        a.setMessage("是否确认任务已完成？该操作不可逆。");//弹窗内容
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
                                updatestate();//修改状态为待确认
                            }
                        });
                        a.show();
                    }else {
                        Toast.makeText(MorderActivity.this, "操作失败～", Toast.LENGTH_SHORT).show();
                    }
                    //
                    break;
            }
            return true;
    }
    public void getorder() {
        BmobQuery<Order> query = new BmobQuery<>();
        query.include("author,recipient");
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
        query.getObject(order_id, new QueryListener<Order>() {
            @Override
            public void done(Order object, BmobException e) {
                dialog.dismiss();
                if (e == null) {

                    //帖子内容
                    if (object.getRecipient()!=null){
                        recipient_user_id=object.getRecipient().getObjectId();
                    }
                    post_author_id=object.getAuthor().getObjectId();
                    boluo = Integer.parseInt(object.getPrice());
                    boluos.setText(boluo + "");
                    username.setText(object.getAuthor().getUsername());
                    Glide.with(MorderActivity.this).load("https://q.qlogo.cn/headimg_dl?dst_uin=" + object.getAuthor().getQq() + "&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(MorderActivity.this)).into(headportrait);
                    order_author_id = object.getAuthor().getObjectId();
                    time.setText(object.getCreatedAt());
                    title.setText(object.getTitle());
                    content.setText(object.getContent());
                    post_content = object.getContent();
                    if (object.getPhoto() != null) {
                        Glide.with(MorderActivity.this).load(object.getPhoto().getUrl()).crossFade(200).into(photo);
                    }
                    order_state=object.getState();
                    take_order.setText(order_state);
                    if(object.getRecipient()!=null) {
                        recipient_user_id = object.getRecipient().getObjectId();//获取接单者用户id
                    }else {
                        recipient_user_id = null;//待接单状态
                    }

                } else {
                    Toast.makeText(MorderActivity.this, "数据加载失败～", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }public void updatestate() {
        //修改状态为待确认
        Order p2 = new Order();
        p2.setState("待确认");//修改状态
        p2.setAudit_state(true);
        p2.setPrice(boluo+"");
        p2.update(order_id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    //更改成功
                    Toast.makeText(MorderActivity.this, "操作成功～", Toast.LENGTH_SHORT).show();
                }else{
                    //失败
                    Toast.makeText(MorderActivity.this, "操作失败～", Toast.LENGTH_SHORT).show();
                }
            }

        });

//    }public void acaccomplish() {
//        //查询接单者原有菠萝数量
//        BmobQuery<_User> bmobQuery = new BmobQuery<_User>();
//        bmobQuery.getObject(recipient_user_id, new QueryListener<_User>() {
//            @Override
//            public void done(_User object, BmobException e) {
//                if (e == null) {
//                    //获取到接单者菠萝数量
//                    recipient_user_boluo=object.getBoluo();
//                    Snackbar.make(toolbar, "1当前数量："+object.getBoluo(), Snackbar.LENGTH_SHORT).show();
//                    updatestate();//更改菠萝数量
//                } else {
//                    //出现错误
//                }
//            }
//        });
//        //更改接单者菠萝数量
//    }public void updateboluo(){
//        _User p2 = new _User();
//        p2.setBoluo(1200);//数量更改
//        p2.update(recipient_user_id, new UpdateListener() {
//            @Override
//            public void done(BmobException e) {
//                if(e==null){
//                    //更改成功
//                    Snackbar.make(toolbar, "修改成功", Snackbar.LENGTH_SHORT).show();
//                }else{
//                    //失败
//                   Snackbar.make(toolbar, "出现错误"+e, Snackbar.LENGTH_SHORT).show();
//                }
//            }
//
//        });


    };};