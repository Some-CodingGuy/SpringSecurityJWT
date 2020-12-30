package be.n.springsecjwt.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JWTUtil {

    private String secret_key = "Some secret key";

    /**
     * Extract the user name from the JWT.
     * @param token
     *          JWT containing the user name.
     * @return
     *          The user name extracted from the JWT.
     */
    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract the expiration time from the JWT.
     * @param token
     *          JWT containing the expiration time.
     * @return
     *          The expiration time extracted from the JWT.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Return all name/value pairs of information from the JWT.
     * @param token
     *          JWT containing the relevant information.
     * @return
     *        A map of name/value pairs extracted from the token.
     */
    private Claims extractAllClaims(String  token){
        return Jwts.parser().setSigningKey(secret_key).parseClaimsJws(token).getBody();
    }

    /**
     * Extract the expiration time from the JWT and compare it to the current time.
     * @param token
     *          JWT containing the expiration time.
     * @return
     *          True if the expiration time has passed already.
     */
    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    /**
     * Takes a UserDetails object in, and creates a JWT from it.
     * It passes in a map of claims (currently empty) that will be included in the JWT payload
     * @param userDetails
     *          UserDetails object containing the details of the user whose JWT we are going to generate.
     * @return
     *          The JWT in String format.
     */
    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Create the JWT using the given arguments.
     * JWTs are made up of three parts:
     * <pre>
     * - Header
     *      * Contains some general information about the encryption algorithm
     *        and the type of token it is.
     * - Payload
     *      * Here we find the actual information stored in the JWT.
     *        This part contains name/value pairs containing the actual
     *        information that needs to be transmitted.
     * - Signature
     *      * This last part is used to verify the integrity of the JWT.
     *        It is created encrypting the first two parts of the JWT with
     *        an encryption algorithm using a secret key known only by the server.
     * </pre>
     * The header is automatically generated with default values.
     * The content of the payload is generated using the claims (a list of name/value pairs containing the pertinent info).
     * We also add an "expiration date". The JWT will expire 8 hours after the generation time.
     * For the signature we use the HS256 algorithm.
     * @param claims
     *          Pairs of claims containing the information to be stored in the JWT.
     * @param user
     *          Name of the user whose JWT we create.
     * @return
     *          The JWT in string format (header.payload.signature).
     */
    private String createToken(Map<String, Object> claims, String user){
        return Jwts.builder().setClaims(claims).setSubject(user).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 8)))
                .signWith(SignatureAlgorithm.HS256, secret_key).compact();
    }

    /**
     * Validate the given token against the user details.
     * @param token
     *          JWT containing the relevant info.
     * @param userDetails
     *          Details of the user against which we will compare the JWT.
     * @return
     *          True if the info in the token matches the details of the user.
     */
    public Boolean validateToken(String token, UserDetails userDetails){
        String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
