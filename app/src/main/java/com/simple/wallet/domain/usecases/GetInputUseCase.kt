package com.simple.wallet.domain.usecases

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.state.toSuccess
import com.simple.task.executeAsyncByFast
import com.simple.wallet.domain.entities.scan.ScanInputType
import com.simple.wallet.domain.tasks.InputTypeTask

class GetInputUseCase(
    private val inputTypeTaskList: List<InputTypeTask>
) : BaseUseCase<String, List<ScanInputType>> {

    override suspend fun execute(param: String?): List<ScanInputType> {
        checkNotNull(param)

        return inputTypeTaskList.executeAsyncByFast(param).toSuccess()?.data ?: ScanInputType.values().toList()
    }
}