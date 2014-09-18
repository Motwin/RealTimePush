//
//  MTAppDelegate.h
//  RealTimePush
//
//  Copyright (c) Motwin 2010, 2012. All rights reserved.
//

#import <UIKit/UIKit.h>

@class MNClientChannel;
@class MTViewController;

@interface MTAppDelegate : UIResponder <UIApplicationDelegate> {
    MNClientChannel *channel;
}

@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) MTViewController *viewController;

@end
