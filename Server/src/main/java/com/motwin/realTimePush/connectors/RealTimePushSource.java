package com.motwin.realTimePush.connectors;

import java.util.Random;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.motwin.streamdata.Jsons;
import com.motwin.streamdata.spi.PollableSource;
import com.motwin.streamdata.spi.SourceMetadata;
import com.motwin.streamdata.spi.impl.SourceMetadataImpl;

public class RealTimePushSource implements PollableSource {

    private final Random random = new Random();
    private ArrayNode    rows   = null;

    public RealTimePushSource() {
        init();
    }

    @Override
    public SourceMetadata getMetadata() {
        return new SourceMetadataImpl(new String[0], new boolean[0]);
    }

    @Override
    public String getSourceType() {
        return "JSON";

    }

    @Override
    public Callable<JsonNode> execute(JsonNode aParameters) throws Exception {
        return new Callable<JsonNode>() {

            @Override
            public JsonNode call() throws Exception {

                for (int i = 0; i < random.nextInt(5) + 1; i++) {
                    ObjectNode row = (ObjectNode) rows.get(random.nextInt(rows.size()));

                    Long newValue = Long.valueOf((row.get("price").asLong()) + random.nextInt(10) - random.nextInt(10));
                    newValue = (newValue < 0 || newValue > 100) ? Long.valueOf(random.nextInt(100)) : newValue;
                    row.put("price", newValue);
                }

                return rows;
            }

        };
    }

    private void init() {
        rows = Jsons.newArray();
        for (int i = 1; i <= 20; i++) {
            ObjectNode row = Jsons.newObject();
            row.put("title", String.format("Value %s", i));
            row.put("price", Long.valueOf(random.nextInt(100)));
            row.put("param1", "value1");
            row.put("param2", "value2");
            row.put("param3", "value3");
            row.put("param4", "value4");
            row.put("param5", "value5");
            row.put("param6", "value6");
            row.put("param7", "value7");
            row.put("param8", "value8");
            rows.add(row);
        }

    }
}
