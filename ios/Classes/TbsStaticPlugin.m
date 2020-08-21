#import "TbsStaticPlugin.h"
#if __has_include(<tbs_static/tbs_static-Swift.h>)
#import <tbs_static/tbs_static-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "tbs_static-Swift.h"
#endif

@implementation TbsStaticPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftTbsStaticPlugin registerWithRegistrar:registrar];
}
@end
