package com.flutterplugin.tbs_static

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import com.tencent.smtt.sdk.QbSdk
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
/** TbsStaticPlugin */
public class TbsStaticPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {

  constructor(mContext: Context, mActivity: Activity){
    this.mActivity=mActivity
    this.mContext=mContext
  }
  constructor()

  var mContext: Context? = null
  var mActivity: Activity? = null
  var mFlutterPluginBinding: FlutterPlugin.FlutterPluginBinding? = null


  //兼容旧方式集成插件
  companion object {
    var methodChannel: MethodChannel? = null
    //注册MethodChannel
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "tbs_static")
      channel.setMethodCallHandler(TbsStaticPlugin(registrar.context(),registrar.activity()))
      //注册
      System.out.println("register com.tbs_static/x5WebView")
      registrar.platformViewRegistry().registerViewFactory("com.tbs_static/x5WebView", X5WebViewFactory(registrar.messenger(), registrar.activity(), registrar.view()))
    }
  }

  /**
   * 具体方法实现
   */
  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    }else if ( call.method == "openWebActivity"){
      val url = call.argument<String>("url")
      val title = call.argument<String>("title")
      val landspace = call.argument<Boolean>("landspace")
      val headers = call.argument<HashMap<String,String>>("headers")?:HashMap()
      val isUrlIntercept=call.argument<Boolean>("isUrlIntercept")
      val intent = Intent(mActivity, X5WebViewActivity::class.java)
      intent.putExtra("url", url)
      intent.putExtra("title", title)
      intent.putExtra("headers", headers)
      intent.putExtra("isUrlIntercept", isUrlIntercept)
      intent.putExtra("landspace", landspace)
      mActivity?.startActivity(intent)
      result.success(null)
    } else if (call.method == "preinstallStaticTbs"){
     /* var ok = false
      if (QbSdk.canLoadX5(mActivity)) {
        result.success(ok)
      } else {
        *//*Thread(Runnable {

        }).start()*//*
        ok = QbSdk.preinstallStaticTbs(mActivity)
        result.success(ok)
      }*/

    }else {
      result.notImplemented()
    }
  }


  //新方式集成插件
  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    if (mActivity == null) {
      mFlutterPluginBinding = binding
      return
    }
    mFlutterPluginBinding = binding
    mContext = binding.applicationContext

    methodChannel = MethodChannel(binding.binaryMessenger, "tbs_static")
    methodChannel?.setMethodCallHandler(TbsStaticPlugin(mContext!!,mActivity!!))

   }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    mFlutterPluginBinding = null
    methodChannel?.setMethodCallHandler(null)
    methodChannel = null
  }

  override fun onDetachedFromActivity() {
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    if (mFlutterPluginBinding == null) {
      this.mActivity = binding.activity
      return
    }
    this.mActivity = binding.activity
    this.mContext = binding.activity.applicationContext
    methodChannel = MethodChannel(mFlutterPluginBinding?.binaryMessenger, "tbs_static")
    methodChannel?.setMethodCallHandler(TbsStaticPlugin(mContext!!,mActivity!!))

    mFlutterPluginBinding?.platformViewRegistry?.registerViewFactory("com.tbs_static/x5WebView", X5WebViewFactory(mFlutterPluginBinding?.binaryMessenger!!, mActivity!!, null))
  }

  override fun onDetachedFromActivityForConfigChanges() {
  }
}
