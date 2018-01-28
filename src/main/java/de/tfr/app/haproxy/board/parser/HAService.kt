package de.tfr.app.haproxy.board.parser

data class HAService(val name: String, val status: Status, val node: String) {
    public enum class Status { Up, Open, NoCheck, Unkown }

    fun isUp() = status == Status.Up

}