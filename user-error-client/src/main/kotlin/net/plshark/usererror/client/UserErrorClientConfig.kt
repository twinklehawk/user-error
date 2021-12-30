package net.plshark.usererror.client

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@ConfigurationProperties("user-error.client")
data class UserErrorClientConfig(
    var baseUrl: String
) {

    @Bean
    fun webClient() = WebClient.create()
}
