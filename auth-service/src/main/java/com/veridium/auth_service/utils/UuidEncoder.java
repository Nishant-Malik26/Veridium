package com.veridium.auth_service.utils;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public class UuidEncoder {
    public static String encode(UUID uuid) {
        if (uuid == null) {
            return null; // Or throw an IllegalArgumentException
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());

        return Base64.getUrlEncoder().withoutPadding().encodeToString(byteBuffer.array());
    }

    public static UUID decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return null;
        }
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(encoded);
            // SAFETY: Verify the decoded array is exactly 16 bytes long
            if (bytes.length != 16) {
                throw new IllegalArgumentException("Invalid encoded UUID length");
            }
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
        } catch (IllegalArgumentException e) {
            // Handles both Base64 decoding failures and bad lengths
            throw new IllegalArgumentException("Failed to decode UUID string", e);
        }
    }
}
