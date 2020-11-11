package com.morrah77.ratpack_app.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class AuthService {
    public String generateToken() {
        return JWT.create()
                .withSubject("general purpose")
                .withIssuer("MushBetter Interviewer")
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC256("changeit"));
    }

    // TODO implement validation against issuer, expiration etc
    public Boolean isTokenValid(String token) {
        return Objects.nonNull(token) && !token.isEmpty();
    }

    public UserProfile getUserProfile(String token) {
        CommonProfile profile = new CommonProfile();
        // TODO fetch user profile from storage when storage implemented
        profile.build("007", new HashMap<>(){{put("token", token);}});
        return profile;
    }
}
