package Min.app.plus.adapter;

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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import Min.app.plus.MuserActivity;
import Min.app.plus.R;
import Min.app.plus.UserActivity;
import Min.app.plus.bmob.Urelation;
import Min.app.plus.utils.GlideActivity;

/**
 * 作者：daboluo on 2023/9/19 18:50
 * Email:daboluo719@gmail.com
 */
public class FansAdapter extends RecyclerView.Adapter<FansAdapter.ViewHolder>{

    private Context context;
    private List<Urelation> ships;
    public FansAdapter(List<Urelation> list){
        this.ships = list;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username,signature;
        public LinearLayout linearlayout;
        public ImageView headportrait;

        public ViewHolder(View view){
            super(view);

            linearlayout =  view.findViewById(R.id.linearlayout);
            headportrait=view.findViewById(R.id.headportrait);
            username=view.findViewById(R.id.username);
            signature=view.findViewById(R.id.signature);
        }
    }

    @NonNull
    @Override
    public FansAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ship, parent, false);
        context = parent.getContext();
        return new FansAdapter.ViewHolder(view);
//---------------------

//----------------------
    }
    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull FansAdapter.ViewHolder holder, int position) {

        Urelation fenxiang = ships.get(position);




        Glide.with(context).load("https://q.qlogo.cn/headimg_dl?dst_uin=" +fenxiang.getAuthor().getQq() + "&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(context)).into(holder.headportrait);
        holder.username.setText(fenxiang.getAuthor().getUsername());
        holder.signature.setText(fenxiang.getAuthor().getSignature());
        holder.linearlayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View p1)
            {
                if(userobjectid.equals(ships.get(position).getAuthor().getObjectId())){
                    Intent intent = new Intent(context, MuserActivity.class);
                    context.startActivity(intent);
                }else {
                    Intent intent = new Intent(context, UserActivity.class);
                    intent.putExtra("user_id",ships.get(position).getAuthor().getObjectId());
                    context.startActivity(intent);
                }
                // TODO: Implement this method
            }
        });

    }
    @Override
    public int getItemCount() {
        return ships.size();
    }

}
