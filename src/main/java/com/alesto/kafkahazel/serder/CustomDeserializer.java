package com.alesto.kafkahazel.serder;

import java.io.IOException;

import com.alesto.kafkahazel.domain.RecordPayload;
import com.alesto.kafkahazel.domain.User;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class CustomDeserializer extends StdDeserializer<RecordPayload> {

    protected CustomDeserializer() {
        super(RecordPayload.class);
    }

    @Override
    public RecordPayload deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        TreeNode node = p.readValueAsTree();

        return p.getCodec().treeToValue(node, User.class);
    }
}
