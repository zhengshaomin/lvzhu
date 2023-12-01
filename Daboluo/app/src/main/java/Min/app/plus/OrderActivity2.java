package Min.app.plus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import Min.app.plus.bmob.Order;
import Min.app.plus.utils.GlideActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * 作者：daboluo on 2023/8/18 22:54
 * Email:daboluo719@gmail.com
 */
//用户版的订单详情
public class OrderActivity2 extends AppCompatActivity {

    private Toolbar toolbar;
    private String order_id,store_id,goods_id,store_manager_id,storemanagerid;
    private String string_order_state=null,order_state;//状态码
    private ImageView store_icon,goods_icon;
    private TextView store_name,goods_name,goods_information,goods_price,consumername,phonenumber,address,creattime,completiontime,remarks,merchantsremarks;
    private LinearLayout linearlayout;
    private TextView completed_order,contact;
    private int consumer_boluob,merchants_boluob,payment_price;//消费者菠萝数量，商家菠萝数量，实际付款数量
    private AlertDialog dialog;//loading
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order2);

        Intent intent = getIntent();
        order_id = intent.getStringExtra("order_id");//订单id
        string_order_state=intent.getStringExtra("string_order_state");

        linearlayout=findViewById(R.id.linearlayout);
        completed_order=findViewById(R.id.completed_order);

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

        store_icon=findViewById(R.id.store_icon);
        goods_icon=findViewById(R.id.goods_icon);
        store_name=findViewById(R.id.store_name);
        goods_name=findViewById(R.id.goods_name);
        contact=findViewById(R.id.contact);
        goods_information=findViewById(R.id.goods_information);
        goods_price=findViewById(R.id.goods_price);
        consumername=findViewById(R.id.consumername);
        phonenumber=findViewById(R.id.phonenumber);
        address=findViewById(R.id.address);
        creattime=findViewById(R.id.creattime);
        completiontime=findViewById(R.id.completiontime);
        remarks=findViewById(R.id.remarks);
        merchantsremarks=findViewById(R.id.merchantsremarks);


        store_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            //联系商家
            public void onClick(View view) {

                Intent intent = new Intent(OrderActivity2.this, UserActivity.class);
                intent.putExtra("user_id",storemanagerid);
                startActivity(intent);
            }
        });

        completed_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(string_order_state.equals("已完成")){
                    apply_for_after_sales();
                }


            }
        });
        getorder_information();
        View view = LayoutInflater.from(OrderActivity2.this).inflate(R.layout.dialog_loading, null, false);
        dialog = new AlertDialog.Builder(OrderActivity2.this).setView(view).create();
        dialog.setCancelable(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        }

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

    public void getorder_information() {
        BmobQuery<Order> bmobQuery = new BmobQuery<Order>();
        bmobQuery.include("store.manager,goods,consumer");
        bmobQuery.getObject(order_id, new QueryListener<Order>() {
            @Override
            public void done(Order object, BmobException e) {
                if (e == null) {
                    Glide.with(OrderActivity2.this).load(object.getStore().getIcon().getUrl()).crossFade(800).transform(new GlideActivity(OrderActivity2.this)).into(store_icon);
                    Glide.with(OrderActivity2.this).load(object.getGoods().getIcon().getUrl()).crossFade(800).into(goods_icon);
                    store_name.setText(object.getStore().getName());
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
                    store_manager_id=object.getStore().getManager().getObjectId();
                    merchantsremarks.setText(object.getMerchantsremarks());
                    storemanagerid=object.getStore().getManager().getObjectId();
                    order_state=object.getState();
                    if(order_state.equals("待处理")){
                        completed_order.setText("正在处理");
                    }else if(order_state.equals("已完成")){
                        completed_order.setText("申请售后");
                    }else if(object.getState().equals("售后")){
                        completed_order.setText("正在处理");
                    }


                } else {
                    //当bmob出现错误时，调用api
                }
            }
        });
    //商家完成订单
    }
    //消费者申请售后
    public void apply_for_after_sales() {
        Intent intent = new Intent(OrderActivity2.this, ApplyAfterSales.class);
        intent.putExtra("order_id", order_id);
        startActivity(intent);
    }
}
