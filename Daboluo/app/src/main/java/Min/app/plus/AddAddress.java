package Min.app.plus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import Min.app.plus.bmob._User;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;

public class AddAddress extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText address;
    private Button promise;
    private String str_address=null,user_me_id,receiving_address;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addreceiving_address_dialog);

        Intent intent = getIntent();
        user_me_id=intent.getStringExtra("user_me_id");
        receiving_address=intent.getStringExtra("receiving_address");


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("添加地址"+user_me_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        address = findViewById(R.id.address);
        address.setText(receiving_address);
        promise = findViewById(R.id.promise);
        promise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (address.getText().toString().length()<1){
                    //地址不能为空
                }else {
                    updataaddress();
                }
            }
        });

    }public void updataaddress(){
        _User p2 = new _User();
        p2.setAddress(address.getText().toString().trim());
        p2.update(user_me_id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toasty.success(AddAddress.this, "添加成功", Toast.LENGTH_SHORT, true).show();
                }else{
                    Toasty.error(AddAddress.this, "出现错误", Toast.LENGTH_SHORT, true).show();
                }
            }

        });

    }}