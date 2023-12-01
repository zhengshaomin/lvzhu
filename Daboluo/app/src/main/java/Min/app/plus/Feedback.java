package Min.app.plus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import Min.app.plus.bmob.Feed;
import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author daboluo
 */
public class Feedback extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText content;
    private Button send;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed);
        Intent intent = getIntent();

        toolbar = findViewById(R.id.toolbar);
        content=findViewById(R.id.content);
        send=findViewById(R.id.send);
        toolbar.setTitle("意见反馈");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Feed feed = new Feed();
                feed.setContent(content.getText().toString());//内容
                //添加一对一关联，用户关联帖子
                feed.setAuthor(BmobUser.getCurrentUser(_User.class));//作者
                feed.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            Snackbar.make(toolbar, "反馈成功", Snackbar.LENGTH_SHORT).show();
                            content.setText(null);
                        } else {
                            Snackbar.make(toolbar, "反馈失败", Snackbar.LENGTH_SHORT).show();
                        }
                    }});
            }
        });
    }}