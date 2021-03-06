package com.adp.template.security.jwt;

import com.adp.template.security.model.MainUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import java.util.Date;

@Component
public class JwtProvider {
    private final static Logger LOGGER= LoggerFactory.getLogger(JwtProvider.class);

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(Authentication authentication){
        MainUser mainUser= (MainUser) authentication.getPrincipal();
        return Jwts.builder().setSubject(mainUser.getUserName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+expiration*1000))
                .signWith(SignatureAlgorithm.HS512,secret)
                .compact();
    }

    public String getUserNameFromToken(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        }catch (MalformedJwtException e){
            LOGGER.error("MalformedJwtException ex: "+e);
        }
        catch (UnsupportedJwtException e){
            LOGGER.error("UnsupportedJwtException ex: "+e);
        }
        catch (ExpiredJwtException e){
            LOGGER.error("ExpiredJwtException ex: "+e);
        }
        catch (IllegalArgumentException e){
            LOGGER.error("IllegalArgumentException -> void token ex: "+e);
        }
        catch (SignatureException e){
            LOGGER.error("SignatureException -> sign fail ex: "+e);
        }
        return false;
    }
}
