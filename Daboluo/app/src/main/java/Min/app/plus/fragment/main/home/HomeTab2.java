package Min.app.plus.fragment.main.home;

import static Min.app.plus.utils.DialogUtils.dialoagnoticeshow;
import static Min.app.plus.utils.QueryBasisInfoUtils.userobjectid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import java.util.Arrays;
import java.util.List;

import Min.app.plus.FollowActivity;
import Min.app.plus.R;
import Min.app.plus.StartActivity;
import Min.app.plus.adapter.HomeTabOneAdapter;
import Min.app.plus.adapter.HomeTabTwoAdapter;
import Min.app.plus.bmob.Posts;
import Min.app.plus.bmob.Reply;
import Min.app.plus.bmob.Urelation;
import Min.app.plus.bmob._User;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import es.dmoral.toasty.Toasty;

/**
 * 作者：daboluo on 2023/9/20 13:41
 * Email:daboluo719@gmail.com
 */
public class HomeTab2 extends Fragment {

    private View view;
    private Uri uri1;
    private Intent intent1;
//    private Banner banner;

    private String TAG = "StartActivity";
    private RecyclerView home_tab2_recycler;
    private LinearLayoutManager layoutManager2;
    private HomeTabTwoAdapter adapter;
    private List<Posts> postList2 = new ArrayList<>();
    private ArrayList<String> followid = new ArrayList<String>();
    private Boolean likes = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getfollow();

        }
    };

    private SmartRefreshLayout home2_smart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.home_tab2, container, false);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        }.start();
//        banner=getActivity().findViewById(R.id.banner);

        home_tab2_recycler=getActivity().findViewById(R.id.home2_recycler_view);
        layoutManager2 = new LinearLayoutManager(getActivity());

        adapter = new HomeTabTwoAdapter(postList2 = getData());
        home_tab2_recycler.setLayoutManager(layoutManager2);



        home2_smart=getActivity().findViewById(R.id.home_smart);
        //监听下拉和上拉状态
        //下拉刷新
        home2_smart.setOnRefreshListener(refreshlayout -> {
            home2_smart.setEnableRefresh(true);//启用刷新
            getpost();
            home2_smart.finishRefresh();//结束刷新
        });
        //上拉加载

    }

    private List<Posts> getData() {
        List<Posts> list = new ArrayList<>();
        return list;


    }
    private void getfollow(){
        BmobQuery<Urelation> bmobQuery = new BmobQuery<>();
        bmobQuery.include("author,object");
        bmobQuery.addWhereEqualTo("author",userobjectid);//查询用户发布的贴子
        bmobQuery.order("-createdAt");//依照数据排序时间排序
        bmobQuery.setLimit(500);
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
        bmobQuery.findObjects(new FindListener<Urelation>() {
            @Override
            public void done(List<Urelation> object, BmobException e) {
                if (e == null) {

                    for (int i = 0; i < object.size(); i++) {
                        followid.add(object.get(i).getObject().getObjectId());
                    }
                    getpost();
                } else {
                    Toasty.error(getActivity(), "数据加载失败！", Toast.LENGTH_SHORT,true).show();
                }

            }
        });
    }
    public void getpost() {

        BmobQuery<Posts> eq1 = new BmobQuery<>();
        eq1.addWhereEqualTo("visible",true);//查询可见的
        BmobQuery<_User> innerQuery = new BmobQuery<_User>();
        //String[] friendIds={"ssss","aaaa"};//好友的objectId数组
        innerQuery.addWhereContainedIn("objectId", followid);
        BmobQuery<Posts> eq2 = new BmobQuery<>();
        eq2.addWhereMatchesQuery("author", "_User", innerQuery);//查询关注的

        List<BmobQuery<Posts>> andQuerys = new ArrayList<BmobQuery<Posts>>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);


        BmobQuery<Posts> bmobQuery = new BmobQuery<>();
        bmobQuery.and(andQuerys);
        bmobQuery.include("author");
        bmobQuery.order("-createdAt");//依照数据排序时间排序
        bmobQuery.setLimit(100);
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);// 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        bmobQuery.findObjects(new FindListener<Posts>() {
            @Override
            public void done(List<Posts> object, BmobException e) {
                Log.d(TAG,"李奇成数量"+object.size());
                if (e == null) {
//                    Posts bean = new Posts();
                    postList2.clear();
                    Log.d(TAG,"李奇成数量"+object.size());
                    for (int i = 0; i < object.size(); i++) {
                        postList2.add(object.get(i));
                        // 默认都给他们赋值当前都没有点赞
//                        bean.setZanFocus(false);
                    }
                    home_tab2_recycler.setAdapter(adapter);
                    // home_swip.setRefreshing(false);
                } else {
                    Toasty.error(getActivity(), "数据加载失败！"+e, Toast.LENGTH_SHORT,true).show();
                    //home_swip.setRefreshing(false);

                }

            }
        });

    }
    //3秒后执行
    private void time(){
        CountDownTimer timer=new CountDownTimer(3000, 10)
        {
            public void onTick(long millisUntilFinished)
            {
                //tv_time.setText(millisUntilFinished/1000+"秒");
            }
            public void onFinish()
            {
                getpost();
            }
        };
        timer.start();
    }


}