package com.meu.stock.mappers

import com.meu.stock.bd.category.CategoryEntity
import com.meu.stock.bd.client.ClientEntity
import com.meu.stock.bd.note.NoteEntity
import com.meu.stock.bd.product.ProductEntity
import com.meu.stock.model.Category
import com.meu.stock.model.Client
import com.meu.stock.model.Note
import com.meu.stock.model.Product
import java.text.SimpleDateFormat
import java.util.Date


/**
 * Converte um objeto da camada de dados (ClientEntity) para um objeto
 * da camada de domínio/UI (Client).
 *
 * Marcado como 'internal' para ser acessível em todo o módulo 'app'.
 */
internal fun ClientEntity.toClient(): Client {
    return Client(
        id = this.id,
        name = this.fullName,
        phone = this.phone,
        email = this.email,
        cpf = this.cpf,
        notes = this.notes
    )
}

/**
 * Converte um objeto da camada de dados (CategoryEntity) para um objeto
 * da camada de domínio/UI (Category).
 */
internal fun CategoryEntity.toCategory(): Category {
    return Category(
        id = this.id.toString(),
        name = this.name,
        description = this.description
    )
}
/**
 * Converte um objeto da camada de dados (CategoryEntity) para um objeto
 * da camada de domínio/UI (Category).
 */
internal fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = this.id?.toLong(),
        name = this.name,
        description = this.description
    )
}

/**
 * Converte um objeto da camada de dados (ProductEntity) para um objeto
 * da camada de domínio/UI (Product).
 */
internal fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        stockQuantity = this.stockQuantity,
        lowStockQuantity = this.lowStockQuantity,
        categoryId = this.categoryId,
        priceUnit = this.priceUnit,
        priceSale = this.priceSale
    )
}
/**
 * Converte a entidade de banco de dados (ProductEntity) para o modelo de UI (Product).
 * Usado ao ler produtos do banco de dados.
 */
fun ProductEntity.toProduct(): Product {
    return Product(
        id = this.id,
        name = this.name,
        description = this.description ?: "", // Garante que a UI receba uma String, não um nulo
        stockQuantity = this.stockQuantity,
        lowStockQuantity = this.lowStockQuantity,
        categoryId = this.categoryId,
        priceUnit = this.priceUnit,
        priceSale = this.priceSale,
        categoryName = "" // O nome da categoria virá da navegação, não do banco diretamente
    )
}


/**
 * Converte a entidade de banco de dados (NoteEntity) para o modelo de UI (Note).
 * Esta função é usada ao ler notas do banco de dados.
 */
fun NoteEntity.toNote(): Note {
    return Note(
        id = this.id,
        title = this.title,
        content = this.content,
        reminderDate = this.reminderDate,
        reminderTime = this.reminderTime,
        lastUpdated = this.updatedAt.toFormattedString() // Formata a data para exibição
    )
}

/**
 * Converte o modelo de UI (Note) para a entidade de banco de dados (NoteEntity).
 * Esta função é usada ao salvar uma nova nota ou atualizar uma existente.
 */
fun Note.toNoteEntity(): NoteEntity {
    val currentTime = Date()
    return NoteEntity(
        id = this.id ?: 0, // Se o ID for nulo (nova nota), o Room gerará um. Se não, usa o ID existente para atualizar.
        title = this.title,
        content = this.content,
        // Para uma nota nova, 'createdAt' e 'updatedAt' são a mesma.
        // O ideal seria ter uma lógica no ViewModel para diferenciar criação de atualização.
        createdAt = currentTime,
        updatedAt = currentTime,
        reminderDate = this.reminderDate,
        reminderTime = this.reminderTime
    )
}

/**
 * Função de extensão auxiliar para formatar um objeto Date para uma string legível.
 * Exemplo: "25 de out de 2025, 19:30"
 */
private fun Date.toFormattedString(): String {
    val format = SimpleDateFormat("dd 'de' MMM 'de' yyyy, HH:mm",
        java.util.Locale("pt", "BR")
    )
    return format.format(this)
}


