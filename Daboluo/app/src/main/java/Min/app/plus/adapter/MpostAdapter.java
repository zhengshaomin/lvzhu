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
import Min.app.plus.MpostList;
import Min.app.plus.PostActivity;
import Min.app.plus.R;
import Min.app.plus.bmob.Posts;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.AnimationTools;
import Min.app.plus.utils.AutoLinKTextViewUtil;
import Min.app.plus.utils.GlideActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 作者：daboluo on 2023/8/26 13:46
 * Email:daboluo719@gmail.com
 */
public class MpostAdapter extends RecyclerView.Adapter<MpostAdapter.ViewHolder>{

    private Context context;
    private List<Posts> list;
    public MpostAdapter(List<Posts> list){
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
    public MpostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cspost, parent, false);
        context = parent.getContext();
        return new MpostAdapter.ViewHolder(view);
//---------------------

//----------------------
    }
    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull MpostAdapter.ViewHolder holder, int position) {

        Posts post = list.get(position);
        if (post.getAnonymous() == true) {
            holder.username.setText("匿名");
            Glide.with(context).load("https://z3.ax1x.com/2021/11/20/ILiyNR.jpg").crossFade(800).transform(new GlideActivity(context)).into(holder.headportrait);
        } else {
            holder.username.setText(post.getAuthor().getUsername());
            Glide.with(context).load("https://q.qlogo.cn/headimg_dl?dst_uin=" + post.getAuthor().getQq() + "&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(context)).into(holder.headportrait);
        }
        if(post.getPhoto()!=null){
            holder.photo.setVisibility(View.VISIBLE);
            Glide.with(context).load(post.getPhoto().getUrl()).crossFade(1000).into(holder.photo);
        }else {
            holder.photo.setVisibility(View.GONE);
        }
        holder.content.setText(post.getContent());
        //holder.time.setText(post.getCreatedAt());

        _User user= BmobUser.getCurrentUser(_User.class);
        // 查询喜欢这个帖子的所有用户，因此查询的是用户表
        BmobQuery<_User> query = new BmobQuery<>();
        query.addWhereRelatedTo("likes", new BmobPointer(post));
        query.findObjects(new FindListener<_User>() {

            @Override
            public void done(List<_User> object, BmobException e) {
                if(e==null){

                    if (isLike(object,user)){
                        holder.like.setImageResource(R.drawable.dianzan_1);
                    } else  holder.like.setImageResource(R.drawable.dianzan);

                    holder.likevalue.setText(object.size()+"");
                    Log.i("bmob","查询个数："+object.size());
                }else{
                    Log.i("bmob","失败："+e.getMessage());
                }
            }

        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationTools.scale(holder.like);
                int count=Integer.parseInt(holder.likevalue.getText().toString());
                // 查询喜欢这个帖子的所有用户，因此查询的是用户表
                BmobQuery<_User> query = new BmobQuery<>();
                //_User user = BmobUser.getCurrentUser(_User.class);
                //likes是Post表中的字段，用来存储所有喜欢该帖子的用户
                query.addWhereRelatedTo("likes", new BmobPointer(post));
                query.findObjects(new FindListener<>() {

                    @Override
                    public void done(List<_User> object,BmobException e) {
                        if(e==null){
                            //cancel
                            if (isLike(object,user)){
                                holder.like.setImageResource(R.drawable.dianzan);
                                BmobRelation relation = new BmobRelation();
                                relation.remove(user);
                                post.setLikes(relation);
                                post.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                            holder.likevalue.setText(count-1+"");
                                            holder.like.setImageResource(R.drawable.dianzan);
                                            Log.i("bmob","关联关系删除成功");
                                        }else{
                                            Log.i("bmob","失败："+e.getMessage());
                                        }
                                    }

                                });
                            } else {
                                holder.like.setImageResource(R.drawable.dianzan_1);
                                //将用户B添加到Post表中的likes字段值中，表明用户B喜欢该帖子
                                BmobRelation relation = new BmobRelation();
                                relation.add(user);
                                //多对多关联指向`post`的`likes`字段
                                post.setLikes(relation);
                                post.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                            holder.likevalue.setText(count+1+"");
                                            sendlike(post.getAuthor().getObjectId(),post.getObjectId());//发送点赞信息
                                            Log.i("bmob","用户B和该帖子关联成功");
                                        }else{
                                            Log.i("bmob","失败："+e.getMessage());
                                        }
                                    }

                                });
                            }



                            Log.i("bmob","查询个数："+object.size());
                        }else{
                            Log.i("bmob","失败："+e.getMessage());
                        }
                    }

                });

            }
        });
        AutoLinKTextViewUtil.getInstance().interceptHyperLink(holder.content);//拦截链接地址，设置显示样式和点击事件
        holder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.post_id=list.get(position).getObjectId(); //这里就获取到了Id。
                Intent intent = new Intent(context, PostActivity.class);
                intent.putExtra("post_id", holder.post_id);
                context.startActivity(intent);
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
                holder.post_id=list.get(position).getObjectId(); //这里就获取到了Id。

                Intent intent = new Intent(context, MpostActivity.class);
                intent.putExtra("post_id", holder.post_id);
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
