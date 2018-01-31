package de.tfr.app.haproxy.board.parser

import de.tfr.app.haproxy.board.parser.HAService.Status
import org.apache.commons.csv.CSVRecord

enum class Headers(val column: String) { ServiceName("# pxname"), NodeName("svname"), Status("status"); }

fun parse(csvLine: CSVRecord): HAService {
    val serviceName = csvLine.get(Headers.ServiceName.column)
    val nodeName = csvLine.get(Headers.NodeName.column)
    val status = csvLine.get(Headers.Status.column)
    return HAService(name = serviceName, status = parseStatus(status), node = nodeName)
}

fun parseStatus(input: String): Status {
    return when (input) {
        "OPEN" -> Status.Open
        "UP" -> Status.Up
        "DOWN" -> Status.Down
        "no check" -> Status.NoCheck
        else -> Status.Unknown
    }

}