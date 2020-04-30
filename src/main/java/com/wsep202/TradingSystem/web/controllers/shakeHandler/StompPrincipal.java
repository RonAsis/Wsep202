package com.wsep202.TradingSystem.web.controllers.shakeHandler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.Principal;

@AllArgsConstructor
@Data
public class StompPrincipal implements Principal {

    private String name;

}
