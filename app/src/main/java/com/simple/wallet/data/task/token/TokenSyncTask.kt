package com.simple.wallet.data.task.token

import com.simple.task.Task
import com.simple.wallet.domain.entities.Token

interface TokenSyncTask : Task<Unit, List<Token>> {

}