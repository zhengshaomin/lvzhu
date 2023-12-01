package Min.app.plus;

import static Min.app.plus.utils.DialogUtils.dialogedittextshow;
import static Min.app.plus.utils.DialogUtils.dialogloadingdismiss;
import static Min.app.plus.utils.DialogUtils.dialogloadingshow;
import static Min.app.plus.utils.QueryBasisInfoUtils.getuserinfo;
import static Min.app.plus.utils.QueryBasisInfoUtils.useraddress;
import static Min.app.plus.utils.QueryBasisInfoUtils.userboluocoin;
import static Min.app.plus.utils.QueryBasisInfoUtils.userobjectid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;

import Min.app.plus.bmob.Goods;
import Min.app.plus.bmob.Order;
import Min.app.plus.bmob.Store;
import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;

//支付页面
public class PayActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView pay_goods_list;
    private TextView view_goods_price,service_address,order_remarks;
    private Button view_pay;

    private String store_manager_user_id,store_id,orders_remarks;//收货地址
    private int boluo_total;//商品总价
    //购物车系列
    private ArrayList<String> shopping_car_goods_objectid = new ArrayList<>();//购物车商品id
    private ArrayList<String> shopping_car_goods_name = new ArrayList<>();//购物车商品名字
    private ArrayList<String> shopping_car_goods_icon = new ArrayList<>();//购物车商品图标
    private ArrayList<String> shopping_car_goods_information = new ArrayList<>();//购物车商品信息
    private ArrayList<String> shopping_car_goods_price = new ArrayList<>();//购物车商品价格

    //添加收货地址,订单备注
    private EditText address,remarks;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);

        }};

    private boolean issuccess=false;//下单状态
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay);

        Intent intent = getIntent();
        shopping_car_goods_objectid = intent.getStringArrayListExtra("shopping_car_goods_objectid");
        shopping_car_goods_name = intent.getStringArrayListExtra("shopping_car_goods_name");
        shopping_car_goods_icon = intent.getStringArrayListExtra("shopping_car_goods_icon");
        shopping_car_goods_information = intent.getStringArrayListExtra("shopping_car_goods_information");
        shopping_car_goods_price = intent.getStringArrayListExtra("shopping_car_goods_price");
        boluo_total = intent.getIntExtra("boluo_total", 0);
        store_id = intent.getStringExtra("store_id");//店铺id
        store_manager_user_id = intent.getStringExtra("store_manager_user_id");//店铺管理员id

//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                Message msg = new Message();
//                handler.sendMessage(msg);
//            }
//        }.start();


        toolbar = findViewById(R.id.toolbar);

        service_address=findViewById(R.id.service_address);
        service_address.setText(useraddress);
        order_remarks=findViewById(R.id.order_remarks);
        pay_goods_list = findViewById(R.id.pay_goods_list);
        pay_goods_list.setAdapter(new PayActivity.ShoppingAdapter());
        view_goods_price=findViewById(R.id.view_goods_price);
        view_goods_price.setText(boluo_total+"");
        view_pay=findViewById(R.id.view_pay);
        view_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //支付
                //先提交订单，再修改菠萝币数量（先减去自己，再增加商家）
                if(issuccess){
                    Toasty.warning(PayActivity.this, "请勿重复下单", Toast.LENGTH_SHORT,true).show();
                }else if(service_address.getText().toString().length()<1){
                    Toasty.warning(PayActivity.this, "请添加收货地址", Toast.LENGTH_SHORT,true).show();
                }else if(order_remarks.getText().toString().length()<1){
                    Toasty.warning(PayActivity.this, "请添加备注", Toast.LENGTH_SHORT,true).show();
                }else if(userboluocoin<boluo_total){
                    Toasty.warning(PayActivity.this, "菠萝数量不够", Toast.LENGTH_SHORT,true).show();
                }else {
                    dialogloadingshow(PayActivity.this);
                    updata_user_boluob();//先减去自身，再增加商户，最后增加订单

                }
            }
        });
        service_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addreceiving_address_dialog();
                editdialog("添加地址");
            }
        });
        order_remarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editdialog("添加备注");
            }
        });
        toolbar.setTitle("提交订单");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
    //更新自己菠萝币数量
    public void updata_user_boluob() {
        int coin=userboluocoin - boluo_total;
        _User user = new _User();
        user.setBoluocoin(coin+"");//用户原有菠萝减去商品价值
        user.update(userobjectid, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //成功
                    getuserinfo();//更新用户信息
                    addorder();//修改自己菠萝币成功之后，添加订单
                } else {
                    dialogloadingdismiss();
                    Toasty.error(PayActivity.this, "出现错误啦～"+e, Toast.LENGTH_SHORT,true).show();
                }
            }
        });


    }
    //添加订单
    public void addorder(){
        List<BmobObject> categories = new ArrayList<>();
        for (int i = 0; i < shopping_car_goods_objectid.size(); i++) {
            Order order = new Order();

            Goods goods=new Goods();
            goods.setObjectId(shopping_car_goods_objectid.get(i));
            order.setGoods(goods);//商品

            order.setPrice(shopping_car_goods_price.get(i));
            Store store=new Store();
            store.setObjectId(store_id);
            order.setStore(store);

            order.setUser_visible(true);
            order.setState("待处理");
            order.setConsumer(BmobUser.getCurrentUser(_User.class));//消费者
            order.setAddress(useraddress);//收货地址
            order.setRemarks(orders_remarks);//订单备注
            categories.add(order);
        }
        new BmobBatch().insertBatch(categories).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> results, BmobException e) {
                dialogloadingdismiss();//关闭对话框
                if (e == null) {
                    issuccess=true;
                    Toasty.success(PayActivity.this, "下单成功", Toast.LENGTH_SHORT,true).show();
                    for (int i = 0; i < results.size(); i++) {
                        BatchResult result = results.get(i);
                        BmobException ex = result.getError();
                        if (ex == null) {
                            //Snackbar.make(mBtnSave, "第" + i + "个数据批量添加成功：" + result.getCreatedAt() + "," + result.getObjectId() + "," + result.getUpdatedAt(), Snackbar.LENGTH_LONG).show();
                        } else {

                            //Snackbar.make(mBtnSave, "第" + i + "个数据批量添加失败：" + ex.getMessage() + "," + ex.getErrorCode(), Snackbar.LENGTH_LONG).show();

                        }
                    }
                } else {
                    //Snackbar.make(mBtnSave, "失败：" + e.getMessage() + "," + e.getErrorCode(), Snackbar.LENGTH_LONG).show();
                }
            }
        });


    }
    //备注输入框弹窗
    private void editdialog(String title){
        View view = LayoutInflater.from(PayActivity.this).inflate(R.layout.dialogedittext, null, false);
        AlertDialog dialog = new AlertDialog.Builder(PayActivity.this).setView(view).create();
        EditText edit = view.findViewById(R.id.edit);
        TextView promise=view.findViewById(R.id.promise);
        TextView titles=view.findViewById(R.id.title);
        titles.setText(title);
        dialog.setView(view); // 自定义dialog
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        promise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //修改地址
                if (title.equals("添加地址")){
                    _User p2 = new _User();
                    p2.setAddress(edit.getText().toString().trim());
                    p2.update(userobjectid, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            dialog.dismiss();
                            if(e==null){
                                service_address.setText(edit.getText().toString().trim());
                                useraddress=edit.getText().toString().trim();
                            }else{
                                Toasty.error(PayActivity.this, "出现错误！"+e, Toast.LENGTH_SHORT,true).show();
                            }
                        }

                    });
                }else if (title.equals("添加备注")){
                    dialog.dismiss();
                    order_remarks.setText(edit.getText().toString().trim());
                    orders_remarks=edit.getText().toString().trim();
                }

            }
        });
    }
    class ShoppingAdapter extends BaseAdapter{
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
            final PayActivity.ShoppingAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder =new PayActivity.ShoppingAdapter.ViewHolder();
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
                viewHolder = (PayActivity.ShoppingAdapter.ViewHolder)convertView.getTag();
            }
            viewHolder.good_name.setText(shopping_car_goods_name.get(position));
            viewHolder.goods_infomation.setText(shopping_car_goods_information.get(position));
//            viewHolder.goods_stock.setText("库存 "+goods.getStock());
            viewHolder.goods_price.setText(""+shopping_car_goods_price.get(position));
            Glide.with(PayActivity.this).load(shopping_car_goods_icon.get(position)).crossFade(800).into(viewHolder.goods_icon);

            viewHolder.goods_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boluo_total=boluo_total-Integer.parseInt(shopping_car_goods_price.get(position));
                    //view_goods_price.setText(""+boluo_total);
                    shopping_car_goods_name.remove(position);
                    shopping_car_goods_information.remove(position);
                    shopping_car_goods_icon.remove(position);
                    shopping_car_goods_price.remove(position);
                    shopping_car_goods_objectid.remove(position);
                    pay_goods_list.setAdapter(new PayActivity.ShoppingAdapter());
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
