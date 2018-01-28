package de.tfr.app.haproxy.board.parser

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Authenticator
import java.net.PasswordAuthentication
import java.net.URL


class HAProxyConnection(val url: String, val userName: String, val password: String) {

    fun initAuth() {
        Authenticator.setDefault(object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(userName, password.toCharArray())
            }
        })
    }

    fun readHaProxy(): List<HAService> {
        val streamReader = BufferedReader(InputStreamReader(URL(url).openStream()))
        val parser = CSVParser(streamReader, CSVFormat.DEFAULT.withFirstRecordAsHeader())

        return parser.records.map { parse(it) }.toList()
    }
}