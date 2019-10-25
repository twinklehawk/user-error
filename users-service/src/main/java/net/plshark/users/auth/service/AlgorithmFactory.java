package net.plshark.users.auth.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import com.auth0.jwt.algorithms.Algorithm;
import net.plshark.users.auth.AuthProperties;

/**
 * Builds an algorithm from the auth properties
 */
public class AlgorithmFactory {

    public static final String NONE = "none";
    public static final String ECDSA256 = "ecdsa256";

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
        Algorithm algorithm;

        switch (name) {
            case NONE:
                algorithm = Algorithm.none();
                break;
            case ECDSA256: {
                KeyStore keyStore = KeyStore.getInstance(props.getKeystore().getType());
                try (FileInputStream stream = new FileInputStream(props.getKeystore().getLocation())) {
                    keyStore.load(stream, props.getKeystore().getPassword().toCharArray());
                }
                ECPrivateKey privateKey = (ECPrivateKey) keyStore.getKey(props.getKey().getAlias(), props.getKey().getPassword().toCharArray());
                ECPublicKey publicKey = (ECPublicKey) keyStore.getCertificate(props.getKey().getAlias()).getPublicKey();
                algorithm = Algorithm.ECDSA256(publicKey, privateKey);
                break;
            } default:
                throw new IllegalArgumentException("Unsupported algorithm: " + name);
        }

        return algorithm;
    }
}
