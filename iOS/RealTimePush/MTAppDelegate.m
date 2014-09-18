//
//  MTAppDelegate.m
//  RealTimePush
//
//  Copyright (c) Motwin 2010, 2012. All rights reserved.
//

#import "MTAppDelegate.h"
#import "MTViewController.h"
#import "MotwinPlatform.h"

@implementation MTAppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];


    self.viewController = [[MTViewController alloc] initWithNibName:@"MTViewController_iPhone" bundle:nil];

    channel = [[MNClientChannel alloc] initWithServerURL:[NSURL URLWithString:@"zsocket://tests.motwin.net:1247"] serverAppName:@"realTimePush" serverAppVersion:@"3.2.0"];
    [channel makeAvailableWithKey:@"mainServer"];
    [channel connect];

    self.window.rootViewController = self.viewController;
    [self.window makeKeyAndVisible];

    return YES;
}


@end
