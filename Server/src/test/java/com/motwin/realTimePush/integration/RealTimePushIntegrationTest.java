package com.motwin.realTimePush.integration;

import static com.motwin.client.test.extension.requesting.RequestExtension.requestingExtension;
import static com.motwin.client.test.extension.streamdata3.StreamdataExtensionV3.column;
import static com.motwin.client.test.extension.streamdata3.StreamdataExtensionV3.continuousQueryController;
import static com.motwin.client.test.extension.streamdata3.StreamdataExtensionV3.countRows;
import static com.motwin.client.test.extension.streamdata3.StreamdataExtensionV3.hasExactlyColumns;
import static com.motwin.client.test.extension.streamdata3.StreamdataExtensionV3.query;
import static com.motwin.client.test.extension.streamdata3.StreamdataExtensionV3.rowExists;
import static com.motwin.client.test.extension.streamdata3.StreamdataExtensionV3.streamdataV3Extension;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.motwin.client.test.Client;
import com.motwin.client.test.extension.streamdata3.StreamdataExtensionV3.ControllerBuilder;
import com.motwin.client.test.extension.streamdata3.matchers.HasValueMatcher;
import com.motwin.client.test.instruction.builder.Instructions;

public class RealTimePushIntegrationTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testGetRealTimePush() throws Exception {
        // Creating a new client to simulate a mobile connection to the server
        Client client = new Client(null, null, true, true, 60000000);

        // build a test scenario
        client.checking(new Instructions() {
            {
                ControllerBuilder realTimePushController = continuousQueryController("realTimePush");

                // request and StreamData will be used
                using(requestingExtension());
                using(streamdataV3Extension());

                // connects the client to the server using
                // DEFAULT_END_POINT_CONTEXT configuration to reach the server
                connect(IntegrationTestCommon.END_POINT_CONTEXT);
                wait(aConnection(), 30, TimeUnit.SECONDS);

                // send a streamData request to the Feed table
                send(realTimePushController.start("select * from RealTimePush"));

                // wait for sync with server
                wait(realTimePushController.didDataChange(20), 60, TimeUnit.SECONDS);

                // check content of the response table returnd by the server
                assertThat(realTimePushController.schema().matches(
                        hasExactlyColumns(column("title", String.class), column("param1", String.class),
                                column("param2", String.class), column("param3", String.class),
                                column("param4", String.class), column("param5", String.class),
                                column("param6", String.class), column("param7", String.class),
                                column("param8", String.class), column("price", Long.class))));
                assertThat(realTimePushController.table().matches(countRows(greaterThanOrEqualTo(1))));
                assertThat(realTimePushController.table().isPersistent(false));

                // end the connection
                disconnect();
                wait(aDisconnection(), 30, TimeUnit.SECONDS);

            }
        });

        // validate the scenario
        client.assertExecutedSuccesfully();

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCanPerformComplexQuery() throws Exception {
        // Creating a new client to simulate a mobile connection to the server
        Client client = new Client(null, null, true, true, 60000000);

        // build a test scenario
        client.checking(new Instructions() {
            {
                ControllerBuilder realTimePushController = continuousQueryController("realTimePush");

                // request and StreamData will be used
                using(requestingExtension());
                using(streamdataV3Extension());

                // connects the client to the server using
                // DEFAULT_END_POINT_CONTEXT configuration to reach the server
                connect(IntegrationTestCommon.END_POINT_CONTEXT);
                wait(aConnection(), 30, TimeUnit.SECONDS);

                // send a streamData request to the Feed table
                send(realTimePushController.start(query(
                        "SELECT * FROM RealTimePush WHERE price > ? ORDER BY price LIMIT 10").addArgument(0).build()));

                // wait for sync with server
                wait(realTimePushController.didDataChange(10)); // , 30,
                                                                // TimeUnit.SECONDS

                // check content of the response table returnd by the server
                assertThat(realTimePushController.schema().matches(
                        hasExactlyColumns(column("title", String.class), column("param1", String.class),
                                column("param2", String.class), column("param3", String.class),
                                column("param4", String.class), column("param5", String.class),
                                column("param6", String.class), column("param7", String.class),
                                column("param8", String.class), column("price", Long.class))));
                assertThat(realTimePushController.table().matches(countRows(greaterThanOrEqualTo(1))));
                assertThat(realTimePushController.table().isPersistent(false));

                // end the connection
                disconnect();
                wait(aDisconnection(), 30, TimeUnit.SECONDS);

            }
        });

        // validate the scenario
        client.assertExecutedSuccesfully();

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCanPerformWhereQuery() throws Exception {
        // Creating a new client to simulate a mobile connection to the server
        Client client = new Client(null, null, true, true, 60000000);

        // build a test scenario
        client.checking(new Instructions() {
            {
                ControllerBuilder realTimePushController = continuousQueryController("realTimePush");

                // request and StreamData will be used
                using(requestingExtension());
                using(streamdataV3Extension());

                // connects the client to the server using
                // DEFAULT_END_POINT_CONTEXT configuration to reach the server
                connect(IntegrationTestCommon.END_POINT_CONTEXT);
                wait(aConnection(), 30, TimeUnit.SECONDS);

                // send a streamData request to the Feed table
                send(realTimePushController.start(query("select * from RealTimePush where title=?").addArgument(
                        "Value 1").build()));

                // wait for sync with server
                wait(realTimePushController.didDataChange(), 30, TimeUnit.SECONDS);

                // check content of the response table returnd by the server
                assertThat(realTimePushController.schema().matches(
                        hasExactlyColumns(column("title", String.class), column("param1", String.class),
                                column("param2", String.class), column("param3", String.class),
                                column("param4", String.class), column("param5", String.class),
                                column("param6", String.class), column("param7", String.class),
                                column("param8", String.class), column("price", Long.class))));
                assertThat(realTimePushController.table().matches(countRows(greaterThanOrEqualTo(1))));
                assertThat(realTimePushController.table().isPersistent(false));
                assertThat(realTimePushController.table().matches(
                        rowExists(HasValueMatcher.hasValue("title", "Value 1"))));

                // end the connection
                disconnect();
                wait(aDisconnection(), 30, TimeUnit.SECONDS);

            }
        });

        // validate the scenario
        client.assertExecutedSuccesfully();

    }
}
