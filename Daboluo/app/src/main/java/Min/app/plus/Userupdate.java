package Min.app.plus;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.Glide;

import java.util.Calendar;

import Min.app.plus.bmob._User;
import Min.app.plus.utils.GlideActivity;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;

/**
 * @author daboluo
 */
public class Userupdate extends AppCompatActivity {

    private CoordinatorLayout c;
    private Toolbar toolbar;
    private LinearLayout head;
    private ImageView headportrait;
    private EditText username,signature,qq,birthday,telephone;
    private Switch sex;
    private Button accomplish;
    private String user_id;
    private Boolean sexs;
    private int select = 0; //表示单选对话框初始时选中哪一项
    private String[] report = {"男娃儿", "女娃娃"};
    private String d;
    private int year,month,day,boluo;
    private AlertDialog dialog;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            getuser();

        }};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cs5);

        Bmob.initialize(this, "6d233c0993a3ab132ba5e11b0942960b");

        toolbar=findViewById(R.id.toolbar);
        username=findViewById(R.id.username);
        head=findViewById(R.id.head);
        birthday=findViewById(R.id.birthday);
        signature=findViewById(R.id.signature);
        headportrait=findViewById(R.id.headportrait);
        telephone=findViewById(R.id.telephone);
        sex=findViewById(R.id.sex);
        qq=findViewById(R.id.qq);
        accomplish=findViewById(R.id.accomplish);
        toolbar.setTitle("编辑资料");
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
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.warning(Userupdate.this, "头像暂时不能直接修改！", Toast.LENGTH_SHORT,true).show();
            }
        });
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View view2 = LayoutInflater.from(Userupdate.this).inflate(R.layout.time, null, false);
                final AlertDialog dialog2 = new AlertDialog.Builder(Userupdate.this).setView(view2).create();
                DatePicker datePicker=view2.findViewById(R.id.datePikcker);
                Calendar calendar=Calendar.getInstance();
                year=calendar.get(Calendar.YEAR);
                month=calendar.get(Calendar.MONTH);
                day=calendar.get(Calendar.DAY_OF_MONTH);
                datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Userupdate.this.year=year;
                        Userupdate.this.month=monthOfYear;
                        Userupdate.this.day=dayOfMonth;
                    }
                });

               dialog2.setButton(DialogInterface.BUTTON_NEGATIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        birthday.setText(year+"-"+(month+1)+"-"+day);
                        dialog2.dismiss();
                    }
                });
               dialog2.setButton(DialogInterface.BUTTON_POSITIVE, "取消", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       dialog2.dismiss();
                   }
               });
                dialog2.setCancelable(false);
                dialog2.show();
            }
        });
        d=year+"-"+(month+1)+"-"+day;
        sex.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //所有人可见
                    sex.setText("女娃娃");
                    sexs=true;
                } else {
                    //仅自己可见
                    sex.setText("男娃娃");
                    sexs=false;
                }
            }
        });
        telephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.warning(Userupdate.this, "手机号码暂时不能修改！", Toast.LENGTH_SHORT,true).show();
            }
        });
        accomplish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(d.equals(d=year+"-"+(month+1)+"-"+day)){
                    _User p2 = new _User();
                    p2.setUsername(username.getText().toString());
                    p2.setSignature(signature.getText().toString());
                    p2.setSex(sexs);
                    p2.setQq(qq.getText().toString());
                    p2.update(user_id, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Toasty.success(Userupdate.this, "修改成功", Toast.LENGTH_SHORT,true).show();
                            }else{
                                Toasty.error(Userupdate.this, "修改失败", Toast.LENGTH_SHORT,true).show();
                            }
                        }

                    });
                }else {
                    _User p2 = new _User();
                    p2.setUsername(username.getText().toString());
                    p2.setSignature(signature.getText().toString());
                    p2.setSex(sexs);
                    p2.setBirthday(year+"-"+(month+1)+"-"+day);
                    p2.setQq(qq.getText().toString());
                    p2.update(user_id, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Toasty.success(Userupdate.this, "修改成功", Toast.LENGTH_SHORT,true).show();
                            }else{
                                Toasty.error(Userupdate.this, "修改失败", Toast.LENGTH_SHORT,true).show();
                            }
                        }

                    });
                }

            }
        });
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        }.start();
    }
    public void getuser(){
        BmobQuery<_User> bq1 = new BmobQuery<_User>();
        bq1.getObject((String) BmobUser.getObjectByKey("objectId"), new QueryListener<_User>() {
            @Override
            public void done(_User object, BmobException e) {
                if (e == null) {
                    user_id=object.getObjectId();
                    username.setText(object.getUsername());
                   if (object.getSex()==null){
                   }else {
                       sex.setChecked(object.getSex());
                   }
                    birthday.setText(object.getBirthday());
                    qq.setText(object.getQq());
                    telephone.setText(object.getMobilePhoneNumber());
                    signature.setText(object.getSignature());
                    Glide.with(Userupdate.this).load("https://q.qlogo.cn/headimg_dl?dst_uin=" + object.getQq() + "&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(Userupdate.this)).into(headportrait);

                }
            }});

    }}