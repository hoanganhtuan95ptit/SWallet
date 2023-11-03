package com.simple.wallet.domain.entities

data class Url(
    var url: String = "",

    var name: String = "",

    var image: String = "",

    var description: String = "",

    var tag: Tag = Tag.UNKNOWN
) {

    enum class Tag(val value: String) {
        SCAM("SCAM"), VERIFIED("VERIFIED"), PROMOTION("PROMOTION"), UNKNOWN("UNKNOWN")
    }

    companion object{

        fun String.toUrlTag() = Url.Tag.values().first { this.equals(it.value, true) }
    }
}