package com.meu.stock.contracts


import com.meu.stock.model.Client
import kotlinx.coroutines.flow.Flow

/**
 * Interface (Contrato) para o caso de uso que busca clientes.
 */
interface IGetClientsUseCase {
     operator fun invoke(): Flow<List<Client>>
}
