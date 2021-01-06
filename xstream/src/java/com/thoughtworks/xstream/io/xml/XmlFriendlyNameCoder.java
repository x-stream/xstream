/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2013, 2019, 2020, 2021 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 15. August 2009 by Joerg Schaible, copied from XmlFriendlyReplacer.
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.io.naming.NameCoder;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;


/**
 * Encode and decode tag and attribute names in XML drivers.
 * <p>
 * This NameCoder is designed to ensure the correct encoding and decoding of names used for Java
 * types and fields to XML tags and attribute names.
 * </p>
 * <p>
 * The default replacements are:
 * </p>
 * <ul>
 * <li><b>$</b> (dollar) chars are replaced with <b>_-</b> (underscore dash) string.</li>
 * <li><b>_</b> (underscore) chars are replaced with <b>__</b> (double underscore) string.</li>
 * <li>other characters that are invalid in XML names are encoded with <b>_.XXXX</b> (underscore
 * dot followed by hex representation of character).</li>
 * </ul>
 * <p>
 * The valid characters are defined by the intersection of the XML 1.0 specification (4th edition) and later
 * specifications till XML 1.1 specification.
 * </p>
 *
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @author Tatu Saloranta
 * @author Michael Schnell
 * @see <a href="https://www.w3.org/TR/REC-xml/#dt-name">XML 1.0 name definition (5th edition)</a>
 * @see <a href="https://www.w3.org/TR/2006/REC-xml-20060816/#NT-Letter">XML 1.0 name definition (4th edition)</a>
 * @see <a href="https://www.w3.org/TR/xml11/#dt-name">XML 1.1 name definition</a>
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.8">Java identifier definition</a>
 * @since 1.4
 */
public class XmlFriendlyNameCoder implements NameCoder, Cloneable {
    private static final BitSet XML_NAME_START_CHARS;
    private static final BitSet XML_NAME_CHARS;
    static {
        final BitSet XML_NAME_START_CHARS_4TH = new BitSet(0xFFFFF);
        XML_NAME_START_CHARS_4TH.set(':');
        XML_NAME_START_CHARS_4TH.set('_');
        XML_NAME_START_CHARS_4TH.set('A', 'Z' + 1);
        XML_NAME_START_CHARS_4TH.set('a', 'z' + 1);
        XML_NAME_START_CHARS_4TH.set(0xC0, 0xD6 + 1);
        XML_NAME_START_CHARS_4TH.set(0xD8, 0xF6 + 1);

        final BitSet XML_NAME_START_CHARS_5TH = (BitSet)XML_NAME_START_CHARS_4TH.clone();

        XML_NAME_START_CHARS_4TH.set(0xF8, 0x131 + 1);
        XML_NAME_START_CHARS_4TH.set(0x134, 0x13E + 1);
        XML_NAME_START_CHARS_4TH.set(0x141, 0x148 + 1);
        XML_NAME_START_CHARS_4TH.set(0x14A, 0x17E + 1);
        XML_NAME_START_CHARS_4TH.set(0x180, 0x1C3 + 1);
        XML_NAME_START_CHARS_4TH.set(0x1CD, 0x1F0 + 1);
        XML_NAME_START_CHARS_4TH.set(0x1CD, 0x1F0 + 1);
        XML_NAME_START_CHARS_4TH.set(0x1F4, 0x1F5 + 1);
        XML_NAME_START_CHARS_4TH.set(0x1FA, 0x217 + 1);
        XML_NAME_START_CHARS_4TH.set(0x250, 0x2A8 + 1);
        XML_NAME_START_CHARS_4TH.set(0x2BB, 0x2C1 + 1);
        XML_NAME_START_CHARS_4TH.set(0x386);
        XML_NAME_START_CHARS_4TH.set(0x388, 0x38A + 1);
        XML_NAME_START_CHARS_4TH.set(0x38C);
        XML_NAME_START_CHARS_4TH.set(0x38E, 0x3A1 + 1);
        XML_NAME_START_CHARS_4TH.set(0x3A3, 0x3CE + 1);
        XML_NAME_START_CHARS_4TH.set(0x3D0, 0x3D6 + 1);
        XML_NAME_START_CHARS_4TH.set(0x3DA);
        XML_NAME_START_CHARS_4TH.set(0x3DC);
        XML_NAME_START_CHARS_4TH.set(0x3DE);
        XML_NAME_START_CHARS_4TH.set(0x3E0);
        XML_NAME_START_CHARS_4TH.set(0x3E2, 0x3F3 + 1);
        XML_NAME_START_CHARS_4TH.set(0x401, 0x40C + 1);
        XML_NAME_START_CHARS_4TH.set(0x40E, 0x44F + 1);
        XML_NAME_START_CHARS_4TH.set(0x451, 0x45C + 1);
        XML_NAME_START_CHARS_4TH.set(0x45E, 0x481 + 1);
        XML_NAME_START_CHARS_4TH.set(0x490, 0x4C4 + 1);
        XML_NAME_START_CHARS_4TH.set(0x4C7, 0x4C8 + 1);
        XML_NAME_START_CHARS_4TH.set(0x4CB, 0x4CC + 1);
        XML_NAME_START_CHARS_4TH.set(0x4D0, 0x4EB + 1);
        XML_NAME_START_CHARS_4TH.set(0x4EE, 0x4F5 + 1);
        XML_NAME_START_CHARS_4TH.set(0x4F8, 0x4F9 + 1);
        XML_NAME_START_CHARS_4TH.set(0x531, 0x556 + 1);
        XML_NAME_START_CHARS_4TH.set(0x559);
        XML_NAME_START_CHARS_4TH.set(0x561, 0x586 + 1);
        XML_NAME_START_CHARS_4TH.set(0x5D0, 0x5EA + 1);
        XML_NAME_START_CHARS_4TH.set(0x5F0, 0x5F2 + 1);
        XML_NAME_START_CHARS_4TH.set(0x621, 0x63A + 1);
        XML_NAME_START_CHARS_4TH.set(0x641, 0x64A + 1);
        XML_NAME_START_CHARS_4TH.set(0x671, 0x6B7 + 1);
        XML_NAME_START_CHARS_4TH.set(0x6BA, 0x6BE + 1);
        XML_NAME_START_CHARS_4TH.set(0x6C0, 0x6CE + 1);
        XML_NAME_START_CHARS_4TH.set(0x6D0, 0x6D3 + 1);
        XML_NAME_START_CHARS_4TH.set(0x6D5);
        XML_NAME_START_CHARS_4TH.set(0x6E5, 0x6E6 + 1);
        XML_NAME_START_CHARS_4TH.set(0x905, 0x939 + 1);
        XML_NAME_START_CHARS_4TH.set(0x93D);
        XML_NAME_START_CHARS_4TH.set(0x958, 0x961 + 1);
        XML_NAME_START_CHARS_4TH.set(0x985, 0x98C + 1);
        XML_NAME_START_CHARS_4TH.set(0x98F, 0x990 + 1);
        XML_NAME_START_CHARS_4TH.set(0x993, 0x9A8 + 1);
        XML_NAME_START_CHARS_4TH.set(0x9AA, 0x9B0 + 1);
        XML_NAME_START_CHARS_4TH.set(0x9B2);
        XML_NAME_START_CHARS_4TH.set(0x9B6, 0x9B9 + 1);
        XML_NAME_START_CHARS_4TH.set(0x9DC, 0x9DD + 1);
        XML_NAME_START_CHARS_4TH.set(0x9DF, 0x9E1 + 1);
        XML_NAME_START_CHARS_4TH.set(0x9F0, 0x9F1 + 1);
        XML_NAME_START_CHARS_4TH.set(0xA05, 0xA0A + 1);
        XML_NAME_START_CHARS_4TH.set(0xA0F, 0xA10 + 1);
        XML_NAME_START_CHARS_4TH.set(0xA13, 0xA28 + 1);
        XML_NAME_START_CHARS_4TH.set(0xA2A, 0xA30 + 1);
        XML_NAME_START_CHARS_4TH.set(0xA32, 0xA33 + 1);
        XML_NAME_START_CHARS_4TH.set(0xA35, 0xA36 + 1);
        XML_NAME_START_CHARS_4TH.set(0xA38, 0xA39 + 1);
        XML_NAME_START_CHARS_4TH.set(0xA59, 0xA5C + 1);
        XML_NAME_START_CHARS_4TH.set(0xA5E);
        XML_NAME_START_CHARS_4TH.set(0xA72, 0xA74 + 1);
        XML_NAME_START_CHARS_4TH.set(0xA85, 0xA8B + 1);
        XML_NAME_START_CHARS_4TH.set(0xA8D);
        XML_NAME_START_CHARS_4TH.set(0xA8F, 0xA91 + 1);
        XML_NAME_START_CHARS_4TH.set(0xA93, 0xAA8 + 1);
        XML_NAME_START_CHARS_4TH.set(0xAAA, 0xAB0 + 1);
        XML_NAME_START_CHARS_4TH.set(0xAB2, 0xAB3 + 1);
        XML_NAME_START_CHARS_4TH.set(0xAB5, 0xAB9 + 1);
        XML_NAME_START_CHARS_4TH.set(0xABD);
        XML_NAME_START_CHARS_4TH.set(0xAE0);
        XML_NAME_START_CHARS_4TH.set(0xB05, 0xB0C + 1);
        XML_NAME_START_CHARS_4TH.set(0xB0F, 0xB10 + 1);
        XML_NAME_START_CHARS_4TH.set(0xB13, 0xB28 + 1);
        XML_NAME_START_CHARS_4TH.set(0xB2A, 0xB30 + 1);
        XML_NAME_START_CHARS_4TH.set(0xB32, 0xB33 + 1);
        XML_NAME_START_CHARS_4TH.set(0xB36, 0xB39 + 1);
        XML_NAME_START_CHARS_4TH.set(0xB3D);
        XML_NAME_START_CHARS_4TH.set(0xB5C, 0xB5D + 1);
        XML_NAME_START_CHARS_4TH.set(0xB5F, 0xB61 + 1);
        XML_NAME_START_CHARS_4TH.set(0xB85, 0xB8A + 1);
        XML_NAME_START_CHARS_4TH.set(0xB8E, 0xB90 + 1);
        XML_NAME_START_CHARS_4TH.set(0xB92, 0xB95 + 1);
        XML_NAME_START_CHARS_4TH.set(0xB99, 0xB9A + 1);
        XML_NAME_START_CHARS_4TH.set(0xB9C);
        XML_NAME_START_CHARS_4TH.set(0xB9E, 0xB9F + 1);
        XML_NAME_START_CHARS_4TH.set(0xBA3, 0xBA4 + 1);
        XML_NAME_START_CHARS_4TH.set(0xBA8, 0xBAA + 1);
        XML_NAME_START_CHARS_4TH.set(0xBAE, 0xBB5 + 1);
        XML_NAME_START_CHARS_4TH.set(0xBB7, 0xBB9 + 1);
        XML_NAME_START_CHARS_4TH.set(0xC05, 0xC0C + 1);
        XML_NAME_START_CHARS_4TH.set(0xC0E, 0xC10 + 1);
        XML_NAME_START_CHARS_4TH.set(0xC12, 0xC28 + 1);
        XML_NAME_START_CHARS_4TH.set(0xC2A, 0xC33 + 1);
        XML_NAME_START_CHARS_4TH.set(0xC35, 0xC39 + 1);
        XML_NAME_START_CHARS_4TH.set(0xC60, 0xC61 + 1);
        XML_NAME_START_CHARS_4TH.set(0xC85, 0xC8C + 1);
        XML_NAME_START_CHARS_4TH.set(0xC8E, 0xC90 + 1);
        XML_NAME_START_CHARS_4TH.set(0xC92, 0xCA8 + 1);
        XML_NAME_START_CHARS_4TH.set(0xCAA, 0xCB3 + 1);
        XML_NAME_START_CHARS_4TH.set(0xCB5, 0xCB9 + 1);
        XML_NAME_START_CHARS_4TH.set(0xCDE);
        XML_NAME_START_CHARS_4TH.set(0xCE0, 0xCE1 + 1);
        XML_NAME_START_CHARS_4TH.set(0xD05, 0xD0C + 1);
        XML_NAME_START_CHARS_4TH.set(0xD0E, 0xD10 + 1);
        XML_NAME_START_CHARS_4TH.set(0xD12, 0xD28 + 1);
        XML_NAME_START_CHARS_4TH.set(0xD2A, 0xD39 + 1);
        XML_NAME_START_CHARS_4TH.set(0xD60, 0xD61 + 1);
        XML_NAME_START_CHARS_4TH.set(0xE01, 0xE2E + 1);
        XML_NAME_START_CHARS_4TH.set(0xE30);
        XML_NAME_START_CHARS_4TH.set(0xE32, 0xE33 + 1);
        XML_NAME_START_CHARS_4TH.set(0xE40, 0xE45 + 1);
        XML_NAME_START_CHARS_4TH.set(0xE81, 0xE82 + 1);
        XML_NAME_START_CHARS_4TH.set(0xE84);
        XML_NAME_START_CHARS_4TH.set(0xE87, 0xE88 + 1);
        XML_NAME_START_CHARS_4TH.set(0xE8A);
        XML_NAME_START_CHARS_4TH.set(0xE8D);
        XML_NAME_START_CHARS_4TH.set(0xE94, 0xE97 + 1);
        XML_NAME_START_CHARS_4TH.set(0xE99, 0xE9F + 1);
        XML_NAME_START_CHARS_4TH.set(0xEA1, 0xEA3 + 1);
        XML_NAME_START_CHARS_4TH.set(0xEA5);
        XML_NAME_START_CHARS_4TH.set(0xEA7);
        XML_NAME_START_CHARS_4TH.set(0xEAA, 0xEAB + 1);
        XML_NAME_START_CHARS_4TH.set(0xEAD, 0xEAE + 1);
        XML_NAME_START_CHARS_4TH.set(0xEB0);
        XML_NAME_START_CHARS_4TH.set(0xEB2, 0xEB3 + 1);
        XML_NAME_START_CHARS_4TH.set(0xEBD);
        XML_NAME_START_CHARS_4TH.set(0xEC0, 0xEC4 + 1);
        XML_NAME_START_CHARS_4TH.set(0xF40, 0xF47 + 1);
        XML_NAME_START_CHARS_4TH.set(0xF49, 0xF69 + 1);
        XML_NAME_START_CHARS_4TH.set(0x10A0, 0x10C5 + 1);
        XML_NAME_START_CHARS_4TH.set(0x10D0, 0x10F6 + 1);
        XML_NAME_START_CHARS_4TH.set(0x1100);
        XML_NAME_START_CHARS_4TH.set(0x1102, 0x1103 + 1);
        XML_NAME_START_CHARS_4TH.set(0x1105, 0x1107 + 1);
        XML_NAME_START_CHARS_4TH.set(0x1109);
        XML_NAME_START_CHARS_4TH.set(0x110B, 0x110C + 1);
        XML_NAME_START_CHARS_4TH.set(0x110E, 0x1112 + 1);
        XML_NAME_START_CHARS_4TH.set(0x113C);
        XML_NAME_START_CHARS_4TH.set(0x113E);
        XML_NAME_START_CHARS_4TH.set(0x1140);
        XML_NAME_START_CHARS_4TH.set(0x114C);
        XML_NAME_START_CHARS_4TH.set(0x114E);
        XML_NAME_START_CHARS_4TH.set(0x1150);
        XML_NAME_START_CHARS_4TH.set(0x1154, 0x1155 + 1);
        XML_NAME_START_CHARS_4TH.set(0x1159);
        XML_NAME_START_CHARS_4TH.set(0x115F, 0x1161 + 1);
        XML_NAME_START_CHARS_4TH.set(0x1163);
        XML_NAME_START_CHARS_4TH.set(0x1165);
        XML_NAME_START_CHARS_4TH.set(0x1167);
        XML_NAME_START_CHARS_4TH.set(0x1169);
        XML_NAME_START_CHARS_4TH.set(0x116D, 0x116E + 1);
        XML_NAME_START_CHARS_4TH.set(0x1172, 0x1173 + 1);
        XML_NAME_START_CHARS_4TH.set(0x1175);
        XML_NAME_START_CHARS_4TH.set(0x119E);
        XML_NAME_START_CHARS_4TH.set(0x11A8);
        XML_NAME_START_CHARS_4TH.set(0x11AB);
        XML_NAME_START_CHARS_4TH.set(0x11AE, 0x11AF + 1);
        XML_NAME_START_CHARS_4TH.set(0x11B7, 0x11B8 + 1);
        XML_NAME_START_CHARS_4TH.set(0x11BA);
        XML_NAME_START_CHARS_4TH.set(0x11BC, 0x11C2 + 1);
        XML_NAME_START_CHARS_4TH.set(0x11EB);
        XML_NAME_START_CHARS_4TH.set(0x11F0);
        XML_NAME_START_CHARS_4TH.set(0x11F9);
        XML_NAME_START_CHARS_4TH.set(0x1E00, 0x1E9B + 1);
        XML_NAME_START_CHARS_4TH.set(0x1EA0, 0x1EF9 + 1);
        XML_NAME_START_CHARS_4TH.set(0x1F00, 0x1F15 + 1);
        XML_NAME_START_CHARS_4TH.set(0x1F18, 0x1F1D + 1);
        XML_NAME_START_CHARS_4TH.set(0x1F20, 0x1F45 + 1);
        XML_NAME_START_CHARS_4TH.set(0x1F48, 0x1F4D + 1);
        XML_NAME_START_CHARS_4TH.set(0x1F50, 0x1F57 + 1);
        XML_NAME_START_CHARS_4TH.set(0x1F59);
        XML_NAME_START_CHARS_4TH.set(0x1F5B);
        XML_NAME_START_CHARS_4TH.set(0x1F5D);
        XML_NAME_START_CHARS_4TH.set(0x1F5F, 0x1F7D + 1);
        XML_NAME_START_CHARS_4TH.set(0x1F80, 0x1FB4 + 1);
        XML_NAME_START_CHARS_4TH.set(0x1FB6, 0x1FBC + 1);
        XML_NAME_START_CHARS_4TH.set(0x1FBE);
        XML_NAME_START_CHARS_4TH.set(0x1FC2, 0x1FC4 + 1);
        XML_NAME_START_CHARS_4TH.set(0x1FC6, 0x1FCC + 1);
        XML_NAME_START_CHARS_4TH.set(0x1FD0, 0x1FD3 + 1);
        XML_NAME_START_CHARS_4TH.set(0x1FD6, 0x1FDB + 1);
        XML_NAME_START_CHARS_4TH.set(0x1FE0, 0x1FEC + 1);
        XML_NAME_START_CHARS_4TH.set(0x1FF2, 0x1FF4 + 1);
        XML_NAME_START_CHARS_4TH.set(0x1FF6, 0x1FFC + 1);
        XML_NAME_START_CHARS_4TH.set(0x2126);
        XML_NAME_START_CHARS_4TH.set(0x212A, 0x212B + 1);
        XML_NAME_START_CHARS_4TH.set(0x212E);
        XML_NAME_START_CHARS_4TH.set(0x2180, 0x2182 + 1);
        XML_NAME_START_CHARS_4TH.set(0x3041, 0x3094 + 1);
        XML_NAME_START_CHARS_4TH.set(0x30A1, 0x30FA + 1);
        XML_NAME_START_CHARS_4TH.set(0x3105, 0x312C + 1);
        XML_NAME_START_CHARS_4TH.set(0x3007);
        XML_NAME_START_CHARS_4TH.set(0x3021, 0x3029 + 1);
        XML_NAME_START_CHARS_4TH.set(0x4E00, 0x9FA5 + 1);
        XML_NAME_START_CHARS_4TH.set(0xAC00, 0xD7A3 + 1);

        XML_NAME_START_CHARS_5TH.set(0xF8, 0x2FF + 1);
        XML_NAME_START_CHARS_5TH.set(0x370, 0x37D + 1);
        XML_NAME_START_CHARS_5TH.set(0x37F, 0x1FFF + 1);
        XML_NAME_START_CHARS_5TH.set(0x200C, 0x200D + 1);
        XML_NAME_START_CHARS_5TH.set(0x2070, 0x218F + 1);
        XML_NAME_START_CHARS_5TH.set(0x2C00, 0x2FEF + 1);
        XML_NAME_START_CHARS_5TH.set(0x3001, 0xD7FF + 1);
        XML_NAME_START_CHARS_5TH.set(0xF900, 0xFDCF + 1);
        XML_NAME_START_CHARS_5TH.set(0xFDF0, 0xFFFD + 1);
        XML_NAME_START_CHARS_5TH.set(0x10000, 0xEFFFF + 1);

        final BitSet XML_NAME_CHARS_4TH = new BitSet(0xFFFFF);
        XML_NAME_CHARS_4TH.set('-');
        XML_NAME_CHARS_4TH.set('.');
        XML_NAME_CHARS_4TH.set('0', '9' + 1);
        XML_NAME_CHARS_4TH.set(0xB7);

        final BitSet XML_NAME_CHARS_5TH = (BitSet)XML_NAME_CHARS_4TH.clone();

        XML_NAME_CHARS_4TH.or(XML_NAME_START_CHARS_4TH);
        XML_NAME_CHARS_4TH.set(0x2D0);
        XML_NAME_CHARS_4TH.set(0x2D1);
        XML_NAME_CHARS_4TH.set(0x300, 0x345 + 1);
        XML_NAME_CHARS_4TH.set(0x360, 0x361 + 1);
        XML_NAME_CHARS_4TH.set(0x387);
        XML_NAME_CHARS_4TH.set(0x483, 0x486 + 1);
        XML_NAME_CHARS_4TH.set(0x591, 0x5A1 + 1);
        XML_NAME_CHARS_4TH.set(0x5A3, 0x5B9 + 1);
        XML_NAME_CHARS_4TH.set(0x5BB, 0x5BD + 1);
        XML_NAME_CHARS_4TH.set(0x5BF);
        XML_NAME_CHARS_4TH.set(0x5C1, 0x5C2 + 1);
        XML_NAME_CHARS_4TH.set(0x5C4);
        XML_NAME_CHARS_4TH.set(0x640);
        XML_NAME_CHARS_4TH.set(0x64B, 0x652 + 1);
        XML_NAME_CHARS_4TH.set(0x660, 0x669 + 1);
        XML_NAME_CHARS_4TH.set(0x670);
        XML_NAME_CHARS_4TH.set(0x6D6, 0x6DC + 1);
        XML_NAME_CHARS_4TH.set(0x6DD, 0x6DF + 1);
        XML_NAME_CHARS_4TH.set(0x6E0, 0x6E4 + 1);
        XML_NAME_CHARS_4TH.set(0x6E7, 0x6E8 + 1);
        XML_NAME_CHARS_4TH.set(0x6EA, 0x6ED + 1);
        XML_NAME_CHARS_4TH.set(0x6F0, 0x6F9 + 1);
        XML_NAME_CHARS_4TH.set(0x901, 0x903 + 1);
        XML_NAME_CHARS_4TH.set(0x93C);
        XML_NAME_CHARS_4TH.set(0x93E, 0x94C + 1);
        XML_NAME_CHARS_4TH.set(0x94D);
        XML_NAME_CHARS_4TH.set(0x951, 0x954 + 1);
        XML_NAME_CHARS_4TH.set(0x962, 0x963 + 1);
        XML_NAME_CHARS_4TH.set(0x966, 0x96F + 1);
        XML_NAME_CHARS_4TH.set(0x981, 0x983 + 1);
        XML_NAME_CHARS_4TH.set(0x9BC);
        XML_NAME_CHARS_4TH.set(0x9BE);
        XML_NAME_CHARS_4TH.set(0x9BF);
        XML_NAME_CHARS_4TH.set(0x9C0, 0x9C4 + 1);
        XML_NAME_CHARS_4TH.set(0x9C7, 0x9C8 + 1);
        XML_NAME_CHARS_4TH.set(0x9CB, 0x9CD + 1);
        XML_NAME_CHARS_4TH.set(0x9D7);
        XML_NAME_CHARS_4TH.set(0x9E2, 0x9E3 + 1);
        XML_NAME_CHARS_4TH.set(0x9E6, 0x9EF + 1);
        XML_NAME_CHARS_4TH.set(0xA02);
        XML_NAME_CHARS_4TH.set(0xA3C);
        XML_NAME_CHARS_4TH.set(0xA3E);
        XML_NAME_CHARS_4TH.set(0xA3F);
        XML_NAME_CHARS_4TH.set(0xA40, 0xA42 + 1);
        XML_NAME_CHARS_4TH.set(0xA47, 0xA48 + 1);
        XML_NAME_CHARS_4TH.set(0xA4B, 0xA4D + 1);
        XML_NAME_CHARS_4TH.set(0xA66, 0xA6F + 1);
        XML_NAME_CHARS_4TH.set(0xA70, 0xA71 + 1);
        XML_NAME_CHARS_4TH.set(0xA81, 0xA83 + 1);
        XML_NAME_CHARS_4TH.set(0xABC);
        XML_NAME_CHARS_4TH.set(0xABE, 0xAC5 + 1);
        XML_NAME_CHARS_4TH.set(0xAC7, 0xAC9 + 1);
        XML_NAME_CHARS_4TH.set(0xACB, 0xACD + 1);
        XML_NAME_CHARS_4TH.set(0xAE6, 0xAEF + 1);
        XML_NAME_CHARS_4TH.set(0xB01, 0xB03 + 1);
        XML_NAME_CHARS_4TH.set(0xB3C);
        XML_NAME_CHARS_4TH.set(0xB3E, 0xB43 + 1);
        XML_NAME_CHARS_4TH.set(0xB47, 0xB48 + 1);
        XML_NAME_CHARS_4TH.set(0xB4B, 0xB4D + 1);
        XML_NAME_CHARS_4TH.set(0xB56, 0xB57 + 1);
        XML_NAME_CHARS_4TH.set(0xB66, 0xB6F + 1);
        XML_NAME_CHARS_4TH.set(0xB82, 0xB83 + 1);
        XML_NAME_CHARS_4TH.set(0xBBE, 0xBC2 + 1);
        XML_NAME_CHARS_4TH.set(0xBC6, 0xBC8 + 1);
        XML_NAME_CHARS_4TH.set(0xBCA, 0xBCD + 1);
        XML_NAME_CHARS_4TH.set(0xBD7);
        XML_NAME_CHARS_4TH.set(0xBE7, 0xBEF + 1);
        XML_NAME_CHARS_4TH.set(0xC01, 0xC03 + 1);
        XML_NAME_CHARS_4TH.set(0xC3E, 0xC44 + 1);
        XML_NAME_CHARS_4TH.set(0xC46, 0xC48 + 1);
        XML_NAME_CHARS_4TH.set(0xC4A, 0xC4D + 1);
        XML_NAME_CHARS_4TH.set(0xC55, 0xC56 + 1);
        XML_NAME_CHARS_4TH.set(0xC66, 0xC6F + 1);
        XML_NAME_CHARS_4TH.set(0xC82, 0xC83 + 1);
        XML_NAME_CHARS_4TH.set(0xCBE, 0xCC4 + 1);
        XML_NAME_CHARS_4TH.set(0xCC6, 0xCC8 + 1);
        XML_NAME_CHARS_4TH.set(0xCCA, 0xCCD + 1);
        XML_NAME_CHARS_4TH.set(0xCD5, 0xCD6 + 1);
        XML_NAME_CHARS_4TH.set(0xCE6, 0xCEF + 1);
        XML_NAME_CHARS_4TH.set(0xD02, 0xD03 + 1);
        XML_NAME_CHARS_4TH.set(0xD3E, 0xD43 + 1);
        XML_NAME_CHARS_4TH.set(0xD46, 0xD48 + 1);
        XML_NAME_CHARS_4TH.set(0xD4A, 0xD4D + 1);
        XML_NAME_CHARS_4TH.set(0xD57);
        XML_NAME_CHARS_4TH.set(0xD66, 0xD6F + 1);
        XML_NAME_CHARS_4TH.set(0xE31);
        XML_NAME_CHARS_4TH.set(0xE34, 0xE3A + 1);
        XML_NAME_CHARS_4TH.set(0xE46);
        XML_NAME_CHARS_4TH.set(0xE47, 0xE4E + 1);
        XML_NAME_CHARS_4TH.set(0xE50, 0xE59 + 1);
        XML_NAME_CHARS_4TH.set(0xEB1);
        XML_NAME_CHARS_4TH.set(0xEB4, 0xEB9 + 1);
        XML_NAME_CHARS_4TH.set(0xEBB, 0xEBC + 1);
        XML_NAME_CHARS_4TH.set(0xEC6);
        XML_NAME_CHARS_4TH.set(0xEC8, 0xECD + 1);
        XML_NAME_CHARS_4TH.set(0xED0, 0xED9 + 1);
        XML_NAME_CHARS_4TH.set(0xF18, 0xF19 + 1);
        XML_NAME_CHARS_4TH.set(0xF20, 0xF29 + 1);
        XML_NAME_CHARS_4TH.set(0xF35);
        XML_NAME_CHARS_4TH.set(0xF37);
        XML_NAME_CHARS_4TH.set(0xF39);
        XML_NAME_CHARS_4TH.set(0xF3E);
        XML_NAME_CHARS_4TH.set(0xF3F);
        XML_NAME_CHARS_4TH.set(0xF71, 0xF84 + 1);
        XML_NAME_CHARS_4TH.set(0xF86, 0xF8B + 1);
        XML_NAME_CHARS_4TH.set(0xF90, 0xF95 + 1);
        XML_NAME_CHARS_4TH.set(0xF97);
        XML_NAME_CHARS_4TH.set(0xF99, 0xFAD + 1);
        XML_NAME_CHARS_4TH.set(0xFB1, 0xFB7 + 1);
        XML_NAME_CHARS_4TH.set(0xFB9);
        XML_NAME_CHARS_4TH.set(0x20D0, 0x20DC + 1);
        XML_NAME_CHARS_4TH.set(0x20E1);
        XML_NAME_CHARS_4TH.set(0x3005);
        XML_NAME_CHARS_4TH.set(0x302A, 0x302F + 1);
        XML_NAME_CHARS_4TH.set(0x3031, 0x3035 + 1);
        XML_NAME_CHARS_4TH.set(0x3099);
        XML_NAME_CHARS_4TH.set(0x309A);
        XML_NAME_CHARS_4TH.set(0x309D, 0x309E + 1);
        XML_NAME_CHARS_4TH.set(0x30FC, 0x30FE + 1);

        XML_NAME_CHARS_5TH.or(XML_NAME_START_CHARS_5TH);
        XML_NAME_CHARS_5TH.set(0x300, 0x36F + 1);
        XML_NAME_CHARS_5TH.set(0x203F, 0x2040 + 1);

        XML_NAME_START_CHARS = (BitSet)XML_NAME_START_CHARS_4TH.clone();
        XML_NAME_START_CHARS.and(XML_NAME_START_CHARS_5TH);
        XML_NAME_CHARS = (BitSet)XML_NAME_CHARS_4TH.clone();
        XML_NAME_CHARS.and(XML_NAME_CHARS_5TH);
    }

    private final String dollarReplacement;
    private final String escapeCharReplacement;
    private transient Map escapeCache;
    private transient Map unescapeCache;
    private final String hexPrefix;

    /**
     * Construct a new XmlFriendlyNameCoder.
     *
     * @since 1.4
     */
    public XmlFriendlyNameCoder() {
        this("_-", "__");
    }

    /**
     * Construct a new XmlFriendlyNameCoder with custom replacement strings for dollar and the escape character.
     *
     * @param dollarReplacement
     * @param escapeCharReplacement
     * @since 1.4
     */
    public XmlFriendlyNameCoder(String dollarReplacement, String escapeCharReplacement) {
        this(dollarReplacement, escapeCharReplacement, "_.");
    }

    /**
     * Construct a new XmlFriendlyNameCoder with custom replacement strings for dollar, the escape character and the
     * prefix for hexadecimal encoding of invalid characters in XML names.
     *
     * @param dollarReplacement
     * @param escapeCharReplacement
     * @since 1.4
     */
    public XmlFriendlyNameCoder(
        String dollarReplacement, String escapeCharReplacement, String hexPrefix) {
        this.dollarReplacement = dollarReplacement;
        this.escapeCharReplacement = escapeCharReplacement;
        this.hexPrefix = hexPrefix;
        readResolve();
    }

    /**
     * {@inheritDoc}
     */
    public String decodeAttribute(String attributeName) {
        return decodeName(attributeName);
    }

    /**
     * {@inheritDoc}
     */
    public String decodeNode(String elementName) {
        return decodeName(elementName);
    }

    /**
     * {@inheritDoc}
     */
    public String encodeAttribute(String name) {
        return encodeName(name);
    }

    /**
     * {@inheritDoc}
     */
    public String encodeNode(String name) {
        return encodeName(name);
    }

    private String encodeName(String name) {
        String s = (String)escapeCache.get(name);
        if (s == null) {
            final int length = name.length();

            // First, fast (common) case: nothing to escape
            int i = 0;

            for (; i < length; i++) {
                final char c = name.charAt(i);
                if (c < 'A' || (c > 'Z' && c < 'a') || c > 'z') {
                    break;
                }
            }

            if (i == length) {
                return name;
            }

            // Otherwise full processing
            final StringBuffer result = new StringBuffer(length + 8);

            // We know first N chars are safe
            if (i > 0) {
                result.append(name.substring(0, i));
            }

            for (; i < length; i++ ) {
                char c = name.charAt(i);
                if (c == '$') {
                    result.append(dollarReplacement);
                } else if (c == '_') {
                    result.append(escapeCharReplacement);
                } else if ((i == 0 && !isXmlNameStartChar(c)) || (i > 0 && !isXmlNameChar(c))) {
                    result.append(hexPrefix);
                    if (c < 16) result.append("000");
                    else if (c < 256) result.append("00");
                    else if (c < 4096) result.append("0");
                    result.append(Integer.toHexString(c));
                } else {
                    result.append(c);
                }
            }
            s = result.toString();
            escapeCache.put(name, s);
        }
        return s;
    }

    private String decodeName(String name) {
        String s = (String)unescapeCache.get(name);
        if (s == null) {
            final char dollarReplacementFirstChar = dollarReplacement.charAt(0);
            final char escapeReplacementFirstChar = escapeCharReplacement.charAt(0);
            final char hexPrefixFirstChar = hexPrefix.charAt(0);
            final int length = name.length();

            // First, fast (common) case: nothing to decode
            int i = 0;

            for (; i < length; i++ ) {
                char c = name.charAt(i);
                // We'll do a quick check for potential match
                if (c == dollarReplacementFirstChar
                    || c == escapeReplacementFirstChar
                    || c == hexPrefixFirstChar) {
                    // and if it might be a match, just quit, will check later on
                    break;
                }
            }

            if (i == length) {
                return name;
            }

            // Otherwise full processing
            final StringBuffer result = new StringBuffer(length + 8);

            // We know first N chars are safe
            if (i > 0) {
                result.append(name.substring(0, i));
            }

            for (; i < length; i++ ) {
                char c = name.charAt(i);
                if (c == dollarReplacementFirstChar && name.startsWith(dollarReplacement, i)) {
                    i += dollarReplacement.length() - 1;
                    result.append('$');
                } else if (c == hexPrefixFirstChar && name.startsWith(hexPrefix, i)) {
                    i += hexPrefix.length();
                    c = (char)Integer.parseInt(name.substring(i, i + 4), 16);
                    i += 3;
                    result.append(c);
                } else if (c == escapeReplacementFirstChar
                    && name.startsWith(escapeCharReplacement, i)) {
                    i += escapeCharReplacement.length() - 1;
                    result.append('_');
                } else {
                    result.append(c);
                }
            }

            s = result.toString();
            unescapeCache.put(name, s);
        }
        return s;
    }

    public Object clone() {
        try {
            XmlFriendlyNameCoder coder = (XmlFriendlyNameCoder)super.clone();
            coder.readResolve();
            return coder;

        } catch (CloneNotSupportedException e) {
            throw new ObjectAccessException("Cannot clone XmlFriendlyNameCoder", e);
        }
    }

    private Object readResolve() {
        escapeCache = createCacheMap();
        unescapeCache = createCacheMap();
        return this;
    }

    protected Map createCacheMap() {
        return new HashMap();
    }

    private static boolean isXmlNameStartChar(final int cp) {
        return XML_NAME_START_CHARS.get(cp);
    }

    private static boolean isXmlNameChar(final int cp) {
        return XML_NAME_CHARS.get(cp);
    }
}
