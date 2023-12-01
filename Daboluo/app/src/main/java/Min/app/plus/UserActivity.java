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
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import Min.app.plus.bmob.Chat;
import Min.app.plus.bmob.Posts;
import Min.app.plus.bmob.Report;
import Min.app.plus.bmob.Urelation;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.GlideActivity;
import Min.app.plus.utils.mListView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;

/**
 * @author daboluo
 */
public class UserActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView user_headportrait,sex;
    private TextView user_username,user_follow,user_fans,user_signature,user_boluob;
    private String user_me_id,user_he_name,user_he_head,title,ship_id,ship_id2,user_he_id,post_id;
    private int select = 0,f=0,intuser=0,follow2=0; //表示单选对话框初始时选中哪一项
    private Button follow,send;
    private int iff=0;
    List<Posts> Postlist = new ArrayList<Posts>();
    //private mListView user_list;
    private String[] reports = {"色情低俗", "攻击谩骂","血腥暴力","政治敏感","诈骗信息","其他行为"};
    private ArrayList<String> ship_list = new ArrayList<>();
    private ArrayList<String> ship_list2 = new ArrayList<>();
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            getheuser();
            follow_quantity();
            fens_quantity();
        }};
    private AlertDialog dialog;
    private LinearLayout layout_post,layout_reply,layout_signature,layout_boluob,layout_follow,layout_fans;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user2);

        Intent intent = getIntent();
        user_he_id = intent.getStringExtra("user_id");

        toolbar =findViewById(R.id.toolbar);
        follow = findViewById(R.id.follow);
        send=findViewById(R.id.send);
        user_headportrait=findViewById(R.id.user_headportrait);
        user_username=findViewById(R.id.user_username);
        user_signature=findViewById(R.id.user_signature);
        user_follow=findViewById(R.id.user_follow);
        user_fans=findViewById(R.id.user_fans);
        user_boluob=findViewById(R.id.user_boluob);
        layout_signature=findViewById(R.id.layout_signature);
        layout_reply=findViewById(R.id.layout_reply);
        layout_post=findViewById(R.id.layout_post);
        layout_boluob=findViewById(R.id.layout_boluob);
        layout_follow=findViewById(R.id.layout_follow);
        layout_fans=findViewById(R.id.layou_fans);
        //user_list=findViewById(R.id.user_list);
        toolbar.setTitle("Ta的资料");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        View view = LayoutInflater.from(UserActivity.this).inflate(R.layout.dialog_loading, null, false);
        dialog = new AlertDialog.Builder(UserActivity.this).setView(view).create();
        dialog.setCancelable(false);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                if(f==1) {//判断是否关注，关注之后才能发私信
                    getship2();
                }else {
                    dialog.dismiss();
                    Toasty.warning(UserActivity.this, "关注之后才可以发私信哦～", Toast.LENGTH_SHORT,true).show();
                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        layout_boluob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentd = new Intent(UserActivity.this, Account.class);
                startActivity(intentd);
            }
        });
        layout_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this,FollowActivity.class);
                intent.putExtra("userid",user_he_id);
                intent.putExtra("title",user_username.getText().toString()+"的关注");
                startActivity(intent);
            }
        });
        layout_fans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this,FensActivity.class);
                intent.putExtra("userid",user_he_id);
                intent.putExtra("title",user_username.getText().toString()+"的粉丝");
                startActivity(intent);
            }
        });

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                if (f==1){
                    //取消关注
                    dialog.dismiss();
                    AlertDialog.Builder a=new AlertDialog.Builder(UserActivity.this);
                    a.setIcon(R.drawable.diablosicon);//图标
                    a.setTitle("提示");//标题
                    a.setMessage("取消对ta的关注，将无法与ta发送私信！");//弹窗内容
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
                            Urelation p = new Urelation();
                            p.setObjectId(ship_id);
                            p.delete(new UpdateListener() {

                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        Toasty.warning(UserActivity.this, "已取消关注～", Toast.LENGTH_SHORT,true).show();
                                        f=0;
                                        follow.setText("关注");
                                    }else{
                                        Toasty.error(UserActivity.this, "操作失败～", Toast.LENGTH_SHORT,true).show();
                                    }
                                }

                            });
                        }
                    });
                    a.show();
                }else {
                    //关注
                    Urelation p1 = new Urelation();
                    p1.setAuthor(BmobUser.getCurrentUser(_User.class));
                    _User u=new _User();
                    u.setObjectId(user_he_id);
                    p1.setObject(u);
                    p1.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                Toasty.success(UserActivity.this, "关注成功～", Toast.LENGTH_SHORT,true).show();
                                getship();
                                f=1;
                            } else {
                                dialog.dismiss();
                                Toasty.error(UserActivity.this, "关注失败～", Toast.LENGTH_SHORT,true).show();
                            }
                        }
                    });
                }
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

        layout_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        layout_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, ReplyList.class);
                intent.putExtra("user_id",user_he_id);
                startActivity(intent);
            }
        });
        layout_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, PostList.class);
                intent.putExtra("user_id",user_he_id);
                startActivity(intent);
            }
        });
    }
    //查询对方信息
    public void getheuser() {
        BmobQuery<_User> query2 = new BmobQuery<>();
        query2.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
        query2.getObject(user_he_id, new QueryListener<_User>() {
            @Override
            public void done(_User object, BmobException e) {
                if (e == null) {
                    //帖子内容
                    getship();
                    intuser = 1;
                    user_boluob.setText(object.getBoluocoin());
                    user_he_name=object.getUsername();
                    user_he_head="https://q.qlogo.cn/headimg_dl?dst_uin=" + object.getQq() + "&spec=640&img_type=jpg";
                    Glide.with(UserActivity.this).load("https://q.qlogo.cn/headimg_dl?dst_uin=" + object.getQq() + "&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(UserActivity.this)).into(user_headportrait);
                    user_username.setText(object.getUsername());
                    user_signature.setText(object.getSignature());
                } else {
                    Toasty.error(UserActivity.this, "数据加载失败～", Toast.LENGTH_SHORT,true).show();
                }
            }
        });
    }
    public void follow_quantity(){
        //关注数量
        BmobQuery<Urelation> query = new BmobQuery<Urelation>();
        query.addWhereEqualTo("author",user_he_id);
        query.count(Urelation.class, new CountListener() {
            @Override
            public void done(Integer count, BmobException e) {
                if(e==null){
                    user_follow.setText(""+count);
                }else{
                    user_follow.setText(""+0);
                }
            }
        });

    }
    public void fens_quantity(){
        //粉丝数量
        BmobQuery<Urelation> query = new BmobQuery<Urelation>();
        query.addWhereEqualTo("object",user_he_id);
        query.count(Urelation.class, new CountListener() {
            @Override
            public void done(Integer count, BmobException e) {
                if(e==null){
                    user_fans.setText(""+count);
                }else{
                    user_fans.setText(""+0);
                }
            }
        });
    }
    public void getship() {

        BmobQuery<Urelation> eq1 = new BmobQuery<Urelation>();
        eq1.addWhereEqualTo("author", BmobUser.getCurrentUser(_User.class));//查询用户发布的贴子
//--and条件2
        BmobQuery<Urelation> eq2 = new BmobQuery<Urelation>();
        _User u = new _User();
        u.setObjectId(user_he_id);
        eq2.addWhereEqualTo("object", new BmobPointer(u));//查询用户发布的贴子
        List<BmobQuery<Urelation>> andQuerys = new ArrayList<BmobQuery<Urelation>>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);
//查询符合整个and条件的人
        BmobQuery<Urelation> query = new BmobQuery<Urelation>();
        query.and(andQuerys);
        query.findObjects(new FindListener<Urelation>() {
            @Override
            public void done(List<Urelation> object, BmobException e) {
                dialog.dismiss();
                if (e == null) {
                    if (object.size() >= 1) {
                        f = 1;
                        follow.setText("已关注");

                        for (Urelation get : object) {
                            ship_list.add(get.getObjectId());
                            ship_id = ship_list.get(0);
                        }
                    }
                } else {
                    Toasty.error(UserActivity.this, "刷新失败～", Toast.LENGTH_SHORT,true).show();
                }
            }
        });
    }public void getship2() {
        //查询是否关注我
        BmobQuery<Urelation> eq1 = new BmobQuery<Urelation>();
        eq1.addWhereEqualTo("object", BmobUser.getCurrentUser(_User.class));//查询用户发布的贴子
        BmobQuery<Urelation> eq2 = new BmobQuery<Urelation>();
        _User u = new _User();
        u.setObjectId(user_he_id);
        eq2.addWhereEqualTo("author", new BmobPointer(u));//查询用户发布的贴子
        List<BmobQuery<Urelation>> andQuerys = new ArrayList<BmobQuery<Urelation>>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);
        BmobQuery<Urelation> query = new BmobQuery<Urelation>();
        query.and(andQuerys);
        query.findObjects(new FindListener<Urelation>() {
            @Override
            public void done(List<Urelation> object, BmobException e) {
                if (e == null) {
                    if (object.size() >= 1) {
                        //关注我了
                        follow2=1;
                        for (Urelation get : object) {
                            ship_list2.add(get.getObjectId());
                            ship_id2 = ship_list2.get(0);
                            //获取她关注我的关系id
                            getchat();
                        }
                    } else {
                        dialog.dismiss();
                        //没有关注我
                        Intent intent = new Intent(UserActivity.this, ChatActivity.class);
                        intent.putExtra("relationship_id",ship_id);//二者之间的关系id
                        intent.putExtra("recipient_id", user_he_id);//接收者
                        intent.putExtra("user_he_name", user_he_name);
                        startActivity(intent);
                    }
                } else {
                    Toasty.error(UserActivity.this, "刷新失败～", Toast.LENGTH_SHORT,true).show();
                }
            }
        });
    }public void getchat(){
        BmobQuery<Chat> bmobQuery = new BmobQuery<>();
        bmobQuery.include("msg_author,msg_recipient,relationship");
        bmobQuery.addWhereEqualTo("relationship", ship_id2);//查询可见的
        bmobQuery.order("createdAt");//依照数据排序时间排序
        bmobQuery.setLimit(1);
        bmobQuery.findObjects(new FindListener<Chat>() {
            @Override
            public void done(List<Chat> object, BmobException e) {
                dialog.dismiss();
                if (e == null) {
                    if (object.size() >= 1) {
                        //他的关系id给我发过消息
                        Intent intent = new Intent(UserActivity.this, ChatActivity.class);
                        intent.putExtra("relationship_id",ship_id2);//二者之间的关系id
                        intent.putExtra("recipient_id", user_he_id);//接收者
                        intent.putExtra("user_he_name", user_he_name);
                        startActivity(intent);
                    } else {
                        //他的关系id给没有我发过消息
                        Intent intent = new Intent(UserActivity.this, ChatActivity.class);
                        intent.putExtra("relationship_id",ship_id);//二者之间的关系id
                        intent.putExtra("recipient_id", user_he_id);//接收者
                        intent.putExtra("user_he_name", user_he_name);
                        startActivity(intent);
                    }
                } else {
                    Toasty.error(UserActivity.this, "数据加载失败～", Toast.LENGTH_SHORT,true).show();
                    //home_swip.setRefreshing(false);

                }

            }
        });

    }public void send_msg(){
        Chat n = new Chat();
        n.setMsg_author(BmobUser.getCurrentUser(_User.class));//作者
        _User recipient = new _User();
        recipient.setObjectId(user_he_id);
        n.setMsg_recipient(recipient);//接收者
        Urelation r =new Urelation();
        r.setObjectId(ship_id);
        n.setRelationship(r);//绑定订单
        n.setMsg_content("关注了你～");//内容
        n.setNews(true);//表示这是一条新消息
        n.setVisible(true);
        n.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {

                if (e == null) {

                } else {

                }
            }
        });
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

                AlertDialog.Builder dialog = new AlertDialog.Builder(UserActivity.this);
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
                            _User user = new _User();
                            user.setObjectId(user_he_id);
                            final Report report = new Report();
                            report.setReport_user(user);
                            report.setAuthor(author);
                            report.setReport_content(reports[select]);
                            report.save(new SaveListener<String>() {

                                @Override
                                public void done(String p1, BmobException e) {
                                    if (e == null) {
                                        Toasty.success(UserActivity.this, "举报成功", Toast.LENGTH_SHORT,true).show();
                                    } else {
                                        Toasty.error(UserActivity.this, "举报失败", Toast.LENGTH_SHORT,true).show();
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

}