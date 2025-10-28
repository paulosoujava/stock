package com.meu.stock.data.product

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.meu.stock.data.product.ProductEntity
import com.meu.stock.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE category_id = :categoryId ORDER BY name ASC")
    fun getProductsByCategoryId(categoryId: Long): Flow<List<ProductEntity>>
    @Query("SELECT COUNT(id) FROM products")
    fun getProductCount(): Flow<Int>

    // --- FUNÇÃO DE SALVAR (CRIAR/ATUALIZAR) ---
    // Usar @Upsert é a forma mais moderna e limpa de fazer um "insert or update".
    // Ele insere se o ID não existe, ou atualiza se o ID já existe.
    @Upsert
    suspend fun saveProduct(product: ProductEntity)

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): ProductEntity?

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProduct(id: Long)

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR id LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchProducts(query: String): List<ProductEntity>

    /**
     * Retorna uma lista de todos os produtos onde a quantidade em estoque
     * é menor ou igual ao nível de alerta de estoque baixo.
     */
    @Query("SELECT * FROM products WHERE stock_quantity <= low_stock_quantity")
    fun getLowStockProducts(): Flow<List<ProductEntity>>

    /**
     * Atualiza a quantidade em estoque de um produto específico.
     * @param productId O ID do produto a ser atualizado.
     * @param newStockQuantity A nova quantidade em estoque.
     */
    @Query("UPDATE products SET stock_quantity = :newStockQuantity WHERE id = :productId")
    suspend fun updateStockQuantity(productId: Long, newStockQuantity: Int)

    /**
     * Esta query:
     * 1. Faz um JOIN com a tabela 'categories' para buscar o nome da categoria (`c.name`).
     * 2. Usa 'AS' para dar um "apelido" (alias) às colunas do banco, fazendo com que
     *    correspondam EXATAMENTE aos nomes das propriedades do seu modelo `Product`.
     * 3. O Room agora consegue construir o objeto `Product` sem ambiguidades.
     */
    @Query("""
        SELECT
            p.id,
            p.name,
            p.description,
            p.stock_quantity AS stockQuantity,          -- Mapeia stock_quantity para stockQuantity
            p.low_stock_quantity AS lowStockQuantity,    -- Mapeia low_stock_quantity para lowStockQuantity
            p.category_id AS categoryId,               -- Mapeia category_id para categoryId
            c.name AS categoryName,                    -- BUSCA E MAPEIA o nome da categoria
            p.price_unit AS priceUnit,                 -- Mapeia price_unit para priceUnit
            p.price_sale AS priceSale                  -- Mapeia price_sale para priceSale
        FROM
            products AS p
        INNER JOIN
            categories AS c ON p.category_id = c.id
        ORDER BY
            p.name ASC
    """)
    fun getAllProducts(): Flow<List<Product>>
}
