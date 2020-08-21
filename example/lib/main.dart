import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:tbs_static/tbs_static.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await TbsStatic.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: InkWell(
            onTap: () async {
              try {

                var gg = await TbsStatic.openWebActivity(" https://10.155.0.134:31311/static/testvideo/video.html?sid=60cbd910-d555-11ea-af73-45d5ffed74da&userName=cross&token=CA50B2B0E37011EA87A219C1CF68FE0E&serverAddress=wss://10.155.0.135:30670/hari&accountId=AZ019121",title: "测试界面",landspace:true );
              } on PlatformException {

              }
            },
            child: Text("跳转!"),
          ),
        ),
      ),
    );
  }
}
