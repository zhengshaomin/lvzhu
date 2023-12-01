package Min.app.plus.adapter;

import static androidx.core.content.ContextCompat.startActivity;
import static Min.app.plus.utils.QueryBasisInfoUtils.sendlike;
import static Min.app.plus.utils.QueryBasisInfoUtils.userobjectid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import Min.app.plus.MuserActivity;
import Min.app.plus.PostActivity;
import Min.app.plus.R;
import Min.app.plus.ReplyActivity;
import Min.app.plus.UserActivity;
import Min.app.plus.bmob.Reply;
import Min.app.plus.utils.GlideActivity;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;


/**
 * 作者：daboluo on 2023/9/19 14:18
 * Email:daboluo719@gmail.com
 */
//评论、点赞提醒页面
public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder>{

    private Context context;
    private List<Reply> replys;
    public ReplyAdapter(List<Reply> list){
        this.replys = list;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView news_username,news_content,news_type,newone;
        public LinearLayout news_linearLayout;
        public ImageView news_headportrait,photo,like;
        public String post_id,reply_id;

        public ViewHolder(View view){
            super(view);

            news_username =  view.findViewById(R.id.news_username);
            news_linearLayout =  view.findViewById(R.id.news_linearlayout);
            news_headportrait =  view.findViewById(R.id.news_headportrait);
            news_content=view.findViewById(R.id.news_content);
            news_type =  view.findViewById(R.id.time);
            newone=view.findViewById(R.id.newone);
        }
    }

    @NonNull
    @Override
    public ReplyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        context = parent.getContext();
        return new ReplyAdapter.ViewHolder(view);
//---------------------

//----------------------
    }
    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ReplyAdapter.ViewHolder holder, int position) {

        Reply fenxiang=replys.get(position);
        if(replys.get(position).getType()==true){
            holder.news_type.setText("点赞");
        }else {
            holder.news_type.setText("回复了你");
        }
        if(replys.get(position).getNews()){
            holder.newone.setVisibility(View.VISIBLE);
        }
        Glide.with(context).load("https://q.qlogo.cn/headimg_dl?dst_uin=" + fenxiang.getAuthor().getQq()+ "&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(context)).into(holder.news_headportrait);
        holder.news_username.setText(fenxiang.getAuthor().getUsername());
        holder.news_content.setText(fenxiang.getContent());
        holder.news_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.newone.setVisibility(View.GONE);
                renew(replys.get(position).getObjectId());
                //提醒关于帖子的评论或回复
                if(replys.get(position).getPost().getObjectId()!=null){
                    Intent intent = new Intent(context, PostActivity.class);
                    intent.putExtra("post_id", replys.get(position).getPost().getObjectId());
                    context.startActivity(intent);
                }else {
                    Toasty.error(context, "帖子不存在或者被删除了", Toast.LENGTH_SHORT,true).show();
                }
            }
        });
        holder.news_headportrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userobjectid.equals(replys.get(position).getAuthor().getObjectId())){
                    Intent intent = new Intent(context, MuserActivity.class);
                    context.startActivity(intent);
                }else {
                    Intent intent = new Intent(context, UserActivity.class);
                    intent.putExtra("user_id", replys.get(position).getAuthor().getObjectId());
                    context.startActivity(intent);
                }
            }
        });



    }
    @Override
    public int getItemCount() {
        return replys.size();
    }

    public void renew(String reply_id) {
        Reply p2 = new Reply();
        p2.setNews(false);
        p2.update(reply_id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //getdata();
                } else {}
            }

        });
    }
}

