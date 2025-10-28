package com.meu.stock.contracts

import kotlinx.coroutines.flow.Flow

interface IGetTotalClientsUseCase {
    operator fun invoke(): Flow<Int>
}