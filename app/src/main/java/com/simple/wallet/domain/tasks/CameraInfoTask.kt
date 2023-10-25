package com.simple.wallet.domain.tasks

import com.simple.adapter.ViewItemCloneable
import com.simple.task.Task

interface CameraInfoTask : Task<String, List<ViewItemCloneable>> {
}