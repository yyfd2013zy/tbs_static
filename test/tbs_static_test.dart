import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:tbs_static/tbs_static.dart';

void main() {
  const MethodChannel channel = MethodChannel('tbs_static');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await TbsStatic.platformVersion, '42');
  });
}
