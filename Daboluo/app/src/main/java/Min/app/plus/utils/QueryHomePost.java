package Min.app.plus.utils;

import android.app.Activity;
import android.widget.Toast;

import java.util.List;

import Min.app.plus.bmob.Posts;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import es.dmoral.toasty.Toasty;

/**
 * 作者：daboluo on 2023/8/21 16:45
 * Email:daboluo719@gmail.com
 */
//查询首页帖子详情
public class QueryHomePost {

    public static List<Posts> postobject;//贴子对象
    public void getpost(Activity activity) {

        BmobQuery<Posts> bmobQuery = new BmobQuery<>();
        bmobQuery.include("author");
        bmobQuery.addWhereEqualTo("visible",true);//查询可见的
        bmobQuery.order("-createdAt");//依照数据排序时间排序
        bmobQuery.setLimit(100);
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);// 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        bmobQuery.findObjects(new FindListener<Posts>() {
            @Override
            public void done(List<Posts> object, BmobException e) {

                if (e == null) {
                    postobject=object;
                } else {
                    Toasty.error(activity, "数据加载失败！", Toast.LENGTH_SHORT,true).show();
                    //home_swip.setRefreshing(false);

                }

            }
        });

    }
}
