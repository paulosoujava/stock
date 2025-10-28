package com.meu.stock.usecases



import com.meu.stock.contracts.IClientRepository
import com.meu.stock.contracts.IGetClientByIdUseCase
import com.meu.stock.contracts.IGetClientsUseCase
import com.meu.stock.mappers.toClient
import com.meu.stock.model.Client
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetClientByIdUseCase @Inject constructor(
    private val clientRepository: IClientRepository
): IGetClientByIdUseCase {
    // O UseCase pode retornar um Flow nulo se o cliente não for encontrado
    override operator fun invoke(clientId: String): Flow<Client?> {
        return clientRepository.getClientById(clientId).map { entity ->
            entity?.toClient() // Converte para o modelo da UI, se não for nulo
        }
    }
}
