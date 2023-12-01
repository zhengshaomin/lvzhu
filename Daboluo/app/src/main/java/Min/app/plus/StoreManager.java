package Min.app.plus;

import static Min.app.plus.utils.DialogUtils.dialogloadingdismiss;
import static Min.app.plus.utils.DialogUtils.dialogloadingshow;
import static Min.app.plus.utils.QueryBasisInfoUtils.storeobjectid;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import Min.app.plus.bmob.Goods;
import Min.app.plus.bmob.Notice;
import Min.app.plus.bmob.Store;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.GlideActivity;
import Min.app.plus.utils.mListView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;

//我的店铺管理页
public class StoreManager extends AppCompatActivity {

    List<Goods> mPostlist = new ArrayList<Goods>();
    private Toolbar toolbar;
    private mListView store_listview;
    private ImageView store_icon;
    private TextView store_name,store_information,service_scope;
    private String store_id=null;
    private AlertDialog dialog;
    private FloatingActionButton floatingActionButton;
    //转到修改数据页
    private String goods_id,goods_name,goods_information,goods_iconurl;
    private int goods_price;
    private boolean goods_state;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storemanager);

        store_listview = findViewById(R.id.store_listview);

        store_icon = findViewById(R.id.store_icon);
        store_name = findViewById(R.id.store_name);
        store_information = findViewById(R.id.store_information);
        service_scope=findViewById(R.id.service_scope);

        floatingActionButton=findViewById(R.id.floatingActionButton);

        dialogloadingshow(StoreManager.this);//显示加载弹窗

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("店铺详情");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(StoreManager.this,AddGoods.class);
                intent.putExtra("store_id", store_id);
                startActivity(intent);

            }
        });
        store_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(StoreManager.this, AlterStore.class);
                intent.putExtra("store_id", store_id);
                startActivity(intent);
            }
        });
        store_listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean enable = false;
                if (store_listview != null && store_listview.getChildCount() > 0) {
                    // 检查listView第一个item是否可见
                    boolean firstItemVisible = store_listview.getFirstVisiblePosition() == 0;
                    // 检查第一个item的顶部是否可见
                    boolean topOfFirstItemVisible = store_listview.getChildAt(0).getTop() == 0;
                    // 启用或者禁用SwipeRefreshLayout刷新标识
                    enable = firstItemVisible && topOfFirstItemVisible;
                } else if (store_listview != null && store_listview.getChildCount() == 0) {
                    // 没有数据的时候允许刷新
                    enable = true;
                }
                // 把标识传给swipeRefreshLayout
                //home_swip.setEnabled(enable);
            }
        });
        get_user_store();
    }
    public void get_user_store(){
        BmobQuery<Store> bmobQuery = new BmobQuery<Store>();
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
        bmobQuery.getObject(storeobjectid, new QueryListener<Store>() {
            @Override//ezEaJJJK
            public void done(Store object, BmobException e) {
                dialogloadingdismiss();//关闭加载弹窗
                if (e == null) {
                    store_id=object.getObjectId();
                    store_name.setText(object.getName());
                    Glide.with(StoreManager.this).load(object.getIcon().getUrl()).crossFade(800).transform(new GlideActivity(StoreManager.this)).into(store_icon);
                    store_information.setText(object.getInformation());
                    getstore_goods();
                    service_scope.setText("服务范围"+object.getService_scope());
                }else {
                    Toasty.error(StoreManager.this,"出现错误啦", Toast.LENGTH_SHORT,true).show();
                }

            }
        });

    }
    public void getstore_goods() {

        BmobQuery<Goods> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("store", store_id);//查询店铺下的商品
        bmobQuery.order("-createdAt");//依照数据排序时间排序
        bmobQuery.setLimit(500);
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
        bmobQuery.findObjects(new FindListener<Goods>() {
            @Override
            public void done(List<Goods> object, BmobException e) {

                if (e == null) {
                    mPostlist.clear();
                    for (int i = 0; i < object.size(); i++) {
                        mPostlist.add(object.get(i));
                    }
                    store_listview.setAdapter(new StoreManager.ItemListAdapter());
//                    // home_swip.setRefreshing(false);
                } else {
                    //Snackbar.make(view, "数据加载失败", Snackbar.LENGTH_SHORT).show();
                    //home_swip.setRefreshing(false);

                }

            }
        });
    }
    public void menu_dialog() {
        View view = LayoutInflater.from(StoreManager.this).inflate(R.layout.goods_menu, null, false);
        AlertDialog dialog = new AlertDialog.Builder(StoreManager.this).setView(view).create();
        Button alter_goods = view.findViewById(R.id.alter_goods);
        Button delete_goods = view.findViewById(R.id.delete_goods);
        Button cancel = view.findViewById(R.id.cancel);
//        ImageView img = (ImageView) view.findViewById(R.id.large_image);
//        Glide.with(PostActivity.this).load(PHOTOURL).into(img);
        dialog.setView(view); // 自定义dialog
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        alter_goods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                //修改商品信息
                Intent intent = new Intent(StoreManager.this, AlterGoods.class);
                intent.putExtra("goods_id", goods_id);
                intent.putExtra("goods_name", goods_name);
                intent.putExtra("goods_information", goods_information);
                intent.putExtra("goods_iconurl", goods_iconurl);
                intent.putExtra("goods_price", goods_price);
                intent.putExtra("goods_state", goods_state);
                startActivity(intent);

            }
        });
        delete_goods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                setDialog();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }
    public void setDialog(){
        View view = LayoutInflater.from(StoreManager.this).inflate(R.layout.dialogdeletegoods, null, false);
        AlertDialog dialog = new AlertDialog.Builder(StoreManager.this).setView(view).create();
        TextView content = view.findViewById(R.id.content);
        TextView promise = view.findViewById(R.id.promise);
        TextView cancel = view.findViewById(R.id.cancel);
        dialog.setView(view); // 自定义dialog
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        content.setText("删除商品后将无法直接恢复，是否继续？");
        promise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //确定
                dialog.cancel();

                dialogloadingshow(StoreManager.this);
                deletegoods(goods_id);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //取消
                dialog.cancel();
            }
        });
    }
    private void deletegoods(String goods_id){
        Goods p2 = new Goods();
        p2.setObjectId(goods_id);
        p2.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                dialogloadingdismiss();
                if(e==null){
                    getstore_goods();
                    Toasty.success(StoreManager.this,"操作成功", Toast.LENGTH_SHORT,true).show();
                }else{
                    Toasty.error(StoreManager.this,"删除失败", Toast.LENGTH_SHORT,true).show();
                }
            }

        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_store_manager, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.alter_store:

                Intent intent = new Intent(StoreManager.this, Store_Order_Management.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);

    }
    class ItemListAdapter extends BaseAdapter
    {
        private Goods goods;
        //适配器
        @Override
        public int getCount()
        {
            if (mPostlist.size() > 0)
            {
                return mPostlist.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position)
        {
            return mPostlist.get(position);
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final StoreManager.ItemListAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder =new StoreManager.ItemListAdapter.ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_store_goods, null);
                viewHolder.linearLayout=convertView.findViewById(R.id.linearLayout);
                viewHolder.good_name=convertView.findViewById(R.id.goods_name);
                viewHolder.goods_infomation=convertView.findViewById(R.id.goods_information);
                viewHolder.goods_stock=convertView.findViewById(R.id.goods_stock);
                viewHolder.goods_price=convertView.findViewById(R.id.goods_price);
                viewHolder.goods_icon=convertView.findViewById(R.id.goods_icon);
                viewHolder.more_menu=convertView.findViewById(R.id.more_menu);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (StoreManager.ItemListAdapter.ViewHolder)convertView.getTag();
            }
            goods=mPostlist.get(position);
            viewHolder.good_name.setText(goods.getName());
            viewHolder.goods_infomation.setText(goods.getInformation());
            if(mPostlist.get(position).isState()){
                viewHolder.goods_stock.setText("有货");
            }else {
                viewHolder.goods_stock.setText("缺货");
            }
            viewHolder.goods_price.setText(""+goods.getPrice());
            Glide.with(StoreManager.this).load(goods.getIcon().getUrl()).crossFade(800).into(viewHolder.goods_icon);

            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    goods_id=mPostlist.get(position).getObjectId();
                    menu_dialog();
                    goods_name=mPostlist.get(position).getName();
                    goods_information=mPostlist.get(position).getInformation();
                    goods_iconurl=mPostlist.get(position).getIcon().getUrl();
                    goods_price=mPostlist.get(position).getPrice();
                    goods_state=mPostlist.get(position).isState();

                }
            });


            return convertView;
        }
        public class ViewHolder
        {
            public TextView good_name,goods_infomation,goods_stock,goods_price;
            public LinearLayout linearLayout;
            public ImageView goods_icon,more_menu;

        }
    }}
