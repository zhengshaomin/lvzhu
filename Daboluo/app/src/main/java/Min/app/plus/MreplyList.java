package Min.app.plus;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
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
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;

/**
 * @author daboluo
 */
public class MreplyList extends AppCompatActivity {

    private SwipeMenuListView mreply_list;
    private int t;
    List<Reply> replys = new ArrayList<Reply>();

    private String post_id=null, user_id,reply_id,recipient_id,ship_id,user_he_name,user_he_head;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            getdata();

        }};
    private Toolbar toolbar;
    private SwipeMenuCreator creator;
    private SmartRefreshLayout srlControl;
    private android.app.AlertDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mreplylist);


        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("我的评论");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        srlControl = findViewById(R.id.srl_control);
        //监听下拉和上拉状态
        //下拉刷新
        srlControl.setOnRefreshListener(refreshlayout -> {
            srlControl.setEnableRefresh(true);//启用刷新

            getdata();
            srlControl.finishRefresh();//结束刷新
        });

        View view = LayoutInflater.from(MreplyList.this).inflate(R.layout.dialog_loading, null, false);
        dialog = new AlertDialog.Builder(MreplyList.this).setView(view).create();
        dialog.setCancelable(false);
        mreply_list=findViewById(R.id.view_reply_list);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" iteM

                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        MreplyList.this);
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set item title
                deleteItem.setTitle("删除");
                // set item title fontsize
                deleteItem.setTitleSize(18);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(deleteItem);
            }
        };
//
        mreply_list.setMenuCreator(creator);

        mreply_list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // 删除
                        dialog.show();
                        reply_id=replys.get(position).getObjectId();
                        delete();
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });


        mreply_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // Snackbar.make(view, "这是第"+position+"个item", Snackbar.LENGTH_SHORT).show();

                    //提醒关于帖子的评论或回复
                    if(replys.get(position).getPost().getObjectId()!=null){
                        post_id=replys.get(position).getPost().getObjectId();
                        Intent intent = new Intent(MreplyList.this,PostActivity.class);
                        intent.putExtra("post_id", post_id);
                        startActivity(intent);
                    }else {
                        Toasty.warning(MreplyList.this, "帖子不存在或者被删除了", Toast.LENGTH_SHORT,true).show();
                    }

                //do what you want
            }
        });


        mreply_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean enable = false;
                if (mreply_list != null && mreply_list.getChildCount() > 0) {
                    // 检查view_reply_listView第一个item是否可见
                    boolean firstItemVisible = mreply_list.getFirstVisiblePosition() == 0;
                    // 检查第一个item的顶部是否可见
                    boolean topOfFirstItemVisible = mreply_list.getChildAt(0).getTop() == 0;
                    // 启用或者禁用view_reply_swipeRefreshLayout刷新标识
                    enable = firstItemVisible && topOfFirstItemVisible;
                } else if (mreply_list != null && mreply_list.getChildCount() == 0) {
                    // 没有数据的时候允许刷新
                    enable = true;
                }
                // 把标识传给view_reply_swipeRefreshLayout
                //view_reply_swip.setEnabled(enable);
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
    }public void delete(){
        Reply delete = new Reply();
        delete.setObjectId(reply_id);
        delete.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                dialog.dismiss();
                if(e==null){
                    getdata();
                    //删除成功刷新数据
                }else {
                    Toasty.error(MreplyList.this, "出现错误", Toast.LENGTH_SHORT,true).show();
                }
            }

        });
    }
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());

    }
    public void getdata() {

        BmobQuery<Reply> bmobQuery = new BmobQuery<>();
        bmobQuery.include("author,post,recipient");
        bmobQuery.addWhereEqualTo("author", BmobUser.getCurrentUser(_User.class));//查询用户发布的贴子
        bmobQuery.order("-createdAt");//依照数据排序时间排序
        bmobQuery.setLimit(500);
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
        bmobQuery.findObjects(new FindListener<Reply>() {
            @Override
            public void done(List<Reply> object, BmobException e) {
                srlControl.finishRefresh();//结束刷新
                if(e==null){
                    replys.clear();
                    for (int i = 0; i < object.size(); i++)
                    {
                        replys.add(object.get(i));
                    }
                    mreply_list.setAdapter(new ItemListAdapter());

                }else{
                    //Toast.makeText(getActivity(), "出现错误！", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }
    class ItemListAdapter extends BaseAdapter
    {
        private Reply fenxiang;
        //适配器
        @Override
        public int getCount()
        {
            if (replys.size() > 0)
            {
                return replys.size();
            }
            return 0;
        }


        @Override
        public Object getItem(int position)
        {
            return replys.get(position);
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ItemListAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ItemListAdapter.ViewHolder();
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
                viewHolder = (ItemListAdapter.ViewHolder)convertView.getTag();
            }


            fenxiang = replys.get(position);
            viewHolder.content.setText(fenxiang.getContent());
            viewHolder.username.setText(fenxiang.getAuthor().getUsername());
            viewHolder.time.setText(fenxiang.getCreatedAt());
            viewHolder.post_content.setText(fenxiang.getPost().getContent());
            viewHolder.recipient_name.setText("@"+fenxiang.getRecipient().getUsername());
            // Glide.with(getActivity()).load(fenxiang.getAuthor().getQq()).into(viewHolder.image);

            Glide.with(MreplyList.this).load("https://q.qlogo.cn/headimg_dl?dst_uin="+fenxiang.getAuthor().getQq()+"&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(MreplyList.this)).into(viewHolder.headportrait);

            return convertView;
        }


        public class ViewHolder
        {
            public TextView username,content,post_content,time,recipient_name;
            public LinearLayout item;
            public ImageView headportrait;
        }

    };}
