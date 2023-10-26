package com.simple.wallet.domain.entities

data class Message(
    val message: String,

    val chainId: Long,

    var type: Type = Type.SIGN_PERSONAL_MESSAGE
) : Entity {

    var extra: Extra? = null

    enum class Type {
        UNKNOWN,

        SIGN_PERMIT,
        SIGN_MESSAGE,
        SIGN_MESSAGE_TYPED,
        SIGN_PERSONAL_MESSAGE;

        companion object {
            fun String.toMessageType() = when (this) {
                "signPersonalMessage", "personal_sign" -> SIGN_PERSONAL_MESSAGE
                "signMessage", "eth_sign" -> SIGN_MESSAGE
                "signTypedMessage", "eth_signTypedData", "eth_signTypedData_v3", "eth_signTypedData_v4" -> SIGN_MESSAGE_TYPED
                else -> UNKNOWN
            }

        }

    }

    interface Extra : Entity
}
