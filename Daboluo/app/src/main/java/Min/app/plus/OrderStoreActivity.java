package Min.app.plus;

import static Min.app.plus.utils.QueryBasisInfoUtils.getuserinfo;
import static Min.app.plus.utils.QueryBasisInfoUtils.userboluocoin;
import static Min.app.plus.utils.QueryBasisInfoUtils.userobjectid;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import Min.app.plus.bmob.Order;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.GlideActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;

/**
 * 作者：daboluo on 2023/8/18 22:54
 * Email:daboluo719@gmail.com
 */
//商家版的订单详情
public class OrderStoreActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String order_id,store_id,goods_id,consumer_id,store_manager_id,consumerid;
    private String string_order_state=null,order_state;//状态码
    private ImageView headportrait,goods_icon;
    private TextView username,goods_name,goods_information,goods_price,consumername,phonenumber,address,creattime,completiontime,remarks,merchantsremarks;
    private LinearLayout linearlayout;
    private TextView completed_order,contact,refuse;
    private int consumer_boluob,merchants_boluob,payment_price;//消费者菠萝数量，商家菠萝数量，实际付款数量
    private AlertDialog dialog;//loading
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderstore);

        Intent intent = getIntent();
        order_id = intent.getStringExtra("order_id");//订单id
        string_order_state=intent.getStringExtra("string_order_state");

        linearlayout=findViewById(R.id.linearlayout);
        completed_order=findViewById(R.id.completed_order);

        contact=findViewById(R.id.contact);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("订单详情");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        headportrait=findViewById(R.id.headportrait);
        goods_icon=findViewById(R.id.goods_icon);
        username=findViewById(R.id.username);
        goods_name=findViewById(R.id.goods_name);
        goods_information=findViewById(R.id.goods_information);
        goods_price=findViewById(R.id.goods_price);
        consumername=findViewById(R.id.consumername);
        phonenumber=findViewById(R.id.phonenumber);
        address=findViewById(R.id.address);
        creattime=findViewById(R.id.creattime);
        completiontime=findViewById(R.id.completiontime);
        remarks=findViewById(R.id.remarks);
        merchantsremarks=findViewById(R.id.merchantsremarks);


        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            //联系用户
            public void onClick(View view) {
                Intent intent = new Intent(OrderStoreActivity.this, UserActivity.class);
                intent.putExtra("user_id",consumerid);
                startActivity(intent);
            }
        });
        completed_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(completed_order.getText().toString().equals("完成")){
                    dialog.show();
                    complete_the_order();
                }else if(completed_order.getText().toString().equals("同意退款")){
                    dialog.show();
                    updata_store_manager_user_boluob2();
                }
            }
        });
        getorder_information();
        View view = LayoutInflater.from(OrderStoreActivity.this).inflate(R.layout.dialog_loading, null, false);
        dialog = new AlertDialog.Builder(OrderStoreActivity.this).setView(view).create();
        dialog.setCancelable(false);

    }
    //查询订单信息
    public void getorder_information() {
        BmobQuery<Order> bmobQuery = new BmobQuery<Order>();
        bmobQuery.include("store.manager,goods,consumer");
        bmobQuery.getObject(order_id, new QueryListener<Order>() {
            @Override
            public void done(Order object, BmobException e) {
                if (e == null) {
                    Glide.with(OrderStoreActivity.this).load(object.getStore().getIcon().getUrl()).crossFade(800).transform(new GlideActivity(OrderStoreActivity.this)).into(headportrait);
                    Glide.with(OrderStoreActivity.this).load(object.getGoods().getIcon().getUrl()).crossFade(800).into(goods_icon);
                    username.setText(object.getStore().getName());
                    goods_name.setText(object.getGoods().getName());
                    goods_information.setText(object.getGoods().getInformation());
                    goods_price.setText(object.getPrice());
                    consumername.setText(object.getConsumer().getUsername());
                    phonenumber.setText(object.getConsumer().getMobilePhoneNumber());
                    address.setText(object.getAddress());
                    creattime.setText(object.getCreatedAt());
                    completiontime.setText(object.getUpdatedAt());
                    remarks.setText(object.getRemarks());
                    payment_price=Integer.parseInt(object.getPrice());
                    consumer_id=object.getConsumer().getObjectId();
                    store_manager_id=object.getStore().getManager().getObjectId();
                    merchantsremarks.setText(object.getMerchantsremarks());
                    consumerid=object.getConsumer().getObjectId();
                    order_state=object.getState();
                    if(order_state.equals("待处理")){
                        completed_order.setText("完成");
                    }else if(order_state.equals("已完成")){
                        completed_order.setText("已完成");
                    }else if(object.getState().equals("售后")){
                        if(object.getAfter_sales_type().equals("申请退款")){
                            completed_order.setText("同意退款");
                        }else {
                            completed_order.setText("正在退款");
                        }
                    }


                } else {
                    //当bmob出现错误时，调用api
                }
            }
        });
        //商家完成订单
    }
    @Override
    //处理用户权限允许
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了拨打电话的权限，可以继续拨打电话操作
            } else {
                // 用户拒绝了拨打电话的权限，您可以提供一个提示或其他操作
            }
        }
    }
    //完成订单
    public void complete_the_order() {
        Order order = new Order();
        order.setState("已完成");
        order.update(order_id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    goods_id = null;
                    completed_order.setText("已完成");
                    updata_store_manager_user_boluob();//点击完成后，商家修改自己的菠萝币数量
                    Toasty.success(OrderStoreActivity.this, "修改成功～", Toast.LENGTH_SHORT,true).show();
                } else {
                    dialog.dismiss();
                    Toasty.error(OrderStoreActivity.this, "修改失败～", Toast.LENGTH_SHORT,true).show();
                }
            }
        });
        //消费者申请售后
    }
    //同意退款
    private void refund(){
        Order order = new Order();
        order.setAfter_sales_type("正在退款");
        order.setMerchantsremarks("同意退款");
        order.update(order_id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                dialog.dismiss();
                if (e == null) {
                    completed_order.setText("退款中");
                    getuserinfo();//更新自己信息
                    Toasty.success(OrderStoreActivity.this, "修改成功～", Toast.LENGTH_SHORT,true).show();
                } else {
                    Toasty.error(OrderStoreActivity.this, "修改失败～", Toast.LENGTH_SHORT,true).show();
                }
            }
        });
    }
    //完成订单增加自己菠萝币数量
    public void updata_store_manager_user_boluob() {
        int coin = userboluocoin + payment_price;
        _User user = new _User();
        user.setBoluocoin(coin + "");//用户原有菠萝减去商品价值
        user.update(userobjectid, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                dialog.dismiss();
                completed_order.setEnabled(true);
                dialog.dismiss();
                if (e == null) {
                    getuserinfo();//更新自己信息
                } else {
                    Toasty.error(OrderStoreActivity.this, "出现错误啦～", Toast.LENGTH_SHORT,true).show();
                }
            }
        });



    }
    //已完成后申请退款
    public void updata_store_manager_user_boluob2() {
        int coin = userboluocoin - payment_price;
        _User user = new _User();
        user.setBoluocoin(coin + "");//用户原有菠萝减去商品价值
        user.update(userobjectid, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                dialog.dismiss();
                if (e == null) {
                    refund();

                } else {
                    Toasty.error(OrderStoreActivity.this, "出现错误啦～", Toast.LENGTH_SHORT, true).show();
                }
            }
        });
    }
}
