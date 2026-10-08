package com.veridium.auth_service.utils;



import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

    public static String hashify(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verify(String input, String hashedValue) {
        if (input == null || hashedValue == null) {
            return false;
        }
        // Hash the input using your existing method and compare it to the target hash
        String newInputHash = hashify(input);

        // Use MessageDigest.isEqual for a time-constant comparison to protect against timing attacks
        return MessageDigest.isEqual(
                newInputHash.getBytes(StandardCharsets.UTF_8),
                hashedValue.getBytes(StandardCharsets.UTF_8)
        );
    }
}