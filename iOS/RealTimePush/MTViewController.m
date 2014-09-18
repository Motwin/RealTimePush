//
//  Copyright (c) Motwin 2010, 2012. All rights reserved.
//

#import "MTViewController.h"
#import "MotwinPlatform.h"

@implementation MTViewController

- (void)viewDidLoad
{
    [super viewDidLoad];

    MNClientChannel *mainChannel = [MNClientChannel channelForKey:@"mainServer"];
    MDQuery *query = [MDQuery queryWithString:@"SELECT * FROM RealTimePush order by price desc"];
    //SELECT * From RealTimePush where price > 10 order by price limit 10

    queryController = [[MDContinuousQueryController alloc] initWithQuery:query
                                                           clientChannel:mainChannel
                                                                delegate:self];
}

- (void)viewWillAppear:(BOOL)animated
{
    [queryController start];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [queryController stop];
}

#pragma mark - MDContinuousQueryControllerDelegate

- (void)queryControllerDidChangeContent:(MDContinuousQueryController *)controller
{
    [tableView reloadData];
}

#pragma mark - UITableViewDataSource

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return [queryController numberOfSections];
}

- (NSInteger)tableView:(UITableView *)aTableView numberOfRowsInSection:(NSInteger)section
{
    return [queryController numberOfRowsInSection:section];
}

- (UITableViewCell *)tableView:(UITableView *)aTableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identifier = @"tableViewCellIdentifier";
    UITableViewCell *cell = [aTableView dequeueReusableCellWithIdentifier:identifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue2 reuseIdentifier:identifier];
    }

    NSDictionary *dataObject = [queryController objectAtIndexPath:indexPath];
    cell.textLabel.text = [dataObject objectForKey:@"title"];
    cell.detailTextLabel.text = [[dataObject objectForKey:@"price"] stringValue];

    return cell;
}

@end
