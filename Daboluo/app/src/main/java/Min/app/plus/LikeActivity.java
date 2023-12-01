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

import Min.app.plus.adapter.LikeAdapter;
import Min.app.plus.bmob.Reply;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import es.dmoral.toasty.Toasty;

/**
 * 作者：daboluo on 2023/9/21 00:43
 * Email:daboluo719@gmail.com
 */
//点赞列表
public class LikeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SmartRefreshLayout srlControl;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private LikeAdapter adapter;
    List<Reply> mPostlist = new ArrayList<Reply>();

    private String title,user_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postlist);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        title = intent.getStringExtra("title");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
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

        recyclerView = findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this);

        adapter = new LikeAdapter(mPostlist = getData());
        recyclerView.setLayoutManager(layoutManager);
        srlControl = findViewById(R.id.srl_control);
        //监听下拉和上拉状态
        //下拉刷新
        srlControl.setOnRefreshListener(refreshlayout -> {
            srlControl.setEnableRefresh(true);//启用刷新

            getdata();

        });

    }
    private List<Reply> getData() {
        List<Reply> list = new ArrayList<>();
//        list.add(new Chat("Hello",Chat.TYPE_RECEIVED));
        return list;
    }
    public void getdata() {

        BmobQuery<Reply> eq1 = new BmobQuery<Reply>();
        eq1.addWhereEqualTo("author",user_id);//查询接收者是我的回复
        BmobQuery<Reply> eq2 = new BmobQuery<Reply>();
        eq2.addWhereEqualTo("type",true);//查询可见的
        List<BmobQuery<Reply>> andQuerys = new ArrayList<BmobQuery<Reply>>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);

        BmobQuery<Reply> query = new BmobQuery<Reply>();
        query.and(andQuerys);

        query.include("author,post");
        query.order("-createdAt");//依照数据排序时间排序
        query.setLimit(500);
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
        query.findObjects(new FindListener<Reply>() {
            @Override
            public void done(List<Reply> object, BmobException e) {
                srlControl.finishRefresh();//结束刷新
                if(e==null){
                    mPostlist.clear();
                    for (int i = 0; i < object.size(); i++)
                    {
                        mPostlist.add(object.get(i));
                    }
                    recyclerView.setAdapter(adapter);

                }else{
                    Toasty.error(LikeActivity.this, "出现错误！", Toast.LENGTH_SHORT,true).show();
                }
            }
        });

    }
}
