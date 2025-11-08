package com.meu.stock.usecases.clients


import com.meu.stock.contracts.IClientRepository
import com.meu.stock.contracts.IGetTotalClientsUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTotalClientsUseCase @Inject constructor(
    private val clientRepository: IClientRepository
): IGetTotalClientsUseCase {
    override operator fun invoke(): Flow<Int> {
        return clientRepository.getTotalClients()
    }
}
