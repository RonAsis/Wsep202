package com.wsep202.TradingSystem.config.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.primitives.Ints;
import com.wsep202.TradingSystem.dto.ShoppingBagDto;
import com.wsep202.TradingSystem.dto.ShoppingCartDto;
import com.wsep202.TradingSystem.web.controllers.shakeHandler.ConnectionStart;

import java.io.IOException;
import java.util.*;

public class ConnectionStartDeserializer extends StdDeserializer<ConnectionStart> {

    public ConnectionStartDeserializer() {
        this(null);
    }

    public ConnectionStartDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ConnectionStart deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        Map.Entry<String, JsonNode> userLogging = fields.next();
        String username = userLogging.getValue().asText();
        UUID uuid = UUID.fromString(fields.next().getValue().asText());
        return ConnectionStart.builder()
                .username(username)
                .uuid(uuid)
                .build();
    }

    private Integer getIntegerValue(String s) {
        return Optional.ofNullable(s)
                .map(Ints::tryParse)
                .orElse(-1);
    }

}
