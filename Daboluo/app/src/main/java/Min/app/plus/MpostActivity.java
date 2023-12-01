package Min.app.plus;

import static Min.app.plus.utils.DialogUtils.dialog_photo;
import static Min.app.plus.utils.DialogUtils.dialogloadingdismiss;
import static Min.app.plus.utils.DialogUtils.dialogloadingshow;
import static Min.app.plus.utils.QueryBasisInfoUtils.sendlike;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import Min.app.plus.bmob.Posts;
import Min.app.plus.bmob.Reply;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.AnimationTools;
import Min.app.plus.utils.AutoLinKTextViewUtil;
import Min.app.plus.utils.GlideActivity;
import Min.app.plus.utils.mListView;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;

/**
 * @author daboluo
 */
public class MpostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String post_id,user_me_id,reply_author_id,post_content,post_author_id;//帖子id，我的id，评论者的id
    private ViewTreeObserver view;
    private mListView reply_list;
    private EditText reply_content;
    private Button reply_send;
    private ImageView headportrait,photo,like,reply,share;
    private TextView username,time,content,reply_text,answer,cancel,likevalue;
    List<Reply> mPostlist = new ArrayList<Reply>();
    private String CONTENT,ID,PHOTOURL;
    private Boolean VISIBLE,ANONYMOUS,type=true;//type为ture接收者是帖子作者，反之是指定用户
    private LinearLayout answers;
    private int replys,views;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            getreply();
            getuser();
            getpost();
        }};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mpost);

        Intent intent = getIntent();
        post_id = intent.getStringExtra("post_id");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("详情");
        setSupportActionBar(toolbar);
        headportrait = findViewById(R.id.headportrait);
        username = findViewById(R.id.username);
        time = findViewById(R.id.time);
        photo=findViewById(R.id.photo);
        content = findViewById(R.id.content);
        reply_list = findViewById(R.id.reply_list);
        reply_send = findViewById(R.id.reply_send);
        reply_content = findViewById(R.id.reply_content);

        like=findViewById(R.id.like);
        reply=findViewById(R.id.reply);
        share=findViewById(R.id.share);
        likevalue=findViewById(R.id.likevalue);
        answers=findViewById(R.id.answers);
        answer=findViewById(R.id.answer);
        cancel=findViewById(R.id.cancel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        headportrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MpostActivity.this, MuserActivity.class);
                startActivity(intent);
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_photo(MpostActivity.this,PHOTOURL);
            }
        });
        dialogloadingshow(MpostActivity.this);

        reply_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reply_content.getText().toString().length()<1) {
                    Toasty.warning(MpostActivity.this, "内容不能为空", Toast.LENGTH_SHORT,true).show();

                }else if(type==true){
                    dialogloadingshow(MpostActivity.this);
                    //评论
                    _User author = _User.getCurrentUser(_User.class);
                    _User postauthor =new _User();
                    postauthor.setObjectId(post_author_id);
                    Posts post = new Posts();
                    post.setObjectId(post_id);
                    final Reply reply = new Reply();
                    reply.setContent(reply_content.getText().toString());
                    reply.setPost(post);
                    reply.setAuthor(author);
                    reply.setRecipient(postauthor);
                    reply.setType(false);//提醒
                    reply.setNews(true);
                    reply.setVisible(true);
                    reply.save(new SaveListener<String>() {

                        @Override
                        public void done(String p1, BmobException e) {
                            dialogloadingdismiss();//关闭加载弹窗
                            if (e == null) {
                                Toasty.success(MpostActivity.this, "评论成功", Toast.LENGTH_SHORT,true).show();
                                getreply();
                                reply_content.setText(null);
                            } else {
                                Toasty.error(MpostActivity.this, "评论失败", Toast.LENGTH_SHORT,true).show();
                            }
                            // TODO: Implement this method
                        }
                    });
                }else{
                    dialogloadingshow(MpostActivity.this);
                    _User author = _User.getCurrentUser(_User.class);
                    _User recipient =new _User();
                    recipient.setObjectId(reply_author_id);
                    Posts post = new Posts();
                    post.setObjectId(post_id);
                    final Reply reply = new Reply();
                    reply.setContent("回复"+reply_content.getText().toString());
                    reply.setPost(post);
                    reply.setAuthor(author);
                    reply.setRecipient(recipient);
                    reply.setType(false);//提醒
                    reply.setNews(true);
                    reply.setVisible(true);
                    reply.save(new SaveListener<String>(){
                        @Override
                        public void done(String p1, BmobException e) {
                            answers.setVisibility(View.GONE);
                            type=true;
                            dialogloadingdismiss();//关闭加载弹窗
                            if (e == null) {
                                Toasty.success(MpostActivity.this, "回复成功", Toast.LENGTH_SHORT,true).show();
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        Message msg = new Message();
                                        handler.sendMessage(msg);
                                    }
                                }.start();
                                reply_content.setText(null);

                            } else {
                                Toasty.error(MpostActivity.this, "回复失败", Toast.LENGTH_SHORT,true).show();
                            }
                            // TODO: Implement this method
                        }
                    });
                    //回复
                }
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Posts post=new Posts();
                post.setObjectId(post_id);
                AnimationTools.scale(like);
                int count=Integer.parseInt(likevalue.getText().toString());
                // 查询喜欢这个帖子的所有用户，因此查询的是用户表
                BmobQuery<_User> query = new BmobQuery<>();
                _User user = BmobUser.getCurrentUser(_User.class);
                //likes是Post表中的字段，用来存储所有喜欢该帖子的用户
                query.addWhereRelatedTo("likes", new BmobPointer(post));
                query.findObjects(new FindListener<>() {

                    @Override
                    public void done(List<_User> object,BmobException e) {
                        if(e==null){
                            //cancel
                            if (isLike(object,user)){
                                like.setImageResource(R.drawable.dianzan);
                                BmobRelation relation = new BmobRelation();
                                relation.remove(user);
                                post.setLikes(relation);
                                post.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                            likevalue.setText(count-1+"");
                                            like.setImageResource(R.drawable.dianzan);
                                            Log.i("bmob","关联关系删除成功");
                                        }else{
                                            Log.i("bmob","失败："+e.getMessage());
                                        }
                                    }

                                });
                            } else {
                                like.setImageResource(R.drawable.dianzan_1);
                                //将用户B添加到Post表中的likes字段值中，表明用户B喜欢该帖子
                                BmobRelation relation = new BmobRelation();
                                relation.add(user);
                                //多对多关联指向`post`的`likes`字段
                                post.setLikes(relation);
                                post.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                            sendlike(post_author_id,post_id);//发送点赞信息
                                            likevalue.setText(count+1+"");
                                            Log.i("bmob","用户B和该帖子关联成功");
                                        }else{
                                            Log.i("bmob","失败："+e.getMessage());
                                        }
                                    }

                                });
                            }



                            Log.i("bmob","查询个数："+object.size());
                        }else{
                            Log.i("bmob","失败："+e.getMessage());
                        }
                    }

                });
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answers.setVisibility(View.GONE);
                type=true;
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
        reply_list.setOnScrollListener(new AbsListView.OnScrollListener()
        {

            @Override
            public void onScrollStateChanged(AbsListView p1, int p2)
            {
                // TODO: Implement this method
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                boolean enable = false;
                if (reply_list != null &&reply_list.getChildCount() > 0)
                {
                    // 检查listView第一个item是否可见
                    boolean firstItemVisible = reply_list.getFirstVisiblePosition() == 0;
                    // 检查第一个item的顶部是否可见
                    boolean topOfFirstItemVisible = reply_list.getChildAt(0).getTop() == 0;
                    // 启用或者禁用SwipeRefreshLayout刷新标识
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                else if (reply_list != null && reply_list.getChildCount() == 0)
                {
                    // 没有数据的时候允许刷新
                    enable = true;
                }
                // 把标识传给swipeRefreshLayout
                //sw.setEnabled(enable);

            }
        });


    }
    //查询点赞
    private void getlike() {
        Posts post = new Posts();
        post.setObjectId(post_id);
        _User user = BmobUser.getCurrentUser(_User.class);
        // 查询喜欢这个帖子的所有用户，因此查询的是用户表
        BmobQuery<_User> query = new BmobQuery<>();
        query.addWhereRelatedTo("likes", new BmobPointer(post));
        query.findObjects(new FindListener<_User>() {

            @Override
            public void done(List<_User> object, BmobException e) {
                if (e == null) {
                    if (isLike(object, user)) {
                        like.setImageResource(R.drawable.dianzan_1);
                    } else like.setImageResource(R.drawable.dianzan);

                    likevalue.setText(object.size() + "");
                    Log.i("bmob", "查询个数：" + object.size());
                } else {
                    Log.i("bmob", "失败：" + e.getMessage());
                }
            }

        });
    }
    private boolean isLike(List<_User> object,_User user){
        boolean islike = false;
        for (int i = 0; i < object.size(); i++) {
            if (object.get(i).getObjectId().equals(user.getObjectId())){
                return true;
            }
        }
        return islike;
    }

    public void getpost() {
        BmobQuery<Posts> query = new BmobQuery<>();
        query.include("author");
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
        query.getObject(post_id, new QueryListener<Posts>() {
            @Override
            public void done(Posts object, BmobException e) {
                if (e == null) {
                    //帖子内容
                    getlike();
                    ID=object.getObjectId();
                    CONTENT=object.getContent();
                    VISIBLE=object.getVisible();
                    ANONYMOUS=object.getAnonymous();
                    if(object.getPhoto()!=null){
                        Glide.with(MpostActivity.this).load(object.getPhoto().getUrl()).crossFade(200).into(photo);
                        PHOTOURL=object.getPhoto().getUrl();
                    }
                    if(object.getAnonymous()==true){
                        username.setText("匿名");
                        Glide.with(MpostActivity.this).load("https://z3.ax1x.com/2021/11/20/ILiyNR.jpg").crossFade(800).transform(new GlideActivity(MpostActivity.this)).into(headportrait);
                    }else {
                        username.setText(object.getAuthor().getUsername());
                        Glide.with(MpostActivity.this).load("https://q.qlogo.cn/headimg_dl?dst_uin="+object.getAuthor().getQq()+"&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(MpostActivity .this)).into(headportrait);
                    }
//                    views=object.getView();
                    post_author_id=object.getAuthor().getObjectId();
                    time.setText(object.getCreatedAt());
                    post_content=object.getContent();
                    content.setText(object.getContent());
                    AutoLinKTextViewUtil.getInstance().interceptHyperLink(content);//拦截链接地址，设置显示样式和点击事件
//                    replyvalue.setText(""+object.getReplys());
//                    viewvalue.setText(""+object.getView());

                } else {
                    Toasty.error(MpostActivity.this, "数据加载失败", Toast.LENGTH_SHORT,true).show();
                }
            }
        });

    }
    public void getreply(){
        BmobQuery<Reply> eq1 = new BmobQuery<Reply>();
        Posts post = new Posts();
        post.setObjectId(post_id);
        eq1.addWhereEqualTo("post", new BmobPointer(post));
        BmobQuery<Reply> eq2 = new BmobQuery<Reply>();
        eq2.addWhereEqualTo("type",false);//查询可见的
        List<BmobQuery<Reply>> andQuerys = new ArrayList<BmobQuery<Reply>>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);

        BmobQuery<Reply> query = new BmobQuery<Reply>();
        query.and(andQuerys);
//希望同时查询该评论的发布者的信息，以及该帖子的作者的信息，这里用到上面`include`的并列对象查询和内嵌对象的查询
        query.include("author,post.author,postauthor,recipient");
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
        //query2.order("-createdAt");//依照数据排序时间排序
        query.findObjects(new FindListener<Reply>() {
            @Override
            public void done(List<Reply> object, BmobException e) {
                dialogloadingdismiss();//关闭加载弹窗
                // TODO: Implement this method
                if (e == null) {
                    mPostlist.clear();
                    for (int i = 0; i < object.size(); i++) {
                        mPostlist.add(object.get(i));
                        int x = i + 1;
                    }
                    reply_list.setAdapter(new MpostActivity.ItemListAdapter());
                    getview();
                } else {
                    Toasty.error(MpostActivity.this, "数据加载失败", Toast.LENGTH_SHORT,true).show();
                }
            }
        });
    }
    public void getuser(){
        BmobQuery<_User> bq1 = new BmobQuery<_User>();
        bq1.getObject((String) BmobUser.getObjectByKey("objectId"), new QueryListener<_User>() {
            @Override
            public void done(_User object, BmobException e) {
                if (e == null) {
                    user_me_id=object.getObjectId();
                }
            }});

    }public void getview(){
        views=views+1;
        getupdate();

    }
    public void getupdate(){
//        Posts post = new Posts();
//        post.setReplys(replys);
//        post.setView(views);
//        post.update(post_id, new UpdateListener() {
//            @Override
//            public void done(BmobException e) {
//                if(e==null){
//
//                }else{
//
//                }
//            }
//
//        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mpost, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.b){
            //todo
            Intent intent = new Intent(MpostActivity.this,UpdateActivity.class);
            intent.putExtra("ID",ID);
            intent.putExtra("CONTENT",CONTENT);
            intent.putExtra("VISIBLE", VISIBLE);
            intent.putExtra("ANONYMOUS",ANONYMOUS);
            intent.putExtra("PHOTOURL",PHOTOURL);

        }else if(item.getItemId() == R.id.c){
            AlertDialog.Builder a=new AlertDialog.Builder(MpostActivity.this);
            a.setIcon(R.drawable.diablos);//图标
            a.setTitle("提示");//标题
            a.setMessage("确认删除这个帖子，删除后将无法恢复");//弹窗内容
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
                    Posts post = new Posts();
                    post.setObjectId(post_id);
                    post.delete(new UpdateListener() {

                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Toasty.success(MpostActivity.this, "删除成功", Toast.LENGTH_SHORT,true).show();
                                finish();
                            }else{
                                Toasty.error(MpostActivity.this, "删除失败", Toast.LENGTH_SHORT,true).show();
                            }
                        }

                    });
                }
            });
            a.show();
        }
        return super.onOptionsItemSelected(item);



    }
    class ItemListAdapter extends BaseAdapter
    {
        private Reply reply;
        //适配器
        @Override
        public int getCount()
        {
            if (mPostlist.size() > 0)
            {
                return mPostlist.size();
            }
            return 0;
        }


        @Override
        public Object getItem(int position)
        {
            return mPostlist.get(position);
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final MpostActivity.ItemListAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new MpostActivity.ItemListAdapter.ViewHolder();
                convertView =getLayoutInflater().inflate(R.layout.item_reply, null);
                viewHolder.x=convertView.findViewById(R.id.x);
                viewHolder.reply_username=convertView.findViewById(R.id.reply_username);
                viewHolder.reply_content=convertView.findViewById(R.id.reply_content);
                viewHolder.reply_headportrait=convertView.findViewById(R.id.reply_headportrait);
                viewHolder.reply_time=convertView.findViewById(R.id.reply_time);
                viewHolder.item=convertView.findViewById(R.id.reply_linearLayout);
                viewHolder.answer_user=convertView.findViewById(R.id.answer_user);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (MpostActivity.ItemListAdapter.ViewHolder)convertView.getTag();
            }


            reply = mPostlist.get(position);
            if(reply.getRecipient().getObjectId()!=reply.getPost().getAuthor().getObjectId()){
                viewHolder.x.setVisibility(View.VISIBLE);
                viewHolder.answer_user.setText(reply.getRecipient().getUsername());

            }
            viewHolder.reply_username.setText(reply.getAuthor().getUsername());
            viewHolder.reply_time.setText(reply.getCreatedAt());
            viewHolder.reply_content.setText(reply.getContent());
            //Glide.with(getActivity()).load(fenxiang.getauthor().getheadurl()).into(viewHolder.image);加载图片
            Glide.with(MpostActivity.this).load("https://q.qlogo.cn/headimg_dl?dst_uin="+reply.getAuthor().getQq()+"&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(MpostActivity.this)).into(viewHolder.reply_headportrait);

            viewHolder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reply_content.setText("@"+reply.getAuthor().getUsername()+"/"+reply.getContent()+"//");
                }
            });
            viewHolder.reply_headportrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reply_author_id=mPostlist.get(position).getAuthor().getObjectId();
                    if(user_me_id.equals(reply_author_id)){
                        Intent intent = new Intent(MpostActivity.this, MuserActivity.class);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(MpostActivity.this, UserActivity.class);
                        intent.putExtra("user_id",reply_author_id);
                        startActivity(intent);
                    }

                }
            });

            viewHolder.item.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1)
                {

                    answers.setVisibility(View.VISIBLE);
                    answer.setText("回复："+mPostlist.get(position).getAuthor().getUsername());
                    reply_author_id=mPostlist.get(position).getAuthor().getObjectId();
                   // reply_content=mPostlist.get(position).getContent();
                    type=false;
                    // TODO: Implement this method
                }
            });

            viewHolder.item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    Snackbar.make(toolbar, "删除这条评论", Snackbar.LENGTH_LONG).setAction("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Reply r = new Reply();
                            r.setObjectId(mPostlist.get(position).getObjectId());
                            r.delete(new UpdateListener() {

                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        Snackbar.make(toolbar, "删除成功", Snackbar.LENGTH_SHORT).show();
                                        getreply();
                                    }else{
                                        Snackbar.make(toolbar, "删除失败", Snackbar.LENGTH_SHORT).show();
                                    }
                                }

                            });

                        }}).show();
                    return false;
                }
            });


            return convertView;
        }


        public class ViewHolder
        {
            public TextView reply_username,reply_content,reply_time,answer_user;
            public LinearLayout item,x;
            public ImageView reply_headportrait;
        }


    };};
