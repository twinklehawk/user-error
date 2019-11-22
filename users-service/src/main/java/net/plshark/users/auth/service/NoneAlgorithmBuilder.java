package net.plshark.users.auth.service;

import com.auth0.jwt.algorithms.Algorithm;
import net.plshark.users.auth.AuthProperties;
import org.springframework.stereotype.Component;

/**
 * Builder for the NONE algorithm
 */
@Component
public class NoneAlgorithmBuilder implements AlgorithmBuilder {

    public static final String NONE = "none";

    @Override
    public Algorithm build(AuthProperties authProperties) {
        Algorithm algorithm = null;
        String name = authProperties.getAlgorithm();
        if (NONE.equals(name))
            algorithm = Algorithm.none();
        return algorithm;
    }
}
