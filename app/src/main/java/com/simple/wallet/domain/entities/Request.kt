package com.simple.wallet.domain.entities

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnore
import com.simple.core.utils.extentions.asObjectOrNull
import kotlinx.parcelize.Parcelize
import java.io.Serializable

class Request(
    val id: Long = 0,

    val method: Enum<*> = Method.SIGN_MESSAGE,
) : Entity {


    @JsonIgnore
    internal val extras: HashMap<Enum<*>, Any> = LinkedHashMap()


    var topic: String?
        @JsonIgnore set(value) = putExtra(ExtraType.TOPIC, value)
        @JsonIgnore get() = getExtra(ExtraType.TOPIC)

    var slide: Slide?
        @JsonIgnore set(value) = putExtra(ExtraType.SLIDE, value)
        @JsonIgnore get() = getExtra(ExtraType.SLIDE)

    var power: Power?
        @JsonIgnore set(value) = putExtra(ExtraType.POWER, value)
        @JsonIgnore get() = getExtra(ExtraType.POWER)

    var message: Message?
        @JsonIgnore set(value) = putExtra(ExtraType.MESSAGE, value)
        @JsonIgnore get() = getExtra(ExtraType.MESSAGE)

    var transaction: Transaction?
        @JsonIgnore set(value) = putExtra(ExtraType.TRANSACTION, value)
        @JsonIgnore get() = getExtra(ExtraType.TRANSACTION)

    var walletAddress: String?
        @JsonIgnore set(value) = putExtra(ExtraType.WALLET_ADDRESS, value)
        @JsonIgnore get() = getExtra(ExtraType.WALLET_ADDRESS)

    data class Power(
        val logo: String,
        val name: String,
        val url: String,
    ) : Entity {

        var status: Status? = null

        enum class Status {
            VERIFY, UNKNOWN, RISK
        }
    }

    enum class Slide(val value: String) {

        ANOTHER_DEVICE("ANOTHER_DEVICE"), // request from another device
        ANOTHER_APP("ANOTHER_APP"), // request from another app
        APP("APP"); // request from app


        companion object {

            fun String?.toSessionRequestSlideOrDefault() = toSessionRequestSlide() ?: ANOTHER_DEVICE

            fun String?.toSessionRequestSlide() = values().find { it.value.equals(this, true) }
        }
    }

    enum class Method {

        SEND_TRANSACTION,
        SIGN_TRANSACTION,
        SIGN_TRANSACTION_RAW,
        SIGN_PERSONAL_MESSAGE,
        SIGN_MESSAGE,
        SIGN_MESSAGE_TYPED,

        SIGN_AUTH,

        EC_RECOVER,
        REQUEST_ACCOUNTS,
        ESTIMATE_GAS,
        WATCH_ASSET,
        ADD_ETHEREUM_CHAIN,
        SWITCH_ETHEREUM_CHAIN,
        ETH_CALL,
        UNKNOWN;

        companion object {

            fun String.toSessionMethod(): Method = when (this) {

                "eth_sendTransaction" -> SEND_TRANSACTION
                "signTransaction", "eth_signTransaction" -> SIGN_TRANSACTION
                "signRawTransaction", "eth_sendRawTransaction" -> SIGN_TRANSACTION_RAW
                "signPersonalMessage", "personal_sign" -> SIGN_PERSONAL_MESSAGE
                "signMessage", "eth_sign" -> SIGN_MESSAGE
                "signTypedMessage", "eth_signTypedData", "eth_signTypedData_v3", "eth_signTypedData_v4" -> SIGN_MESSAGE_TYPED
                "ecRecover" -> EC_RECOVER
                "requestAccounts", "eth_accounts", "eth_requestAccounts" -> REQUEST_ACCOUNTS
                "watchAsset" -> WATCH_ASSET
                "addEthereumChain", "wallet_addEthereumChain" -> ADD_ETHEREUM_CHAIN
                "switchEthereumChain", "wallet_switchEthereumChain" -> SWITCH_ETHEREUM_CHAIN
                "estimateGas" -> ESTIMATE_GAS
                "eth_call" -> ETH_CALL
                else -> UNKNOWN
            }
        }
    }

    enum class ExtraType(val value: String) {

        AUTH("AUTH"),
        SLIDE("SIDE"),
        POWER("POWER"),
        TOPIC("TOPIC"),
        MESSAGE("MESSAGE"),
        TRANSACTION("TRANSACTION"),
        WALLET_ADDRESS("WALLET_ADDRESS")
    }
}

internal fun Request.putExtra(key: Enum<*>, value: Any?) {

    extras[key] = value ?: return
}

internal inline fun <reified T> Request.getExtra(key: Enum<*>): T? {

    return extras[key].asObjectOrNull<T>()
}