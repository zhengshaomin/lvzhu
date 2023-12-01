package Min.app.plus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;

import java.io.File;

import Min.app.plus.bmob.Posts;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.RealPathFromUriUtils;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import es.dmoral.toasty.Toasty;

/**
 * @author daboluo
 */
public class UpdateActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Switch visible,anonymous;
    private EditText content;
    private String CONTENT,ID,PHOTOURL,CHANGE;
    private LinearLayout choose_photo;
    private ImageView photo;
    private TextView pv;
    private Boolean VISIBLE,ANONYMOUS;
    private AlertDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update);

        final Intent intent=getIntent();
        VISIBLE=intent.getBooleanExtra("VISIBLE",true);
        CONTENT=intent.getStringExtra("CONTENT");
        ID=intent.getStringExtra("ID");
        ANONYMOUS=intent.getBooleanExtra("ANONYMOUS",true);
        PHOTOURL=intent.getStringExtra("PHOTOURL");
        toolbar=findViewById(R.id.toolbar);
        content=findViewById(R.id.content);
        visible=findViewById(R.id.visible);
        anonymous=findViewById(R.id.anonymous);
        photo=findViewById(R.id.photo);
        content.setText(CONTENT);
        visible.setChecked(VISIBLE);
        anonymous.setChecked(ANONYMOUS);
        choose_photo=findViewById(R.id.choose_photo);
        pv=findViewById(R.id.pv);
        toolbar.setTitle("编辑");
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
        if(VISIBLE==true){
            visible.setText("所有人可见");
        } else {
            //仅自己可见
            visible.setText("仅自己可见");
        }
        if(ANONYMOUS==true){
            anonymous.setText("匿名");
        } else {
            //仅自己可见
            anonymous.setText("不匿名");
        }
        choose_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PHOTOURL==null){
                    Intent it = new Intent(Intent.ACTION_PICK);
                    //设置格式
                    it.setType("image/*");
                    startActivityForResult(it, 1000);
                }else {
                    PHOTOURL=null;
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
                    VISIBLE=true;
                } else {
                    //仅自己可见
                    visible.setText("仅自己可见");
                    VISIBLE=false;
                }
            }
        });
        anonymous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //所有人可见
                    anonymous.setText("匿名");
                    ANONYMOUS=true;
                } else {
                    //仅自己可见
                    anonymous.setText("不匿名");
                    ANONYMOUS=false;
                }
            }
        });
        if(PHOTOURL!=null){
            Glide.with(UpdateActivity.this).load(PHOTOURL).crossFade(200).into(photo);
            CHANGE=PHOTOURL;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.send:

                View view = LayoutInflater.from(UpdateActivity.this).inflate(R.layout.dialog_loading, null, false);
                dialog = new AlertDialog.Builder(UpdateActivity.this).setView(view).create();
                dialog.setCancelable(false);
                dialog.show();

                if (PHOTOURL == null) {
                    Posts post = new Posts();
                    post.setContent(content.getText().toString());//内容
                    post.setAnonymous(ANONYMOUS);//是否匿名
                    post.setVisible(VISIBLE);//是否公开

                    //添加一对一关联，用户关联帖子
                    post.setAuthor(BmobUser.getCurrentUser(_User.class));//作者
                    post.update(ID, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            dialog.dismiss();
                            if(e==null){
                                Toasty.success(UpdateActivity.this, "编辑成功！", Toast.LENGTH_SHORT,true).show();
                                content.setText(null);
                            }else{
                                Toasty.error(UpdateActivity.this, "编辑失败！", Toast.LENGTH_SHORT,true).show();
                            }
                        }

                    });
                } else if(CHANGE.equals(PHOTOURL)){

                    Posts post = new Posts();
                    post.setContent(content.getText().toString());//内容
                    post.setAnonymous(ANONYMOUS);//是否匿名
                    post.setVisible(VISIBLE);//是否公开

                    //添加一对一关联，用户关联帖子
                    post.setAuthor(BmobUser.getCurrentUser(_User.class));//作者
                    post.update(ID, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            dialog.dismiss();
                            if(e==null){
                                Toasty.success(UpdateActivity.this, "编辑成功！", Toast.LENGTH_SHORT,true).show();
                                content.setText(null);
                            }else{
                                Toasty.error(UpdateActivity.this, "编辑失败！", Toast.LENGTH_SHORT,true).show();
                            }
                        }

                    });

                }else {

                    BmobFile bmobFile = new BmobFile(new File(PHOTOURL));
                    bmobFile.uploadblock(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                //bmobFile.getFileUrl()--返回的上传文件的完整地址
                                Posts post = new Posts();
                                post.setContent(content.getText().toString());//内容
                                post.setAnonymous(ANONYMOUS);//是否匿名
                                post.setVisible(VISIBLE);//是否公开
                                post.setPhoto(bmobFile);
                                //添加一对一关联，用户关联帖子
                                post.update(ID, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        dialog.dismiss();
                                        if(e==null){
                                            Toasty.success(UpdateActivity.this, "编辑成功！", Toast.LENGTH_SHORT,true).show();
                                            content.setText(null);
                                        }else{
                                            Toasty.error(UpdateActivity.this, "编辑失败！", Toast.LENGTH_SHORT,true).show();
                                        }
                                    }

                                });
                            } else {
                                Toasty.error(UpdateActivity.this, "图片上传失败！", Toast.LENGTH_SHORT,true).show();
                                dialog.dismiss();
                            }

                        }

                        @Override
                        public void onProgress(Integer value) {
                            // 返回的上传进度（百分比）
                        }
                    });

                }
                Posts post = new Posts();
                post.setContent(content.getText().toString());//内容
                post.setAnonymous(ANONYMOUS);//是否匿名
                post.setVisible(VISIBLE);//是否公开
                post.update(ID, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            Toasty.success(UpdateActivity.this, "编辑成功！", Toast.LENGTH_SHORT,true).show();
                            content.setText(null);
                        }else{
                            Toasty.error(UpdateActivity.this, "编辑失败！", Toast.LENGTH_SHORT,true).show();
                        }
                    }

                });

                break;

        }
        return super.onOptionsItemSelected(item);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == RESULT_OK){

            PHOTOURL = RealPathFromUriUtils.getRealPathFromUri(this, data.getData());

            Bitmap bitmap = BitmapFactory.decodeFile(PHOTOURL);

            photo.setImageBitmap(bitmap);
            pv.setText("删除图片");

        }

        requestWritePermission();
    }
    private void requestWritePermission(){
        if (ActivityCompat.checkSelfPermission(UpdateActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UpdateActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }
}