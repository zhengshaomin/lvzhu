package Min.app.plus;

import static Min.app.plus.utils.DialogUtils.dialogtextviewshow;

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
import android.widget.ListView;
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
import Min.app.plus.bmob.Store;
import Min.app.plus.utils.GlideActivity;
import Min.app.plus.utils.mListView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import es.dmoral.toasty.Toasty;

//店铺页
public class StoreActivity extends AppCompatActivity {

    List<Goods> mPostlist = new ArrayList<Goods>();
    private Toolbar toolbar;
    private mListView store_listview;
    //店铺资系列
    private ImageView store_icon;
    private TextView store_name,store_information,service_scope;
    private String store_id=null,store_manager_user_id;
    private AlertDialog dialog;
    private FloatingActionButton floatingActionButton;
    //转到修改数据页

    //购物系列
    private ImageView view_shopping_car;
    private TextView view_goods_price,shopping_goods_quantity;
    private Button view_pay;
    private ListView shopping_cart_list;
    private int boluo_total;//商品总价
    //购物车系列
    private ArrayList<String> shopping_car_goods_objectid = new ArrayList<>();//购物车商品id
    private ArrayList<String> shopping_car_goods_name = new ArrayList<>();//购物车商品名字
    private ArrayList<String> shopping_car_goods_icon = new ArrayList<>();//购物车商品图标
    private ArrayList<String> shopping_car_goods_information = new ArrayList<>();//购物车商品信息
    private ArrayList<String> shopping_car_goods_price = new ArrayList<>();//购物车商品价格
    private ArrayList<String> shopping_cart = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store);

        Intent intent = getIntent();
        store_id = intent.getStringExtra("store_id");

        store_listview = findViewById(R.id.store_listview);

        store_icon = findViewById(R.id.store_icon);
        store_name = findViewById(R.id.store_name);
        store_information = findViewById(R.id.store_information);
        service_scope=findViewById(R.id.service_scope);


        view_shopping_car=findViewById(R.id.view_shopping_car);
        view_goods_price=findViewById(R.id.view_goods_price);
        shopping_goods_quantity=findViewById(R.id.shopping_goods_quantity);
        view_pay=findViewById(R.id.view_pay);


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

        view_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreActivity.this, PayActivity.class);
                intent.putStringArrayListExtra("shopping_car_goods_objectid",shopping_car_goods_objectid);
                intent.putStringArrayListExtra("shopping_car_goods_name",shopping_car_goods_name);
                intent.putStringArrayListExtra("shopping_car_goods_icon",shopping_car_goods_icon);
                intent.putStringArrayListExtra("shopping_car_goods_information",shopping_car_goods_information);
                intent.putStringArrayListExtra("shopping_car_goods_price",shopping_car_goods_price);
                intent.putExtra("store_id",store_id);//d店铺ID
                intent.putExtra("store_manager_user_id",store_manager_user_id);
                intent.putExtra("boluo_total",boluo_total);//菠萝币总数
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
        view_shopping_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_shopping_cart_list();
            }
        });
        get_user_store();
    }public void get_user_store(){
        BmobQuery<Store> bmobQuery = new BmobQuery<>();
        bmobQuery.include("manager");
        bmobQuery.getObject(store_id, new QueryListener<Store>() {
            @Override
            public void done(Store object, BmobException e) {
                if (e == null) {
                        store_manager_user_id=object.getManager().getObjectId();
                        store_id=object.getObjectId();
                        store_name.setText(object.getName());
                        Glide.with(StoreActivity.this).load(object.getIcon().getUrl()).crossFade(800).transform(new GlideActivity(StoreActivity.this)).into(store_icon);
                        store_information.setText(object.getInformation());
                        getstore_goods();
                        service_scope.setText("服务范围"+object.getService_scope());
                        if(object.isNotice_state()==true){
                            dialogtextviewshow(StoreActivity.this,"通知",object.getNotice_content());
                        }
                } else {
                }

            }
        });

//查询该店铺内的所有商品
    }public void getstore_goods() {

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
                    store_listview.setAdapter(new StoreActivity.ItemListAdapter());
//                    // home_swip.setRefreshing(false);
                } else {
                    //Snackbar.make(view, "数据加载失败", Snackbar.LENGTH_SHORT).show();
                    //home_swip.setRefreshing(false);

                }

            }
        });

    }
    public void dialog_shopping_cart_list() {
        View view = LayoutInflater.from(StoreActivity.this).inflate(R.layout.dialog_shopping_cart_listview, null, false);
        AlertDialog dialog = new AlertDialog.Builder(StoreActivity.this).setView(view).create();
        shopping_cart_list = view.findViewById(R.id.shopping_cart_list);
        shopping_cart_list.setAdapter(new StoreActivity.ShoppingAdapter());
        dialog.setView(view); // 自定义dialog
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

    }
    class ItemListAdapter extends BaseAdapter {
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
            final StoreActivity.ItemListAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder =new StoreActivity.ItemListAdapter.ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_goods, null);
                viewHolder.linearLayout=convertView.findViewById(R.id.linearLayout);
                viewHolder.good_name=convertView.findViewById(R.id.goods_name);
                viewHolder.goods_infomation=convertView.findViewById(R.id.goods_information);
                viewHolder.goods_stock=convertView.findViewById(R.id.goods_stock);
                viewHolder.goods_price=convertView.findViewById(R.id.goods_price);
                viewHolder.goods_icon=convertView.findViewById(R.id.goods_icon);
                viewHolder.more_menu=convertView.findViewById(R.id.more_menu);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (StoreActivity.ItemListAdapter.ViewHolder)convertView.getTag();
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
            Glide.with(StoreActivity.this).load(goods.getIcon().getUrl()).crossFade(800).into(viewHolder.goods_icon);

            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mPostlist.get(position).isState()){
                        boluo_total=boluo_total+mPostlist.get(position).getPrice();
                        view_goods_price.setText(""+boluo_total);
                        shopping_car_goods_objectid.add(mPostlist.get(position).getObjectId());
                        shopping_car_goods_name.add(mPostlist.get(position).getName());
                        shopping_car_goods_information.add(mPostlist.get(position).getInformation());
                        shopping_car_goods_icon.add(mPostlist.get(position).getIcon().getUrl());
                        shopping_car_goods_price.add(mPostlist.get(position).getPrice()+"");
                        shopping_goods_quantity.setText(shopping_car_goods_objectid.size()+"");
                    }else {
                        Toasty.warning(StoreActivity.this, "该商品处于缺货状态", Toast.LENGTH_SHORT,true).show();
                    }

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

//////////////////////////////////////////////
    }
    //购物车
    class ShoppingAdapter extends BaseAdapter {
        //private Chat fenxiang;
        //适配器
        @Override
        public int getCount()
        {
            if (shopping_car_goods_objectid.size() > 0)
            {
                return shopping_car_goods_objectid.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position)
        {
            return shopping_car_goods_objectid.get(position);
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final StoreActivity.ShoppingAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder =new StoreActivity.ShoppingAdapter.ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_shoppingcar_goods, null);
                viewHolder.linearLayout=convertView.findViewById(R.id.linearLayout);
                viewHolder.good_name=convertView.findViewById(R.id.goods_name);
                viewHolder.goods_infomation=convertView.findViewById(R.id.goods_information);
                viewHolder.goods_stock=convertView.findViewById(R.id.goods_stock);
                viewHolder.goods_price=convertView.findViewById(R.id.goods_price);
                viewHolder.goods_icon=convertView.findViewById(R.id.goods_icon);
                viewHolder.goods_delete=convertView.findViewById(R.id.goods_delete);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (StoreActivity.ShoppingAdapter.ViewHolder)convertView.getTag();
            }
            viewHolder.good_name.setText(shopping_car_goods_name.get(position));
            viewHolder.goods_infomation.setText(shopping_car_goods_information.get(position));
//            viewHolder.goods_stock.setText("库存 "+goods.getStock());
            viewHolder.goods_price.setText(""+shopping_car_goods_price.get(position));
            Glide.with(StoreActivity.this).load(shopping_car_goods_icon.get(position)).crossFade(800).into(viewHolder.goods_icon);

            viewHolder.goods_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boluo_total=boluo_total-Integer.parseInt(shopping_car_goods_price.get(position));
                    view_goods_price.setText(""+boluo_total);
                    shopping_car_goods_name.remove(position);
                    shopping_car_goods_information.remove(position);
                    shopping_car_goods_icon.remove(position);
                    shopping_car_goods_price.remove(position);
                    shopping_car_goods_objectid.remove(position);
                    shopping_cart_list.setAdapter(new StoreActivity.ShoppingAdapter());
                    shopping_goods_quantity.setText(shopping_car_goods_objectid.size()+"");
                }
            });


            return convertView;
        }
        public class ViewHolder
        {
            public TextView good_name,goods_infomation,goods_stock,goods_price;
            public LinearLayout linearLayout;
            public ImageView goods_icon,goods_delete;

        }
    }}