package Min.app.plus;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import Min.app.plus.utils.CodeUtils;

public class YxapiActivity extends AppCompatActivity {

    private TextView yx_name,yx_dept,yx_xh,yx_major,yx_build,yx_floor,yx_room,yx_bed,yx_school;
    private String str_name,str_dept,str_xh,str_major,str_school,str_build,str_floor,str_room,str_bed;
    private Toolbar toolbar;
    private ImageView barCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yx);

        Intent intent = getIntent();
        str_name = intent.getStringExtra("name");

        str_dept = intent.getStringExtra("dept");
        str_xh = intent.getStringExtra("xh");
        str_major = intent.getStringExtra("major");
        str_school = intent.getStringExtra("school");
        str_build = intent.getStringExtra("build");
        str_floor = intent.getStringExtra("floor");
        str_room = intent.getStringExtra("room");
        str_bed = intent.getStringExtra("bed");
        initUi();
    }private void initUi(){

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("旅院迎新");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        yx_name=findViewById(R.id.yx_name);
        yx_dept=findViewById(R.id.yx_dept);
        yx_xh=findViewById(R.id.yx_xh);
        yx_major=findViewById(R.id.yx_major);
        yx_school=findViewById(R.id.yx_school);
        yx_build=findViewById(R.id.yx_build);
        yx_floor=findViewById(R.id.yx_floor);
        yx_room=findViewById(R.id.yx_room);
        yx_bed=findViewById(R.id.yx_bed);

        yx_name.setText(str_name);
        yx_dept.setText(str_dept);
        yx_xh.setText(str_xh);
        yx_major.setText(str_major);
        yx_school.setText(str_school);
        yx_build.setText(str_build);
        yx_floor.setText(str_floor+" 楼");
        yx_room.setText(str_room);
        yx_bed.setText(str_bed+" 号");


    }public void dialog_photo() {
        View view = LayoutInflater.from(YxapiActivity.this).inflate(R.layout.dialog_photo, null, false);
        AlertDialog dialog_photo = new AlertDialog.Builder(YxapiActivity.this).setView(view).create();
        ImageView img = (ImageView) view.findViewById(R.id.large_image);
        img.setImageBitmap(CodeUtils.createBarcode(str_xh));
        dialog_photo.setView(view); // 自定义dialog
        dialog_photo.setIcon(R.drawable.diablos);
        dialog_photo.setTitle("缴费条形码");
        //dialog_photo.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog_photo.show();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_photo.cancel();
            }
        });

    }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_yx, menu);
            return true;
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if(item.getItemId() == R.id.a){
                //todo
                View dialog_view = getLayoutInflater().inflate(R.layout.dialog_photo, null);
                ImageView img = (ImageView) dialog_view.findViewById(R.id.large_image);
                Glide.with(YxapiActivity.this).load("http://www.diablos.cn/bdlc.jpg").into(img);
                AlertDialog.Builder builder = new AlertDialog.Builder(YxapiActivity.this);
                builder.setIcon(R.drawable.diablos)
                        .setTitle("报道须知")
//                .setMessage("Hel一行内容")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //getroom(ksh.getText().toString());

                            }
                        })
                        .setView(dialog_view)
                        .setCancelable(false)
                        .create()
                        .show();


                return false;
            }else if(item.getItemId() == R.id.b){
                dialog_photo();
            }else if(item.getItemId() == R.id.c){
                Intent intent = new Intent(YxapiActivity.this,WebActivity.class);
                intent.putExtra("title", "虚拟校园");
                intent.putExtra("url", "https://www.720yun.com/t/65vkuw1hsie?scene_id=43141748&sa=oans#scene_id=43141748");
                startActivity(intent);
            }
            return super.onOptionsItemSelected(item);

    }
}
