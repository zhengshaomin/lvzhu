package Min.app.plus.fragment.orderstatus;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import Min.app.plus.utils.GlideActivity;
import Min.app.plus.StoreActivity;
import Min.app.plus.bmob.Order;
import Min.app.plus.OrderActivity2;
import Min.app.plus.R;
import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import es.dmoral.toasty.Toasty;

public class Order_Other extends Fragment {

    /**
     * 个人订单其他页面
     * */

    private View view;
    private ListView order_other_listview;
    List<Order> mOrderlist = new ArrayList<Order>();
    private String user_id,goods_user,order_id,store_id;
    private SmartRefreshLayout order_other_srlControl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null){
            view=inflater.inflate(R.layout.order_other, container, false);
        }
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        order_other_listview =getActivity().findViewById(R.id.order_other_listview);
        order_other_srlControl=getActivity().findViewById(R.id.order_other_srlControl);

        order_other_listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean enable = false;
                if (order_other_listview != null && order_other_listview.getChildCount() > 0) {
                    // 检查listView第一个item是否可见
                    boolean firstItemVisible = order_other_listview.getFirstVisiblePosition() == 0;
                    // 检查第一个item的顶部是否可见
                    boolean topOfFirstItemVisible = order_other_listview.getChildAt(0).getTop() == 0;
                    // 启用或者禁用SwipeRefreshLayout刷新标识
                    enable = firstItemVisible && topOfFirstItemVisible;
                } else if (order_other_listview != null && order_other_listview.getChildCount() == 0) {
                    // 没有数据的时候允许刷新
                    enable = true;
                }
                // 把标识传给swipeRefreshLayout
                //home_swip.setEnabled(enable);

            }
        });

        //监听下拉和上拉状态
        //下拉刷新
        order_other_srlControl.setOnRefreshListener(refreshlayout -> {
            order_other_srlControl.setEnableRefresh(true);//启用刷新
            getstore_processed_order();
            order_other_srlControl.finishRefresh();//结束刷新
        });
        //上拉加载

        getstore_processed_order();
        //查询店铺下的待处理订单
    }
    public void getstore_processed_order(){
        BmobQuery<Order> eq1=new BmobQuery<>();
        eq1.addWhereEqualTo("state","售后");//查询待处理的订单
        BmobQuery<Order>eq2=new BmobQuery<>();
        eq2.addWhereEqualTo("consumer", BmobUser.getCurrentUser(_User.class));//查询该店铺的订单

        List<BmobQuery<Order>> andQuerys = new ArrayList<BmobQuery<Order>>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);

        BmobQuery<Order> bmobQuery = new BmobQuery<>();
        bmobQuery.include("goods,store,consumer");
        bmobQuery.and(andQuerys);
        bmobQuery.order("-createdAt");//依照数据排序时间排序
        bmobQuery.setLimit(500);
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
        bmobQuery.findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> object, BmobException e) {

                if (e == null) {
                    mOrderlist.clear();

                    for (int i = 0; i < object.size(); i++) {
                        mOrderlist.add(object.get(i));
                    }
                    order_other_listview.setAdapter(new Order_Other.ItemListAdapter());

                } else {
                    Toasty.error(getActivity(), "数据加载失败！", Toast.LENGTH_SHORT,true).show();
                    //home_swip.setRefreshing(false);

                }

            }
        });




    }
    class ItemListAdapter extends BaseAdapter {
        private Order order;
        //适配器
        @Override
        public int getCount()
        {
            if (mOrderlist.size() > 0)
            {
                return mOrderlist.size();
            }
            return 0;
        }
        @Override
        public Object getItem(int position)
        {
            return mOrderlist.get(position);
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Order_Other.ItemListAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new Order_Other.ItemListAdapter.ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_order_b, null);
                viewHolder.linearLayout=convertView.findViewById(R.id.linearLayout);
                viewHolder.good_name=convertView.findViewById(R.id.goods_name);
                viewHolder.goods_infomation=convertView.findViewById(R.id.goods_information);
                viewHolder.goods_price=convertView.findViewById(R.id.goods_price);
                viewHolder.goods_icon=convertView.findViewById(R.id.goods_icon);
                viewHolder.store_icon=convertView.findViewById(R.id.store_icon);
                viewHolder.store_name=convertView.findViewById(R.id.store_name);
                viewHolder.time=convertView.findViewById(R.id.time);
                viewHolder.state=convertView.findViewById(R.id.state);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (Order_Other.ItemListAdapter.ViewHolder)convertView.getTag();
            }
            order=mOrderlist.get(position);
            viewHolder.state.setText(order.getAfter_sales_type());//售后状态
            viewHolder.good_name.setText(order.getGoods().getName());
            viewHolder.goods_infomation.setText(order.getGoods().getInformation());
            viewHolder.goods_price.setText(order.getPrice());
            if(order.getGoods().getIcon().getUrl()!=null){
                Glide.with(getActivity()).load(order.getGoods().getIcon().getUrl()).crossFade(800).into(viewHolder.goods_icon);
            }
            if(order.getGoods().getIcon().getUrl()!=null){
                Glide.with(getActivity()).load(order.getGoods().getIcon().getUrl()).crossFade(800).into(viewHolder.goods_icon);
            }
            viewHolder.store_name.setText(order.getStore().getName());

            viewHolder.time.setText(order.getCreatedAt());
            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    order_id=mOrderlist.get(position).getObjectId(); //这里就获取到了Id。
                    Intent intent = new Intent(getActivity(), OrderActivity2.class);
                    intent.putExtra("order_id", order_id);
                    intent.putExtra("string_order_state","退款");
                    startActivity(intent);
                }
            });
            viewHolder.store_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    store_id=mOrderlist.get(position).getStore().getObjectId(); //这里就获取到了Id。
                    Intent intent = new Intent(getActivity(), StoreActivity.class);
                    intent.putExtra("store_id", store_id);
                    startActivity(intent);

                }
            });



            return convertView;
        }
        public class ViewHolder
        {
            public TextView good_name,goods_infomation,goods_price,store_name,state,time;
            public LinearLayout linearLayout;
            public ImageView goods_icon,store_icon;

        }


    }
}