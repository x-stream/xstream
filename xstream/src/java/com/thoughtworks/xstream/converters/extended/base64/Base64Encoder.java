package com.thoughtworks.xstream.converters.extended.base64;

/**
 * Encodes binary data to plain text as Base64.
 *
 * @author Joe Walnes
 */
public class Base64Encoder {

    private static final char[] sixtyFourChars
            = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    public String encode(byte[] input) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < input.length; i += 3) {
            byte[] subset = new byte[Math.min(3, input.length - i)];
            for (int s = 0; s < subset.length; s++) {
                subset[s] = input[i + s];
            }
            result.append(tripleByteToQuadChar(subset));
        }
        return result.toString();
    }

    private char[] tripleByteToQuadChar(byte[] in) {
        // input  |------||------||------| (3 values each with 8 bits)
        //        101010101010101010101010
        // output |----||----||----||----| (4 values each with 6 bits)
        if (in.length > 3) {
            throw new Error();
        }
        int oneBigNumber = (in[0] << 16) | ((in.length > 1 ? in[1] : 0) << 8) | ((in.length > 2 ? in[2] : 0));
        char[] result = new char[4];
        for (int i = 0; i < 4; i++) {
            result[i] = in.length + 1 == i ? '=' : sixtyFourChars[63 & (oneBigNumber >> (6 * (3 - i)))];
        }
        return result;
    }

}
