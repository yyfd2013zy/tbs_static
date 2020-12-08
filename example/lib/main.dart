import 'package:flutter/material.dart';

import 'package:flutter/services.dart';
import 'package:tbs_static/tbs_static.dart';
import 'package:tbs_static_example/X5WebViewPage.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  @override
  void initState() {
    super.initState();
  }



  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home:Builder(builder: buildScoffold),
    );
  }

  Widget buildScoffold(BuildContext context) {
    return   Scaffold(
      appBar: AppBar(
        title: const Text('Plugin example app'),
      ),
      body: Center(
        child: Column(
          children: <Widget>[
            new MaterialButton(
              color: Colors.blue,
              textColor: Colors.white,
              child: new Text('跳转到原生X5WebView'),
              onPressed: () async {
                try {
                  //跳转到native方式的使用了Tba浏览器的WebViewActivity
                  await TbsStatic.openWebActivity("http://debugtbs.qq.com",title: "TestPage",landspace:false );
                } on PlatformException {

                }
              },
            ),

            new MaterialButton(
              color: Colors.blue,
              textColor: Colors.white,
              child: new Text('跳转到X5WebViewWidget'),
              onPressed: () async {
                Navigator.push(context, MaterialPageRoute(builder: (_) {
                  return new X5WebViewPage();
                }));
              },
            ),
          ],
        ),

      ),
    );
  }
}
