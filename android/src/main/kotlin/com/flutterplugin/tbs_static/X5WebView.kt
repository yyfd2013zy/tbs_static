package com.flutterplugin.tbs_static

import android.annotation.TargetApi
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.view.View
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.WebSettings

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class X5WebView(private val context: Context, private val id: Int, private val params: Map<String, Any>, val messenger: BinaryMessenger? = null, private val containerView: View?) : PlatformView, MethodChannel.MethodCallHandler {
    private var webView: InputAwareWebView
    private val channel: MethodChannel = MethodChannel(messenger, "com.tbs_static/x5WebView_$id")

    init {
        val displayListenerProxy = DisplayListenerProxy()
        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayListenerProxy.onPreWebViewInitialization(displayManager)
       webView = InputAwareWebView(context, containerView)
        displayListenerProxy.onPostWebViewInitialization(displayManager)
        channel.setMethodCallHandler(this)

        webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings?.setAllowFileAccess(true)
            settings?.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN)
            settings?.setSupportZoom(true)
            settings?.setBuiltInZoomControls(true)
            settings?.setSupportMultipleWindows(true)
            settings?.setUseWideViewPort(true)
            settings?.setLoadWithOverviewMode(true)
            settings?.setDisplayZoomControls(false)//设定缩放控件隐藏
            settings?.setAppCacheEnabled(true)
            settings?.setAllowContentAccess(true)
            settings?.setSavePassword(true)
            settings?.setSaveFormData(true)
            settings?.setLoadsImagesAutomatically(true)
            settings?.setBlockNetworkImage(false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                settings?.setMediaPlaybackRequiresUserGesture(false)
            }
            settings?.setJavaScriptCanOpenWindowsAutomatically(false)
            if (params["javascriptChannels"] != null) {
                val names = params["javascriptChannels"] as List<String>
                for (name in names) {
                    webView.addJavascriptInterface(JavascriptChannel(name, channel, context), name)
                }
            }
            loadUrl(params["url"].toString())
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(p0: WebView?, p1: String?): Boolean {

                    p0?.loadUrl(p1)
                    return false
                }

                override fun onPageFinished(p0: WebView?, url: String) {
                    super.onPageFinished(p0, url)
                    //向flutter通信
                    val arg = hashMapOf<String, Any>()
                    arg["url"] = url
                    channel.invokeMethod("onPageFinished", arg)
                }
                override fun onReceivedSslError(p0: WebView?, p1: SslErrorHandler?, p2: SslError?) {
                    print( "webview onReceivedSslError : ${p2.toString()}")
//            super.onReceivedSslError(p0, p1, p2)
                    p1?.proceed()
                }
            }
            webChromeClient = object : WebChromeClient() {


                override fun onProgressChanged(p0: WebView?, p1: Int) {
                    super.onProgressChanged(p0, p1)
                    //加载进度
                    val arg = hashMapOf<String, Any>()
                    arg["progress"] = p1
                    channel.invokeMethod("onProgressChanged", arg)
                }
            }


        }

    }
    override fun onFlutterViewDetached() {
        webView.setContainerView(null)
    }

    override fun onFlutterViewAttached(flutterView: View) {
        webView.setContainerView(flutterView)
    }

    override fun onInputConnectionUnlocked() {
        webView.isLockInputConnection(false)
    }

    override fun onInputConnectionLocked() {
        webView.isLockInputConnection(true)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "loadUrl" -> {
                val arg = call.arguments as Map<String, Any>
                val url = arg["url"].toString()
                val headers = arg["headers"] as? Map<String, String>
                webView.loadUrl(url, headers)
                result.success(null)
            }
            "canGoBack" -> {
                result.success(webView.canGoBack())
            }
            "canGoForward" -> {
                result.success(webView.canGoForward())
            }
            "goBack" -> {
                webView.goBack()
                result.success(null)
            }
            "goForward" -> {
                webView.goForward()
                result.success(null)
            }
            "goBackOrForward" -> {
                val arg = call.arguments as Map<String, Any>
                val point = arg["i"] as Int
                webView.goBackOrForward(point)
                result.success(null)
            }
            "reload" -> {
                webView.reload()
                result.success(null)
            }
            "currentUrl" -> {
                result.success(webView.url)
            }
            "evaluateJavascript" -> {
                val arg = call.arguments as Map<String, Any>
                val js = arg["js"].toString()
                webView.evaluateJavascript(js) { value -> result.success(value) }
            }

            "addJavascriptChannels" -> {
                val arg = call.arguments as Map<String, Any>
                val names = arg["names"] as List<String>
                for (name in names) {
                    webView.addJavascriptInterface(JavascriptChannel(name, channel, context), name)
                }
                webView.reload()
                result.success(null)

            }
            "isX5WebViewLoadSuccess" -> {
                val exception = webView.x5WebViewExtension
                if (exception == null) {
                    result.success(false)
                } else {
                    result.success(true)
                }
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    override fun getView(): View {
        return webView
    }

    override fun dispose() {
        channel.setMethodCallHandler(null)
        webView.dispose()
        webView.destroy()
    }
}