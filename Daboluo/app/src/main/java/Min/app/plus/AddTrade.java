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

import androidx.annotation.Nullable;
import androidx.annotation.StyleableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;


/**
 * @author daboluo
 */
public class AddTrade extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText title,content,price;
    private LinearLayout choose_photo;
    private ImageView photo;
    private String photoaway2;
    private AlertDialog dialog;
    private TextView pv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtrade);

        Bmob.initialize(this, "6d233c0993a3ab132ba5e11b0942960b");


       /* toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("发布商品");
        title=findViewById(R.id.title);
        content=findViewById(R.id.content);
        price=findViewById(R.id.price);
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
                if(photoaway2==null){
                    Intent it = new Intent(Intent.ACTION_PICK);
                    //设置格式
                    it.setType("image");
                    startActivityForResult(it, 1000);
                }else {
                    photoaway2=null;
                    photo.setImageBitmap(null);
                    pv.setText("添加图片");
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

                if(title.getText().toString().length()<1){
                    Snackbar.make(toolbar, "请添加标题", Snackbar.LENGTH_SHORT).show();
                }else  if(content.getText().toString().length()<1){
                    Snackbar.make(toolbar, "请添加描述", Snackbar.LENGTH_SHORT).show();
                }else if (photoaway == null) {
                    Snackbar.make(toolbar, "请添加图片", Snackbar.LENGTH_SHORT).show();
                } else {

                    View view = LayoutInflater.from(AddTrade.this).inflate(R.layout.s, null, false);
                    dialog = new AlertDialog.Builder(AddTrade.this).setView(view).create();
                    dialog.setCancelable(false);
                    dialog.show();

                BmobFile bmobFile = new BmobFile(new File(photoaway2));
                bmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                                //bmobFile.getFileUrl()--返回的上传文件的完整地址
                                Goods trade = new Goods();
                                trade.setTitle(title.getText().toString());//标题
                                trade.setContent(content.getText().toString());//内容
                                trade.setPrice(Double.parseDouble(price.getText().toString().trim()));
                                trade.setPhoto(bmobFile);
                                //添加一对一关联，用户关联帖子
                                trade.setAuthor(BmobUser.getCurrentUser(_User.class));//作者
                                trade.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        dialog.dismiss();
                                        if (e == null) {
                                            Snackbar.make(toolbar, "发布成功", Snackbar.LENGTH_SHORT).show();
                                            content.setText(null);
                                        } else {
                                            Snackbar.make(toolbar, "发布失败", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Snackbar.make(toolbar, "图片上传失败", Snackbar.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }

                        }

                        @Override
                        public void onProgress(Integer value) {
                            // 返回的上传进度（百分比）
                        }
                    });

                //}
                break;

        }
        return super.onOptionsItemSelected(item);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == RESULT_OK){

            photoaway2= RealPathFromUriUtils.getRealPathFromUri(this, data.getData());

            Bitmap bitmap = BitmapFactory.decodeFile(photoaway2);

            photo.setImageBitmap(bitmap);
            pv.setText("删除图片");

        }

        requestWritePermission();
    }
    private void requestWritePermission(){
        if (ActivityCompat.checkSelfPermission(AddTrade.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddTrade.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
*/
    }
}