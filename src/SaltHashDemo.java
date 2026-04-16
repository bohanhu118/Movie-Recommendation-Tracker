/**
 * Demonstrates how to use the SaltHash class for password hashing.
 */
public class SaltHashDemo {
    public static void main(String[] args) {
        String p = "abc123";
        String stored = SaltHash.make(p);
        System.out.println(stored.contains(":"));
        System.out.println(SaltHash.check(stored, p));
        System.out.println(SaltHash.check(stored, "wrong"));
        String plain = "hello";
        System.out.println(SaltHash.check(plain, "hello"));
        System.out.println(SaltHash.check(plain, "no"));
    }
}

