package com.togofresh.togofresh.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        var encoder = new BCryptPasswordEncoder();
        var raw = "admin";
        var hash = encoder.encode(raw);

        System.out.println("Password original: " + raw);
        System.out.println("Hash generado: " + hash);
        System.out.println("¿Coincide? (admin): " + encoder.matches("admin", hash));


        System.out.println("¿Coincide? con hash viejo: " + encoder.matches("admin", "$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqVxdH4fw6QJiiO6RMaT3l5u0FBRa"));

    }
}
