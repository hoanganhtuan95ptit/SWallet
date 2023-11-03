package com.simple.wallet.domain.usecases.url

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.wallet.domain.entities.Url
import com.simple.wallet.domain.repositories.UrlRepository

class QueryUseCase(
    private val urlRepository: UrlRepository,
) : BaseUseCase<QueryUseCase.Param, List<Url>> {

    override suspend fun execute(param: Param?): List<Url> {
        checkNotNull(param)

        return urlRepository.query(param.query)
    }

    data class Param(val query: String)
}