package com.thoughtworks.xstream.converters.extended.base64;

import java.io.IOException;

/**
 * Encodes binary data to plain text as Base64.
 *
 * @author Joe Walnes
 */
public class Base64Encoder {

    private static final char[] SIXTY_FOUR_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    public String encode(byte[] input) {
        StringBuffer result = new StringBuffer();
        int outputCharCount = 0;
        for (int i = 0; i < input.length; i += 3) {
            int remaining = Math.min(3, input.length - i);
            // input  |------||------||------| (3 values each with 8 bits)
            //        101010101010101010101010
            // output |----||----||----||----| (4 values each with 6 bits)
            int oneBigNumber = (input[i] & 0xff) << 16 | (remaining <= 1 ? 0 : input[i + 1] & 0xff) << 8 | (remaining <= 2 ? 0 : input[i + 2] & 0xff);
            for(int j = 0; j < 4; j++) result.append(remaining + 1 > j ? SIXTY_FOUR_CHARS[0x3f & oneBigNumber >> 6 * (3 - j)] : '=');
            if((outputCharCount += 4) % 76 == 0) result.append('\n');
        }
        return result.toString();
    }

    public byte[] decode(String input) {
        // TODO: Remove dependency on sun.misc.BASE64Decoder!!!
        try {
            return new sun.misc.BASE64Decoder().decodeBuffer(input);
        } catch (IOException e) {
            throw new RuntimeException("Cannot decode Base64 data : " + e.getMessage());
        }
    }
}
