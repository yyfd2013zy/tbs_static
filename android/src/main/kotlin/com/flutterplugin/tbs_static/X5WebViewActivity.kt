package com.flutterplugin.tbs_static
import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.export.external.interfaces.WebResourceError
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient


class X5WebViewActivity : Activity() {
    val TAG = "X5WebViewActivity"
    var webView: WebView? = null
    var content:LinearLayout?=null
    var title = ""
    var url = "https://www.baidu.com"
    var landspace = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        landspace = intent.getBooleanExtra("landspace",false)
        if (landspace){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        }
        Log.d(TAG, "onCreate")
        content = LinearLayout(this)
        val css = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        )
        content?.layoutParams = css
        setContentView(content)
        title = intent.getStringExtra("title")
        url= intent.getStringExtra("url")
        setTitle(title)
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
        if (QbSdk.canLoadX5(this)) {
            Log.i(TAG, "已安装好，直接显示")
            createWebview()
        } else {
            Log.i(TAG, "新安装")
            Thread(Runnable {
                val ok = QbSdk.preinstallStaticTbs(this)
                runOnUiThread {
                    Log.i(TAG, "安装结果：$ok")
                    createWebview()
                }
            }).start()
        }
    }

    private fun createWebview() {
        //手动创建WebView，显示到容器中，这样就能保证WebView一定是在X5内核准备好后创建的
        webView = WebView(applicationContext)
        val css = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        )
        content?.addView(webView, css)

        webView?.setWebViewClient(webViewClient)
        webView?.setWebChromeClient(webChromeClient)
        val webSettings = webView?.getSettings()
        webSettings?.setJavaScriptEnabled(true)
        webSettings?.setAllowFileAccess(true)
        webSettings?.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN)
        webSettings?.setSupportZoom(true)
        webSettings?.setBuiltInZoomControls(true)
        webSettings?.setSupportMultipleWindows(true)
        webSettings?.setUseWideViewPort(true)
        webSettings?.setLoadWithOverviewMode(true)
        webSettings?.setDisplayZoomControls(false)//设定缩放控件隐藏
        webSettings?.setDomStorageEnabled(true)
        webSettings?.setAppCacheEnabled(true)
        webSettings?.setDomStorageEnabled(true)
        webSettings?.setAllowContentAccess(true)
        webSettings?.setSavePassword(true)
        webSettings?.setSaveFormData(true)
        webSettings?.setLoadsImagesAutomatically(true)
        webSettings?.setBlockNetworkImage(false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings?.setMediaPlaybackRequiresUserGesture(false)
        }
        webSettings?.setJavaScriptCanOpenWindowsAutomatically(false)

//      var url = "https://10.155.0.134:31311"
//        var url =  "https://10.155.0.134:31311/static/testvideo/video.html?sid=60cbd910-d555-11ea-af73-45d5ffed74da&userName=cross&token=09080DC0E2A711EA9A1E5DA467561DB4&serverAddress=wss://10.155.0.135:30670/hari&accountId=AZ019121"
//      var url = "https://apprtc.webrtcserver.cn/r/319960624"
        webView?.loadUrl(url)
        Log.d(TAG,"load ${url}")

    }

    var webViewClient: WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(p0: WebView?, p1: String?): Boolean {
            Log.d(TAG, "webview shouldOverrideUrlLoading : $p1")
            p0?.loadUrl(p1)
            return false
        }

        override fun onReceivedError(p0: WebView?, p1: WebResourceRequest?, p2: WebResourceError?) {
            super.onReceivedError(p0, p1, p2)
            Log.d(TAG, "webview onReceivedError :description : ${p2?.description}  errorCode :"+p2?.errorCode)
        }

        override fun onReceivedSslError(p0: WebView?, p1: SslErrorHandler?, p2: SslError?) {
            Log.d(TAG, "webview onReceivedSslError : ${p2.toString()}")
//            super.onReceivedSslError(p0, p1, p2)
            p1?.proceed()
        }
    }

    var webChromeClient: com.tencent.smtt.sdk.WebChromeClient = object : com.tencent.smtt.sdk.WebChromeClient() {
        override fun onProgressChanged(p0: WebView?, p1: Int) {
            super.onProgressChanged(p0, p1)
            Log.d(TAG, "webview onProgressChanged : $p1")
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        webView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        webView?.destroy()
    }

    override fun onBackPressed() {
        if (webView!!.canGoBack()){
            webView?.goBack()
        }else{
            finish()
        }
    }
}