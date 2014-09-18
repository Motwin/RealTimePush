package com.motwin.realTimePush.integration;

import com.motwin.client.test.instruction.builder.Instructions;
import com.motwin.client.test.network.ClientEndPointContext;

public class IntegrationTestCommon {

    public final static ClientEndPointContext END_POINT_CONTEXT = Instructions.aEndPointContext("localhost", Integer
                                                                        .parseInt(System.getProperty(
                                                                                "mos.integration.tests.port", "1247")),
                                                                        Instructions.withUserInfos("realTimePush",
                                                                                "3.3.0", "TestTool", "Java", "3.3.0"));
}
