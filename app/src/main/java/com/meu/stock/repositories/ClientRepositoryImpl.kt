package com.meu.stock.repositories

import com.meu.stock.contracts.IClientRepository
import com.meu.stock.bd.client.ClientDao
import com.meu.stock.bd.client.ClientEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repositório para gerenciar os dados dos clientes.
 * Ele usa o ClientDao para interagir com o banco de dados Room.
 * O @Inject constructor() permite que o Hilt forneça uma instância
 * desta classe com suas dependências (neste caso, o ClientDao).
 */
class ClientRepositoryImpl @Inject constructor(
    private val clientDao: ClientDao
): IClientRepository {
    /**
     * Salva (insere ou atualiza) um cliente no banco de dados.
     * Esta é uma suspend function porque o acesso ao banco de dados
     * deve ser feito fora da thread principal.
     */
    override suspend fun saveClient(client: ClientEntity) {
        clientDao.upsertClient(client)
    }

    /**
     * Retorna um Flow contendo a lista de todos os clientes.
     * O Flow garante que a UI será notificada automaticamente
     * sobre quaisquer mudanças na tabela de clientes.
     */
    override fun getAllClients(): Flow<List<ClientEntity>> = clientDao.getAllClients()

    /**
     * Deleta um cliente do banco de dados usando seu ID.
     * Esta é uma suspend function porque é uma operação de escrita.
     * A chamada é delegada para o DAO correspondente.
     */
    override suspend fun delete(clientId: String) { // <-- MÉTODO ADICIONADO
        clientDao.deleteClientById(clientId)
    }

    /**
     * Retorna um cliente específico do banco de dados usando seu ID.
     * A chamada é delegada para o DAO correspondente.
     */
    override fun getClientById(clientId: String): Flow<ClientEntity?> {
        return clientDao.getClientById(clientId.toLong())
    }
    /**
     * Retorna o numero de  clientes cadastrado no banco
     */
    override fun getTotalClients(): Flow<Int> { // <-- ADICIONE A IMPLEMENTAÇÃO
        return clientDao.getTotalClients()
    }
}