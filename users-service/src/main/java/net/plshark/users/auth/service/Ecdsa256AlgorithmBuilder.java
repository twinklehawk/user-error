package net.plshark.users.auth.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import com.auth0.jwt.algorithms.Algorithm;
import net.plshark.users.auth.AuthProperties;
import org.springframework.stereotype.Component;

/**
 * Builder for the ECDSA256 algorithm
 */
@Component
public class Ecdsa256AlgorithmBuilder implements AlgorithmBuilder {

    public static final String ECDSA256 = "ecdsa256";

    @Override
    public Algorithm build(AuthProperties authProperties) throws IOException, GeneralSecurityException {
        Algorithm algorithm = null;
        String name = authProperties.getAlgorithm();
        if (ECDSA256.equals(name)) {
            if (authProperties.getKeystore() == null)
                throw new IllegalStateException("keystore must be set when using the ECDSA256 algorithm");
            if (authProperties.getKey() == null)
                throw new IllegalStateException("key must be set when using the ECDSA256 algorithm");

            KeyStore keyStore = KeyStore.getInstance(authProperties.getKeystore().getType());
            try (FileInputStream stream = new FileInputStream(authProperties.getKeystore().getLocation())) {
                keyStore.load(stream, authProperties.getKeystore().getPassword().toCharArray());
            }
            ECPrivateKey privateKey = (ECPrivateKey) keyStore.getKey(authProperties.getKey().getAlias(),
                    authProperties.getKey().getPassword().toCharArray());
            ECPublicKey publicKey = (ECPublicKey) keyStore.getCertificate(authProperties.getKey().getAlias()).getPublicKey();
            algorithm = Algorithm.ECDSA256(publicKey, privateKey);
        }
        return algorithm;
    }
}
