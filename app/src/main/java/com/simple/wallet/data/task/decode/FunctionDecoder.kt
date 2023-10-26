package com.simple.wallet.data.task.decode

import org.web3j.abi.TypeDecoder
import org.web3j.abi.TypeEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Uint256

object FunctionDecoder {

    fun decode(inputData: String, function: Function): List<Type<*>> {

        return decode(inputData, function.inputParameters.map { it.javaClass })
    }

    fun decode(inputData: String, listParameterClass: List<Class<Type<*>>>): List<Type<*>> {

        var start = 10

        val list = arrayListOf<Type<*>>()

        for (parameterClass in listParameterClass) {

            val length = getLength(parameterClass)

            val end = start + length

            val parameterEncode = inputData.substring(start, start + length)
            val parameterDecode = TypeDecoder.decode(parameterEncode, parameterClass)

            list.add(parameterDecode)

            start = end
        }

        return list
    }

    private fun getLength(type: Class<Type<*>>) = when (type) {
        Address::class.java -> TypeEncoder.encode(Address.DEFAULT).length
        Uint256::class.java -> TypeEncoder.encode(Uint256.DEFAULT).length
        else -> 0
    }
}
