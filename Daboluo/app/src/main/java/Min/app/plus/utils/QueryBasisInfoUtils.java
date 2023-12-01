package Min.app.plus.utils;

import static androidx.core.content.ContextCompat.startActivity;
import static Min.app.plus.utils.DialogUtils.dialogloadingdismiss;
import static Min.app.plus.utils.DialogUtils.dialogloadingshow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;

import java.util.List;

import Min.app.plus.ApplyActivity;
import Min.app.plus.LoginActivity;
import Min.app.plus.OrderStoreActivity;
import Min.app.plus.PostActivity;
import Min.app.plus.R;
import Min.app.plus.SearchActivity;
import Min.app.plus.bmob.Order;
import Min.app.plus.bmob.Posts;
import Min.app.plus.bmob.Reply;
import Min.app.plus.bmob.Store;
import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;
import tech.liujin.widget.ScaleImageView;

/**
 * 作者：daboluo on 2023/8/20 00:56
 * Email:daboluo719@gmail.com
 */
//查询个人信息
public class QueryBasisInfoUtils {
    public static String  userobjectid,//用户id
            username,//用户昵称
            userqq,//绑定qq
            usersignature,//签名
            userbirthday,//生日
            useraddress,//地址
            usermobilePhoneNumber,//手机号码
            userksh,//考生号
    storeobjectid=null;//店铺id
    public static int userboluocoin;//菠萝币
    public static Boolean usersex,//性别
            userauditor;//审核
    //查询个人信息
    public static void getuserinfo(){
        BmobQuery<_User> bq = new BmobQuery<_User>();
        bq.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);// 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        bq.getObject((String) BmobUser.getObjectByKey("objectId"), new QueryListener<_User>() {
            @Override
            public void done(_User object, BmobException e) {
                if (e == null) {
                    userobjectid = object.getObjectId();
                    username=object.getUsername();
                    userqq=object.getQq();
                    usersignature=object.getSignature();
                    userboluocoin=Integer.parseInt(object.getBoluocoin());//将String转化成int
                    userbirthday=object.getBirthday();
                    useraddress=object.getAddress();
                    usermobilePhoneNumber=object.getMobilePhoneNumber();
                    userksh=object.getKsh();
                    usersex=object.getSex();
                    userauditor=object.getAuditor();
                    if(object.getBoluocoin()==null){
                        updata();
                    }
                }
            }
        });
    }

    //查询店铺信息
    public static void getstoreinfo(){
        BmobQuery<Store> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("manager", BmobUser.getCurrentUser(_User.class));//查询店铺下的商品
        bmobQuery.order("-createdAt");//依照数据排序时间排序
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//缓存
        bmobQuery.findObjects(new FindListener<Store>() {
            @Override
            public void done(List<Store> object, BmobException e) {
                if (e == null) {
                    if (object.size() >= 1) {
                        storeobjectid = object.get(0).getObjectId();
                    } else {
                        //查询成功，但未开通店铺
                    }
                } else {
                    //查询失败
                }
            }});
    }
    //修改信息
    public static void updata(){
        _User order = new _User();
        order.setBoluocoin("0");
        order.update(userobjectid, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    userboluocoin=0;
                } else {

                }
            }
        });
    }

    //发送评论以及点赞操作
    public static void sendlike(String recipient_id, String post_id){
        //String recipient,接受者
        //String content,内容
        //boolean type,类型，true为点赞
        //boolean news,
        //boolean visible,是否可见
        //String post_id帖子id
        //评论
        _User author = _User.getCurrentUser(_User.class);
        _User postauthor = new _User();
        postauthor.setObjectId(recipient_id);
        Posts post = new Posts();
        post.setObjectId(post_id);
        final Reply reply = new Reply();
        reply.setContent("元气满满地赞了你");
        reply.setPost(post);
        reply.setAuthor(author);
        reply.setRecipient(postauthor);
        reply.setType(true);//点赞
        reply.setNews(true);
        reply.setVisible(true);
        reply.save(new SaveListener<String>() {
            @Override
            public void done(String p1, BmobException e) {
                if (e == null) {
                    Log.d("QueryBasislnfoUtils","点赞成功");
                } else {
                    Log.d("QueryBasislnfoUtils","点赞失败");
                }
                // TODO: Implement this method
            }
        });

    }
    public static void Exitlogin(Context context){
        AlertDialog.Builder a = new AlertDialog.Builder(context);
        a.setIcon(R.drawable.diablos);//图标
        a.setCancelable(false);//点击界面其他地方弹窗不会消失
        a.setTitle("提示");//标题
        a.setMessage("退出登陆后将无法使用其功能。");//弹窗内容
//        a.setPositiveButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface p1, int p2) {
//                //getdownload();
//            }
//        });
        a.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface p1, int p2) {
                BmobUser.logOut();
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }
        });
        a.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        a.show();
    }
    public static boolean joinQQGroup(String key,Context context) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

}
