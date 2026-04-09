package com.travelfamilies.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

   private static final String  SECRET="your_secret_key_123456";

   public String generateToken(int userId,int roleId,String username) {

      return JWT.create()
              .withClaim("userID",userId)
              .withClaim("roleID",roleId)
              .withClaim("username",username)
              .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 3600 * 1000))
              .sign(Algorithm.HMAC256(SECRET));
   }

   public static DecodedJWT verifyToken(String token) {

      return JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
   }

   public int getUserId(String token) {

      return verifyToken(token).getClaim("userID").asInt();
   }

   public int getUsername(String token) {
      return verifyToken(token).getClaim("roleID").asInt();
   }
}
