package com.wsep202.TradingSystem.domain.trading_system_management;

import Externals.PasswordSaltPair;
import Externals.SecuritySystem;
import jdk.internal.util.xml.impl.Pair;

import java.util.Optional;

public class ExternalServiceManagement {

    public void connect() {
        // if cant throw exception
        //TODO
    }

    /**
     * Request to get hashed password and its salt from the Security system.
     * @param password - password received from the user
     * @return hashedPass - password after encryption
     */
    public PasswordSaltPair getEncryptedPasswordAndSalt(String password) {
        //get salt for the user password to hash with
        String userSalt = SecuritySystem.generateSalt(512).get();
        //generate unique password for the user using the salt
        Optional<String> hashedPassword = SecuritySystem.hashPassword(password,userSalt);
        PasswordSaltPair passAndSalt = new PasswordSaltPair(hashedPassword,userSalt);
        return passAndSalt; //return the password generated with the used salt to generate it
    }

    /**
     * authenticate the user password against the saved password in DB
     * @param password the password to check
     * @param user the user we want to verify its password
     * @return true if the inserted password is correct
     * otherwise return false
     */
    public boolean isAuthenticatedUserPassword(String password, UserSystem user){
        boolean isLegitimateUser = SecuritySystem.verifyPassword(password,user.getPassword().toString(),user.getSalt());
        return isLegitimateUser;
    }
}
