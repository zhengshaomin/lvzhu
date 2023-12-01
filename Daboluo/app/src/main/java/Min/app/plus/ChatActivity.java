package Min.app.plus;

import static Min.app.plus.utils.DialogUtils.dialogloadingdismiss;
import static Min.app.plus.utils.DialogUtils.dialogloadingshow;
import static Min.app.plus.utils.QueryBasisInfoUtils.userobjectid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Min.app.plus.adapter.ChatAdapter;
import Min.app.plus.bmob.Chat;
import Min.app.plus.bmob.Posts;
import Min.app.plus.bmob.Urelation;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.RealPathFromUriUtils;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.bmob.v3.realtime.Client;
import cn.bmob.v3.realtime.RealTimeDataListener;
import cn.bmob.v3.realtime.RealTimeDataManager;
import es.dmoral.toasty.Toasty;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private static final String TAG = "ChatActivity";
    private List<Chat> msgList = new ArrayList<>();
    private RecyclerView msgRecyclerView;
    private EditText inputText;
    private Button send;
    private LinearLayoutManager layoutManager;
    private ChatAdapter adapter;
    private String photoaway=null;
    private ImageView photo;
    private String relationship_id=null, recipient_id,user_he_name,user_id;//关系id，接收着id
    private JSONObject client_data;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            getmsg();
            monitor_chat();

        }};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        Intent intent = getIntent();
        relationship_id = intent.getStringExtra("relationship_id");
        recipient_id = intent.getStringExtra("recipient_id");
        user_he_name = intent.getStringExtra("user_he_name");

        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        }.start();


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(user_he_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        photo=findViewById(R.id.photo);
        msgRecyclerView = findViewById(R.id.msg_recycler_view);
        //倒序

        inputText = findViewById(R.id.input_text);
        send = findViewById(R.id.send);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        adapter = new ChatAdapter(msgList = getData());
        msgRecyclerView.setLayoutManager(layoutManager);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(photoaway==null){

                    Intent gallery = new Intent(Intent.ACTION_PICK);
                    gallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(gallery, 1000);

                }else {
                    photoaway=null;
                    int resourceId = R.drawable.chattupian; // 替换成你的图片资源ID
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
                    photo.setImageBitmap(bitmap);
                }

            }
        });
/*       我们还需要为button建立一个监听器，我们需要将编辑框的内容发送到 RecyclerView 上：
            ①获取内容，将需要发送的消息添加到 List 当中去。
            ②调用适配器的notifyItemInserted方法，通知有新的数据加入了，赶紧将这个数据加到 RecyclerView 上面去。
            ③调用RecyclerView的scrollToPosition方法，以保证一定可以看的到最后发出的一条消息。*/
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();

                if (photoaway == null) {

                    if (content.length() < 1) {
                        Toasty.warning(ChatActivity.this, "不能发送空消息", Toast.LENGTH_SHORT,true).show();
                    } else  {
                        Chat n = new Chat();
                        n.setMsg_author(BmobUser.getCurrentUser(_User.class));//作者
                        _User recipient = new _User();
                        recipient.setObjectId(recipient_id);
                        n.setMsg_recipient(recipient);

                        Urelation r = new Urelation();
                        r.setObjectId(relationship_id);
                        n.setRelationship(r);//绑定关系

                        n.setMsg_content(content);//内容
                        n.setNews(true);//表示这是一条新消息
                        n.setVisible(true);
                        n.setType(false);
                        n.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    getmsg();
                                    inputText.setText("");//清空输入框中的内容
                                } else {
                                    Toasty.error(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT,true).show();
                                }
                            }
                        });
                    }

                } else {

                    dialogloadingshow(ChatActivity.this);//显示弹窗
                    BmobFile bmobFile = new BmobFile(new File(photoaway));
                    bmobFile.uploadblock(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Chat n = new Chat();
                                n.setMsg_author(BmobUser.getCurrentUser(_User.class));//作者
                                _User recipient = new _User();
                                recipient.setObjectId(recipient_id);
                                n.setMsg_recipient(recipient);

                                Urelation r = new Urelation();
                                r.setObjectId(relationship_id);
                                n.setRelationship(r);//绑定关系

                                n.setMsg_content("[图片]");//内容
                                n.setNews(true);//表示这是一条新消息
                                n.setVisible(true);
                                n.setType(true);
                                n.setPhoto(bmobFile);
                                n.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        dialogloadingdismiss();//关闭弹窗
                                        if (e == null) {
                                            getmsg();
                                            photoaway=null;
                                            int resourceId = R.drawable.chattupian; // 替换成你的图片资源ID
                                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
                                            photo.setImageBitmap(bitmap);
                                        } else {
                                            Toasty.error(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT,true).show();
                                        }
                                    }
                                });
                            } else {
                                Toasty.error(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT, true).show();

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

    public void getmsg() {

            BmobQuery<Chat> bmobQuery = new BmobQuery<>();
            bmobQuery.include("msg_author,msg_recipient,relationship");
            bmobQuery.addWhereEqualTo("relationship", relationship_id);//查询可见的
            bmobQuery.order("createdAt");//依照数据排序时间排序
            bmobQuery.setLimit(500);
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
            bmobQuery.findObjects(new FindListener<Chat>() {
                @Override
                public void done(List<Chat> object, BmobException e) {

                    if (e == null) {
                        msgList.clear();
                        for (int i = 0; i < object.size(); i++) {
                            msgList.add(object.get(i));
                        }
                        msgRecyclerView.setAdapter(adapter);
                        // home_swip.setRefreshing(false);
                    } else {
                        Toasty.error(ChatActivity.this, "数据加载失败！", Toast.LENGTH_SHORT,true).show();
                        //home_swip.setRefreshing(false);

                    }

                }
            });

    }

    private List<Chat> getData() {
        List<Chat> list = new ArrayList<>();
//        list.add(new Chat("Hello",Chat.TYPE_RECEIVED));
        return list;
    }
    //监听聊天消息
    public void monitor_chat(){
        RealTimeDataManager.getInstance().start(new RealTimeDataListener() {
            @Override
            public void onConnectCompleted(Client client, Exception e) {
                if (e == null) {
                    //TODO 如果已连接，设置监听动作为：监听Chat表的更新
                    client.subTableUpdate("Chat");//
                    // }
                } else {
                    monitor_chat();
                }
            }
            @Override
            public void onDataChange(Client client, JSONObject jsonObject) {
                Gson gson = new Gson();
                String action = jsonObject.optString("action");
                String jsonString = gson.toJson(jsonObject);
                if (action.equals(Client.ACTION_UPDATE_TABLE)) {
                    //TODO 如果监听表更新
                    client_data = jsonObject.optJSONObject("data");
                    //Toast.makeText(MainActivity.this, action+"监听到更新：" + data.optString("recipient"), Toast.LENGTH_SHORT).show();
                    if(userobjectid.equals(client_data.optString("msg_recipient"))){
                        Log.d(TAG,"郑绍敏");
//                        Toast.makeText(MainActivity.this, "有消息", Toast.LENGTH_SHORT).show();
                       getmsg();

                    }
                }
            }
            @Override
            public void onDisconnectCompleted(Client client) {

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

            photo.setImageBitmap(bitmap);


        }

        requestWritePermission();
    }
    private void requestWritePermission(){
        if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

    }
}
