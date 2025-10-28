package com.meu.stock.contracts

import com.meu.stock.model.Client
import kotlinx.coroutines.flow.Flow

interface IGetClientByIdUseCase {
    operator fun invoke(clientId: String): Flow<Client?>
}