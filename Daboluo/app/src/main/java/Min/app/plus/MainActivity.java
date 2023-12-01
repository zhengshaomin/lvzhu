package Min.app.plus;

import static Min.app.plus.utils.QueryBasisInfoUtils.getstoreinfo;
import static Min.app.plus.utils.QueryBasisInfoUtils.getuserinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import Min.app.plus.bmob.Reply;
import Min.app.plus.bmob._User;
import Min.app.plus.fragment.main.ChatFragment;
import Min.app.plus.fragment.main.HomeFragment;
import Min.app.plus.fragment.main.MineFragment;
import Min.app.plus.fragment.main.StoreFragment;
import Min.app.plus.utils.MyEvent;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class MainActivity extends AppCompatActivity {



    private String TAG="MainActivity";
    private BottomNavigationView bottom;
    private TextView username,signature;
    private int intuser=0,error=0;
    private String id,post_id,post_user_id,api_visible;
    private boolean like=false;
    private String  puserid;


    private int a,min;

    private String ntitle,ncontent,ndownload,ncancel,nurl;

    private HomeFragment mHomeFragment;//主页
    private ChatFragment mChatFragment;//消息页面
    private StoreFragment mStoreFragment;//店铺页面
//    private NewsFragment mNewsFragment;
    //private TradeFragment mTradeFragment;
    private MineFragment mMineFragment;//个人主页
    //private MineFragment mMineFragment;
    private Fragment[] mFragmentContainer;
    // 用于标记最后一个fragment的标签
    public int mLastFragmentTag;
    private TextView prompt_quantity;


    private JSONObject data;
    private Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        initView();
        loadData();
        getuserinfo();//查询登陆信息
        getstoreinfo();//查询店铺信息
        registerListener();
        newreply_quantity();
    }};

    private AlertDialog dialog;

    private PtrFrameLayout ptrFrameLayout;
    private TextView header;
    private static MainActivity instance;

    public static MainActivity getInstance() {
        if (instance == null) {
            instance = new MainActivity();
        }
        return instance;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        }.start();
        //查询用户数据



    }
    public void newreply_quantity(){

        BmobQuery<Reply> eq1 = new BmobQuery<Reply>();
        eq1.addWhereEqualTo("msg_recipient", BmobUser.getCurrentUser(_User.class));//查询接收者是我的回复
        BmobQuery<Reply> eq2 = new BmobQuery<Reply>();
        eq1.addWhereEqualTo("news",true);//查询可见的
        List<BmobQuery<Reply>> andQuerys = new ArrayList<BmobQuery<Reply>>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);


        BmobQuery<Reply> query = new BmobQuery<Reply>();
        query.and(andQuerys);
        query.include("author");
        query.order("-createdAt");//依照数据排序时间排序
        query.setLimit(100);
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);// 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        query.count(Reply.class, new CountListener() {
            @Override
            public void done(Integer count, BmobException e) {
                if(e==null){
                    prompt_quantity.setVisibility(View.VISIBLE);
                }else{
                }
            }
        });


    }
    private void choseFragment(int lastFragmentTag, int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // 隐藏上一个Fragment
        transaction.hide(mFragmentContainer[lastFragmentTag]);
        // 如果新的Fragment的对象还在容器中，就不需要去new对象了，直接显示就好
        if (!mFragmentContainer[index].isAdded()) {
            transaction.add(R.id.frame,mFragmentContainer[index]);
        }
        // commitNowAllowingStateLoss()允许在保存活动状态后执行提交
        transaction.show(mFragmentContainer[index]).commitNowAllowingStateLoss();

    }
//申明id
    private void initView() {
        bottom = findViewById(R.id.bottom);
//获取整个的NavigationView
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottom.getChildAt(0);
//这里就是获取所添加的每一个Tab(或者叫menu)，
        View tab = menuView.getChildAt(2);

        BottomNavigationItemView itemView = (BottomNavigationItemView) tab;
//加载我们的角标View，新创建的一个布局
        View badge = LayoutInflater.from(this).inflate(R.layout.badge, menuView, false);
//添加到Tab上
        itemView.addView(badge);
        prompt_quantity = badge.findViewById(R.id.prompt_quantity);
//无消息时可以将它隐藏即可
        //prompt_quantity.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_loading, null, false);
        dialog = new AlertDialog.Builder(MainActivity.this).setView(view).create();
        dialog.setCancelable(false);

    }

    /**
     * 加载数据
     */
    private void loadData(){
        mHomeFragment = new HomeFragment();
        mStoreFragment=new StoreFragment();
        //mOrderFragment = new OrderFragment();
        mChatFragment = new ChatFragment();
        // mTradeFragment = new TradeFragment();
        mMineFragment = new MineFragment();
        // 将初始化后的Fragment添加到容器中
        mFragmentContainer = new Fragment[]{mHomeFragment, mStoreFragment, mChatFragment, mMineFragment};

        // 将进入应用默认显示的Fragment对应的tag值设置为0
        mLastFragmentTag = 0;
        // 默认显示为HomeFragment
        // 获取Fragment管理器并开始操作
        // 替换（第一个参数为容器【remove】，第二个参数为要放在容器中新的Fragment【add】）
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, mHomeFragment)
                .show(mHomeFragment)
                .commit();
}
    /**
     * 注册监听
     */
    private void registerListener() {
        bottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_homepage:
                        if (mLastFragmentTag != 0) {
                            choseFragment(mLastFragmentTag, 0);
                            mLastFragmentTag = 0;
                        }
                        // 监听事件中“return true”
                        // 表示这个按钮的监听事件在这个方法中已经处理完成了，不需要别人再去处理了
                        return true;

                    case R.id.menu_order:
                        if (mLastFragmentTag != 1) {
                            choseFragment(mLastFragmentTag, 1);
                            mLastFragmentTag = 1;
                        }
                        return true;
                    case R.id.menu_prompt:
                        if (mLastFragmentTag != 2) {
                            choseFragment(mLastFragmentTag, 2);
                            mLastFragmentTag = 2;
                            prompt_quantity.setVisibility(View.GONE);
                        }
                        return true;
                    case R.id.menu_mine:
                        if (mLastFragmentTag != 3) {
                            choseFragment(mLastFragmentTag, 3);
                            mLastFragmentTag = 3;
                        }
                        return true;
                }
                return false;
            }
        });

}

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sssd(MyEvent event) {
        String type=event.getType();
        String message = event.getMessage();
        // 在这里处理接收到的消息
        Log.d(TAG,"MainActivity到消息");
        //Toast.makeText(MainActivity.this, "接受到消息", Toast.LENGTH_SHORT).show();
        if(type.equals("Reply")){
            Log.d(TAG,"MainActivity到消息diaouyo");
//            notificationmanager(MainActivity.this,message);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}