package com.meu.stock.contracts

import com.meu.stock.data.client.ClientEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface (Contrato) para o repositório de clientes.
 * Define as operações que podem ser realizadas, sem expor a implementação.
 */
interface IClientRepository {
    suspend fun saveClient(client: ClientEntity)
    fun getAllClients(): Flow<List<ClientEntity>>
    suspend fun delete(clientId: String)
    fun getClientById(clientId: String): Flow<ClientEntity?>
    fun getTotalClients(): Flow<Int>
}
