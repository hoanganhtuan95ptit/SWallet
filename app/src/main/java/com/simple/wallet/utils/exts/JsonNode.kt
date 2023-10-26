package com.simple.wallet.utils.exts

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode

fun JsonNode?.getString() = if (this is TextNode) {

    textValue()
} else {

    this?.toPrettyString()
}

fun JsonNode?.getStringOrNull(key: String): String? {

    val jsonNode = this?.get(key)

    return getString()
}

fun JsonNode?.getStringOrDefault(key: String, default: String = ""): String = getStringOrNull(key) ?: default


fun JsonNode?.getString(key: String, default: String = ""): String = getStringOrNull(key) ?: default


fun JsonNode.toStringV2() = if (this is TextNode) {
    textValue()
} else {
    toPrettyString()
}
