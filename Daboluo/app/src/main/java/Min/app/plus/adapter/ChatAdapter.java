package Min.app.plus.adapter;

import static Min.app.plus.utils.DialogUtils.dialog_photo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import Min.app.plus.MuserActivity;
import Min.app.plus.PostActivity;
import Min.app.plus.R;
import Min.app.plus.UserActivity;
import Min.app.plus.bmob.Chat;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.GlideActivity;
import cn.bmob.v3.BmobUser;
import tech.liujin.widget.ScaleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

    private Context context;
    String user_he_id;
    private List<Chat> list;
    public ChatAdapter(List<Chat> list){
        this.list = list;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout;
        TextView left_msg,left_time;
        ImageView left_headportrait,left_photo;

        LinearLayout rightLayout;
        TextView right_msg,right_time;
        ImageView right_headportrait,right_photo;

        public ViewHolder(View view){
            super(view);
            leftLayout = view.findViewById(R.id.left_layout);
            left_msg = view.findViewById(R.id.left_msg);
            left_time=view.findViewById(R.id.left_time);
            left_photo=view.findViewById(R.id.left_photo);
            left_headportrait=view.findViewById(R.id.left_headportrait);

            rightLayout = view.findViewById(R.id.right_layout);
            right_msg = view.findViewById(R.id.right_msg);
            right_time=view.findViewById(R.id.right_time);
            right_photo=view.findViewById(R.id.right_photo);
            right_headportrait=view.findViewById(R.id.right_headportrait);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg,parent,false);
        context= parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat msg = list.get(position);

        String t=msg.getCreatedAt();
        if(msg.getMsg_author().getObjectId().equals(BmobUser.getCurrentUser(_User.class).getObjectId())){
            //如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.right_msg.setText(msg.getMsg_content());
            holder.right_time.setText(t.substring(11,16));
            Glide.with(context).load("https://q.qlogo.cn/headimg_dl?dst_uin=" + msg.getMsg_author().getQq() + "&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(context)).into(holder.right_headportrait);
            //同样使用View.GONE
            holder.leftLayout.setVisibility(View.GONE);
            if(msg.getType()){
                holder.right_msg.setVisibility(View.GONE);
                holder.right_photo.setVisibility(View.VISIBLE);
                Glide.with(context).load(msg.getPhoto().getUrl()).crossFade(1000).into(holder.right_photo);
            }
        }else {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.left_msg.setText(msg.getMsg_content());
            holder.left_time.setText(t.substring(11,16));
            user_he_id=msg.getMsg_author().getObjectId();

            Glide.with(context).load("https://q.qlogo.cn/headimg_dl?dst_uin=" + msg.getMsg_author().getQq() + "&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(context)).into(holder.left_headportrait);
            //注意此处隐藏右面的消息布局用的是 View.GONE
            holder.rightLayout.setVisibility(View.GONE);
            if(msg.getType()){
                holder.left_msg.setVisibility(View.GONE);
                holder.left_photo.setVisibility(View.VISIBLE);
                Glide.with(context).load(msg.getPhoto().getUrl()).crossFade(1000).into(holder.left_photo);
            }
        }
        holder.right_headportrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editpro=new Intent(context, MuserActivity.class);
                context.startActivity(editpro);
            }
        });
        holder.left_headportrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra("user_id",msg.getMsg_author().getObjectId());
                context.startActivity(intent);

            }
        });
        holder.right_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_photo(context,msg.getPhoto().getUrl());
            }
        });
        holder.left_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_photo(context,msg.getPhoto().getUrl());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
