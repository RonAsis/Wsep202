package com.wsep202.TradingSystem.domain.trading_system_management;

import org.springframework.security.core.GrantedAuthority;

public enum GrantedAuthorityImpl implements GrantedAuthority {

   USER(){
       @Override
       public String getAuthority() {
           return "ROLE_USER";
       }
   },
   ADMIN{
       @Override
       public String getAuthority() {
           return "ROLE_USER";
       }
   };

}
