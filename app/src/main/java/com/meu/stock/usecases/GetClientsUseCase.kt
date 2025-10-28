package com.meu.stock.usecases

import com.meu.stock.contracts.IClientRepository
import com.meu.stock.contracts.IGetClientsUseCase
import com.meu.stock.repositories.ClientRepositoryImpl
import com.meu.stock.model.Client
import com.meu.stock.data.client.ClientEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetClientsUseCase @Inject constructor(
    private val clientRepository: IClientRepository
): IGetClientsUseCase {

    override operator fun invoke(): Flow<List<Client>> {
        return clientRepository.getAllClients()
            .map { entityList ->
                entityList.map { entity ->
                    entity.toClient()
                }
            }
    }
}

private fun ClientEntity.toClient(): Client {
    return Client(
        id = this.id,
        name = this.fullName,
        phone = this.phone,
        email = this.email,
        cpf = this.cpf,
        notes = this.notes
    )
}
