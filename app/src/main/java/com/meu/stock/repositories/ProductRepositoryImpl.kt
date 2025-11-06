package com.meu.stock.repositories

import com.meu.stock.contracts.IProductRepository
import com.meu.stock.bd.category.CategoryDao
import com.meu.stock.bd.product.ProductDao
import com.meu.stock.mappers.toEntity
import com.meu.stock.mappers.toProduct
import com.meu.stock.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao
) : IProductRepository {

    override fun getProductsByCategoryId(categoryId: Long): Flow<List<Product>> {
        return productDao.getProductsByCategoryId(categoryId)
            .map { entities ->
                entities.map { it.toProduct() }
            }
    }
    override fun getProductCount(): Flow<Int> {
        return productDao.getProductCount()
    }
    override suspend fun saveProduct(product: Product) {
        // Usa o mapper para converter o modelo da UI para a entidade do banco
        // e chama a função 'save' do DAO.
        productDao.saveProduct(product.toEntity())
    }


    override suspend fun getProductById(id: Long): Product? {
        // Chama o DAO para buscar a entidade. Se não for nula,
        // converte para o modelo de UI antes de retornar.
        return productDao.getProductById(id)?.toProduct()
    }


    override suspend fun deleteProduct(id: Long) {
        // Chama a função de deleção do DAO diretamente.
        productDao.deleteProduct(id)
    }

    override suspend fun searchProducts(query: String): List<Product> {
        val productEntities = productDao.searchProducts(query)
        return productEntities.map { productEntity ->
            val categoryEntity = categoryDao.getCategoryById(productEntity.categoryId).firstOrNull()
            productEntity.toProduct().copy(
                categoryName = categoryEntity?.name ?: "Sem Categoria"
            )
        }
    }
    override fun getLowStockProducts(): Flow<List<Product>> {
        return productDao.getLowStockProducts().map { entities ->
            entities.map { entity ->
                val categoryEntity = categoryDao.getCategoryById(entity.categoryId).firstOrNull()
                entity.toProduct().copy(
                    categoryName = categoryEntity?.name ?: "Sem Categoria"
                )
            }
        }
    }

    override suspend fun updateStockQuantity(productId: Long, newStockQuantity: Int) {
        // A lógica é simplesmente repassar a chamada para o DAO.
        productDao.updateStockQuantity(productId, newStockQuantity)
    }

    override fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts()
    }
}
