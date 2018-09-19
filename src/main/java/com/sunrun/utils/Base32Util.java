package com.sunrun.utils;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base32;
import org.jivesoftware.smack.util.MD5;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Base32Util {
    private static final Base32 base32Hex = new Base32(true);

    public static String decode(String data) {
        return new String(base32Hex.decode(data.toUpperCase()), Charsets.UTF_8);
    }

    public static boolean isBase32(String data) {
        return data == null ? false : base32Hex.isInAlphabet(data.toUpperCase());
    }

    public static String hash(byte[] bytes) {
        return MD5.hex(bytes).toLowerCase();
    }

    public static String encode(String data) {
        return data == null ? null : new String(base32Hex.encode(data.getBytes(Charsets.UTF_8))).toLowerCase();
    }
}
