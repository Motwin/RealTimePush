package com.motwin.sample.realTimePush;

import org.apache.commons.lang.StringUtils;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.motwin.android.log.Logger;
import com.motwin.android.streamdata.ContinuousQueryController;
import com.motwin.android.streamdata.ContinuousQueryController.SyncStatus;
import com.motwin.android.streamdata.ContinuousQueryError;
import com.motwin.android.streamdata.Query;
import com.motwin.sample.realTimePush.datamodel.M;
import com.motwin.sample.realTimePush.widget.FlashingTextView;

/**
 * This Activity displays the detail for an element
 * 
 * @author Motwin
 * 
 */
public class RealTimePushDetails extends RealTimePushActivity {

    private final static String       TAG = "RealTimePushDetails";

    private Cursor                    data;
    private String                    title;
    private Integer                   price;

    private ContinuousQueryController continuousQueryController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the Activity layout
        setContentView(R.layout.list2);

        // Initialize default values
        data = null;
        price = null;

        // Get the title from extras
        title = this.getIntent().getExtras().getString("title");

        // Create the ContinuousQuery
        String listEntryQueryString;
        listEntryQueryString = "select * from RealTimePush where title=?";
        continuousQueryController = MotwinFacade.getContinuousQueryFactory().newContinuousQueryController(
                new Query(listEntryQueryString, ImmutableList.<Object> builder().add(title).build()));
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
        super.onPause();
        // Stops the ContinuousQuery
        continuousQueryController.stop();
        // Clear UI
        if (data != null) {
            data.close();
        }
    }

    /**
     * Update UI from provided value
     * 
     * @param aData
     *            the provided data. This cursor is expected to contain exactly
     *            one row
     */
    private void updateView(Cursor aData) {
        Preconditions.checkNotNull(aData, "aData cannot be null");
        Preconditions.checkArgument(aData.getCount() == 1, "Cursor has %s elements. Not supported", aData.getCount());
        Preconditions.checkArgument(aData.moveToFirst(), "Empty cursor");

        // Close previous data
        if (data != null) {
            data.close();
        }
        data = aData;

        // Update UI from Cursor data
        int titleColumnIndex = aData.getColumnIndexOrThrow(M.RealTimePush.title);
        int priceColumnIndex = aData.getColumnIndexOrThrow(M.RealTimePush.price);

        // Title
        ((TextView) findViewById(R.id.title)).setText(aData.getString(titleColumnIndex));

        // Update Price
        Integer newPrice = aData.getInt(priceColumnIndex);
        FlashingTextView priceView = (FlashingTextView) findViewById(R.id.price);
        if (price == null) {
            price = newPrice;
        }
        priceView.setPrice(newPrice, price);
        price = newPrice;

        // Params
        TableLayout layout = (TableLayout) findViewById(R.id.paramTable);
        layout.removeAllViews();
        for (int i = 0; i < aData.getColumnCount(); i++) {
            String columnName = aData.getColumnName(i);
            if (StringUtils.startsWith(columnName, "param")) {
                View row = LayoutInflater.from(RealTimePushDetails.this).inflate(R.layout.simplecell, null);

                TextView paramName = (TextView) row.findViewById(R.id.name);
                TextView paramValue = (TextView) row.findViewById(R.id.value);

                paramName.setText(columnName);
                paramValue.setText(aData.getString(i));

                layout.addView(row);
            }
        }
    }

    /**
     * ContinuousQuery Callback
     * 
     * @author Motwin
     * 
     */
    class Callback implements ContinuousQueryController.Callback {
        @Override
        public void continuousQueryDataChanged(ContinuousQueryController aQuery, Cursor aQueryData) {
            Logger.v(TAG, "%s data changed", aQuery);

            if (aQueryData != null) {
                updateView(aQueryData);
            }

        }

        @Override
        public void continuousQuerySyncStatusChanged(ContinuousQueryController aQueryController, SyncStatus aStatus) {
            Logger.i(TAG, "Status changed : %s", aStatus);

        }

        @Override
        public void continuousQueryExceptionCaught(ContinuousQueryController aQueryController,
                                                   com.motwin.android.exception.ExceptionContainer aCause) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RealTimePushDetails.this);
            builder.setTitle("Error " + aCause.getCode());
            if (aCause.getContent() instanceof ContinuousQueryError) {
                builder.setMessage(((ContinuousQueryError) aCause.getContent()).getMessage());
            } else {
                builder.setMessage(aCause.getContent().toString());
            }
            builder.show();
        };
    }

}
