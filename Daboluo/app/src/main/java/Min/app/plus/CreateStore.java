package Min.app.plus;

import static Min.app.plus.utils.DialogUtils.dialogtextviewshow;
import static Min.app.plus.utils.QueryBasisInfoUtils.getstoreinfo;

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

import Min.app.plus.bmob.Store;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.RealPathFromUriUtils;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import es.dmoral.toasty.Toasty;

public class CreateStore extends AppCompatActivity {

    private Toolbar toolbar;
    private String store_id,photoaway=null;
    private Switch state,notice_state;
    private Boolean states=true,notice_states=true;//表示有货
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

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("创建店铺");
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
        service_scope=findViewById(R.id.service_scope);
        notice_content=findViewById(R.id.notice_content);
        choose_photo=findViewById(R.id.choose_photo);
        icon=findViewById(R.id.icon);
        state=findViewById(R.id.state);
        notice_state=findViewById(R.id.notice_state);
        promise=findViewById(R.id.promise);

        dialogtextviewshow(CreateStore.this,"致有想法的您","我们非常高兴欢迎您加入旅助App！这是一个充满无限可能的时刻，我们迫不及待地期待着您的加入，共同探索新的商机和成功的道路。\n\n店铺创建成功后会等待后台审核，审核成功后你的店铺将会在首页展示，如需加急通过审核或者其它问题请联系\nQQ:765618041\n微信：DIABLOSER\n欢迎您的加入！");
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
                    state.setText("营业");
                    states=true;
                } else {
                    //仅自己可见
                    state.setText("打烊");
                    states=false;
                }
            }
        });
        notice_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //所有人可见
                    notice_state.setText("开启");
                    notice_states=true;
                } else {
                    //仅自己可见
                    notice_state.setText("关闭");
                    notice_states=false;
                }
            }
        });
        promise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (photoaway == null) {

                    Toasty.warning(CreateStore.this, "请选择图片～", Toast.LENGTH_SHORT,true).show();

                } else {

                    View view2 = LayoutInflater.from(CreateStore.this).inflate(R.layout.dialog_loading, null, false);
                    dialog = new AlertDialog.Builder(CreateStore.this).setView(view2).create();
                    dialog.setCancelable(false);
                    dialog.show();

                    BmobFile bmobFile = new BmobFile(new File(photoaway));
                    bmobFile.uploadblock(new UploadFileListener() {

                        @Override
                        public void done(BmobException e) {
                            if (e == null) {

                                //bmobFile.getFileUrl()--返回的上传文件的完整地址
                                Store store = new Store();
                                store.setIcon(bmobFile);//图片
                                store.setManager(BmobUser.getCurrentUser(_User.class));
                                store.setName(name.getText().toString());//名字
                                store.setInformation(information.getText().toString());//介绍
                                store.setService_scope(service_scope.getText().toString());//服务范围
                                store.setName(notice_content.getText().toString());//公告内容
                                store.setState(states);//状态
                                store.setNotice_state(notice_states);//公告状态
                                store.setAudit_state(false);//审核状态
                                store.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        dialog.dismiss();
                                        if (e == null) {
                                            getstoreinfo();
                                            Toasty.success(CreateStore.this, "创建成功，请等待审核", Toast.LENGTH_SHORT,true).show();
                                            name.setText(null);
                                            information.setText(null);
                                        } else {
                                            Toasty.error(CreateStore.this, "创建失败～", Toast.LENGTH_SHORT,true).show();
                                        }
                                    }
                                });
                            } else {
                                Toasty.error(CreateStore.this, "图片上传失败～", Toast.LENGTH_SHORT,true).show();
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
        if (ActivityCompat.checkSelfPermission(CreateStore.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateStore.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }
}