import 'dart:async';
import 'package:flutter/services.dart';
import 'package:flutter/foundation.dart';

class TbsStatic {
  static const MethodChannel _channel = const MethodChannel('tbs_static');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  ///打开原生X5WebView
  static Future<void> openWebActivity(String url,
      {String title,bool landspace,
      Map<String, String> headers,
      InterceptUrlCallBack callback}) async {
    if (defaultTargetPlatform == TargetPlatform.android) {
      final Map<String, dynamic> params = <String, dynamic>{
        'title': title,
        'url': url,
        'headers': headers,
        'landspace': landspace,
        'isUrlIntercept': callback != null
      };
      if (callback != null) {
        _channel.setMethodCallHandler((call) {
          try {
            if (call.method == "onUrlLoad") {
              print("onUrlLoad----${call.arguments}");
              Map arg = call.arguments;
              callback(arg["url"], Map<String, String>.from(arg["headers"]));
            }
          } catch (e) {
            print(e);
          }
          return;
        });
      }
      //这里很重要！
      return await _channel.invokeMethod("openWebActivity", params);
    } else {
      return;
    }
  }

  static Future<bool> preinstallStaticTbs() async {
    bool res =  await _channel.invokeMethod("preinstallStaticTbs");
    return res;

  }

  ///设置内核下载安装事件,这里没有用到
  static Future<void> setX5SdkListener(X5SdkListener listener) async {
    _channel.setMethodCallHandler((call) {
      switch (call.method) {
        case "onInstallFinish":
          listener.onInstallFinish(call.arguments);
          break;
        default:
          throw MissingPluginException(
              '${call.method} was invoked but has no handler');
          break;
      }
      return;
    });
  }

  ///获取x5的日志
  static Future<String> getCrashInfo() async {
    if (defaultTargetPlatform == TargetPlatform.android) {
      var res = await _channel.invokeMethod("getCarshInfo");
      return res;
    } else {
      return "";
    }
  }
}

typedef void InstallFinish(int p0);
typedef void DownloadFinish(int p0);
typedef void DownloadProgress(int progress);
typedef void InterceptUrlCallBack(String url, Map<String, String> headers);

class X5SdkListener {
  ///安装内核借国
  InstallFinish onInstallFinish;

  X5SdkListener(
      {@required this.onInstallFinish,});
}
