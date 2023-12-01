package Min.app.plus.utils;

import static androidx.core.content.ContextCompat.startActivity;
import static Min.app.plus.utils.QueryBasisInfoUtils.useraddress;
import static Min.app.plus.utils.QueryBasisInfoUtils.userobjectid;
import static cn.bmob.v3.Bmob.getApplicationContext;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import Min.app.plus.R;
import Min.app.plus.StartActivity;
import Min.app.plus.WebActivity;
import Min.app.plus.bmob._User;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import tech.liujin.widget.ScaleImageView;

/**
 * 作者：daboluo on 2023/8/30 12:32
 * Email:daboluo719@gmail.com
 */
public class DialogUtils {

    public static AlertDialog dialogloading;//loading
    public static ProgressDialog prodialog;//进度条弹窗

    //自定义文本弹窗
    public static void dialogtextviewshow(Context context, String title, String content) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_store_notice2, null, false);
        AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();
        dialog.setCancelable(false);
        TextView titles = view.findViewById(R.id.title);
        titles.setText(title);
        TextView diacontent = view.findViewById(R.id.content);
        TextView promise = view.findViewById(R.id.promise);
        dialog.setView(view); // 自定义dialog
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        diacontent.setText(content);
        promise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //确定
                dialog.cancel();

            }
        });
    }

    //自定义输入文本框
    public static void dialogedittextshow(Context context) {

        View view = LayoutInflater.from(context).inflate(R.layout.dialogedittext, null, false);
        AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();
        EditText address = view.findViewById(R.id.edit);
        address.setText(useraddress);
        TextView promise = view.findViewById(R.id.promise);
        dialog.setView(view); // 自定义dialog
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        promise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //修改地址
                _User p2 = new _User();
                p2.setAddress(address.getText().toString().trim());
                p2.update(userobjectid, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        dialog.dismiss();
                        if (e == null) {
                            useraddress = address.getText().toString().trim();
                        } else {
                            Toasty.error(context, "出现错误！" + e, Toast.LENGTH_SHORT, true).show();
                        }
                    }

                });
            }
        });

    }

    //自定义加载视图
    public static void dialogloadingshow(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null, false);
        dialogloading = new AlertDialog.Builder(context).setView(view).create();
        dialogloading.setCancelable(false);
        dialogloading.show();
    }

    //关闭自定义加载视图
    public static void dialogloadingdismiss() {
        dialogloading.dismiss();
    }

    //通知弹窗
    public static void dialoagnoticeshow(Context context, boolean type, String title, String content, String url, String promise, String cancel) {
        AlertDialog.Builder a = new AlertDialog.Builder(context);
        a.setIcon(R.drawable.diablos);//图标
        a.setCancelable(false);//点击界面其他地方弹窗不会消失
        a.setTitle(title);//标题
        a.setMessage(content);//弹窗内容
        a.setPositiveButton(cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface p1, int p2) {

            }
        });
        a.setNegativeButton(promise, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface p1, int p2) {

                if (type == false) {
                    //false，跳转内部浏览器
                    Intent intent = new Intent(context, WebActivity.class);
                    intent.putExtra("title", title);
                    intent.putExtra("url", url);
                    context.startActivity(intent);
                } else {
                    //为true时，开启更新下载
                    //getdownload();
                    dialogdownloadshow(context, title, content, url);
                }

            }
        });
        a.show();
    }

    //软件更新进度条弹窗
    public static void dialogdownloadshow(Context context, String title, String content, String url) {
        prodialog = (new ProgressDialog(context));//构建对话框
        prodialog.setIcon(R.drawable.diablos);//设置图标
        prodialog.setCancelable(false);
        prodialog.setTitle(title);//设置标题
        prodialog.setMessage(content);//设置内容
        prodialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置进度条样式
        prodialog.show();//显示出来

        Download down = new Download();
        down.setondown(new Download.ondown() {
            @Override
            public void downing(int len, int oklen) {
                prodialog.setProgress((int) (((double) oklen / (double) len) * 100));
            }

            @Override
            public void downok(String ruest) {
                prodialog.dismiss();
                //String galleryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();//系统相册
                File apkfile = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + title + ".apk");
                //Snackbar.make(bottom, "下载完成", Snackbar.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//安装完成后打开新版本
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // 给目标应用一个临时授权
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
                    //如果SDK版本>=24，即：Build.VERSION.SDK_INT >= 24，使用FileProvider兼容安装apk
                    String packageName = getApplicationContext().getApplicationContext().getPackageName();
                    String authority = new StringBuilder(packageName).append(".fileProvider").toString();
                    Uri apkUri = FileProvider.getUriForFile(context, authority, apkfile);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
                }
                getApplicationContext().startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());//安装完之后会提示”完成” “打开”。


            }
        });

        down.dowmFile(url, getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + title + ".apk");///mnt/sdcard


    }

    //图片大图展示
    public static void dialog_photo(Context context, String photo_url) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_photo, null, false);
        AlertDialog dialog_photo = new AlertDialog.Builder(context).setView(view).create();
        ScaleImageView img = view.findViewById(R.id.large_image);
        Button bt = view.findViewById(R.id.bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogdownloadphoto(context, "123", photo_url);
            }
        });
        Glide.with(context).load(photo_url).into(img);
        dialog_photo.setView(view); // 自定义dialog
        dialog_photo.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog_photo.show();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_photo.cancel();
            }
        });

    }

    //图片保存
    public static void dialogdownloadphoto(Context context, String title, String url) {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null, false);
        dialogloading = new AlertDialog.Builder(context).setView(view).create();
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        dialogloading.setCancelable(false);
        dialogloading.show();

        Download down = new Download();
        down.setondown(new Download.ondown() {
            @Override
            public void downing(int len, int oklen) {
                progressBar.setProgress((int) (((double) oklen / (double) len) * 100));
            }

            @Override
            public void downok(String ruest) {
                dialogloading.dismiss();
                File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + title + ".png");
                Toasty.success(context, "下载完成", Toasty.LENGTH_LONG, true).show();
                try {
                    MediaStore.Images.Media.insertImage(context.getContentResolver(),
                            file.getAbsolutePath(), "123", null);
                    Log.d("","保存成功");
                } catch (FileNotFoundException e) {
                    Log.d("","保存失败");
                    e.printStackTrace();
                }
            }
        });
        down.dowmFile(url, getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + title + ".png");///mnt/sdcard

    }

}
