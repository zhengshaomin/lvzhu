package Min.app.plus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.io.File;

import Min.app.plus.bmob.Posts;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.RealPathFromUriUtils;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import es.dmoral.toasty.Toasty;

/**
 * @author daboluo
 */
public class AddActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Switch visible,anonymous;
    private Boolean visibles=true,anonymouss=false;
    private EditText content;
    private LinearLayout choose_photo;
    private ImageView photo;
    private String photoaway=null;
    private AlertDialog dialog;


    private TextView pv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);



        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("发布");
        content=findViewById(R.id.content);
        visible=findViewById(R.id.visible);
        anonymous=findViewById(R.id.anonymous);
        choose_photo=findViewById(R.id.choose_photo);
        photo=findViewById(R.id.photo);
        pv=findViewById(R.id.pv);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });


        choose_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(photoaway==null){

                    Intent gallery = new Intent(Intent.ACTION_PICK);
                    gallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(gallery, 1000);

                }else {
                    photoaway=null;
                    photo.setImageBitmap(null);
                    pv.setText("添加图片");
                }

            }
        });


        visible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //所有人可见
                    visible.setText("所有人可见");
                    visibles=true;
                } else {
                    //仅自己可见
                    visible.setText("仅自己可见");
                    visibles=false;
                }
            }
        });
        anonymous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //所有人可见
                    anonymous.setText("匿名");
                    anonymouss=true;
                } else {
                    //仅自己可见
                    anonymous.setText("不匿名");
                    anonymouss=false;
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send:

                View view = LayoutInflater.from(AddActivity.this).inflate(R.layout.dialog_loading, null, false);
                dialog = new AlertDialog.Builder(AddActivity.this).setView(view).create();
                dialog.setCancelable(false);
                dialog.show();

                if (photoaway == null) {

                    Posts post = new Posts();
                    post.setContent(content.getText().toString());//内容
                    post.setAnonymous(anonymouss);//是否匿名
                    post.setVisible(visibles);//是否公开
                    post.setAudit_state(false);//审核状态
//                    post.setView(0);
//                    post.setReplys(0);
                    //添加一对一关联，用户关联帖子
                    post.setAuthor(BmobUser.getCurrentUser(_User.class));//作者
                    post.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            dialog.dismiss();
                            if (e == null) {
                                Toasty.success(AddActivity.this, "发布成功", Toast.LENGTH_SHORT, true).show();
                                content.setText(null);
                            } else {
                                Toasty.error(AddActivity.this, "发布失败", Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    });

                } else {

                    BmobFile bmobFile = new BmobFile(new File(photoaway));
                    bmobFile.uploadblock(new UploadFileListener() {

                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                //bmobFile.getFileUrl()--返回的上传文件的完整地址
                                Posts post = new Posts();
                                post.setContent(content.getText().toString());//内容
                                post.setAnonymous(anonymouss);//是否匿名
                                post.setVisible(visibles);//是否公开
                                post.setAudit_state(false);//审核状态
                                post.setPhoto(bmobFile);
//                                post.setView(0);
//                                post.setReplys(0);
                                //添加一对一关联，用户关联帖子
                                post.setAuthor(BmobUser.getCurrentUser(_User.class));//作者
                                post.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        dialog.dismiss();
                                        if (e == null) {
                                            Toasty.success(AddActivity.this, "发布成功", Toast.LENGTH_SHORT, true).show();
                                            content.setText(null);
                                        } else {
                                            Toasty.error(AddActivity.this, "发布失败", Toast.LENGTH_SHORT, true).show();
                                        }
                                    }
                                });
                            } else {
                                Toasty.error(AddActivity.this, "发布失败", Toast.LENGTH_SHORT, true).show();
                                dialog.dismiss();
                            }

                        }

                        @Override
                        public void onProgress(Integer value) {
                            // 返回的上传进度（百分比）
                        }
                    });

                }
                break;

        }
        return super.onOptionsItemSelected(item);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == RESULT_OK){

            photoaway = RealPathFromUriUtils.getRealPathFromUri(this, data.getData());

            Bitmap bitmap = BitmapFactory.decodeFile(photoaway);

            photo.setImageBitmap(bitmap);
            pv.setText("删除图片");


        }

        requestWritePermission();
    }
    private void requestWritePermission(){
        if (ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

    }
}
