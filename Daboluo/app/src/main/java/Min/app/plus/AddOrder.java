package Min.app.plus;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import Min.app.plus.bmob.Order;
import Min.app.plus.bmob._User;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class AddOrder extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText title,content,boluo;
    private String ordertype=null,type=null,user_me_id;
    private AlertDialog dialog;
    private int select = 0,boluos,mboluo;//表示单选对话框初始时选中哪一项
    private String[] types = {"求助", "取快递","拿外卖","洗衣服","求购","其他"};

    private TextView pv,text_type;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            getuser();//查询是否实名

        }};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addorder);

        Bmob.initialize(this, "6d233c0993a3ab132ba5e11b0942960b");


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("发布");
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        boluo = findViewById(R.id.boluo);
        text_type = findViewById(R.id.text_type);
        pv = findViewById(R.id.pv);
        setSupportActionBar(toolbar);
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

        text_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(AddOrder.this);
                dialog.setTitle("请选择话题分类");
                dialog.setCancelable(false);
                dialog.setSingleChoiceItems(types, select, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        select = which;
                        //System.out.println("选择: " + items[select]);
                    }
                });
                dialog.setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface p1, int p2) {
                        if (select != -1) {
                            ordertype = types[select];
                            text_type.setText("话题分类：" + types[select]);
                        }
                    }
                });

                dialog.show();

            }
        });
    }public void getuser() {
        BmobQuery<_User> bq1 = new BmobQuery<_User>();
        bq1.getObject((String) BmobUser.getObjectByKey("objectId"), new QueryListener<_User>() {
            @Override
            public void done(_User object, BmobException e) {
                if (e == null) {
                    mboluo = Integer.parseInt(object.getBoluocoin());
                    user_me_id=object.getObjectId();
                } else {
                    // Snackbar.make(navigationview, "数据加载失败", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }public void updateboluo() {
        int coin=mboluo-boluos;
        _User user = new _User();
        user.setBoluocoin(coin+"");
        user.update(user_me_id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {

                    addorder();//发布

                } else {
                    Toast.makeText(AddOrder.this, "出现错误啦～", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }public void addorder(){
        View view = LayoutInflater.from(AddOrder.this).inflate(R.layout.dialog_loading, null, false);
        dialog = new AlertDialog.Builder(AddOrder.this).setView(view).create();
        dialog.setCancelable(false);
        dialog.show();

        if (ordertype == null) {
            dialog.dismiss();
            Toast.makeText(AddOrder.this, "请选择分类～", Toast.LENGTH_SHORT).show();
        } else {
            Order order = new Order();
            order.setTitle(title.getText().toString());//标题
            order.setContent(content.getText().toString());//内容
            order.setAudit_state(false);//审核状态
            order.setState("待接单");//订单状态
            //order.setType(types[select]);//分类
            order.setPrice(boluos+"");
            order.setType(type);
            //添加一对一关联，用户关联帖子
            order.setAuthor(BmobUser.getCurrentUser(_User.class));//作者
            order.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    dialog.dismiss();
                    if (e == null) {
                        Toast.makeText(AddOrder.this, "发布成功～", Toast.LENGTH_SHORT).show();
                        content.setText(null);
                    } else {
                        Toast.makeText(AddOrder.this, "发布失败～", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send:

                boluos=Integer.parseInt(boluo.getText().toString());

                if(mboluo>=boluos){
                    updateboluo();//修改自身数目
                }else {
                    Toast.makeText(AddOrder.this, "余额不足～", Toast.LENGTH_SHORT).show();
                }
                break;

        }
        return super.onOptionsItemSelected(item);

    }
}

