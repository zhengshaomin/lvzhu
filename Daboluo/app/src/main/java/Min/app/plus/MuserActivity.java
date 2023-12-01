package Min.app.plus;

import static Min.app.plus.utils.QueryBasisInfoUtils.userboluocoin;
import static Min.app.plus.utils.QueryBasisInfoUtils.username;
import static Min.app.plus.utils.QueryBasisInfoUtils.userobjectid;
import static Min.app.plus.utils.QueryBasisInfoUtils.userqq;
import static Min.app.plus.utils.QueryBasisInfoUtils.usersignature;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import Min.app.plus.bmob.Posts;
import Min.app.plus.bmob.Urelation;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.GlideActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.QueryListener;
import es.dmoral.toasty.Toasty;

/**
 * @author daboluo
 */
public class MuserActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView muser_headportrait,sex;
    private TextView muser_username,muser_signature,muser_follow,muser_fans,muser_boluob;
   // private mListView muser_list;
    private String ship_id,t_username,post_id;
    private LinearLayout layout_post,layout_reply,layout_signature,layout_boluob,layout_follow,layout_fans;
    private ArrayList<String> ship_list = new ArrayList<>();
    private int intuser=0;
    List<Posts> mPostlist = new ArrayList<Posts>();
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            follow_quantity();
            fens_quantity();
            //getpost();
        }};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.muser2);


        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        }.start();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("我的资料");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        muser_headportrait=findViewById(R.id.muser_headportrait);
        muser_username=findViewById(R.id.muser_username);
        muser_signature=findViewById(R.id.muser_signature);
        layout_boluob=findViewById(R.id.layout_boluob);
        layout_follow=findViewById(R.id.layout_follow);
        layout_fans=findViewById(R.id.layou_fans);
        muser_follow=findViewById(R.id.muser_follow);
        muser_fans=findViewById(R.id.muser_fans);
        muser_boluob=findViewById(R.id.muser_boluob);
        layout_signature=findViewById(R.id.layout_signature);
        layout_reply=findViewById(R.id.layout_reply);
        layout_post=findViewById(R.id.layout_post);
        Glide.with(MuserActivity.this).load("https://q.qlogo.cn/headimg_dl?dst_uin=" + userqq + "&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(MuserActivity.this)).into(muser_headportrait);
        muser_username.setText(username);
        muser_signature.setText(usersignature);
        muser_boluob.setText(userboluocoin+"");
        //muser_list=findViewById(R.id.muser_list);
        layout_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MuserActivity.this,FollowActivity.class);
                intent.putExtra("userid",userobjectid);
                intent.putExtra("title","我的关注");
                startActivity(intent);
            }
        });
        layout_fans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MuserActivity.this,FensActivity.class);
                intent.putExtra("userid",userobjectid);
                intent.putExtra("title","我的粉丝");
                startActivity(intent);
            }
        });
        layout_boluob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MuserActivity.this,Account.class);
                startActivity(intent);
            }
        });
        layout_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        layout_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MuserActivity.this, MreplyList.class);
                startActivity(intent);
            }
        });
        layout_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MuserActivity.this, MpostList.class);
                startActivity(intent);
            }
        });
        /*muser_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean enable = false;
                if (muser_list != null && muser_list.getChildCount() > 0) {
                    // 检查listView第一个item是否可见
                    boolean firstItemVisible = muser_list.getFirstVisiblePosition() == 0;
                    // 检查第一个item的顶部是否可见
                    boolean topOfFirstItemVisible = muser_list.getChildAt(0).getTop() == 0;
                    // 启用或者禁用SwipeRefreshLayout刷新标识
                    enable = firstItemVisible && topOfFirstItemVisible;
                } else if (muser_list != null && muser_list.getChildCount() == 0) {
                    // 没有数据的时候允许刷新
                    enable = true;
                }
                // 把标识传给swipeRefreshLayout
                //home_swip.setEnabled(enable);

            }
        });*/
    }
    public void getuser(){
        BmobQuery<_User> bq1 = new BmobQuery<_User>();
        bq1.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
        bq1.getObject((String) BmobUser.getObjectByKey("objectId"), new QueryListener<_User>() {
            @Override
            public void done(_User object, BmobException e) {
                if (e == null) {
                    intuser=1;
                    //user_id=object.getObjectId();

//                    if(object.getSex()==null){}else if(object.getSex()==false){
//                        sex.setImageResource(R.drawable.nan);
//                    }else {
//                        sex.setImageResource(R.drawable.nv);
//                    }
                } else {
                    Toasty.error(MuserActivity.this, "数据加载失败", Toast.LENGTH_SHORT,true).show();
                }
            }
        });
    }
    public void follow_quantity(){
        //关注数量
        BmobQuery<Urelation> query = new BmobQuery<Urelation>();
        query.addWhereEqualTo("author",BmobUser.getObjectByKey("objectId"));
        query.count(Urelation.class, new CountListener() {
            @Override
            public void done(Integer count, BmobException e) {
                if(e==null){
                    muser_follow.setText(""+count);
                }else{
                    muser_follow.setText(""+0);
                }
            }
        });

    }
    public void fens_quantity(){
        //粉丝数量
        BmobQuery<Urelation> query = new BmobQuery<Urelation>();
        query.addWhereEqualTo("object",BmobUser.getObjectByKey("objectId"));
        query.count(Urelation.class, new CountListener() {
            @Override
            public void done(Integer count, BmobException e) {
                if(e==null){
                    muser_fans.setText(""+count);
                }else{
                    muser_fans.setText(""+0);
                }
            }
        });
    }
    public void getpost() {

//        BmobQuery<Posts> bmobQuery = new BmobQuery<>();
//        bmobQuery.include("author");
//        bmobQuery.addWhereEqualTo("author",BmobUser.getObjectByKey("objectId"));//查询可见的
//        bmobQuery.order("-createdAt");//依照数据排序时间排序
//        bmobQuery.setLimit(100);
//        bmobQuery.findObjects(new FindListener<Posts>() {
//            @Override
//            public void done(List<Posts> object, BmobException e) {
//
//                if (e == null) {
//                    mPostlist.clear();
//                    for (int i = 0; i < object.size(); i++) {
//                        mPostlist.add(object.get(i));
//                    }
//                    muser_list.setAdapter(new MuserActivity.ItemListAdapter());
//                    // home_swip.setRefreshing(false);
//                } else {
//                    Toasty.error(MuserActivity.this, "数据加载失败", Toast.LENGTH_SHORT,true).show();
//                    //home_swip.setRefreshing(false);
//
//                }
//
//            }
//        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_meuser, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.edit:

                Intent intent4 = new Intent(MuserActivity.this,Userupdate.class);
                startActivity(intent4);

                break;

        }
        return super.onOptionsItemSelected(item);
    }
    class ItemListAdapter extends BaseAdapter
    {
        private Posts fenxiang;
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
            final MuserActivity.ItemListAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new MuserActivity.ItemListAdapter.ViewHolder();
                convertView =getLayoutInflater().inflate(R.layout.item_post, null);
                viewHolder.username = (TextView) convertView.findViewById(R.id.username);
                viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearlayout);
                viewHolder.content = (TextView) convertView.findViewById(R.id.content);
                viewHolder.headportrait = (ImageView) convertView.findViewById(R.id.headportrait);
                viewHolder.time = (TextView) convertView.findViewById(R.id.time);
                viewHolder.replyvalue = convertView.findViewById(R.id.replyvalue);
                viewHolder.viewvalue=convertView.findViewById(R.id.viewvalue);
                viewHolder.photo = convertView.findViewById(R.id.photo);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (MuserActivity.ItemListAdapter.ViewHolder) convertView.getTag();
            }
            fenxiang = mPostlist.get(position);

            if (fenxiang.getAnonymous() == true) {
                viewHolder.username.setText("匿名");
                Glide.with(MuserActivity.this).load("https://z3.ax1x.com/2021/11/20/ILiyNR.jpg").crossFade(800).transform(new GlideActivity(MuserActivity.this)).into(viewHolder.headportrait);
            } else {
                viewHolder.username.setText(fenxiang.getAuthor().getUsername());
                Glide.with(MuserActivity.this).load("https://q.qlogo.cn/headimg_dl?dst_uin=" + fenxiang.getAuthor().getQq() + "&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(MuserActivity.this)).into(viewHolder.headportrait);
            }
            if(mPostlist.get(position).getPhoto()!=null){
                viewHolder.photo.setVisibility(View.VISIBLE);
                Glide.with(MuserActivity.this).load(mPostlist.get(position).getPhoto().getUrl()).into(viewHolder.photo);
            }
            viewHolder.content.setText(fenxiang.getContent());
            viewHolder.time.setText(fenxiang.getCreatedAt());
//            viewHolder.replyvalue.setText(fenxiang.getReplys()+"");
//            viewHolder.viewvalue.setText(fenxiang.getView()+"");

            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1)
                {
                    post_id=mPostlist.get(position).getObjectId(); //这里就获取到了Id。

                    Intent intent = new Intent(MuserActivity.this, MpostActivity.class);
                    intent.putExtra("post_id", post_id);
                    startActivity(intent);

                    // TODO: Implement this method
                }
            });

            return convertView;
        }
        public class ViewHolder
        {
            public TextView username,content,time,replyvalue,viewvalue;
            public LinearLayout linearLayout;
            public ImageView headportrait,photo;
        }

    }}