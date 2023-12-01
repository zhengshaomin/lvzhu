package Min.app.plus.fragment.main.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Min.app.plus.R;
import Min.app.plus.adapter.HomeTabOneAdapter;
import Min.app.plus.bmob.Posts;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import es.dmoral.toasty.Toasty;

/**
 * 作者：daboluo on 2023/9/20 13:40
 * Email:daboluo719@gmail.com
 */
public class HomeTab1 extends Fragment {

    private View view1;
    private Uri uri1;
    private Intent intent1;
//    private Banner banner;

    private String TAG="StartActivity";
    private RecyclerView postRecyclerView;
    private LinearLayoutManager layoutManager;
    private HomeTabOneAdapter adapter;
    private List<Posts> postList = new ArrayList<>();
    private String order_author_id,order_id;
    private ArrayList<String> imageurllist = new ArrayList<String>();
    private ArrayList<String> title_list = new ArrayList<>();
    private ArrayList<Boolean> type_list = new ArrayList<>();
    private ArrayList<String> idorurl_list = new ArrayList<>();
    private Boolean likes=false;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            getpost();

//            getbanner();

        }};
    private int intuser=0;
    //private TextView adds,news_reply;
    private int select = 0; //表示单选对话框初始时选中哪一项
    private String[] types = {"全部","生活分享", "求助咨询","寻物启示","失物招领","表白吐槽","闲置转让","其他"};

    private String type,post_id;
    private SmartRefreshLayout home_smart;
    private JSONObject reply_data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view1==null){
            view1=inflater.inflate(R.layout.home_tab1, container, false);
        }
        return view1;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bmob.initialize(getActivity(), "6d233c0993a3ab132ba5e11b0942960b");

        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        }.start();
//        banner=getActivity().findViewById(R.id.banner);

        postRecyclerView=getActivity().findViewById(R.id.home_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());

        adapter = new HomeTabOneAdapter(postList = getData());
        postRecyclerView.setLayoutManager(layoutManager);



        home_smart=getActivity().findViewById(R.id.home_smart);
        //监听下拉和上拉状态
        //下拉刷新
        home_smart.setOnRefreshListener(refreshlayout -> {
            home_smart.setEnableRefresh(true);//启用刷新
            getpost();
            home_smart.finishRefresh();//结束刷新
        });
        //上拉加载

    }

    private List<Posts> getData() {
        List<Posts> list = new ArrayList<>();
        return list;


    }
    public void getpost() {

        BmobQuery<Posts> bmobQuery = new BmobQuery<>();
        bmobQuery.include("author");
        bmobQuery.addWhereEqualTo("visible",true);//查询可见的
        bmobQuery.order("-createdAt");//依照数据排序时间排序
        bmobQuery.setLimit(100);
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);// 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        bmobQuery.findObjects(new FindListener<Posts>() {
            @Override
            public void done(List<Posts> object, BmobException e) {

                if (e == null) {
//                    Posts bean = new Posts();
                    postList.clear();
                    for (int i = 0; i < object.size(); i++) {
                        postList.add(object.get(i));
                        // 默认都给他们赋值当前都没有点赞
//                        bean.setZanFocus(false);
                    }
                    postRecyclerView.setAdapter(adapter);
                    // home_swip.setRefreshing(false);
                } else {
                    Log.d(TAG,"数据加载失败"+e);
                    Toasty.error(getActivity(), "数据加载失败！"+e, Toast.LENGTH_SHORT,true).show();
                    //home_swip.setRefreshing(false);

                }

            }
        });

    }
    // 轮播图单击打开网页链接
    public void openurl(String s)
    {
        uri1 = Uri.parse(s);
        intent1 = new Intent(Intent.ACTION_VIEW, uri1);
        startActivity(intent1);
    }
    //数据监听服务，监听评论回复表，有数据更新则显示红点

    }