package Min.app.plus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import Min.app.plus.adapter.FollowAdapter;
import Min.app.plus.bmob.Urelation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import es.dmoral.toasty.Toasty;

/**
 * @author daboluo
 */
public class FollowActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String ship_object_id,userid,title;

    private SmartRefreshLayout srlControl;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private FollowAdapter adapter;
    List<Urelation> ships = new ArrayList<Urelation>();
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            initView();
            loadData();
            gettrade();
        }};
    private AlertDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ship);


        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        userid=intent.getStringExtra("userid");


        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        }.start();



    }
    //申明id
    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        srlControl = findViewById(R.id.srl_control);
        recyclerView=findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(FollowActivity.this);
        adapter = new FollowAdapter(ships = getData());
        recyclerView.setLayoutManager(layoutManager);

    }

    /**
     * 加载数据
     */
    private void loadData(){
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //监听下拉和上拉状态
        //下拉刷新
        srlControl.setOnRefreshListener(refreshlayout -> {
            srlControl.setEnableRefresh(true);//启用刷新

            gettrade();
            srlControl.finishRefresh();//结束刷新
        });


    }
    private List<Urelation> getData() {
        List<Urelation> list = new ArrayList<>();
//        list.add(new Chat("Hello",Chat.TYPE_RECEIVED));
        return list;


    }
    public void gettrade(){

            BmobQuery<Urelation> bmobQuery = new BmobQuery<>();
            bmobQuery.include("author,object");
            bmobQuery.addWhereEqualTo("author",userid);//查询用户发布的贴子
            bmobQuery.order("-createdAt");//依照数据排序时间排序
            bmobQuery.setLimit(500);
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
            bmobQuery.findObjects(new FindListener<Urelation>() {
                @Override
                public void done(List<Urelation> object, BmobException e) {
                    if (e == null) {
                        ships.clear();
                        //value.setText("关注了"+object.size()+"位用户");
                        for (int i = 0; i < object.size(); i++) {
                           ships.add(object.get(i));
                        }
                        recyclerView.setAdapter(adapter);
                    } else {
                        //Toast.makeText(getActivity(), "出现错误！", Toast.LENGTH_SHORT).show();
                        Toasty.error(FollowActivity.this, "数据加载失败！", Toast.LENGTH_SHORT,true).show();
                    }

                }
            });
    }
}
