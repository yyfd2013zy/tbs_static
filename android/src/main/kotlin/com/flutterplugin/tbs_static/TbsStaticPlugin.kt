package com.flutterplugin.tbs_static

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.NonNull

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
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "tbs_static")
      channel.setMethodCallHandler(TbsStaticPlugin(registrar.context(),registrar.activity()))
    }
  }


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
    } else {
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
  }

  override fun onDetachedFromActivityForConfigChanges() {
  }
}
