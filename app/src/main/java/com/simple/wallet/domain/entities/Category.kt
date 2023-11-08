package com.simple.wallet.domain.entities

data class Category(val id: String, val deeplink: String) : Entity {

    enum class Id(val value: String) {

        TRANSFER("TRANSFER"), D_APP("D_APP"), SWAP("SWAP"), CROSS_SWAP("CROSS_SWAP")
    }
}