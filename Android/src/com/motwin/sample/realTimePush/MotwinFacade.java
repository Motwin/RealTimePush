/**
 * 
 */
package com.motwin.sample.realTimePush;

import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import com.google.common.base.Preconditions;
import com.motwin.android.broadcast.ClientChannelIntent;
import com.motwin.android.broadcast.ClientUpdateIntent;
import com.motwin.android.log.Logger;
import com.motwin.android.network.clientchannel.ClientChannel;
import com.motwin.android.network.clientchannel.ClientChannelFactory;
import com.motwin.android.streamdata.ContinuousQueryFactory;
import com.motwin.android.streamdata.ContinuousQueryFactoryBuilder;

/**
 * Motwin Facade is a Singleton class that will allow accessing all Motwin SDK
 * features
 * 
 * @author Motwin
 * 
 */
public class MotwinFacade {

    public static final String            TAG           = "MotwinFacade";

    /**
     * Set the name of your application database here
     */
    public static final String            DATABASE_NAME = "RealTimePushDB";
    /**
     * Enter the name of the server configuration file (located in assets
     * directory)
     */
    public static final String            CONF_SERVER   = "confServer.properties";
    /**
     * Enter the name of mapping file (located in assets directory).
     * 
     * Can be null.
     */
    public static final String            MAPPING       = null;

    private static final Handler          HANDLER       = new Handler();

    private static boolean                ready         = false;

    private static Context                applicationContext;
    private static Activity               currentActivity;

    private static ClientChannel          clientChannel;
    private static ContinuousQueryFactory continuousQueryFactory;

    private static BroadcastReceiver      screenOnNotifier;
    private static BroadcastReceiver      screenOffNotifier;
    private static BroadcastReceiver      clientUpdateReceiver;
    private static BroadcastReceiver      clientChannelStateReceiver;

    private static Runnable               destroyTask;

    /**
     * Call this method once on application startup to initialize the required
     * settings for your application. Typically, this will be called in
     * onCreate() method of your Activity if isTaskRoot() returns true.
     * 
     * @param anApplicationContext
     *            The application context
     */
    public static void initSettings(Context anApplicationContext) {
        System.setProperty("java.net.preferIPv6Addresses", "false");

        applicationContext = anApplicationContext;
        ready = true;

        // ////////////////////////////////////////////////////////////////////
        // Add the 2 following broadcast receivers if you want your application
        // to stop sending/receiving data while screen is off
        // ////////////////////////////////////////////////////////////////////
        screenOffNotifier = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (clientChannel != null) {
                    Logger.d(TAG, "SCREEN OFF : disconnect");
                    clientChannel.disconnect();
                }
            }
        };
        applicationContext.registerReceiver(screenOffNotifier, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        screenOnNotifier = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (clientChannel != null) {
                    Logger.d(TAG, "SCREEN ON : connect");
                    clientChannel.connect();
                }
            }
        };
        applicationContext.registerReceiver(screenOnNotifier, new IntentFilter(Intent.ACTION_SCREEN_ON));

        // ////////////////////////////////////////////////////////////////////
        // Add the following broadcast receivers to handle application update
        // notifications
        // ////////////////////////////////////////////////////////////////////
        clientUpdateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context aContext, Intent aIntent) {
                Preconditions
                        .checkArgument(aIntent instanceof ClientUpdateIntent, "Unexpected intent type %s", aIntent);
                handleClientUpdate((ClientUpdateIntent) aIntent);
            }
        };
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(clientUpdateReceiver,
                new IntentFilter(ClientUpdateIntent.ACTION_CLIENT_UPDATE));

        // ////////////////////////////////////////////////////////////////////
        // Add the following broadcast receivers to handle client channel
        // status change notification
        // ////////////////////////////////////////////////////////////////////
        clientChannelStateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context aContext, Intent aIntent) {
                Preconditions.checkArgument(aIntent instanceof ClientChannelIntent, "Unexpected intent type %s",
                        aIntent);
                handleClientChannelStateChanged((ClientChannelIntent) aIntent);

            }
        };

        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(clientChannelStateReceiver,
                new IntentFilter(ClientChannelIntent.ACTION_CLIENT_CHANNEL_STATE_CHANGED));

        // ////////////////////////////////////////////////////////////////////
        // This destroyTask is intented to handle screen rotation, or physical
        // keyboard actions. When screen is rotated, or physical keyboard
        // output, the current Activity is destroyed and immediatly recreated.
        //
        // If destroy method immediatly releases all resources, this will cause
        // the application to be reset, and a new application session will be
        // created each time screen will change orientation.
        //
        // This destroy task will actually perform a few seconds after destroy
        // method is called, if it is not cancelled in the mean time. This
        // allows to delay resource release for a very short time in ordre to
        // not loose data if it is immediatly recreated after destroy
        // ////////////////////////////////////////////////////////////////////
        if (destroyTask != null) {
            HANDLER.removeCallbacks(destroyTask);
        }
        destroyTask = new Runnable() {

            @Override
            public void run() {
                performDestroy();

            }
        };

    }

    /**
     * Set the current Activity of the application
     */
    public static void setCurrentActivity(Activity anActivity) {
        currentActivity = anActivity;

    }

    /**
     * Get the instance of ClientChannel. Before calling this method, your must
     * have initialized the settings by calling
     * {@link MotwinFacade#initSettings(Context)}
     * 
     * 
     * If you want your application to keep connected while navigating from
     * Activity to Activity, you need to call getClientChannel().connect()
     * method on onStart of your Activity, and getClientChannel().disconnect()
     * method on onStop of your Activity.
     * 
     * @return the clientChannel
     */
    public static ClientChannel getClientChannel() {
        Preconditions.checkState(ready, "Motwin Facade is not ready. Use initSettings before this");
        if (clientChannel == null) {
            clientChannel = ClientChannelFactory.build(CONF_SERVER, MAPPING, applicationContext);
        }
        return clientChannel;
    }

    /**
     * Get the instance of ContinuousQueryFactory. Before calling this method,
     * your must have initialized the settings by calling
     * {@link MotwinFacade#initSettings(Context)}
     * 
     * 
     * @return the clientChannel
     */
    public static ContinuousQueryFactory getContinuousQueryFactory() {
        Preconditions.checkState(ready, "Motwin Facade is not ready. Use initSettings before this");
        if (continuousQueryFactory == null) {
            continuousQueryFactory = ContinuousQueryFactoryBuilder.build(DATABASE_NAME, applicationContext,
                    getClientChannel());
        }
        return continuousQueryFactory;
    }

    /**
     * Call this method once on application closure to destroy all singleton
     * objects. Typically, this will be called in onDestroy() method if
     * isTaskRoot() returns true.
     * 
     * This method will plan a destroy task to be called after a very short
     * period of time, in order to make sure not to loose the application
     * session if Activity is immedialtly recreated after destroy (in case of
     * screen rotation or physical keyboard action for instance)
     * 
     */
    public static void destroy() {
        ready = false;

        if (screenOnNotifier != null) {
            applicationContext.unregisterReceiver(screenOnNotifier);
        }
        if (screenOffNotifier != null) {
            applicationContext.unregisterReceiver(screenOffNotifier);
        }
        if (clientUpdateReceiver != null) {
            LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(clientUpdateReceiver);
        }
        if (clientChannelStateReceiver != null) {
            LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(clientChannelStateReceiver);
        }

        Logger.i(TAG, "Schedule Facade Destroy");
        HANDLER.postDelayed(destroyTask, 1 * 1000);
    }

    /**
     * Actually perform the destroy : release all resources.
     */
    private static void performDestroy() {
        clientChannel = null;
        if (continuousQueryFactory != null) {
            continuousQueryFactory.close();
            continuousQueryFactory = null;
        }
        applicationContext = null;
        currentActivity = null;
        ready = false;

    }

    /**
     * Handle an application update notification.
     * 
     * Get all update information from the intent. An update has
     * <ul>
     * <li>a title and a message to describe the update</li>
     * <li>a URL where the new version can be downloaded</li>
     * <li>a boolean indicating if the update is required or recommended.
     * <p>
     * If update is recommended, the application will be able to get connected
     * with the server, and so, work properly.
     * </p>
     * <p>
     * If update is required, the application will never get connected to the
     * server.
     * </p>
     * </li>
     * </ul>
     * 
     * @param aIntent
     *            the intent containing the update information.
     */
    private static void handleClientUpdate(ClientUpdateIntent aIntent) {
        Preconditions.checkNotNull(aIntent, "aIntent cannot be null");

        // Get informations from the intent
        final String title = aIntent.getTitle();
        final String message = aIntent.getMessage();
        final String url = aIntent.getUrl();
        final boolean mandatory = aIntent.isMandatory();

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        builder.setTitle(title).setCancelable(true).setMessage(message)
                .setPositiveButton(R.string.button_update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        currentActivity.startActivity(intent);
                        dialog.cancel();
                    }
                });

        // If update is required, cancel button of the AlertDialog will close
        // the application
        if (mandatory) {
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface paramDialogInterface) {
                    currentActivity.finish();
                }
            });
        }
        // If update is recommended, cancel button of the AlertDialog will
        // dismiss the dialog, and user will be able to navigate in the
        // application
        else {
            builder.setNegativeButton(R.string.button_ignore, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        }

        builder.create().show();
    }

    /**
     * Handle notification of ClientChannel state changing.
     * 
     * @param aIntent
     *            the intent containing the ClientChannel information
     */
    private static void handleClientChannelStateChanged(ClientChannelIntent aIntent) {
        Preconditions.checkNotNull(aIntent, "aIntent cannot be null");

        Logger.d(TAG, "%s state changed to %s", aIntent.getClientChannel(), aIntent.getClientChannelState());

        Map<String, Object> infos = aIntent.getInfos();
        if (infos != null) {
            Logger.d(TAG, "Additional infos %s", infos);
        }

        // TODO : handle application session when state is SESSION_OPENED. This
        // means that a new application session have been opened server side. In
        // this case, you might want to reset login status for instance, or
        // reset all information related to one application session.
    }

}
