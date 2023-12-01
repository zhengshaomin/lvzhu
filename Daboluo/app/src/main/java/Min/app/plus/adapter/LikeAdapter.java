package Min.app.plus.adapter;

import static Min.app.plus.utils.QueryBasisInfoUtils.sendlike;

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

import Min.app.plus.MpostActivity;
import Min.app.plus.PostActivity;
import Min.app.plus.R;
import Min.app.plus.bmob.Posts;
import Min.app.plus.bmob.Reply;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.AnimationTools;
import Min.app.plus.utils.GlideActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 作者：daboluo on 2023/9/21 00:57
 * Email:daboluo719@gmail.com
 */
public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder>{

    private Context context;
    private List<Reply> list;
    public LikeAdapter(List<Reply> list){
        this.list = list;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView username,content,time,likevalue;
        LinearLayout linearLayout;
        ImageView headportrait,photo,like,reply,share;
        String post_id;

        public ViewHolder(View view){
            super(view);

            username =  view.findViewById(R.id.username);
            linearLayout =  view.findViewById(R.id.linearlayout);
            content = view.findViewById(R.id.content);
            headportrait = view.findViewById(R.id.headportrait);
            //time = view.findViewById(R.id.time);
            likevalue=view.findViewById(R.id.likevalue);
            like=view.findViewById(R.id.like);
            reply=view.findViewById(R.id.reply);
            share=view.findViewById(R.id.share);
            photo = view.findViewById(R.id.photo);
        }
    }

    @NonNull
    @Override
    public LikeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cspost, parent, false);
        context = parent.getContext();
        return new LikeAdapter.ViewHolder(view);
//---------------------

//----------------------
    }
    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull LikeAdapter.ViewHolder holder, int position) {

        Reply post = list.get(position);
        if (post.getPost().getAnonymous() == true) {
            holder.username.setText("匿名");
            Glide.with(context).load("https://z3.ax1x.com/2021/11/20/ILiyNR.jpg").crossFade(800).transform(new GlideActivity(context)).into(holder.headportrait);
        } else {
            holder.username.setText(post.getPost().getAuthor().getUsername());
            Glide.with(context).load("https://q.qlogo.cn/headimg_dl?dst_uin=" + post.getPost().getAuthor().getQq() + "&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(context)).into(holder.headportrait);
        }
        if(post.getPost().getPhoto()!=null){
            holder.photo.setVisibility(View.VISIBLE);
            Glide.with(context).load(post.getPost().getPhoto().getUrl()).crossFade(1000).into(holder.photo);
        }else {
            holder.photo.setVisibility(View.GONE);
        }
        holder.content.setText(post.getPost().getContent());
        //holder.time.setText(post.getPost().getCreatedAt());
        holder.like.setImageResource(R.drawable.dianzan_1);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationTools.scale(holder.like);


            }
        });


        BmobQuery<_User> query = new BmobQuery<>();
        query.addWhereRelatedTo("relationship", new BmobPointer(post.getPost()));
        query.findObjects(new FindListener<_User>() {

            @Override
            public void done(List<_User> object,BmobException e) {
                if(e==null){
                    holder.likevalue.setText(object.size()+"");
                    Log.i("bmob","查询个数："+object.size());
                }else{
                    Log.i("bmob","失败："+e.getMessage());
                }
            }

        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //分享
            }
        });
        holder.linearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View p1)
            {

                Intent intent = new Intent(context, PostActivity.class);
                intent.putExtra("post_id", list.get(position).getPost().getObjectId());
                context.startActivity(intent);

                // TODO: Implement this method
            }
        });

    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    private boolean isLike(List<_User> object, _User user){
        boolean islike = false;
        for (int i = 0; i < object.size(); i++) {
            if (object.get(i).getObjectId().equals(user.getObjectId())){
                return true;
            }
        }
        return islike;
    }
}
