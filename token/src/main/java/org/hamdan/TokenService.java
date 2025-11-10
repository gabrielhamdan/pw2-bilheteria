package org.hamdan;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;

@ApplicationScoped
public class TokenService {

    private static final String ISSUER = "bilheteria_hamdan";
    private static final int DURATION = 15;

    public String generateToken(String id) {
        return Jwt.issuer(ISSUER)
                .upn(id)
                .groups(new HashSet<>(Arrays.asList("Customer")))
                .expiresIn(Duration.ofMinutes(DURATION))
                .sign();
    }

}
