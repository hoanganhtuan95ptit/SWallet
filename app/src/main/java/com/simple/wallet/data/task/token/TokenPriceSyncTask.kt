package com.simple.wallet.data.task.token

import com.simple.task.Task
import com.simple.wallet.domain.entities.Token

interface TokenPriceSyncTask : Task<List<Token>, List<Token.Price>>