package com.simple.wallet.domain.tasks

import com.simple.task.Task
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Wallet

interface GenerateAddressTask : Task<GenerateAddressTask.Param, Map<String, Chain.Type>> {

    data class Param(val data: String, val walletType: Wallet.Type)
}