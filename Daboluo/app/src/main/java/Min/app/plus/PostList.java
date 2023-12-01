package Min.app.plus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import Min.app.plus.adapter.HomeTabOneAdapter;
import Min.app.plus.bmob.Posts;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import es.dmoral.toasty.Toasty;

/**
 * @author daboluo
 */
public class PostList extends AppCompatActivity {

    private Toolbar toolbar;
    private SmartRefreshLayout srlControl;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private HomeTabOneAdapter adapter;
    List<Posts> mPostlist = new ArrayList<Posts>();
    private String post_id,user_id,title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postlist);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        //title = intent.getStringExtra("title");
        
        toolbar = findViewById(R.id.toolbar);
        recyclerView=findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this);

        adapter = new HomeTabOneAdapter(mPostlist = getData());
        recyclerView.setLayoutManager(layoutManager);
        srlControl = findViewById(R.id.srl_control);
        //监听下拉和上拉状态
        //下拉刷新
        srlControl.setOnRefreshListener(refreshlayout -> {
            srlControl.setEnableRefresh(true);//启用刷新

            getdata();

        });

        toolbar.setTitle("Ta的动态");
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


    }
    private List<Posts> getData() {
        List<Posts> list = new ArrayList<>();
//        list.add(new Chat("Hello",Chat.TYPE_RECEIVED));
        return list;


    }
    public void getdata() {

        BmobQuery<Posts> eq1 = new BmobQuery<Posts>();
        eq1.addWhereEqualTo("author", user_id);//查询用户发布的贴子
        BmobQuery<Posts> bmobQuery2 = new BmobQuery<Posts>();
        BmobQuery<Posts> eq2 = new BmobQuery<Posts>();
        eq2.addWhereEqualTo("visible", true);//查询用户发布的内容为可见部分
        BmobQuery<Posts> eq3 = new BmobQuery<Posts>();
        eq3.addWhereEqualTo("anonymous",false);//查询用户发布非隐身内容
        List<BmobQuery<Posts>> andQuerys = new ArrayList<BmobQuery<Posts>>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);
        andQuerys.add(eq3);
//查询符合整个and条件的人
        BmobQuery<Posts> query = new BmobQuery<Posts>();
        query.and(andQuerys);
        query.include("author");
        query.order("-createdAt");//依照数据排序时间排序
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
        query.findObjects(new FindListener<Posts>() {
            @Override
            public void done(List<Posts> object, BmobException e) {
                srlControl.finishRefresh();//结束刷新
                if(e==null){
                    mPostlist.clear();
                    for (int i = 0; i < object.size(); i++)
                    {
                        mPostlist.add(object.get(i));
                    }
                    recyclerView.setAdapter(adapter);

                }else{
                    Toasty.error(PostList.this, "出现错误！", Toast.LENGTH_SHORT,true).show();

                }
            }
        });

    }}
