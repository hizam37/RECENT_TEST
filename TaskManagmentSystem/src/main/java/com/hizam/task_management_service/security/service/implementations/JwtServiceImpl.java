package com.hizam.task_management_service.security.service.implementations;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;


public interface JwtServiceImpl {


    String extractUserName(String token);

    String generateToken(UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);

    String generateToken(Map<String, Object> extractClaims, UserDetails userDetails);


    boolean isTokenExpired(String token);


     Date extractExpiration(String token);



    <T> T extractClaim(String token, Function<Claims, T> ClaimsResolvers) ;


     Claims extractAllClaims(String token);


    SecretKey getSigningKey();
}
