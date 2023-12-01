package Min.app.plus;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.List;

import Min.app.plus.bmob.Posts;
import Min.app.plus.bmob.Reply;
import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author daboluo
 */
public class AuditActivity extends AppCompatActivity {

   private Toolbar toolbar;
   private SmartRefreshLayout smart;
   private TextView content,time,pending_trial;
   private ImageView photo;
   private CardView pass,violations;
   private String post_id,post_author_id,reply_content, username,post_content;
   private AlertDialog dialog;
   private int select = 0,i;//单选选项
   private String[] reports = {"色情低俗", "攻击谩骂","血腥暴力","政治敏感","诈骗信息","其他行为"};
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            send_message();
            getpost();
        }};
    private AlertDialog dialog_load;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audit);


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("审核专区");
        content = findViewById(R.id.content);
        photo = findViewById(R.id.photo);
        pass = findViewById(R.id.pass);
        violations = findViewById(R.id.violations);
        pending_trial = findViewById(R.id.pending_trial);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        smart=findViewById(R.id.home_smart);
        //监听下拉和上拉状态
        //下拉刷新
        smart.setOnRefreshListener(refreshlayout -> {
            smart.setEnableRefresh(true);//启用刷新
            getpost();
            //
        });
        getpost();

        View view = LayoutInflater.from(AuditActivity.this).inflate(R.layout.dialog_loading, null, false);
        dialog_load = new AlertDialog.Builder(AuditActivity.this).setView(view).create();
        dialog_load.setCancelable(false);
    }

    public void getpost() {

        photo.setVisibility(View.VISIBLE);
        BmobQuery<Posts> bmobQuery = new BmobQuery<>();
        bmobQuery.order("-createdAt");//依照数据排序时间排序
        bmobQuery.setLimit(20);
        bmobQuery.include("author");
        bmobQuery.addWhereEqualTo("audit_state", false);//只查询状态为空的
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
        bmobQuery.findObjects(new FindListener<Posts>() {
            @Override
            public void done(List<Posts> object, BmobException e) {
                smart.finishRefresh();//结束刷新
                if (e == null) {

                    i = object.size();
                    pending_trial.setText("待审：" + i);
                    for (Posts get : object) {

                        post_author_id = get.getAuthor().getObjectId();
                        username = get.getAuthor().getUsername();
                        post_id = get.getObjectId();
                        post_content = get.getContent();
                        content.setText(post_content);
                        if (get.getPhoto() != null) {
                            Glide.with(AuditActivity.this).load(get.getPhoto().getUrl()).crossFade(100).into(photo);
                        }

                    }

                } else {
                    Snackbar.make(toolbar, "数据加载失败", Snackbar.LENGTH_SHORT).show();

                }

            }
        });

        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dialog_load.show();
                Posts p2 = new Posts();
                p2.setAudit_state(true);
                p2.setState("通过");
                p2.update(post_id, new UpdateListener() {

                    @Override
                    public void done(BmobException e) {
                        if (e == null) {

                            reply_content = "【系统提示】亲爱的" + username + "你好，你发布的" + post_content + "无违规内容，故审核通过。";
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    Message msg = new Message();
                                    handler.sendMessage(msg);
                                }
                            }.start();

                        } else {
                            dialog_load.dismiss();
                            Snackbar.make(toolbar, "操作失败", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                });

            }
        });
        violations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog_load.show();
                Posts p2 = new Posts();
                p2.setObjectId(post_id);
                p2.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        dialog_load.dismiss();
                        if (e == null) {
                            Dialog();
                            //删除成功后给作者发送一条通知
                        } else {
                            Snackbar.make(toolbar, "操作失败", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                });
                //
            }
        });

    }
    public void Dialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(AuditActivity.this);
        dialog.setTitle("请选择违规原因");
        dialog.setCancelable(false);
        dialog.setSingleChoiceItems(reports, select, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select = which;
                //System.out.println("选择: " + items[select]);
            }
        });
        dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.setNegativeButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface p1, int p2) {
                if (select != -1) {

                    dialog_load.show();
                    reply_content="【系统提示】亲爱的"+username+"你好，由于你近期发布的"+post_content+"因含有"+reports[select]+"等违规内容，故审核不通过。";
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            Message msg = new Message();
                            handler.sendMessage(msg);
                        }
                    }.start();

                }
            }
        });
        dialog.show();

    }
    public void send_message(){
        _User author = _User.getCurrentUser(_User.class);//审核员的id作为发送者
        _User recipient =new _User();//添加接收者
        recipient.setObjectId(post_author_id);//接收者id为被审帖子的作者
        Posts post = new Posts();//添加主帖
        post.setObjectId("e3f6f0886d");//主帖id
        final Reply reply = new Reply();//添加内容
        reply.setContent(reply_content);//审核成功或者审核失败的原因
        reply.setPost(post);
        reply.setAuthor(author);
        reply.setRecipient(recipient);
        reply.setType(false);//提醒
        reply.setNews(true);//是否为新
        reply.setVisible(true);//是否显示
        reply.save(new SaveListener<String>(){
            @Override
            public void done(String p1, BmobException e) {
                dialog_load.dismiss();
                if (e == null) {

                } else {
                    Snackbar.make(toolbar, "发生错误", Snackbar.LENGTH_SHORT).show();
                }
                // TODO: Implement this method
            }
        });
    }
}