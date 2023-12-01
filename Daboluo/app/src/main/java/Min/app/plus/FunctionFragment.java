package Min.app.plus;

import static android.app.Activity.RESULT_OK;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * @author daboluo
 */
public class FunctionFragment extends Fragment {

    private View viewn = null;
    private TextView healthy, lesson2, provide, check;
    private Boolean auditor = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //getnewuser();
        }
    };

    private String url;
    private ProgressBar progressbar;
    private WebView webview;
    private ImageView web_back, web_next;

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;

    private SmartRefreshLayout func_smart;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (viewn == null) {
            viewn = inflater.inflate(R.layout.function, container, false);
        }
        return viewn;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressbar = getActivity().findViewById(R.id.progressbar);
        webview = getActivity().findViewById(R.id.webview);
        web_back = getActivity().findViewById(R.id.web_back);
        web_next = getActivity().findViewById(R.id.web_next);

        web_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webview.canGoBack()) {
                    webview.goBack();//返回上一页面
                }
                //return super.onKeyDown(keyCode, event);
            }
        });
        web_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webview.loadUrl("http://www.diablos.cn/function.html");
            }
        });
        func_smart=getActivity().findViewById(R.id.home_smart);
        //监听下拉和上拉状态
        //下拉刷新
        func_smart.setOnRefreshListener(refreshlayout -> {
            func_smart.setEnableRefresh(true);//启用刷新
            url = webview.getUrl();
            webview.loadUrl(url);
        });



        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.requestFocus();
        //webview.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webview.loadUrl("http://www.diablos.cn/function.html");
        webview.getUrl();
        webview.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webview.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webview.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        webview.getSettings().setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webview.loadUrl("file:///android_asset/404.html");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //加载完成
                func_smart.finishRefresh();//结束刷新

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //设置在本浏览器打开新网页
                view.loadUrl(url);
                return true;
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {
            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                openImageChooserActivity();
                return true;
            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO 自动生成的方法存根
                if (newProgress == 100) {
                    progressbar.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    progressbar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    progressbar.setProgress(newProgress);//设置进度值
                }}

        });

        webview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {

            }

        });



       /* Bmob.initialize(getActivity(), "6d233c0993a3ab132ba5e11b0942960b");
        healthy=getActivity().findViewById(R.id.healthy);
        lesson2=getActivity().findViewById(R.id.lesson2);
        provide=getActivity().findViewById(R.id.provide);
        check=getActivity().findViewById(R.id.check);

        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        }.start();
        healthy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentc = new Intent(getActivity(),WebActivity.class);
                intentc.putExtra("title", "健康打卡");
                intentc.putExtra("url", "http://health.sctu.edu.cn:56666/");
                startActivity(intentc);
            }
        });
        lesson2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentc = new Intent(getActivity(),WebActivity.class);
                intentc.putExtra("title", "二课系统");
                intentc.putExtra("url", "http://172-16-10-124-12341-p.vpn.sctu.edu.cn:8118/#/?token=c021bad68659bbda31d0f5909c5e0b5c&user_id=0CIP");
                startActivity(intentc);
            }
        });
        provide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentc = new Intent(getActivity(),WebActivity.class);
                intentc.putExtra("title", "外卖到寝");
                intentc.putExtra("url", "https://o2o.keloop.cn/wap/m6t748");
                startActivity(intentc);
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(auditor==true){

                    Intent intentg = new Intent(getActivity(), AuditActivity.class);
                    startActivity(intentg);

                }else {
                    Snackbar.make(view, "sorry！你还没有权限。", Snackbar.LENGTH_SHORT).show();
                }

            }
        });
    }
    public void getnewuser() {
        BmobQuery<_User> bq1 = new BmobQuery<_User>();
        bq1.getObject((String) BmobUser.getObjectByKey("objectId"), new QueryListener<_User>() {
            @Override
            public void done(_User object, BmobException e) {
                if (e == null) {
                   if (object.getAuditor()!=null){
                       auditor=object.getAuditor();
                   }
                }
            }
        });*/
    }

    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;

    }}