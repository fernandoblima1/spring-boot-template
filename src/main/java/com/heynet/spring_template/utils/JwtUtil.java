package com.heynet.spring_template.utils;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
  @Value("${jwt.secret}")
  private String SECRET_KEY;

  @Value("${jwt.expiration_time_in_millis}")
  private Long jwtExpirationInMillis;

  @Value("${jwt.refresh_expiration_time_in_millis:604800000}")
  private Long refreshTokenExpirationInMillis;

  @Value("${environment}")
  private String environment;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(
      UserDetails userDetails,
      Collection<? extends GrantedAuthority> authorities,
      List<String> roles) {
    List<String> permissions =
        authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

    return Jwts.builder()
        .subject(userDetails.getUsername())
        .claim("permissions", permissions)
        .claim("roles", roles)
        .claim("type", "access")
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + jwtExpirationInMillis))
        .signWith(getSigningKey())
        .compact();
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return Jwts.builder()
        .subject(userDetails.getUsername())
        .claim("type", "refresh")
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationInMillis))
        .signWith(getSigningKey())
        .compact();
  }

  public String refreshToken(String refreshToken) {
    return Jwts.builder()
        .subject(extractUsername(refreshToken))
        .claim("type", "refresh")
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationInMillis))
        .signWith(getSigningKey())
        .compact();
  }

  public Boolean validateRefreshToken(String refreshToken) {
    try {
      String username = extractUsername(refreshToken);
      return username != null && !isTokenExpired(refreshToken);
    } catch (Exception e) {
      return false;
    }
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  @SuppressWarnings("unchecked")
  public List<String> extractPermissions(String token) {
    return extractClaim(token, claims -> (List<String>) claims.get("permissions"));
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
  }

  public Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    boolean isUsernameValid = username.equals(userDetails.getUsername());

    // Em ambientes de desenvolvimento, não validar expiração
    if ("dev".equalsIgnoreCase(environment)
        || "test".equalsIgnoreCase(environment)
        || "local".equalsIgnoreCase(environment)) {
      return isUsernameValid;
    }

    return isUsernameValid && !isTokenExpired(token);
  }

  @SuppressWarnings("unchecked")
  public List<String> extractRoles(String token) {
    return extractClaim(token, claims -> (List<String>) claims.get("roles"));
  }

  public String clearToken(String token) {
    return token.replace("Bearer ", "");
  }
}
