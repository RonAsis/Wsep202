package Externals;

import java.util.Optional;

/**
 * this class represents a hashed password with its related generated salt
 */
public class PasswordSaltPair {
    private Optional<String> hashedPassword;  //password 512 bits hash
    private String salt;


    public PasswordSaltPair(Optional<String> hashedPassword, String salt) {
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }


    public Optional<String> getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(Optional<String> hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

}
