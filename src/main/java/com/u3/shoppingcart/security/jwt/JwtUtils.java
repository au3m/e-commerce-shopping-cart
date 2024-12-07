package com.u3.shoppingcart.security.jwt;

import com.u3.shoppingcart.security.user.ShopUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;


import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    @Value("${auth.token.jwtSecret}")
    private String jwtSectret;
    @Value("${auth.token.expirationInMils}")
    private int expirationTime;

    public String generateTokenForUser(Authentication authentication){
        ShopUserDetails userPricipal = (ShopUserDetails) authentication.getPrincipal();

        List<String> roles = userPricipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return Jwts.builder()
                .setSubject(userPricipal.getEmail())
                .claim("id", userPricipal.getId())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expirationTime))
                .signWith(key(), SignatureAlgorithm.HS256).compact();


    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSectret));
    }

    public String getUsernameFromToken(String token){
        return Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();

    }

    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .setSigningKey(key())
                    .build()
                    .parseEncryptedClaims(token);
            return true;
        } catch (IllegalArgumentException | UnsupportedJwtException | MalformedJwtException |
                 SignatureException | ExpiredJwtException e) {
            throw new JwtException(e.getMessage());
        }
    }


}
