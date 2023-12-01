package Min.app.plus.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import Min.app.plus.AddActivity;
import Min.app.plus.R;
import Min.app.plus.ReplyActivity;
import Min.app.plus.bmob.Reply;
import Min.app.plus.bmob._User;
import Min.app.plus.fragment.main.home.HomeTab1;
import Min.app.plus.fragment.main.home.HomeTab2;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;

/**
 * @author daboluo
 */
public class HomeFragment extends Fragment {

    private View view;
    private TabLayout tablayout;
    private ViewPager viewpager;
    Fragment[] fragments = {new HomeTab1(),new HomeTab2()};
    String[] titles = {"全部","关注"};
    private TextView news_reply;
    private ImageView notifications,adds;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null){
            view=inflater.inflate(R.layout.home, container, false);
        }
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tablayout = getActivity().findViewById(R.id.tablayout);
        viewpager = getActivity().findViewById(R.id.viewpager);

        news_reply = getActivity().findViewById(R.id.news_reply);
        adds = getActivity().findViewById(R.id.adds);
        notifications = getActivity().findViewById(R.id.notifications);




        adds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                startActivity(intent);
            }
        });

        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                news_reply.setVisibility(View.GONE);
                Intent intent = new Intent(getActivity(), ReplyActivity.class);
                startActivity(intent);

            }
        });

        viewpager.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });
        tablayout.setupWithViewPager(viewpager, false);

        newreply_quantity();

    }

        public void newreply_quantity(){

            BmobQuery<Reply> eq1 = new BmobQuery<Reply>();
            eq1.addWhereEqualTo("recipient", BmobUser.getCurrentUser(_User.class));//查询接收者是我的回复
            BmobQuery<Reply> eq2 = new BmobQuery<Reply>();
            eq2.addWhereEqualTo("news",true);//查询可见的
            List<BmobQuery<Reply>> andQuerys = new ArrayList<BmobQuery<Reply>>();
            andQuerys.add(eq1);
            andQuerys.add(eq2);


            BmobQuery<Reply> query = new BmobQuery<Reply>();
            query.and(andQuerys);

//        boolean isCache = query.hasCachedResult(Reply.class);
//        if(isCache){
//            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);   // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
//        }else{
//            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);// 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
//            query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存2天
//        }
            query.count(Reply.class, new CountListener() {
                @Override
                public void done(Integer count, BmobException e) {
                    if(e==null){
                        if (count>=1){
                            news_reply.setVisibility(View.VISIBLE);
                        }
                    }else{
                    }
                }
            });
        }
    }