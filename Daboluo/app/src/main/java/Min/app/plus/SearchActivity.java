package Min.app.plus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import Min.app.plus.bmob._User;
import Min.app.plus.utils.GlideActivity;
import Min.app.plus.utils.mListView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import es.dmoral.toasty.Toasty;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String user_id;
    private EditText usernme;
    private TextView search;
    private mListView list;
    List<_User> users = new ArrayList<_User>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("查找用户");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        usernme=findViewById(R.id.username);
        search=findViewById(R.id.search);
        list=findViewById(R.id.list);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(usernme.getText().toString().length()>1){
                    getuser();
                }else {
                    Toasty.warning(SearchActivity.this, "请输入至少一个字符！", Toast.LENGTH_SHORT,true).show();
                }
            }
        });
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean enable = false;
                if (list != null && list.getChildCount() > 0) {
                    // 检查listView第一个item是否可见
                    boolean firstItemVisible = list.getFirstVisiblePosition() == 0;
                    // 检查第一个item的顶部是否可见
                    boolean topOfFirstItemVisible = list.getChildAt(0).getTop() == 0;
                    // 启用或者禁用SwipeRefreshLayout刷新标识
                    enable = firstItemVisible && topOfFirstItemVisible;
                } else if (list != null && list.getChildCount() == 0) {
                    // 没有数据的时候允许刷新
                    enable = true;
                }
                // 把标识传给swipeRefreshLayout

            }
        });


    }
    public void getuser() {
        BmobQuery<_User> eq1=new BmobQuery<>();
        eq1.addWhereEqualTo("username",usernme.getText().toString());//查询已审核
        BmobQuery<_User> eq2=new BmobQuery<>();
        eq2.addWhereEqualTo("mobilePhoneNumber",usernme.getText().toString());//查询已审核
        List<BmobQuery<_User>> andQuerys = new ArrayList<BmobQuery<_User>>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);

        BmobQuery<_User> bmobQuery = new BmobQuery<>();
        bmobQuery.order("-createdAt");//依照数据排序时间排序
        bmobQuery.or(andQuerys);
        bmobQuery.findObjects(new FindListener<_User>() {
            @Override
            public void done(List<_User> object, BmobException e) {
                if (e == null) {
                    users.clear();
                    for (int i = 0; i < object.size(); i++) {
                        users.add(object.get(i));
                    }
                    list.setAdapter(new SearchActivity.ItemListAdaptert());
                } else {
                    Toasty.error(SearchActivity.this, "数据加载失败！", Toast.LENGTH_SHORT,true).show();
                }
            }
        });

    }
    class ItemListAdaptert extends BaseAdapter
    {
        private _User fenxiang;
        //适配器
        @Override
        public int getCount()
        {
            if (users.size() > 0)
            {
                return users.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position)
        {
            return users.get(position);
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final SearchActivity.ItemListAdaptert.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new SearchActivity.ItemListAdaptert.ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_ship, null);
                viewHolder.linearlayout =  convertView.findViewById(R.id.linearlayout);
                viewHolder.headportrait=convertView.findViewById(R.id.headportrait);
                viewHolder.username=convertView.findViewById(R.id.username);
                viewHolder.signature=convertView.findViewById(R.id.signature);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (SearchActivity.ItemListAdaptert.ViewHolder) convertView.getTag();
            }
            fenxiang = users.get(position);
            Glide.with(SearchActivity.this).load("https://q.qlogo.cn/headimg_dl?dst_uin=" +fenxiang.getQq() + "&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(SearchActivity.this)).into(viewHolder.headportrait);
            viewHolder.username.setText(fenxiang.getUsername());
            viewHolder.signature.setText(fenxiang.getSignature());
            viewHolder.linearlayout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1)
                {

                    user_id=users.get(position).getObjectId();
                    if(BmobUser.getCurrentUser(_User.class).getObjectId().equals(user_id)){
                        Intent intent = new Intent(SearchActivity.this, MuserActivity.class);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(SearchActivity.this, UserActivity.class);
                        intent.putExtra("user_id",user_id);
                        startActivity(intent);
                    }
                    // TODO: Implement this method
                }
            });
            return convertView;
        }
        public class ViewHolder
        {
            public TextView username,signature;
            public LinearLayout linearlayout;
            public ImageView headportrait;

        }
    }}
