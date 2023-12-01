package Min.app.plus;

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

import Min.app.plus.adapter.MpostAdapter;
import Min.app.plus.bmob.Posts;
import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import es.dmoral.toasty.Toasty;

/**
 * @author daboluo
 */
public class MpostList extends AppCompatActivity {

    private Toolbar toolbar;
    private SmartRefreshLayout srlControl;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private MpostAdapter adapter;
    //private ListView mp_list;
    List<Posts> mPostlist = new ArrayList<Posts>();

    private String post_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postlist);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("我的发布");
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

        adapter = new MpostAdapter(mPostlist = getData());
        recyclerView.setLayoutManager(layoutManager);
        srlControl = findViewById(R.id.srl_control);
        //监听下拉和上拉状态
        //下拉刷新
        srlControl.setOnRefreshListener(refreshlayout -> {
            srlControl.setEnableRefresh(true);//启用刷新

            getdata();

        });

    }
        private List<Posts> getData() {
            List<Posts> list = new ArrayList<>();
//        list.add(new Chat("Hello",Chat.TYPE_RECEIVED));
            return list;
    }
    public void getdata() {

        BmobQuery<Posts> bmobQuery = new BmobQuery<>();
        bmobQuery.include("author");
        bmobQuery.addWhereEqualTo("author", BmobUser.getCurrentUser(_User.class));//查询用户发布的贴子
        bmobQuery.order("-createdAt");//依照数据排序时间排序
        bmobQuery.setLimit(500);
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
        bmobQuery.findObjects(new FindListener<Posts>() {
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
                    Toasty.error(MpostList.this, "出现错误！", Toast.LENGTH_SHORT,true).show();

                }

            }
        });

    }
}
