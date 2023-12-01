package Min.app.plus;

import static Min.app.plus.utils.DialogUtils.dialogloadingdismiss;
import static Min.app.plus.utils.DialogUtils.dialogloadingshow;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import Min.app.plus.adapter.ReplyAdapter;
import Min.app.plus.bmob.Reply;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.GlideActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;

public class ReplyActivity extends AppCompatActivity {

    private View view;

    private String TAG="ReplyActivity";
    private RecyclerView recyclerview;
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
    private LinearLayoutManager layoutManager;
    private SmartRefreshLayout srlControl;
    private ReplyAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reply);


        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        }.start();

        dialogloadingshow(ReplyActivity.this);
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("消息通知");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
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
        /*srlControl.setOnLoadmoreListener(refreshlayout -> {
            srlControl.setEnableLoadmore(true);//启用加载

            getdata();
            //refreshAdapter.loadMore(MoreDatas());
            srlControl.finishLoadmore();//结束加载
        });

         */
        recyclerview=findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(ReplyActivity.this);

        adapter = new ReplyAdapter(replys = getData());
        recyclerview.setLayoutManager(layoutManager);




    }
    private List<Reply> getData() {
        List<Reply> list = new ArrayList<>();
//        list.add(new Chat("Hello",Chat.TYPE_RECEIVED));
        return list;


    }

    //查询数据
    public void getdata() {
        BmobQuery<Reply>eq1=new BmobQuery<>();
        eq1.addWhereEqualTo("visible",true);//查询可见的
        BmobQuery<Reply>eq2=new BmobQuery<>();
        eq2.addWhereEqualTo("recipient", BmobUser.getCurrentUser(_User.class));//接收者是我的消息

        List<BmobQuery<Reply>> andQuerys = new ArrayList<BmobQuery<Reply>>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);

        BmobQuery<Reply> bmobQuery = new BmobQuery<>();
        bmobQuery.and(andQuerys);
        bmobQuery.include("author,post,recipient");
        bmobQuery.order("-createdAt");//依照数据排序时间排序
        bmobQuery.setLimit(200);

        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);// 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        bmobQuery.findObjects(new FindListener<Reply>() {
            @Override
            public void done(List<Reply> object, BmobException e) {
                dialogloadingdismiss();
                if (e == null) {
                    replys.clear();
                    t=object.size();
                    for (int i = 0; i < object.size(); i++) {
                        replys.add(object.get(i));
                    }
                    recyclerview.setAdapter(adapter);
                } else {
                    Toasty.error(ReplyActivity.this, "出现错误！", Toast.LENGTH_SHORT,true).show();
                    //view_reply_swip.setRefreshing(false);
                    //Snackbar.make(view_reply_swip, "刷新失败", Snackbar.LENGTH_SHORT).show();
                }

            }
        });
    }
    public void renew() {
        Reply p2 = new Reply();
        p2.setNews(false);
        p2.update(reply_id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    getdata();
                } else {
                }
            }

        });
    }
    //删除消息，不展示
    public void delete(){
        Reply delete = new Reply();
        delete.setVisible(false);
        delete.update(reply_id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                dialogloadingdismiss();
                if(e!=null){
                    Toasty.error(ReplyActivity.this, "出现错误！", Toast.LENGTH_SHORT,true).show();
                }else {
                    getdata();
                    //删除成功刷新数据
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
            final ReplyActivity.ItemListAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder =new ReplyActivity.ItemListAdapter.ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_news, null);
                viewHolder.news_username = (TextView) convertView.findViewById(R.id.news_username);
                viewHolder.news_linearLayout = (LinearLayout) convertView.findViewById(R.id.news_linearlayout);
                viewHolder.news_headportrait = (ImageView) convertView.findViewById(R.id.news_headportrait);
                viewHolder.news_content=convertView.findViewById(R.id.news_content);
                viewHolder.news_type = (TextView) convertView.findViewById(R.id.time);
                viewHolder.newone=convertView.findViewById(R.id.newone);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ReplyActivity.ItemListAdapter.ViewHolder)convertView.getTag();
            }
            fenxiang = replys.get(position);
            if(replys.get(position).getType()==true){
                viewHolder.news_type.setText("点赞");
            }else {
                viewHolder.news_type.setText("回复了你");
            }
            if(replys.get(position).getNews()==true){
                viewHolder.newone.setVisibility(View.VISIBLE);
            }
            Glide.with(ReplyActivity.this).load("https://q.qlogo.cn/headimg_dl?dst_uin=" + fenxiang.getAuthor().getQq()+ "&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(ReplyActivity.this)).into(viewHolder.news_headportrait);
            viewHolder.news_username.setText(fenxiang.getAuthor().getUsername());
            viewHolder.news_content.setText(fenxiang.getContent());


            return convertView;
        }
        public class ViewHolder
        {
            public TextView news_username,news_content,news_type,newone;
            public LinearLayout news_linearLayout;
            public ImageView news_headportrait,photo,like;

        }



    }
}