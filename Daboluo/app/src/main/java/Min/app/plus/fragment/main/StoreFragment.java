package Min.app.plus.fragment.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import Min.app.plus.R;
import Min.app.plus.adapter.StoreAdapter;
import Min.app.plus.bmob.Reply;
import Min.app.plus.bmob.Store;
import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import es.dmoral.toasty.Toasty;

public class StoreFragment extends Fragment {

    private View view;

    private RecyclerView storeRecyclerView;//recycler
    private RecyclerView.LayoutManager storelayoutManager;//recycler布局管理器
    private SmartRefreshLayout store_smart;//下拉刷新
    private StoreAdapter adapter;//适配器
    private List<Store> storeList = new ArrayList<>();
    private String user_id,order_author_id,order_id;
    private Boolean likes=false;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            gethuser();
            getstore();
        }};
    private int intuser=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null){
            view=inflater.inflate(R.layout.storefragment, container, false);
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

        storeRecyclerView=getActivity().findViewById(R.id.store_recycler_view);
        //正常列表
        storelayoutManager = new LinearLayoutManager(getActivity());
        //瀑布流
        //storelayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        //设置布局管理器
        storeRecyclerView.setLayoutManager(storelayoutManager);
        //定义适配器



        store_smart=getActivity().findViewById(R.id.store_smart);
        //监听下拉和上拉状态
        //下拉刷新
        store_smart.setOnRefreshListener(refreshlayout -> {
            store_smart.setEnableRefresh(true);//启用刷新
            getstore();
            store_smart.finishRefresh();//结束刷新
        });
        //上拉加载

    }
    public void gethuser() {
        BmobQuery<_User> bq1 = new BmobQuery<_User>();
        bq1.getObject((String) BmobUser.getObjectByKey("objectId"), new QueryListener<_User>() {
            @Override
            public void done(_User object, BmobException e) {
                if (e == null) {
                    user_id = object.getObjectId();
                    intuser = 1;
                }
            }
        });

    }
    public void getstore() {

        BmobQuery<Store> eq1 = new BmobQuery<Store>();
        eq1.addWhereEqualTo("audit_state", true);//查询审核通过的
        BmobQuery<Store> eq2 = new BmobQuery<Store>();
        eq2.addWhereEqualTo("state",true);//查询在营营业的
        List<BmobQuery<Store>> andQuerys = new ArrayList<BmobQuery<Store>>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);
        BmobQuery<Store> query = new BmobQuery<Store>();
        query.and(andQuerys);

        query.order("-createdAt");//依照数据排序时间排序
        query.setLimit(100);
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);// 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        query.findObjects(new FindListener<Store>() {
            @Override
            public void done(List<Store> object, BmobException e) {

                if (e == null) {
                    storeList.clear();
                    adapter = new StoreAdapter(storeList);
                    for (int i = 0; i < object.size(); i++) {
                        storeList.add(object.get(i));
                        adapter.notifyDataSetChanged(); // 通知适配器数据集发生变化
                    }
                    storeRecyclerView.setAdapter(adapter);
                    // home_swip.setRefreshing(false);
                } else {
                    Toasty.error(getActivity(), "数据加载失败！", Toast.LENGTH_SHORT,true).show();
                    //home_swip.setRefreshing(false);

                }

            }
        });


    }}