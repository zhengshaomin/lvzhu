package Min.app.plus.adapter;

import static Min.app.plus.utils.QueryBasisInfoUtils.storeobjectid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import Min.app.plus.R;
import Min.app.plus.StoreActivity;
import Min.app.plus.StoreManager;
import Min.app.plus.bmob.Store;
import Min.app.plus.utils.GlideActivity;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder>{


    private Context context;
    private List<Store> list;
    public StoreAdapter(List<Store> list){
        this.list = list;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView store_name,store_information;
        LinearLayout linearLayout;
        ImageView store_icon;
        String store_id;

        public ViewHolder(View view){
            super(view);

            linearLayout =  view.findViewById(R.id.linearlayout);
            store_name =  view.findViewById(R.id.store_name);
            store_information = view.findViewById(R.id.store_information);
            store_icon = view.findViewById(R.id.store_icon);
        }
    }
    @NonNull
    @Override
    public StoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        context = parent.getContext();
        return new StoreAdapter.ViewHolder(view);
//---------------------
//----------------------
    }
    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull StoreAdapter.ViewHolder holder, int position) {

        Store post = list.get(position);
        holder.store_name.setText(post.getName());
        holder.store_information.setText(post.getInformation());
        Glide.with(context).load(post.getIcon().getUrl()).crossFade(800).into(holder.store_icon);
        holder.linearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View p1)
            {
                holder.store_id=list.get(position).getObjectId(); //这里就获取到了Id。

                Log.d("TAG","郑绍敏"+storeobjectid+"|"+holder.store_id);
                if(storeobjectid==null){
                    Intent intent = new Intent(context, StoreActivity.class);
                    intent.putExtra("store_id", holder.store_id);
                    context.startActivity(intent);
                }else if(storeobjectid.equals(holder.store_id)){
                    Intent intent = new Intent(context, StoreManager.class);
                    context.startActivity(intent);
                }else {
                    Intent intent = new Intent(context, StoreActivity.class);
                    intent.putExtra("store_id", holder.store_id);
                    context.startActivity(intent);
                }

                // TODO: Implement this method
            }
        });
    }
    @Override
    public int getItemCount(){
        return list.size();
    }
}
