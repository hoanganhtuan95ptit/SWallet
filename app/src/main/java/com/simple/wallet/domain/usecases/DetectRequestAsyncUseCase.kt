package com.simple.wallet.domain.usecases

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.coreapp.utils.extentions.offerActive
import com.simple.task.executeSyncByPriority
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.entities.Url
import com.simple.wallet.domain.repositories.UrlRepository
import com.simple.wallet.domain.tasks.RequestDecodeTask
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class DetectRequestAsyncUseCase(
    private val urlRepository: UrlRepository,

    private val requestDecodeTasks: List<RequestDecodeTask>
) : BaseUseCase<DetectRequestAsyncUseCase.Param, Flow<Request>> {

    override suspend fun execute(param: Param?): Flow<Request> = channelFlow {
        checkNotNull(param)

        val request = param.request

        requestDecodeTasks.executeSyncByPriority(param.request)


        request.power?.apply {

            val url = urlRepository.getUrlListBy(this.url).firstOrNull()

            status = when (url?.tag) {
                Url.Tag.SCAM -> Request.Power.Status.RISK
                Url.Tag.VERIFIED -> Request.Power.Status.VERIFY
                else -> Request.Power.Status.UNKNOWN
            }
        }


        offerActive(param.request)


        awaitClose {

        }
    }

    data class Param(val request: Request) : BaseUseCase.Param()
}