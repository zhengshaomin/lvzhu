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

import Min.app.plus.bmob.Store;
import Min.app.plus.utils.RealPathFromUriUtils;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import es.dmoral.toasty.Toasty;

public class AlterStore extends AppCompatActivity {

    private Toolbar toolbar;
    private String store_id,photoaway=null;
    private Switch state,notice_state;
    private Boolean states=true,notice_states=true,audit_state=false;//表示有货
    private EditText name,information,service_scope,notice_content;
    private LinearLayout choose_photo;
    private ImageView icon;
    private Button promise;
    private AlertDialog dialog;

    //转到修改数据页

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alter_store);

        Intent intent = getIntent();
        store_id = intent.getStringExtra("store_id");


        getstore();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("编辑店铺信息");
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
        service_scope = findViewById(R.id.service_scope);
        notice_content = findViewById(R.id.notice_content);
        choose_photo = findViewById(R.id.choose_photo);
        icon = findViewById(R.id.icon);
        state = findViewById(R.id.state);
        notice_state = findViewById(R.id.notice_state);
        promise = findViewById(R.id.promise);

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
                    state.setText("营业");
                    states = true;
                } else {
                    //仅自己可见
                    state.setText("打烊");
                    states = false;
                }
            }
        });
        notice_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //所有人可见
                    notice_state.setText("开启公告");
                    notice_states = true;
                } else {
                    //仅自己可见
                    notice_state.setText("关闭公告");
                    notice_states = false;
                }
            }
        });
        promise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(store_id!=null){
                    View view2 = LayoutInflater.from(AlterStore.this).inflate(R.layout.dialog_loading, null, false);
                    dialog = new AlertDialog.Builder(AlterStore.this).setView(view2).create();
                    dialog.setCancelable(false);
                    dialog.show();
                    alterstore();
                }


            }
        });

    }public void getstore(){
        BmobQuery<Store> bmobQuery = new BmobQuery<Store>();
        bmobQuery.getObject(store_id, new QueryListener<Store>() {
            @Override
            public void done(Store object, BmobException e) {
                if (e == null) {
                    name.setText(object.getName());
                    information.setText(object.getInformation());
                    Glide.with(AlterStore.this).load(object.getIcon().getUrl()).crossFade(800).into(icon);
                    service_scope.setText(object.getService_scope());
                    notice_content.setText(object.getNotice_content());
                    state.setChecked(object.isState());
                    notice_state.setChecked(object.isNotice_state());
                    audit_state=object.isAudit_state();
                    if(object.isState()==true){
                        state.setText("营业");
                    }else {
                        state.setText("打烊");
                    }
                    if(object.isNotice_state()==true){
                        notice_state.setText("开启公告");
                    }else {
                        notice_state.setText("关闭公告");
                    }

                }else {
                    Toasty.error(AlterStore.this, "信息加载失败", Toast.LENGTH_SHORT, true).show();

                }
            }
        });

    }public void alterstore(){
        if(photoaway==null){
            //不修改图片
            Store store = new Store();
            store.setName(name.getText().toString());//名字
            store.setInformation(information.getText().toString());//介绍
            store.setService_scope(service_scope.getText().toString());//服务范围
            store.setNotice_content(notice_content.getText().toString());//公告内容
            store.setState(states);//状态
            store.setAudit_state(audit_state);
            store.setNotice_state(notice_states);//公告状态
            store.update(store_id,new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    dialog.dismiss();
                    if (e == null) {
                        Toasty.success(AlterStore.this, "修改成功", Toast.LENGTH_SHORT, true).show();
                        store_id=null;
                    } else {
                        Toasty.error(AlterStore.this, "修改失败", Toast.LENGTH_SHORT, true).show();
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
                        Store store = new Store();
                        store.setIcon(bmobFile);//图片
                        store.setName(name.getText().toString());//名字
                        store.setInformation(information.getText().toString());//介绍
                        store.setService_scope(service_scope.getText().toString());//服务范围
                        store.setName(notice_content.getText().toString());//公告内容
                        store.setState(states);//状态
                        store.setAudit_state(true);
                        store.setNotice_state(notice_states);//公告状态
                        store.update(store_id,new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                dialog.dismiss();
                                if (e == null) {
                                    Toasty.success(AlterStore.this, "修改成功", Toast.LENGTH_SHORT, true).show();
                                    store_id=null;
                                } else {
                                    Toasty.error(AlterStore.this, "修改失败", Toast.LENGTH_SHORT, true).show();
                                }
                            }
                        });
                    } else {
                        Toasty.error(AlterStore.this, "图片上传失败", Toast.LENGTH_SHORT, true).show();
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
        if (ActivityCompat.checkSelfPermission(AlterStore.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AlterStore.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }
}