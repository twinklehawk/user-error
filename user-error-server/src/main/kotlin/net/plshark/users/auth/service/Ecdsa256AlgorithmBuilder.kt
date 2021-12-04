package net.plshark.users.auth.service

import com.auth0.jwt.algorithms.Algorithm
import net.plshark.users.auth.AuthProperties
import org.springframework.stereotype.Component
import java.io.FileInputStream
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey

/**
 * Builder for the ECDSA256 algorithm
 */
@Component
class Ecdsa256AlgorithmBuilder : AlgorithmBuilder {

    @Throws(IOException::class, GeneralSecurityException::class)
    override fun build(authProperties: AuthProperties): Algorithm? {
        var algorithm: Algorithm? = null
        val name: String = authProperties.algorithm
        if (ECDSA256 == name) {
            checkNotNull(authProperties.keystore) { "keystore must be set when using the ECDSA256 algorithm" }
            checkNotNull(authProperties.key) { "key must be set when using the ECDSA256 algorithm" }

            val keyStore = KeyStore.getInstance(authProperties.keystore.type)
            FileInputStream(authProperties.keystore.location).use { stream ->
                keyStore.load(stream, authProperties.keystore.password.toCharArray())
            }
            val privateKey = keyStore.getKey(authProperties.key.alias, authProperties.key.password.toCharArray()) as
                ECPrivateKey
            val publicKey = keyStore.getCertificate(authProperties.key.alias).publicKey as ECPublicKey
            algorithm = Algorithm.ECDSA256(publicKey, privateKey)
        }
        return algorithm
    }

    companion object {
        const val ECDSA256 = "ecdsa256"
    }
}
