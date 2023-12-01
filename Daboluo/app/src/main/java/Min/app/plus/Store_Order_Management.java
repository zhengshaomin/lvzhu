package Min.app.plus;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import Min.app.plus.fragment.storeorderstatus.Store_Order_Completed;
import Min.app.plus.fragment.storeorderstatus.Store_Order_Other;
import Min.app.plus.fragment.storeorderstatus.Store_Order_Processed;

public class Store_Order_Management extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tablayout;
    private ViewPager viewpager;
    Fragment[] fragments = {new Store_Order_Processed(),new Store_Order_Completed(),new Store_Order_Other()};
    String[] titles = {"待处理","已完成","售后"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ordermanager);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("订单管理");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tablayout=findViewById(R.id.tablayout);
        viewpager=findViewById(R.id.viewpager);


        viewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
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
        tablayout.setupWithViewPager(viewpager,false);


    }
}
