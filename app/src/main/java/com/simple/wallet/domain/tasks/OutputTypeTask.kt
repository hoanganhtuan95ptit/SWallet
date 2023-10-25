package com.simple.wallet.domain.tasks

import com.simple.task.Task

interface OutputTypeTask : Task<String, List<Enum<*>>> {
}