package com.meu.stock.di


import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InternalStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Copia uma imagem de uma URI de conteúdo para o armazenamento interno do app.
     * @param contentUri A URI da imagem selecionada (ex: da galeria).
     * @return A URI do novo arquivo copiado no armazenamento interno.
     */
    fun copyImageToInternalStorage(contentUri: Uri): Uri? {
        return try {
            val inputStream = context.contentResolver.openInputStream(contentUri) ?: return null

            // Cria um arquivo com nome único no diretório de arquivos do app
            val fileName = "promo_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)

            val outputStream = file.outputStream()
            inputStream.copyTo(outputStream)

            inputStream.close()
            outputStream.close()

            // Retorna a URI do arquivo recém-criado
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
