package me.pk2.moodlyencryption.util;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class RandomString {
    /**
     * Generate a random string.
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

    public static final String upper = String.valueOf((char)65) + String.valueOf((char)66) + String.valueOf((char)67) + String.valueOf((char)68) + String.valueOf((char)69) + String.valueOf((char)70) + String.valueOf((char)71) + String.valueOf((char)72) + String.valueOf((char)73) + String.valueOf((char)74) + String.valueOf((char)75) + String.valueOf((char)76) + String.valueOf((char)77) + String.valueOf((char)78) + String.valueOf((char)79) + String.valueOf((char)80) + String.valueOf((char)81) + String.valueOf((char)82) + String.valueOf((char)83) + String.valueOf((char)84) + String.valueOf((char)85) + String.valueOf((char)86) + String.valueOf((char)87) + String.valueOf((char)88) + String.valueOf((char)89) + String.valueOf((char)90);

    public static final String lower = upper.toLowerCase(Locale.ROOT);

    public static final String digits = String.valueOf((char)46) + String.valueOf((char)47) + String.valueOf((char)48) + String.valueOf((char)49) + String.valueOf((char)50) + String.valueOf((char)51) + String.valueOf((char)52) + String.valueOf((char)53) + String.valueOf((char)54) + String.valueOf((char)55);

    public static final String alphanum = upper + lower + digits;

    private final Random random;

    private final char[] symbols;

    private final char[] buf;

    public RandomString(int length, Random random, String symbols) {
        if (length < 1) throw new IllegalArgumentException();
        if (symbols.length() < 2) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    /**
     * Create an alphanumeric string generator.
     */
    public RandomString(int length, Random random) {
        this(length, random, alphanum);
    }

    /**
     * Create an alphanumeric strings from a secure generator.
     */
    public RandomString(int length) {
        this(length, new SecureRandom());
    }

    /**
     * Create session identifiers.
     */
    public RandomString() {
        this(21);
    }
}