package Min.app.plus.fragment.main;


import static Min.app.plus.utils.QueryBasisInfoUtils.userobjectid;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import Min.app.plus.ChatActivity;
import Min.app.plus.R;
import Min.app.plus.bmob.Chat;
import Min.app.plus.bmob.Reply;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.GlideActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.realtime.Client;
import cn.bmob.v3.realtime.RealTimeDataListener;
import cn.bmob.v3.realtime.RealTimeDataManager;
import es.dmoral.toasty.Toasty;

/**
 * @author daboluo
 */
//reply表
public class ChatFragment extends Fragment {

    private View view;


    private SwipeMenuListView chat_list;
    private int t;
    private String user_me_id;
    private String[] str, usernamestr, headportraitstr, relationshipstr;
    private int select = 0;

    List<Chat> chats = new ArrayList<Chat>();
    private List<String> usernamelist = new ArrayList<String>();
    private List<String> headportraitlist = new ArrayList<String>();
    private List<String> relationshiplist = new ArrayList<String>();
    private List<String> recipientlist = new ArrayList<String>();
    private List<String> signaturelist = new ArrayList<String>();

    private String post_id, user_id, reply_id, recipient_id, ship_id, user_he_name, user_he_head, relationship_id = null, order_id = null;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getchatdata();
            min();

        }
    };


    private SwipeMenuCreator creator;
    private SmartRefreshLayout chat_srlControl;
    private AlertDialog dialog;
    private JSONObject data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.chatfragment, container, false);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        chat_srlControl = getActivity().findViewById(R.id.chat_srl_control);
        //监听下拉和上拉状态
        //下拉刷新
        chat_srlControl.setOnRefreshListener(refreshlayout -> {
            chat_srlControl.setEnableRefresh(true);//启用刷新
            getchatdata();
            chat_srlControl.finishRefresh();//结束刷新
        });


        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_loading, null, false);
        dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        dialog.setCancelable(false);
        chat_list = getActivity().findViewById(R.id.chat_list);


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set item title
                deleteItem.setTitle("删除");
                // set item title fontsize
                deleteItem.setTitleSize(18);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(deleteItem);
            }
        };
// 不要忘记了

        chat_list.setMenuCreator(creator);

        chat_list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // 删除
//                        reply_id = chats.get(position).getObjectId();
//                        delete();
                        Toasty.warning(getActivity(), "消息列表无法删除哦～", Toast.LENGTH_SHORT, true).show();
                        break;

                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });


        chat_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                getmsg(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("relationship_id", relationshiplist.get(position));
                intent.putExtra("user_he_name", usernamelist.get(position));
                intent.putExtra("recipient_id", recipientlist.get(position));
                startActivity(intent);
                //do what you want
            }
        });

        chat_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean enable = false;
                if (chat_list != null && chat_list.getChildCount() > 0) {
                    // 检查chat_listView第一个item是否可见
                    boolean firstItemVisible = chat_list.getFirstVisiblePosition() == 0;
                    // 检查第一个item的顶部是否可见
                    boolean topOfFirstItemVisible = chat_list.getChildAt(0).getTop() == 0;
                    // 启用或者禁用view_reply_swipeRefreshLayout刷新标识
                    enable = firstItemVisible && topOfFirstItemVisible;
                } else if (chat_list != null && chat_list.getChildCount() == 0) {
                    // 没有数据的时候允许刷新
                    enable = true;
                }
                // 把标识传给view_reply_swipeRefreshLayout
                //view_reply_swip.setEnabled(enable);
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

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public void getchatdata() {

        BmobQuery<Chat> eq1 = new BmobQuery<>();
        eq1.addWhereEqualTo("visible", true);//查询可见的
        BmobQuery<Chat> eq2 = new BmobQuery<>();
        eq2.addWhereEqualTo("msg_recipient", BmobUser.getCurrentUser(_User.class));

        List<BmobQuery<Chat>> andQuerys = new ArrayList<BmobQuery<Chat>>();
        //andQuerys.add(eq1);
        andQuerys.add(eq2);

        BmobQuery<Chat> bmobQuery = new BmobQuery<>();
        bmobQuery.include("msg_author,msg_recipient,relationship");
        bmobQuery.order("-createdAt");//依照数据排序时间排序
        bmobQuery.and(andQuerys);
        bmobQuery.setLimit(500);
        boolean isCache = bmobQuery.hasCachedResult(Chat.class);
        if (isCache) {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);   // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        } else {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);// 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
            bmobQuery.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存2天
        }
        bmobQuery.findObjects(new FindListener<Chat>() {
            @Override
            public void done(List<Chat> object, BmobException e) {
                if (e == null) {
                    dialog.dismiss();
                    usernamelist.clear();
                    headportraitlist.clear();
                    signaturelist.clear();
                    recipientlist.clear();
                    relationshiplist.clear();
                    for (Chat get : object) {
                        usernamelist.add(get.getMsg_author().getUsername());//用户名
                        signaturelist.add(get.getMsg_author().getSignature());
                        headportraitlist.add(get.getMsg_author().getQq());//头像
                        relationshiplist.add(get.getRelationship().getObjectId());//关系id
                        recipientlist.add(get.getMsg_author().getObjectId());//

                        usernamelist = usernamelist.stream().distinct().collect(Collectors.toList());
                        signaturelist = signaturelist.stream().distinct().collect(Collectors.toList());
                        headportraitlist = headportraitlist.stream().distinct().collect(Collectors.toList());
                        relationshiplist = relationshiplist.stream().distinct().collect(Collectors.toList());
                        recipientlist = recipientlist.stream().distinct().collect(Collectors.toList());
//                        usernamestr=usernamelist.toArray(new String[0]);
//                        headportraitstr=headportraitlist.toArray(new String[0]);
//                        relationshipstr=relationshiplist.toArray(new String[0]);

                    }
                    chat_list.setAdapter(new ChatFragment.ItemListAdapter());
//                    for (int i = 0; i < object.size(); i++) {
//                        chats=chats.stream().distinct().collect(Collectors.toList());//去重
//                        chats.add(object.get(i));
//                    }
//                    chat_list.setAdapter(new ChatFragment.ItemListAdapter());
                    //view_reply_swip.setRefreshing(false);
                } else {
                    Toasty.error(getActivity(), "出现错误！", Toast.LENGTH_SHORT, true).show();
                    //view_reply_swip.setRefreshing(false);
                    //Snackbar.make(view_reply_swip, "刷新失败", Snackbar.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void min() {
        RealTimeDataManager.getInstance().start(new RealTimeDataListener() {
            @Override
            public void onConnectCompleted(Client client, Exception e) {
                dialog.dismiss();
                if (e == null) {
                    //TODO 如果已连接，设置监听动作为：监听Chat表的更新
                    client.subTableUpdate("Chat");//
                    // }
                } else {
//链接失败
                }
            }

            @Override
            public void onDataChange(Client client, JSONObject jsonObject) {
                Gson gson = new Gson();
                String action = jsonObject.optString("action");
                String jsonString = gson.toJson(jsonObject);
                //Log.i(TAG, "更新返回内容是：" + jsonString);
                //Log.i(TAG, "当前更新动作是：" + action);
                if (action.equals(Client.ACTION_UPDATE_TABLE)) {
                    //TODO 如果监听表更新
                    data = jsonObject.optJSONObject("data");
                    if (BmobUser.getCurrentUser(_User.class).getObjectId().equals(data.optString("msg_recipient"))) {
                        getchatdata();
                        //有更新
                    }
                }
            }

            @Override
            public void onDisconnectCompleted(Client client) {

            }
        });
    }
    private void getmsg(int position){
        BmobQuery<Chat> query = new BmobQuery<>();
        query.order("-createdAt");//依照数据排序时间排序
        query.include("msg_author");
        query.addWhereEqualTo("relationship", relationshiplist.get(position));
        query.findObjects(new FindListener<Chat>() {
            @Override
            public void done(List<Chat> object, BmobException e) {
                if(e==null){

                    if(!object.get(0).getNews()){

                    }else if(object.get(0).getMsg_author().getObjectId().equals(userobjectid)){

                    }else {
                        updatamsg(object.get(0).getObjectId());
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage());
                }
            }

        });
    }
    private void updatamsg(String chat_id){
        Chat p2 = new Chat();
        p2.setNews(false);
        p2.update(chat_id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    getchatdata();
                } else {
                }
            }

        });
    }
    class ItemListAdapter extends BaseAdapter
    {
        //private Chat fenxiang;
        //适配器
        @Override
        public int getCount()
        {
            if (usernamelist.size() > 0)
            {
                return usernamelist.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position)
        {
            return usernamelist.get(position);
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ChatFragment.ItemListAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder =new ChatFragment.ItemListAdapter.ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_news, null);
                viewHolder.news_username = (TextView) convertView.findViewById(R.id.news_username);
                viewHolder.news_linearLayout = (LinearLayout) convertView.findViewById(R.id.news_linearlayout);
                viewHolder.news_headportrait = (ImageView) convertView.findViewById(R.id.news_headportrait);
                viewHolder.news_content=convertView.findViewById(R.id.news_content);
                viewHolder.time = (TextView) convertView.findViewById(R.id.time);
                viewHolder.newone=convertView.findViewById(R.id.newone);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ChatFragment.ItemListAdapter.ViewHolder)convertView.getTag();
            }

            //fenxiang=chats.get(position);
            Glide.with(getActivity()).load("https://q.qlogo.cn/headimg_dl?dst_uin=" + headportraitlist.get(position)+ "&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(getActivity())).into(viewHolder.news_headportrait);
            viewHolder.news_username.setText(usernamelist.get(position));
            //viewHolder.news_content.setText(signaturelist.get(position));
            //String t=fenxiang.getCreatedAt();
//            viewHolder.time.setText(t.substring(t.indexOf(" ")));
//
//            if(chats.get(position).isNews()==true){
//                viewHolder.newone.setVisibility(View.VISIBLE);
//            }

            BmobQuery<Chat> query = new BmobQuery<>();
            query.order("-createdAt");//依照数据排序时间排序
            query.include("msg_author");
            query.addWhereEqualTo("relationship", relationshiplist.get(position));
            query.findObjects(new FindListener<Chat>() {
                @Override
                public void done(List<Chat> object, BmobException e) {
                    if(e==null){

                        viewHolder.news_content.setText(object.get(0).getMsg_content());

                        if(!object.get(0).getNews()){

                           }else if(object.get(0).getMsg_author().getObjectId().equals(userobjectid)){

                        }else {
                            viewHolder.newone.setVisibility(View.VISIBLE);
                        }
                    }else{
                        Log.i("bmob","失败："+e.getMessage());
                    }
                }

            });
            return convertView;
        }
        public class ViewHolder
        {
            public TextView news_username,news_content,news_type,newone,time;
            public LinearLayout news_linearLayout;
            public ImageView news_headportrait,photo,like;

        }



    }
}


