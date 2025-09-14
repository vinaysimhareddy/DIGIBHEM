package com.bank;
// We import the BCrypt class from the jbcrypt library we just added.
import org.mindrot.jbcrypt.BCrypt;

public class SecurityUtil {
    /**
     * This method takes a plain-text password and returns a securely hashed version.
     * The 'static' keyword means we can call this method directly from the class
     * without creating a SecurityUtil object, like this: SecurityUtil.hashPassword("my_pass");
     *
     * @param plainPassword The password to hash.
     * @return A securely hashed and salted password string.
     */

    public static String hashPassword(String plainPassword){
         // BCrypt.gensalt(12) generates a random "salt" to ensure that even identical
        // passwords have unique hashes. The number 12 is the "work factor"—it controls
        // how secure and slow the hashing is. 12 is a strong, standard value.
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

     /**
     * This method checks if a plain-text password attempt matches a stored hashed password.
     *
     * @param plainPassword The password entered by the user during a login attempt.
     * @param hashedPassword The hash we previously stored in the database for that user.
     * @return 'true' if the passwords match, 'false' otherwise.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword){
        // BCrypt.checkpw() is a special function that handles the entire comparison for us.
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
