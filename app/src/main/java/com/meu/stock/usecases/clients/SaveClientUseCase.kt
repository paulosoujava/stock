package com.meu.stock.usecases.clients


import com.meu.stock.contracts.IClientRepository
import com.meu.stock.mappers.toEntity
import com.meu.stock.model.Client
import javax.inject.Inject

class SaveClientUseCase @Inject constructor(
    private val clientRepository: IClientRepository
) {
    suspend operator fun invoke(client: Client) {
        if (client.name.isBlank()) {
            throw IllegalArgumentException("O nome do cliente não pode ser vazio.")
        }
        // Converte o Model para a Entity antes de salvar
        clientRepository.saveClient(client.toEntity())
    }
}
