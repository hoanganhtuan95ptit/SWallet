package com.simple.wallet.domain.tasks

import com.simple.task.Task
import com.simple.wallet.domain.entities.Request

interface RequestDecodeTask : Task<Request, Request>