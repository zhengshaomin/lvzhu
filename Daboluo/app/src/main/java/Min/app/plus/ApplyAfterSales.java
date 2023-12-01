package Min.app.plus;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import Min.app.plus.bmob.Order;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;

public class ApplyAfterSales extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView goods_icon;
    private TextView goods_name,goods_information,goods_price,state;
    private EditText after_sales_reasons;
    private Button promise;

    private String order_id,goods_id,store_id;
    private String after_sales_reasons_key=null,after_sales_type_key=null;

    private String[] reports = {"申请退款"};
    private int select = 0; //表示单选对话框初始时选中哪一项
    private AlertDialog dialog;

    private int use=0;//未使用
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aftersales);

        Intent intent = getIntent();
        order_id = intent.getStringExtra("order_id");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("申请售后");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        goods_icon = findViewById(R.id.goods_icon);
        goods_name = findViewById(R.id.goods_name);
        goods_information = findViewById(R.id.goods_information);
        goods_price = findViewById(R.id.goods_price);
        state = findViewById(R.id.state);
        after_sales_reasons = findViewById(R.id.after_sales_reasons);
        promise = findViewById(R.id.promise);

        promise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                after_sales_reasons_key=after_sales_reasons.getText().toString().trim();
                if(after_sales_type_key==null){
                    Toasty.warning(ApplyAfterSales.this, "请选择售后类型～", Toast.LENGTH_SHORT,true).show();
                }else if(after_sales_reasons_key.length()<5){
                    Toasty.warning(ApplyAfterSales.this, "请输入原因不少于5字～", Toast.LENGTH_SHORT,true).show();
                }else {
                    alterorder();
                }
            }
        });
        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ApplyAfterSales.this);
                dialog.setTitle("请选择售后类型");
                dialog.setCancelable(false);
                dialog.setSingleChoiceItems(reports, select, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        select = which;
                        //System.out.println("选择: " + items[select]);
                    }
                });
                dialog.setPositiveButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                });
                dialog.setNegativeButton("确认",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {

                       state.setText(reports[select]);
                       after_sales_type_key=reports[select];
                            //txv1.setText("你选择了："+report[select]);

                    }
                });

                dialog.show();
            }
        });
        getorder_goods();

    }public void getorder_goods() {
        BmobQuery<Order> bmobQuery = new BmobQuery<Order>();
        bmobQuery.include("store,goods,consumer");
        bmobQuery.getObject(order_id, new QueryListener<Order>() {
            @Override
            public void done(Order object, BmobException e) {
                if (e == null) {

                    Glide.with(ApplyAfterSales.this).load(object.getGoods().getIcon().getUrl()).crossFade(800).into(goods_icon);
                    goods_name.setText(object.getGoods().getName());
                    goods_information.setText(object.getGoods().getInformation());
                    goods_price.setText(object.getGoods().getPrice() + "");

                } else {
                    //当bmob出现错误时，调用api
                }
            }
        });
    }public void alterorder(){
        Order order = new Order();
        order.setState("售后");
        order.setAfter_sales_type(after_sales_type_key);
        order.setAfter_sales_reasons(after_sales_reasons_key);
        order.update(order_id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    goods_id=null;
                   Toasty.success(ApplyAfterSales.this, "修改成功～", Toast.LENGTH_SHORT,true).show();
                } else {
                    Toasty.error(ApplyAfterSales.this, "修改失败～", Toast.LENGTH_SHORT,true).show();
                }
            }
        });


    }}
