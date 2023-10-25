package com.simple.wallet.domain.tasks

import com.simple.task.Task
import com.simple.wallet.domain.entities.Wallet

interface WalletTypeDetectTask : Task<String, Wallet.Type> {
}