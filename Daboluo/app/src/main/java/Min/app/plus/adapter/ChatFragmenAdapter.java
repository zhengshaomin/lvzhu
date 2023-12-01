package Min.app.plus.adapter;

import static Min.app.plus.utils.QueryBasisInfoUtils.userobjectid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import Min.app.plus.MuserActivity;
import Min.app.plus.R;
import Min.app.plus.UserActivity;
import Min.app.plus.bmob.Chat;
import Min.app.plus.bmob.Urelation;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.GlideActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 作者：daboluo on 2023/9/20 18:18
 * Email:daboluo719@gmail.com
 */
public class ChatFragmenAdapter extends RecyclerView.Adapter<ChatFragmenAdapter.ViewHolder>{

    private Context context;
    private List<Chat> chats;
    public ChatFragmenAdapter(List<Chat> list){
        this.chats = list;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView news_username,news_content,news_type,newone,time;
        public LinearLayout news_linearLayout;
        public ImageView news_headportrait,photo,like;

        public ViewHolder(View view){
            super(view);

            news_username =  view.findViewById(R.id.chat_username);
            news_linearLayout = view.findViewById(R.id.news_linearlayout);
            news_headportrait =  view.findViewById(R.id.chat_headportrait);
            news_content=view.findViewById(R.id.chat_content);
            time = view.findViewById(R.id.time);
            newone=view.findViewById(R.id.newone);
        }
    }

    @NonNull
    @Override
    public ChatFragmenAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        context = parent.getContext();
        return new ChatFragmenAdapter.ViewHolder(view);
//---------------------

//----------------------
    }
    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ChatFragmenAdapter.ViewHolder holder, int position) {

        Chat chat = chats.get(position);

//        Glide.with(context).load("https://q.qlogo.cn/headimg_dl?dst_uin=" + fenxiang.get(position)+ "&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(getActivity())).into(viewHolder.news_headportrait);
//        holder.news_username.setText(ships.get(position));
//        holder.news_content.setText(ship.get(position));

        //查询最后一条消息
        BmobQuery<Chat> query = new BmobQuery<>();
        query.addWhereEqualTo("", "rtf");
        query.findObjects(new FindListener<Chat>() {
            @Override
            public void done(List<Chat> object, BmobException e) {
                if(e==null){
                    holder.news_content.setText(object.get(0).getMsg_content());
                }else{
                    Log.i("bmob","失败："+e.getMessage());
                }
            }

        });

    }
    @Override
    public int getItemCount() {
        return chats.size();
    }

}
