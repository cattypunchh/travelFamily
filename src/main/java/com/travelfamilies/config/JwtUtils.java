package com.travelfamilies.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    public DecodedJWT verifyToken(String token) {
        return JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
    }

    public String generateToken(Long userId, int roleId, String username) {
        return JWT.create()
                .withClaim("userID", userId)
                .withClaim("roleID", roleId)
                .withClaim("username", username)
                .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 3600 * 1000))
                .sign(Algorithm.HMAC256(secret));
    }

    public int getUserRoleId(String token) {
        Map<String, Claim> claims = verifyToken(token).getClaims();

        return claims.get("roleID").asInt();
    }
}
