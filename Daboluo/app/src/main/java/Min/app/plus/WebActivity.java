package Min.app.plus;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * @author daboluo
 */
public class WebActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String code_id,code_title,photourl,collect_id,code_url;
    private ProgressBar progressbar;
    private WebView webview;
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    private SmartRefreshLayout apply_smart;
    private String title,url;
    private ImageButton web_back,web_next;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        progressbar = findViewById(R.id.progressbar);
        webview = findViewById(R.id.webview);

        web_back = findViewById(R.id.web_back);
        web_next = findViewById(R.id.web_next);

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
                if (webview.canGoForward()) {
                    webview.goForward();//返回
                }
            }
        });
//        func_smart=findViewById(R.id.home_smart);
//        //监听下拉和上拉状态
//        //下拉刷新
//        func_smart.setOnRefreshListener(refreshlayout -> {
//            func_smart.setEnableRefresh(true);//启用刷新
//            url = webview.getUrl();
//            webview.loadUrl(url);
//        });

        apply_smart = findViewById(R.id.apply_srlControl);
        //监听下拉和上拉状态
        //下拉刷新
        apply_smart.setOnRefreshListener(refreshlayout -> {
            apply_smart.setEnableRefresh(true);//启用刷新
            webview.loadUrl(webview.getUrl());//获取当前链接，进行刷新
            apply_smart.finishRefresh();//结束刷新
        });


//
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.requestFocus();
        //webview.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webview.loadUrl(url);
        webview.getUrl();
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setLoadsImagesAutomatically(true);
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
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                /* 不要使用super，否则有些手机访问不了，因为包含了一条 handler.cancel()
                   super.onReceivedSslError(view, handler, error);
                   接受所有网站的证书，忽略SSL错误，执行访问网页 */
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //加载完成
                //func_smart.finishRefresh();//结束刷新

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //设置在本浏览器打开新网页
                view.loadUrl(url);
                if (url.startsWith("http:") || url.startsWith("https:") ) {
                    view.loadUrl(url);
                }else {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }

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
                }
            }

        });

        webview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {

            }

        });





    } private void openImageChooserActivity() {
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
//    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_buycarmi, menu);
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        switch(item.getItemId())
//        {
//            case R.id.browser:
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.fakabang.com/links/D71396B6")));
//                break;
//
//        }
//        return super.onOptionsItemSelected(item);

    }}