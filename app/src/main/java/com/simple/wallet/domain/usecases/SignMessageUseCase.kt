package com.simple.wallet.domain.usecases

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.state.ResultState
import com.simple.task.executeAsyncByPriority
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.tasks.SignParam
import com.simple.wallet.domain.tasks.SignTask

class SignMessageUseCase(
    private val signTaskList: List<SignTask>
) : BaseUseCase<SignMessageUseCase.Param, ResultState<String>> {

    override suspend fun execute(param: Param?): ResultState<String> {
        checkNotNull(param)

        return signTaskList.executeAsyncByPriority(SignParam(param.request))
    }

    data class Param(
        val request: Request
    ) : BaseUseCase.Param()
}