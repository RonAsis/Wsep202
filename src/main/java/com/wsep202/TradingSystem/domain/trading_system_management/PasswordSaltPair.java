package com.wsep202.TradingSystem.domain.trading_system_management;

import java.util.Optional;

/**
 * this class represents a hashed password with its related generated salt
 */
public class PasswordSaltPair {
    private String hashedPassword;  //password 512 bits hash
    private String salt;


    public PasswordSaltPair(String hashedPassword, String salt) {
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }


    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

}
