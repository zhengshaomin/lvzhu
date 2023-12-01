package Min.app.plus;

import static Min.app.plus.utils.QueryBasisInfoUtils.userobjectid;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Min.app.plus.adapter.ChatgptAdapter;
import Min.app.plus.application.App;
import Min.app.plus.bmob.ai_log;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.ai.ChatMessageListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import es.dmoral.toasty.Toasty;

public class ChatgptActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    RecyclerView recyclerView;
    EditText messageEditText;
    ImageButton sendbutton;
    private List<ai_log> ailist = new ArrayList<>();
    ChatgptAdapter adapter;
    private boolean iscancel=false;//不可取消

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            getdata();

        }};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatgpt);

        initUi();
        initLitener();
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        }.start();
    }
    private void initUi() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        messageEditText = findViewById(R.id.message_edit_text);
        sendbutton = findViewById(R.id.send_btn);
    }
    private void initLitener(){
        adapter=new ChatgptAdapter(ailist);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager( ChatgptActivity.this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        toolbar.setTitle("AI对话");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question=messageEditText.getText().toString();
                if(iscancel){
                    //执行取消操作
                    App.bmobAI.Stop();
                    addResponse("已停止回答");
                    sendbutton.setImageResource(R.drawable.ic_baseline_send_fend_24);
                    iscancel=false;
                }else if(question.length()<1){
                    Toasty.warning(ChatgptActivity.this, "请至少输入一个字符", Toast.LENGTH_SHORT,true).show();
                }else {
                    iscancel=true;
                    addToChat(question,"user");
                    sendmessage(question);
                    sendbutton.setImageResource(R.drawable.ic_baseline_cancel_24);
                    messageEditText.setText("");
                }
            }
        });
    }
    //增加一行数据
    private void addToChat(String message,String role){

        runOnUiThread (new Runnable () {
            @Override
            public void run() {
                ailist.add (new ai_log(message,role,userobjectid)) ;
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });
    }
    //先减少一行数据，再增加一行数据
    void addResponse(String response){
        ailist.remove (ailist.size()-1);
        addToChat(response,"assistant");
    }
    //发送消息
    private void sendmessage(String question){
        //连接AI服务器（这个代码为了防止AI连接中断，因为可能会存在某些情况下，比如网络切换、中断等，导致心跳连接失败）
        App.bmobAI.Connect();
        //模拟Chatgpt的答
        ailist.add (new ai_log("请稍等...","assistant",userobjectid));
        App.bmobAI.Chat(question, userobjectid, new ChatMessageListener() {
            @Override
            public void onMessage(String message) {
                //消息流的形式返回AI的结果
            }

            @Override
            public void onFinish(String message) {
                //一次性返回全部结果，这个方法需要等待一段时间，友好性较差
                addResponse(message);
                iscancel=false;
                sendbutton.setImageResource(R.drawable.ic_baseline_send_fend_24);
            }

            @Override
            public void onError(String error) {
                //OpenAI的密钥错误或者超过OpenAI并发时，会返回这个错误
                Log.d("Bmob", "连接发生异常了"+error);
                addResponse("Failed to load response due to "+error);
            }

            @Override
            public void onClose() {
                Log.d("Bmob", "连接被关闭了");
            }
        });
    }
    //查询历史记录
    private void getdata(){
        BmobQuery<ai_log> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("session",userobjectid);//查询可见的
        bmobQuery.order("createdAt");//依照数据排序时间排序
        bmobQuery.setLimit(200);
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);// 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        bmobQuery.findObjects(new FindListener<ai_log>() {
            @Override
            public void done(List<ai_log> object, BmobException e) {

                if (e == null) {
                    ailist.clear();
                    for (int i = 0; i < object.size(); i++) {
                        ailist.add(object.get(i));
                    }
                    recyclerView.setAdapter(adapter);
                    // home_swip.setRefreshing(false);
                } else {
                    Toasty.error(ChatgptActivity.this, "数据加载失败！"+e, Toast.LENGTH_SHORT,true).show();
                    //home_swip.setRefreshing(false);

                }

            }
        });
    }
}
