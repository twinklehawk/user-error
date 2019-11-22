package net.plshark.users.auth.service;

import com.auth0.jwt.algorithms.Algorithm;
import net.plshark.users.auth.AuthProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Builder for HMAC algorithms
 */
@Component
public class HmacAlgorithmBuilder implements AlgorithmBuilder {

    public static final String HMAC256 = "hmac256";
    public static final String HMAC512 = "hmac512";

    @Override
    public Algorithm build(AuthProperties authProperties) {
        Algorithm algorithm = null;
        String name = authProperties.getAlgorithm();
        if (HMAC256.equals(name) || HMAC512.equals(name)) {
            String secret = authProperties.getSecret();
            if (!StringUtils.hasLength(secret))
                throw new IllegalStateException("Must set a secret when using the HMAC256 algorithm");

            if (HMAC256.equals(name))
                algorithm = Algorithm.HMAC256(secret);
            else// if (HMAC512.equals(name))
                algorithm = Algorithm.HMAC512(secret);
        }
        return algorithm;
    }
}
