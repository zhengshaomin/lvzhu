package Min.app.plus.fragment.storeorderstatus;

import static Min.app.plus.utils.QueryBasisInfoUtils.storeobjectid;

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

import Min.app.plus.OrderStoreActivity;
import Min.app.plus.utils.GlideActivity;
import Min.app.plus.bmob.Order;
import Min.app.plus.R;
import Min.app.plus.bmob.Store;
import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import es.dmoral.toasty.Toasty;

public class Store_Order_Other extends Fragment {

    /**
     * 店铺订单售后页面
     * */

    private View view;
    private ListView order_other_listview;
    List<Order> mOrderlist = new ArrayList<Order>();
    private String user_id,goods_user,order_id;
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
        eq2.addWhereEqualTo("store", storeobjectid);//查询该店铺的订单

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
                    order_other_listview.setAdapter(new Store_Order_Other.ItemListAdapter());

                } else {
                    Toasty.error(getActivity(), "数据加载失败！", Toast.LENGTH_SHORT,true).show();
                    //home_swip.setRefreshing(false);

                }

            }
        });




    }
    class ItemListAdapter extends BaseAdapter {
        private Order orders;
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
            final Store_Order_Other.ItemListAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new Store_Order_Other.ItemListAdapter.ViewHolder();
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_order_store_b, null);
                viewHolder.consumer_username =convertView.findViewById(R.id.consumer_username);//用户昵称
                viewHolder.consumer_headportrait = convertView.findViewById(R.id.consumer_headportrait);//用户头像
                viewHolder.goods_icon=convertView.findViewById(R.id.goods_icon);//商品图片
                viewHolder.goods_name=convertView.findViewById(R.id.goods_name);//商品名字
                viewHolder.goods_information=convertView.findViewById(R.id.goods_information);//商品信息
                viewHolder.goods_price=convertView.findViewById(R.id.goods_price);//商品价格
                viewHolder.phonenumber=convertView.findViewById(R.id.phonenumber);//联系电话
                viewHolder.remarks=convertView.findViewById(R.id.remarks);//用户备注
                viewHolder.address=convertView.findViewById(R.id.address);//用户地址
                viewHolder.state=convertView.findViewById(R.id.state);//订单状态
                viewHolder.linearLayout = convertView.findViewById(R.id.linearLayout);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (Store_Order_Other.ItemListAdapter.ViewHolder) convertView.getTag();
            }
            orders = mOrderlist.get(position);

            viewHolder.consumer_username.setText(orders.getConsumer().getUsername());//用户昵称
            Glide.with(getActivity()).load("https://q.qlogo.cn/headimg_dl?dst_uin=" + orders.getConsumer().getQq() + "&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(getActivity())).into(viewHolder.consumer_headportrait);//头像
            viewHolder.goods_name.setText(orders.getGoods().getName());//商品名称
            Glide.with(getActivity()).load(orders.getGoods().getIcon().getUrl()).crossFade(800).into(viewHolder.goods_icon);//头像
            viewHolder.goods_information.setText(orders.getGoods().getInformation());//商品信息
            viewHolder.goods_price.setText(orders.getPrice());//商品价格
            viewHolder.phonenumber.setText(orders.getConsumer().getMobilePhoneNumber());//电话号码
            viewHolder.remarks.setText(orders.getRemarks());//用户备注
            viewHolder.address.setText(orders.getAddress());//收货地址
            viewHolder.state.setText(orders.getAfter_sales_type());//订单状态
            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1)
                {
                    order_id=mOrderlist.get(position).getObjectId(); //这里就获取到了Id。
                    Intent intent = new Intent(getActivity(), OrderStoreActivity.class);
                    intent.putExtra("order_id", order_id);
                    intent.putExtra("string_order_state","退款");
                    startActivity(intent);
//                    // TODO: Implement this method
                }
            });
            return convertView;
        }
        public class ViewHolder
        {
            public TextView consumer_username,address,phonenumber,remarks,goods_name,goods_information,goods_price,state;//用户名，内容，时间，状态，接单按钮
            public LinearLayout linearLayout;
            public ImageView consumer_headportrait,goods_icon;//头像，图片


        }


    }
}