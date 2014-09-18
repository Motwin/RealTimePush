//
//  Copyright (c) 2012 Motwin. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MDContinuousQueryControllerDelegate.h"

@class MDContinuousQueryController;

@interface MTViewController : UIViewController <MDContinuousQueryControllerDelegate, UITableViewDataSource> {
    IBOutlet UITableView *tableView;
    MDContinuousQueryController *queryController;
}

@end
