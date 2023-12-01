package Min.app.plus.fragment.main;

import static Min.app.plus.utils.DialogUtils.dialogloadingdismiss;
import static Min.app.plus.utils.DialogUtils.dialogloadingshow;
import static Min.app.plus.utils.QueryBasisInfoUtils.Exitlogin;
import static Min.app.plus.utils.QueryBasisInfoUtils.joinQQGroup;
import static Min.app.plus.utils.QueryBasisInfoUtils.storeobjectid;
import static Min.app.plus.utils.QueryBasisInfoUtils.userksh;
import static Min.app.plus.utils.QueryBasisInfoUtils.username;
import static Min.app.plus.utils.QueryBasisInfoUtils.userobjectid;
import static Min.app.plus.utils.QueryBasisInfoUtils.userqq;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import Min.app.plus.Account;
import Min.app.plus.ChatgptActivity;
import Min.app.plus.CreateStore;
import Min.app.plus.FensActivity;
import Min.app.plus.FollowActivity;
import Min.app.plus.MpostList;
import Min.app.plus.MreplyList;
import Min.app.plus.MuserActivity;
import Min.app.plus.Order_Management;
import Min.app.plus.R;
import Min.app.plus.SearchActivity;
import Min.app.plus.StoreManager;
import Min.app.plus.WebActivity;
import Min.app.plus.YxapiActivity;
import Min.app.plus.bmob.Urelation;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.GlideActivity;
import Min.app.plus.LikeActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author daboluo
 */
public class MineFragment extends Fragment {
    private View viewn=null;
    private ImageView mine_headportrait;
    private TextView mine_username,search,mine_follow,mine_fens;
    private LinearLayout layout_follow,layout_fans,mine_dispatch,mine_post,mine_account,mine_reply,mine_vpn,mine_healthy,mine_yingxin,mine_chatgpt,mine_takeaway,mine_school_cardvip,mine_store_management,mine_manage;
    private Boolean audit;
    private String ship_id,t_username;
//    private NavigationView navigationview;
    int boluo;
    private String str_name,str_dept,str_xh,str_major,str_school,str_build,str_floor,str_room,str_bed;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            follow_quantity();
            fens_quantity();

        }};
//    private SmartRefreshLayout mine_smart;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (viewn==null) {
            viewn = inflater.inflate(R.layout.mine, container, false);
        }
        return viewn;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        }.start();

        search=getActivity().findViewById(R.id.search);
        mine_headportrait=getActivity().findViewById(R.id.mine_headportrait);
        mine_username=getActivity().findViewById(R.id.mine_username);
       /* mine_signature=getActivity().findViewById(R.id.mine_signature);*/
        layout_follow=getActivity().findViewById(R.id.layout_follow);
        layout_fans=getActivity().findViewById(R.id.layou_fans);
        mine_follow=getActivity().findViewById(R.id.mine_follow);
        mine_fens=getActivity().findViewById(R.id.mine_fens);
        mine_post= getActivity().findViewById(R.id.mine_post);
        mine_dispatch=getActivity().findViewById(R.id.mine_dispatch);
        mine_account=getActivity().findViewById(R.id.mine_account);
        mine_reply=getActivity().findViewById(R.id.mine_reply);
        mine_vpn=getActivity().findViewById(R.id.mine_vpn);
        mine_healthy=getActivity().findViewById(R.id.mine_healthy);
        mine_yingxin=getActivity().findViewById(R.id.mine_yingxin);
        mine_chatgpt=getActivity().findViewById(R.id.mine_chatgpt);
        mine_takeaway=getActivity().findViewById(R.id.mine_takeaway);
        mine_school_cardvip=getActivity().findViewById(R.id.mine_school_cardvip);
        mine_store_management=getActivity().findViewById(R.id.mine_store_management);
        mine_manage=getActivity().findViewById(R.id.mine_manage);

        Glide.with(getActivity()).load("https://q.qlogo.cn/headimg_dl?dst_uin=" + userqq + "&spec=640&img_type=jpg").crossFade(800).transform(new GlideActivity(getActivity())).into(mine_headportrait);
        mine_username.setText(username);
//        mine_signature.setText(usersignature);

        mine_headportrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //你已经登陆了
                Intent intent = new Intent(getActivity(), MuserActivity.class);
                startActivity(intent);

            }
        });
        layout_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),FollowActivity.class);
                intent.putExtra("userid",userobjectid);
                intent.putExtra("title","我的关注");
                startActivity(intent);
            }
        });
        layout_fans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),FensActivity.class);
                intent.putExtra("userid",userobjectid);
                intent.putExtra("title","我的粉丝");
                startActivity(intent);
            }
        });

        mine_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MpostList.class);
                startActivity(intent);
            }
        });
        mine_dispatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), Order_Management.class);
                startActivity(intent);

            }
        });

        mine_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentd = new Intent(getActivity(), Account.class);
                startActivity(intentd);

            }
        });
        mine_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), MreplyList.class);
                startActivity(intent);

            }
        });
        mine_vpn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("title", "校园vpn");
                intent.putExtra("url", "https://222.209.200.212:4433/portal/#!/login");
                startActivity(intent);
            }
        });
        mine_healthy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),WebActivity.class);
                intent.putExtra("title", "健康打卡");
                intent.putExtra("url", "http://health.sctu.edu.cn:56666/login.aspx");
                startActivity(intent);
            }
        });
        mine_yingxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(userksh==null){
                    display();
                }else {
                    dialogloadingshow(getActivity());
                    getstatus(userksh);
                }
//                Intent intent = new Intent(getActivity(),YxapiActivity.class);
////                intent.putExtra("title", "美团外卖");
////                intent.putExtra("url", "https://h5.waimai.meituan.com/waimai/mindex/home");
//                startActivity(intent);
            }
        });
        mine_chatgpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), ChatgptActivity.class);
                startActivity(intent);

            }
        });

        mine_school_cardvip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),WebActivity.class);
                intent.putExtra("title", "校园卡会员");
                intent.putExtra("url", "https://dev.coc.10086.cn/coc3/canvas/rightsget-h5-canvas/login?page=sichuanrp&env=online&channelId=");
                startActivity(intent);
            }
        });
        mine_store_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storeobjectid==null){
                    Intent intent = new Intent(getActivity(), CreateStore.class);
                    startActivity(intent);
                    //dialogtextviewshow(getActivity(),"提示","抱歉你暂时没有开通店铺功能，如需开通此功能请联系\nQQ:765618041\n微信：DIABLOSER\n注：开通此功能完全免费");
                }else {
                    Intent intent = new Intent(getActivity(), StoreManager.class);
                    startActivity(intent);
                }
            }
        });
        mine_takeaway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinQQGroup("puWiUfZoZOpFcW3oDvv0t79DMh7J_5Cj",getActivity());
                }
        });
        mine_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Exitlogin(getActivity());
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });


    }
    public void display() {

        View dialog_view = getLayoutInflater().inflate(R.layout.yxlogin_dialog, null);
        EditText ksh = dialog_view.findViewById(R.id.ksh);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.diablos)
                .setTitle("提示")
//                .setMessage("Hel一行内容")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //getroom(ksh.getText().toString());
                        dialogloadingdismiss();
                        getstatus(ksh.getText().toString());

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
//                .setNeutralButton("middle", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                })
                .setView(dialog_view)
                .setCancelable(false)
                .create()
                .show();


    }
    public void addksh(String ksh){

        _User p2 = new _User();
        p2.setKsh(ksh);
        p2.update(userobjectid, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    //getroom(ksh);
                }else{

                }
            }

        });
    }
    //查询信息
    public void getstatus(String status_api) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url("这是某个学校迎新api自作修改后面添加特定参数"+status_api).build();
                try {
                    Response response = client.newCall(request).execute();//发送请求
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        if (json.optString("state",null).equals("successful")){
                            //successful
                            str_name=json.optString("name",null);
                            str_dept=json.optString("dept",null);
                            str_xh=json.optString("xh",null);
                            str_major=json.optString("major",null)+"  "+json.optString("class",null);
                            if(userksh==null){
                                addksh(status_api);
                                userksh=status_api;
                            }else {
                                getroom(userksh);
                            }
                        }else {
                            //failed
                            dialogloadingdismiss();
                            Toasty.warning(getActivity(), "考生号错误！", Toast.LENGTH_SHORT,true).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
            }public void getroom(String room_api) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url("同上="+room_api).build();
                try {
                    Response response = client.newCall(request).execute();//发送请求
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        if (json.optString("state",null).equals("successful")){
                            //successful
                            str_school=json.optString("school",null);
                            str_build=json.optString("build",null);
                            str_floor=json.optString("floor",null);
                            str_room=json.optString("room",null);
                            str_bed=json.optString("bed",null);
                            dialogloadingdismiss();
                            Intent intent = new Intent(getActivity(), YxapiActivity.class);
                            intent.putExtra("name", str_name);
                            intent.putExtra("dept", str_dept);
                            intent.putExtra("xh", str_xh);
                            intent.putExtra("major", str_major);
                            intent.putExtra("school", str_school);
                            intent.putExtra("build", str_build);
                            intent.putExtra("floor", str_floor);
                            intent.putExtra("room", str_room);
                            intent.putExtra("bed", str_bed);
                            startActivity(intent);
                        }else {
                            //failed
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
    public void follow_quantity(){
        //关注数量
        BmobQuery<Urelation> query = new BmobQuery<Urelation>();
        query.addWhereEqualTo("author",BmobUser.getObjectByKey("objectId"));
        boolean isCache = query.hasCachedResult(Urelation.class);
        if(isCache){
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);   // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        }else{
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);// 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
            query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存2天
        }
        query.count(Urelation.class, new CountListener() {
            @Override
            public void done(Integer count, BmobException e) {
                if(e==null){
                    mine_follow.setText(""+count);
                }else{
                    mine_follow.setText(""+0);
                }
            }
        });

    }
    public void fens_quantity(){
        //粉丝数量
        BmobQuery<Urelation> query = new BmobQuery<Urelation>();
        query.addWhereEqualTo("object",BmobUser.getObjectByKey("objectId"));
        boolean isCache = query.hasCachedResult(Urelation.class);
        if(isCache){
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);   // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        }else{
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);// 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
            query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存2天
        }
        query.count(Urelation.class, new CountListener() {
            @Override
            public void done(Integer count, BmobException e) {
                if(e==null){
                    mine_fens.setText(""+count);
                }else{
                    mine_fens.setText(""+0);
                }
            }
        });

    }
   }