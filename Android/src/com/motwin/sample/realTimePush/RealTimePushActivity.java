package com.motwin.sample.realTimePush;

import android.app.Activity;
import android.os.Bundle;

/**
 * Abstract base class for Activity that require a connection with the distant
 * server application
 * 
 * @author Motwin
 * 
 */
public abstract class RealTimePushActivity extends Activity {

    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);

        // The first Activity of the application will instantiate the
        // MotwinFacade settings
        if (isTaskRoot()) {
            MotwinFacade.initSettings(getApplicationContext());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // connect the client channel.
        // It is highly recommended to do this in onStart, and not in onResume,
        // in order to avoid application to be disconnected when switching from
        // one Activity to the other
        MotwinFacade.getClientChannel().connect();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // This allows MotwinFacade to keep an instance of the running
        // Activity, in order to be able to perform actions requiring an
        // Activity (such as display Dialog)
        MotwinFacade.setCurrentActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // disconnect client channel.
        // It is highly recommended to do this in onStop, and not in onPause,
        // in order to avoid application to be disconnected when switching from
        // one Activity to the other
        MotwinFacade.getClientChannel().disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // The last Activity of the application will destroy the Facade, and
        // release all resources
        if (isTaskRoot()) {
            MotwinFacade.destroy();
        }
    }

}