package com.wsep202.TradingSystem.config.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.primitives.Ints;
import com.wsep202.TradingSystem.dto.ShoppingBagDto;
import com.wsep202.TradingSystem.dto.ShoppingCartDto;

import java.io.IOException;
import java.util.*;

public class ShoppingCartDtoDeserializer extends StdDeserializer<ShoppingCartDto> {

    public ShoppingCartDtoDeserializer() {
        this(null);
    }

    public ShoppingCartDtoDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ShoppingCartDto deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        Iterator<Map.Entry<String, JsonNode>> ShoppingCartJsonNode = node.fields();
        Map<Integer, ShoppingBagDto> shoppingBags = new HashMap<>();
        while(ShoppingCartJsonNode.hasNext()){
            Map.Entry<String, JsonNode> ShoppingBagJsonNode = ShoppingCartJsonNode.next();
            Integer storeId = getIntegerValue(ShoppingBagJsonNode.getKey());
            Iterator<Map.Entry<String, JsonNode>> shoppingBagsJsonNode = ShoppingBagJsonNode.getValue().fields();
            Map<Integer, Integer> shoppingBag = new HashMap<>();
            while(shoppingBagsJsonNode.hasNext()){
                Map.Entry<String, JsonNode> shoppingBagJsonNode = shoppingBagsJsonNode.next();
                Integer productSn = getIntegerValue(shoppingBagJsonNode.getKey());
                int amount = getIntegerValue(shoppingBagJsonNode.getValue().asText());
                shoppingBag.put(productSn, amount);
            }
            ShoppingBagDto shoppingBagDto = ShoppingBagDto.builder()
                    .productListFromStore(shoppingBag)
                    .build();
            shoppingBags.put(storeId, shoppingBagDto);
        }
        return ShoppingCartDto.builder()
                .shoppingBags(shoppingBags)
                .build();
    }

    private Integer getIntegerValue(String s) {
        return Optional.ofNullable(s)
                .map(Ints::tryParse)
                .orElse(-1);
    }

}
