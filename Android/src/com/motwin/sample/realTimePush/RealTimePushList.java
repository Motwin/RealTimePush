package com.motwin.sample.realTimePush;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.motwin.android.exception.ExceptionContainer;
import com.motwin.android.log.Logger;
import com.motwin.android.streamdata.ContinuousQueryController;
import com.motwin.android.streamdata.ContinuousQueryController.SyncStatus;
import com.motwin.android.streamdata.ContinuousQueryError;
import com.motwin.android.streamdata.Query;
import com.motwin.sample.realTimePush.datamodel.M;
import com.motwin.sample.realTimePush.widget.FlashingTextView;

/**
 * This Activity displays a list with changing values
 * 
 * @author Motwin
 * 
 */
public class RealTimePushList extends RealTimePushActivity {

    private final static String       TAG             = "RealTimePushList";

    /**
     * The query string used for ContinuousQuery
     */
    private final String              LIST_FEED_QUERY = "select * from RealTimePush where price > ? order by price";

    private ListView                  listView;
    private ContinuousQueryController continuousQueryController;
    private RealTimePushListAdapter   listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the Activity layout
        setContentView(R.layout.main);

        System.setProperty("java.net.preferIPv6Addresses", "false");

        // Configure the list view
        listView = (ListView) findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        // Define the behavior when clicking on a element of the list
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                // Move cursor to the selected element
                Cursor cursor;
                cursor = listAdapter.getCursor();
                cursor.moveToPosition(position);

                // Get the title of the selected element
                int titleIndex = cursor.getColumnIndexOrThrow(M.RealTimePush.title);
                String title = cursor.getString(titleIndex);

                // Start a new Activity, passing the title as argument
                Intent i = new Intent(RealTimePushList.this, RealTimePushDetails.class);
                i.putExtra("title", title);
                startActivity(i);
            }
        });

        // Create the list Adapter.
        listAdapter = new RealTimePushListAdapter(this, null);
        listView.setAdapter(listAdapter);

        // Create the ContinuousQuery
        continuousQueryController = MotwinFacade.getContinuousQueryFactory().newContinuousQueryController(
                new Query(LIST_FEED_QUERY, ImmutableList.<Object> of(0)));
        continuousQueryController.addListener(new Callback());

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start the ContinuousQuery
        continuousQueryController.start();
    }

    @Override
    protected void onPause() {
        // Stops the ContinuousQuery if necessary
        if (continuousQueryController.getStatus() != ContinuousQueryController.SyncStatus.STOPPED) {
            continuousQueryController.stop();
        }
        // Remove data from UI
        listAdapter.changeCursor(null);
        super.onPause();
    }

    /**
     * ContinuousQuery Listener
     * 
     * @author Motwin
     * 
     */
    class Callback implements ContinuousQueryController.Callback {

        @Override
        public void continuousQueryDataChanged(ContinuousQueryController aQuery, Cursor aQueryData) {
            // Data have been updated : update the UI
            Logger.v(TAG, "%s data changed", aQuery);

            Cursor previousCursor = listAdapter.getCursor();
            listAdapter.changeCursor(aQueryData);

            // Close obsolete cursor
            if (previousCursor != null) {
                previousCursor.close();
            }
        }

        @Override
        public void continuousQuerySyncStatusChanged(ContinuousQueryController aQueryController, SyncStatus aStatus) {
            // The status of the continuous query changed
            Logger.i(TAG, "Status changed : %s", aStatus);

        }

        @Override
        public void
                continuousQueryExceptionCaught(ContinuousQueryController aQueryController, ExceptionContainer aCause) {
            // An error occured on the continuous query : log it.
            String errorMessage;
            if (aCause.getContent() instanceof ContinuousQueryError) {
                errorMessage = ((ContinuousQueryError) aCause.getContent()).getMessage();
            } else {
                errorMessage = aCause.getContent().toString();
            }
            Logger.e(TAG, errorMessage);
        }
    }

    /**
     * Custom cursor adapter for ContinuousQuery result
     * 
     * @author Motwin
     * 
     */
    private static class RealTimePushListAdapter extends CursorAdapter {
        /**
         * Store the values to be able to flash the price value text box
         * depending on the variation of the value
         */
        private final Map<String, Integer> quoteMap = new HashMap<String, Integer>(50);

        public RealTimePushListAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // Create new list cell
            View view = LayoutInflater.from(context).inflate(R.layout.cell, parent, false);

            // Store view elements in the Tag to improve list performance
            ViewWrapper viewWrapper = new ViewWrapper();
            viewWrapper.titleTextView = (TextView) view.findViewById(R.id.title);
            viewWrapper.priceLayout = (FlashingTextView) view.findViewById(R.id.price);
            view.setTag(viewWrapper);

            return view;
        }

        @Override
        public void bindView(final View view, Context context, Cursor cursor) {
            // Update UI according to current value

            // Get view elements from the tag
            ViewWrapper viewWrapper = (ViewWrapper) view.getTag();
            TextView titleTextView = viewWrapper.titleTextView;
            FlashingTextView priceLayout = viewWrapper.priceLayout;

            // Set current value
            int titleIndex, priceIndex;
            titleIndex = cursor.getColumnIndexOrThrow(M.RealTimePush.title);
            priceIndex = cursor.getColumnIndexOrThrow(M.RealTimePush.price);

            // Title
            String title;
            title = cursor.getString(titleIndex);
            titleTextView.setText(title);

            // Get the current price
            int price;
            price = cursor.getInt(priceIndex);
            // Get the previous price value for this element
            Integer previousPrice;
            previousPrice = quoteMap.get(title);
            if (previousPrice == null) {
                previousPrice = price;
            }
            // Stores the new price value
            quoteMap.put(title, price);

            priceLayout.setPrice(price, previousPrice);

            // Force UI refresh
            notifyDataSetChanged();
        }
    }

    private static class ViewWrapper {
        TextView       titleTextView;
        FlashingTextView priceLayout;
    }
}