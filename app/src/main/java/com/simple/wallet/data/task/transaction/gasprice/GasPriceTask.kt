package com.simple.wallet.data.task.transaction.gasprice

import com.one.web3.Web3Task
import com.simple.wallet.domain.entities.Gas

interface GasPriceTask : Web3Task<com.one.web3.Param, List<Gas>>