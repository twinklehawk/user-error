package net.plshark.users.auth.service

import com.auth0.jwt.algorithms.Algorithm
import net.plshark.users.auth.AuthProperties
import java.io.IOException
import java.security.GeneralSecurityException

/**
 * Builds algorithm implementations from AuthProperties
 */
interface AlgorithmBuilder {
    /**
     * Build an algorithm as specified in properties
     * @param authProperties the properties
     * @return the build algorithm or null if this builder does not handle the algorithm specified in the properties
     * @throws IOException if an IO error occurs
     * @throws GeneralSecurityException if a keystore error occurs
     */
    @Throws(IOException::class, GeneralSecurityException::class)
    fun build(authProperties: AuthProperties): Algorithm?
}