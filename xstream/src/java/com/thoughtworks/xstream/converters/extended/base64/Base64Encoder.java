package com.thoughtworks.xstream.converters.extended.base64;

/**
 * Encodes binary data to plain text as Base64.
 *
 * @author Joe Walnes
 */
public class Base64Encoder {

    public char[] tripleByteToQuadChar(byte[] bytes) {
        // input  |------||------||------| (3 values each with 8 bits)
        //        101010101010101010101010
        // output |----||----||----||----| (4 values each with 6 bits)
        char[] result = new char[4];
        result[0] = charOfBase64Number(bytes[0]+256 >> 2);
        result[1] = charOfBase64Number((bytes[0]+256 << 4 & 48) + (bytes[1]+256 >> 4));
        result[2] = charOfBase64Number(((bytes[1]+256 & 15) << 2) + (bytes[2]+256 >> 6));
        result[3] = charOfBase64Number(bytes[2]+256 & 63);
        return result;
    }

    public char charOfBase64Number(int number) {
        if (number < 0 || number > 63) {
            throw new IllegalArgumentException("Value must be in range 0-63. Was: " + number);
        } else if (number < 26) {
            return (char)(number + 'A');
        } else if (number < 52) {
            return (char)(number - 26 + 'a');
        } else if (number < 62) {
            return (char)(number - 52 + '0');
        } else if (number == 62) {
            return '+';
        } else {
            return '/';
        }
    }

}
