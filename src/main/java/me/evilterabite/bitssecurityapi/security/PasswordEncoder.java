package me.evilterabite.bitssecurityapi.security;

import com.lambdaworks.crypto.SCryptUtil;

public class PasswordEncoder {

    public static String encode(String password) {
        return SCryptUtil.scrypt(password, 65536, 7, 5);
    }

    public static boolean check(String enteredPassword, String hashed) {
        return SCryptUtil.check(enteredPassword, hashed);
    }


}
