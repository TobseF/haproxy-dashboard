package de.tfr.app.haproxy

import de.tfr.app.haproxy.board.parser.HAProxyConnection
import de.tfr.app.haproxy.board.parser.HAService
import org.springframework.stereotype.Service


@Service
class HAProxyService(val consul: ConsulConnection) {

    val dataProvider: HAProxyConnection

    init {
        dataProvider = HAProxyConnection(url = consul.url, userName = consul.username, password = consul.password)
        dataProvider.initAuth()
    }


    fun getStats(): List<HAService> {
        return dataProvider.readHaProxy()
    }
}