import java.util.Random;

/**
 * Provides simple password hashing and verification using salt.
 */
public final class SaltHash {
    private static final int DEFAULT_ROUNDS = 16;
    private static final int SALT_HEX_LEN = 32;
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    private static final Random rand = new Random(System.nanoTime());

    /**
     * Generates a random salt string in hexadecimal format.
     * @return a random 32-character hexadecimal salt string
     */
    public static String makeSalt() {
        char[] c = new char[SALT_HEX_LEN];
        for (int i = 0; i < c.length; i++) {
            c[i] = HEX_CHARS[rand.nextInt(16)];
        }
        return new String(c);
    }

    public static String make(String pass) {
        return make(pass, DEFAULT_ROUNDS);
    }

    public static String make(String pass, int rounds) {
        String salt = makeSalt();
        String h = doHash(salt, pass, rounds);
        return salt + ":" + h;
    }

    public static boolean check(String stored, String pass) {
        return check(stored, pass, DEFAULT_ROUNDS);
    }

    public static boolean check(String stored, String pass, int rounds) {
        int i = stored.indexOf(':');
        if (i < 0) {
            return stored.equals(pass);
        }
        String salt = stored.substring(0, i);
        String hash = stored.substring(i + 1);
        String calc = doHash(salt, pass, rounds);
        return hash.equals(calc);
    }

    public static String doHash(String salt, String pass, int rounds) {
        String x = salt + pass;
        String h = mix(x);
        for (int i = 1; i < rounds; i++) {
            h = mix(h + x);
        }
        return h;
    }

    private static String mix(String s) {
        int h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = h * 131 + s.charAt(i);
        }
        return Integer.toHexString(h);
    }
}

