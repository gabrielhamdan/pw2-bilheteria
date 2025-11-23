package org.hamdan;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class JwtBlacklist {

    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    public void revoke(String jwt) {
        blacklist.add(jwt.replace("Bearer", "").trim());
    }

    public void check(String jwt) {
        if (blacklist.contains(jwt)) {
            throw new WebApplicationException("Token revogado", 401);
        }
    }

}
