package net.plshark.users.auth.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.collect.ImmutableList;
import net.plshark.users.auth.AuthProperties;

/**
 * Builds an algorithm from the auth properties
 */
public class AlgorithmFactory {

    private final List<AlgorithmBuilder> algorithmBuilders;

    public AlgorithmFactory(List<AlgorithmBuilder> algorithmBuilders) {
        this.algorithmBuilders = ImmutableList.copyOf(algorithmBuilders);
    }

    /**
     * Build the algorithm configured in the auth properties
     * @param props the properties
     * @return the algorithm
     * @throws GeneralSecurityException if unable to load keys required by the algorithm
     * @throws IOException if unable to load files required by the algorithm
     * @throws IllegalArgumentException if the algorithm specified in the properties is invalid
     */
    public Algorithm buildAlgorithm(AuthProperties props) throws GeneralSecurityException, IOException {
        String name = props.getAlgorithm();
        Algorithm algorithm = null;

        for (AlgorithmBuilder builder : algorithmBuilders) {
            algorithm = builder.build(props);
            if (algorithm != null)
                break;
        }

        if (algorithm == null)
            throw new IllegalArgumentException("Unsupported algorithm: " + name);

        return algorithm;
    }
}
