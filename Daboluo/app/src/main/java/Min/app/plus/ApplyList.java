package Min.app.plus;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.google.android.material.snackbar.Snackbar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import Min.app.plus.bmob.Apply;
import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import es.dmoral.toasty.Toasty;

public class ApplyList extends AppCompatActivity {

    private Toolbar toolbar;
    private SmartRefreshLayout apply_smart;
    private ListView apply_list;
    List<Apply> trades = new ArrayList<Apply>();
    private String goods_id,user_id;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            gettuser();

        }};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applylist);
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg2 = new Message();
                handler.sendMessage(msg2);
            }
        }.start();
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("提现记录");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        apply_list=findViewById(R.id.apply_list);
        apply_smart=findViewById(R.id.apply_srlControl);
        //监听下拉和上拉状态
        //下拉刷新
        apply_smart.setOnRefreshListener(refreshlayout -> {
            apply_smart.setEnableRefresh(true);//启用刷新
            gettrade();
            apply_smart.finishRefresh();//结束刷新
        });
        apply_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean enable = false;
                if (apply_list != null && apply_list.getChildCount() > 0) {
                    // 检查listView第一个item是否可见
                    boolean firstItemVisible = apply_list.getFirstVisiblePosition() == 0;
                    // 检查第一个item的顶部是否可见
                    boolean topOfFirstItemVisible = apply_list.getChildAt(0).getTop() == 0;
                    // 启用或者禁用SwipeRefreshLayout刷新标识
                    enable = firstItemVisible && topOfFirstItemVisible;
                } else if (apply_list != null && apply_list.getChildCount() == 0) {
                    // 没有数据的时候允许刷新
                    enable = true;
                }
                // 把标识传给swipeRefreshLayout
                //apply_list.setEnabled(enable);不允许滑动
            }
        });

    }
    public void gettuser(){
        BmobQuery<_User> bq1 = new BmobQuery<_User>();
        bq1.getObject((String) BmobUser.getObjectByKey("objectId"), new QueryListener<_User>() {
            @Override
            public void done(_User object, BmobException e) {
                if (e == null) {
                    user_id=object.getObjectId();
                    gettrade();
                }
            }});
    }
    public void gettrade(){
        if(user_id==null) {
            Toasty.warning(getApplicationContext(), "请先登录！", Toast.LENGTH_SHORT,true).show();
        }else {
            BmobQuery<Apply> bmobQuery = new BmobQuery<>();
            bmobQuery.include("user");
            bmobQuery.addWhereEqualTo("user", BmobUser.getCurrentUser(_User.class));//查询用户发布的贴子
            bmobQuery.order("-createdAt");//依照数据排序时间排序
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
            bmobQuery.findObjects(new FindListener<Apply>() {
                @Override
                public void done(List<Apply> object, BmobException e) {
                    if (e == null) {
                        trades.clear();
                        for (int i = 0; i < object.size(); i++) {
                            trades.add(object.get(i));
                        }
                        apply_list.setAdapter(new ApplyList.ItemListAdaptert());
                    } else {
                        Toasty.error(getApplicationContext(), "出现错误！", Toast.LENGTH_SHORT,true).show();

                    }

                }
            });
        }
    }
    class ItemListAdaptert extends BaseAdapter
    {
        private Apply fenxiang;
        //适配器
        @Override
        public int getCount()
        {
            if (trades.size() > 0)
            {
                return trades.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position)
        {
            return trades.get(position);
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ApplyList.ItemListAdaptert.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder =new ApplyList.ItemListAdaptert.ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_apply, null);
                viewHolder.headportrait=convertView.findViewById(R.id.headportrait);
                viewHolder.content=convertView.findViewById(R.id.content);
                viewHolder.state=convertView.findViewById(R.id.state);
                viewHolder.time=convertView.findViewById(R.id.time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ApplyList.ItemListAdaptert.ViewHolder) convertView.getTag();
            }
            fenxiang = trades.get(position);
            Glide.with(ApplyList.this).load("https://q.qlogo.cn/headimg_dl?dst_uin="+fenxiang.getUser().getQq() + "&spec=640&img_type=jpg").crossFade(800).into(viewHolder.headportrait);
            viewHolder.content.setText(fenxiang.getBoluo()+"菠萝-"+fenxiang.getAlipay());
            viewHolder.time.setText(fenxiang.getCreatedAt());
            viewHolder.state.setText(fenxiang.getState());

            return convertView;
        }
        public class ViewHolder
        {
            public TextView content,time,state;
            public LinearLayout trade_linearLayout;
            public ImageView headportrait;

        }
    }}