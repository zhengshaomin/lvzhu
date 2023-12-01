package Min.app.plus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import Min.app.plus.bmob.Reply;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.GlideActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import es.dmoral.toasty.Toasty;

/**
 * @author daboluo
 */
public class ReplyList extends AppCompatActivity {

    private Toolbar toolbar;
    private SmartRefreshLayout srlControl;
    private ListView mr_list;
    private String post_id,reply_author_id,user_me_id,post_author_id,title;
    List<Reply> mPostlist = new ArrayList<Reply>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.replylist);

        Intent intent = getIntent();
        reply_author_id = intent.getStringExtra("user_id");

        toolbar = findViewById(R.id.toolbar);
        mr_list = findViewById(R.id.mr_list);
        srlControl = findViewById(R.id.srl_control);
        //监听下拉和上拉状态
        //下拉刷新
        srlControl.setOnRefreshListener(refreshlayout -> {

            srlControl.setEnableRefresh(true);//启用刷新

            getdata();

        });
        toolbar.setTitle("Ta的回复");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        getdata();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mr_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean enable = false;
                if (mr_list!= null &&mr_list.getChildCount() > 0)
                {
                    // 检查listView第一个item是否可见
                    boolean firstItemVisible = mr_list.getFirstVisiblePosition() == 0;
                    // 检查第一个item的顶部是否可见
                    boolean topOfFirstItemVisible = mr_list.getChildAt(0).getTop() == 0;
                    // 启用或者禁用SwipeRefreshLayout刷新标识
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                else if (mr_list != null &&mr_list.getChildCount() == 0)
                {
                    // 没有数据的时候允许刷新
                    enable = true;
                }
                // 把标识传给swipeRefreshLayout
                //mr_swip.setEnabled(enable);

            }
        });
gethuser();
    }
    public void gethuser() {
        BmobQuery<_User> bq1 = new BmobQuery<_User>();
        bq1.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);// 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        bq1.getObject((String) BmobUser.getObjectByKey("objectId"), new QueryListener<_User>() {
            @Override
            public void done(_User object, BmobException e) {
                if (e == null) {
                    user_me_id = object.getObjectId();
                }
            }
        });
    }
    public void getdata() {

        BmobQuery<Reply> bmobQuery = new BmobQuery<>();
        bmobQuery.include("author,post.author,recipient");
        bmobQuery.addWhereEqualTo("author",reply_author_id);//查询用户发布的贴子
        bmobQuery.order("-createdAt");//依照数据排序时间排序
        bmobQuery.setLimit(500);
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
        bmobQuery.findObjects(new FindListener<Reply>() {
            @Override
            public void done(List<Reply> object, BmobException e) {
                srlControl.finishRefresh();//结束刷新
                if(e==null){
                    mPostlist.clear();
                    for (int i = 0; i < object.size(); i++)
                    {
                        mPostlist.add(object.get(i));
                    }
                    mr_list.setAdapter(new ReplyList.ItemListAdapter2());

                }else{
                    Toasty.error(ReplyList.this, "出现错误！", Toast.LENGTH_SHORT,true).show();

                }

            }
        });

    }
    class ItemListAdapter2 extends BaseAdapter
    {
        private Reply fenxiang;
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
            final ReplyList.ItemListAdapter2.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ReplyList.ItemListAdapter2.ViewHolder();
                convertView =getLayoutInflater().inflate(R.layout.item_mreply, null);
                viewHolder.headportrait=(ImageView) convertView.findViewById(R.id.headportrait);
                viewHolder.username=(TextView) convertView.findViewById(R.id.username);
                viewHolder.item =(LinearLayout) convertView.findViewById(R.id.item);
                viewHolder.content=(TextView) convertView.findViewById(R.id.content);
                viewHolder.post_content=convertView.findViewById(R.id.post_content);
                viewHolder.time=(TextView) convertView.findViewById(R.id.time);
                viewHolder.recipient_name=convertView.findViewById(R.id.recipient_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ReplyList.ItemListAdapter2.ViewHolder)convertView.getTag();
            }


            fenxiang = mPostlist.get(position);
            viewHolder.content.setText(fenxiang.getContent());
            viewHolder.username.setText(fenxiang.getAuthor().getUsername());
            viewHolder.time.setText(fenxiang.getCreatedAt());
            viewHolder.post_content.setText(fenxiang.getPost().getContent());
            viewHolder.recipient_name.setText("@"+fenxiang.getRecipient().getUsername());

            Glide.with(ReplyList.this).load("https://q.qlogo.cn/headimg_dl?dst_uin="+fenxiang.getAuthor().getQq()+"&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(ReplyList.this)).into(viewHolder.headportrait);

            viewHolder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mPostlist.get(position).getPost().getObjectId()!=null){
                        post_id=mPostlist.get(position).getPost().getObjectId();
                        Intent intent = new Intent(ReplyList.this,PostActivity.class);
                        intent.putExtra("post_id", post_id);
                        startActivity(intent);
                    }else {
                        Toasty.warning(ReplyList.this, "帖子不存在或者被删除了", Toast.LENGTH_SHORT,true).show();
                    }
                }
            });
           
            return convertView;
        }


        public class ViewHolder
        {
            public TextView username,content,post_content,time,recipient_name;
            public LinearLayout item;
            public ImageView headportrait;
        }

    };}
