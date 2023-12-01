package Min.app.plus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;

import java.io.File;

import Min.app.plus.bmob.Goods;
import Min.app.plus.utils.RealPathFromUriUtils;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import es.dmoral.toasty.Toasty;

public class AlterGoods extends AppCompatActivity {

    private Toolbar toolbar;
    private String store_id,photoaway=null;
    private Switch state,stock;
    private Boolean states=true;//表示有货
    private EditText name,information,price;
    private LinearLayout choose_photo;
    private ImageView icon;
    private Button promise;
    private AlertDialog dialog;

    //转到修改数据页
    private String goods_id,goods_name,goods_information,goods_iconurl;
    private int goods_price;
    private boolean goods_state;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_goods);

        Intent intent = getIntent();
        goods_id = intent.getStringExtra("goods_id");
        goods_name = intent.getStringExtra("goods_name");
        goods_information = intent.getStringExtra("goods_information");
        goods_iconurl = intent.getStringExtra("goods_iconurl");
        goods_price = intent.getIntExtra("goods_price", 0);
        goods_state = intent.getBooleanExtra("goods_state", true);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("修改商品信息");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        name = findViewById(R.id.name);
        information = findViewById(R.id.information);
        price = findViewById(R.id.price);
        choose_photo = findViewById(R.id.choose_photo);
        icon = findViewById(R.id.icon);
        state = findViewById(R.id.state);
        promise = findViewById(R.id.promise);


        name.setText(goods_name);
        information.setText(goods_information);
        Glide.with(AlterGoods.this).load(goods_iconurl).crossFade(800).into(icon);
        price.setText(goods_price + "");
        state.setChecked(goods_state);
        if(goods_state==true){
            state.setText("有货");
        }else {
            state.setText("缺货");
        }

        choose_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoaway == null) {

                    Intent gallery = new Intent(Intent.ACTION_PICK);
                    gallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(gallery, 1000);

                } else {
                    photoaway = null;
                    icon.setImageBitmap(null);
                }

            }
        });

        state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //所有人可见
                    state.setText("有货");
                    states = true;
                } else {
                    //仅自己可见
                    state.setText("缺货");
                    states = false;
                }
            }
        });
        promise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View view2 = LayoutInflater.from(AlterGoods.this).inflate(R.layout.dialog_loading, null, false);
                dialog = new AlertDialog.Builder(AlterGoods.this).setView(view2).create();
                dialog.setCancelable(false);
                dialog.show();
                if(goods_id!=null){
                    altergoods();
                }

            }
        });

    }public void altergoods(){
        if(photoaway==null){
            //不修改图片
            Goods goods = new Goods();
            goods.setState(states);
            goods.setName(name.getText().toString());
            goods.setInformation(information.getText().toString());
            goods.setPrice(Integer.parseInt(price.getText().toString()));
            goods.update(goods_id, new UpdateListener() {
                @Override
                public void done(BmobException e) {

                    dialog.dismiss();
                    if (e == null) {
                        goods_id=null;
                        Toasty.success(AlterGoods.this, "修改成功", Toast.LENGTH_SHORT, true).show();
                        name.setText(null);
                        information.setText(null);
                        price.setText(null);
                    } else {
                        Toasty.error(AlterGoods.this, "修改失败", Toast.LENGTH_SHORT, true).show();
                    }
                }
            });
        }else {
            //修改图片

            BmobFile bmobFile = new BmobFile(new File(photoaway));
            bmobFile.uploadblock(new UploadFileListener() {

                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        //bmobFile.getFileUrl()--返回的上传文件的完整地址
                        Goods goods = new Goods();
                        goods.setIcon(bmobFile);
                        goods.setState(states);
                        goods.setName(name.getText().toString());
                        goods.setInformation(information.getText().toString());
                        goods.setPrice(Integer.parseInt(price.getText().toString()));
                        goods.update(goods_id, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {

                                dialog.dismiss();
                                if (e == null) {
                                    goods_id=null;
                                    Toasty.success(AlterGoods.this, "修改成功", Toast.LENGTH_SHORT, true).show();
                                    name.setText(null);
                                    information.setText(null);
                                    price.setText(null);
                                } else {
                                    Toasty.error(AlterGoods.this, "修改失败", Toast.LENGTH_SHORT, true).show();
                                }
                            }
                        });


                    } else {
                        Toasty.error(AlterGoods.this, "图片上传失败", Toast.LENGTH_SHORT, true).show();
                        dialog.dismiss();
                    }

                }

                @Override
                public void onProgress(Integer value) {
                    // 返回的上传进度（百分比）
                }
            });

        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == RESULT_OK){

            photoaway = RealPathFromUriUtils.getRealPathFromUri(this, data.getData());

            Bitmap bitmap = BitmapFactory.decodeFile(photoaway);

            icon.setImageBitmap(bitmap);



        }

        requestWritePermission();
    }
    private void requestWritePermission(){
        if (ActivityCompat.checkSelfPermission(AlterGoods.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AlterGoods.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }
}
