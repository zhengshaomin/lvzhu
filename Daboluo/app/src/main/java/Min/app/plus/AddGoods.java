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

import java.io.File;

import Min.app.plus.bmob.Goods;
import Min.app.plus.bmob.Store;
import Min.app.plus.utils.RealPathFromUriUtils;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import es.dmoral.toasty.Toasty;

public class AddGoods extends AppCompatActivity {

    private Toolbar toolbar;
    private String store_id,photoaway=null;
    private Switch state;
    private Boolean states=true;//表示有货
    private EditText name,information,price;
    private LinearLayout choose_photo;
    private ImageView icon;
    private Button promise;
    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_goods);

        Intent intent = getIntent();
        store_id = intent.getStringExtra("store_id");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("添加商品");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        name=findViewById(R.id.name);
        information=findViewById(R.id.information);
        price=findViewById(R.id.price);
        choose_photo=findViewById(R.id.choose_photo);
        icon=findViewById(R.id.icon);
        state=findViewById(R.id.state);
        promise=findViewById(R.id.promise);

        choose_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(photoaway==null){

                    Intent gallery = new Intent(Intent.ACTION_PICK);
                    gallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(gallery, 1000);

                }else {
                    photoaway=null;
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
                    states=true;
                } else {
                    //仅自己可见
                    state.setText("缺货");
                    states=false;
                }
            }
        });
        promise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (photoaway == null) {

                    Toasty.warning(AddGoods.this, "请选择图片", Toast.LENGTH_SHORT, true).show();

                } else {

                    View view2 = LayoutInflater.from(AddGoods.this).inflate(R.layout.dialog_loading, null, false);
                    dialog = new AlertDialog.Builder(AddGoods.this).setView(view2).create();
                    dialog.setCancelable(false);
                    dialog.show();

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

                                Store store=new Store();
                                store.setObjectId(store_id);
                                goods.setStore(store);
                                goods.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        dialog.dismiss();
                                        if (e == null) {
                                            Toasty.success(AddGoods.this, "添加成功", Toast.LENGTH_SHORT, true).show();
                                            name.setText(null);
                                            information.setText(null);
                                            price.setText(null);
                                        } else {
                                            Toasty.error(AddGoods.this, "添加失败", Toast.LENGTH_SHORT, true).show();
                                        }
                                    }
                                });
                            } else {
                                Toasty.error(AddGoods.this, "添加失败", Toast.LENGTH_SHORT, true).show();
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
        });

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
        if (ActivityCompat.checkSelfPermission(AddGoods.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddGoods.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }
}