package com.meu.stock.bd.client


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para a entidade Cliente.
 * Define os métodos de acesso ao banco de dados para a tabela 'clients'.
 */
@Dao
interface ClientDao {

    /**
     * Insere um novo cliente. Se um cliente com a mesma chave primária já existir,
     * ele será substituído.
     * A anotação 'suspend' garante que esta operação será executada em uma corrotina.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertClient(client: ClientEntity)

    /**
     * Busca todos os clientes da tabela, ordenados pelo nome completo.
     * Retorna um Flow, o que permite que a UI observe as mudanças no banco
     * de dados em tempo real.
     */
    @Query("SELECT * FROM clients ORDER BY fullName ASC")
    fun getAllClients(): Flow<List<ClientEntity>>

    /**
     * Busca um único cliente pelo seu ID.
     * Retorna um Flow, permitindo que a UI observe mudanças neste cliente específico.
     */
    @Query("SELECT * FROM clients WHERE id = :clientId")
    fun getClientById(clientId: Long): Flow<ClientEntity?>

    /**
     * Deleta um cliente da tabela usando o seu ID.
     */
    @Query("DELETE FROM clients WHERE id = :clientId")
    suspend fun deleteClientById(clientId: String)

    /**
     * Contagem de clientes no banco.
     */
    @Query("SELECT COUNT(id) FROM clients")
    fun getTotalClients(): Flow<Int>
}
