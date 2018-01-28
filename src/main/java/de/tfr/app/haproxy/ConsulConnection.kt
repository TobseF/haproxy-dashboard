package de.tfr.app.haproxy

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "consul.connection")
class ConsulConnection {
    lateinit var password: String
    lateinit var username: String
    lateinit var url: String
}