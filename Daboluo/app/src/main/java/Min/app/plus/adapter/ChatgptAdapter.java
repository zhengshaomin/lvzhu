package Min.app.plus.adapter;

import static Min.app.plus.utils.QueryBasisInfoUtils.userqq;

import android.annotation.SuppressLint;
import android.content.Context;
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

import Min.app.plus.R;
import Min.app.plus.bmob.ai_log;
import Min.app.plus.utils.GlideActivity;

/**
 * 作者：daboluo on 2023/8/27 16:52
 * Email:daboluo719@gmail.com
 */
public class ChatgptAdapter extends RecyclerView.Adapter<ChatgptAdapter.ViewHolder>{

    private Context context;
    private List<ai_log> list;
    public ChatgptAdapter(List<ai_log> list){
        this.list = list;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChatView, rightChatView;
        TextView leftTextView,rightTextView;
        ImageView headportrait;

        public ViewHolder(View view){
            super(view);

            leftChatView=view.findViewById(R.id.left_chat_view);
            rightChatView=view.findViewById(R.id.right_chat_view);
            leftTextView=view.findViewById(R.id.left_chat_text_view);
            rightTextView=view.findViewById(R.id.right_chat_text_view);
            headportrait=view.findViewById(R.id.headportrait);
        }
    }

    @NonNull
    @Override
    public ChatgptAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatgpt_item, parent, false);
        context = parent.getContext();
        return new ChatgptAdapter.ViewHolder(view);
//---------------------

//----------------------
    }
    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ChatgptAdapter.ViewHolder holder, int position) {

         ai_log ai = list.get (position);
        if (ai.getRole().equals("user")){
            holder.leftChatView.setVisibility (View.GONE);
            holder.rightChatView.setVisibility(View.VISIBLE);
            holder.rightTextView.setText (ai.getMessages());
            Glide.with(context).load("https://q.qlogo.cn/headimg_dl?dst_uin=" + userqq + "&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(context)).into(holder.headportrait);
        } else {
            holder.rightChatView.setVisibility (View.GONE);
            holder.leftChatView.setVisibility(View.VISIBLE);
            holder.leftTextView.setText (ai.getMessages());
        }

    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}