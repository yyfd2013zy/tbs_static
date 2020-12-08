import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:tbs_static/tbs_static.dart';
import 'dart:async';

import 'package:tbs_static/x5_webview.dart';


class X5WebViewPage extends StatefulWidget {
  @override
  _X5WebViewState createState() => _X5WebViewState();
}

class _X5WebViewState extends State<X5WebViewPage> {
  X5WebViewController _controller;
  @override
  void initState() {
    super.initState();
    loadX5();
  }

  void loadX5() async {
    //内核下载安装监听
    await TbsStatic.setX5SdkListener(X5SdkListener(onInstallFinish: (int p0) {
      print("X5WebViewPage 5内核安装完成 $p0 ");
      //showToast("视频插件安装完成，请重启应用");
    }));

    TbsStatic.preinstallStaticTbs().then((value) {
      print("X5WebViewPage $value");

    });
  }


  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text(''),
        ),
        body: Center(
          child: Stack(
            children: <Widget>[
              X5WebView(
                url: "http://debugtbs.qq.com",
                javaScriptEnabled: true,
                javascriptChannels:
                JavascriptChannels(["X5Web", "Toast"], (name, data) {
                  switch (name) {
                    case "X5Web":
                      showDialog(
                          context: context,
                          builder: (context) {
                            return AlertDialog(
                              title: Text("获取到的字符串为："),
                              content: Text(data),
                            );
                          });
                      break;
                    case "Toast":
                      print(data);
                      break;
                  }
                }),
                onWebViewCreated: (control) {
                  _controller = control;
                },
                onPageFinished: () async {
                  var url = await _controller.currentUrl();
                  print("webview  $url");
                },
                onProgressChanged: (progress) {
                  print("webview加载进度------$progress%");
                },
              ),
            ],
          ),


        ),
      ),
    );
  }
}

