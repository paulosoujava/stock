package com.meu.stock.views.ui.components



import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Um Composable que gera e exibe uma imagem de QR Code a partir de um conteúdo de texto.
 *
 * A geração do QR Code é feita em uma thread de background (Dispatchers.IO) para não
 * bloquear a interface do usuário, tornando-o eficiente para ser usado em listas.
 *
 * @param content O texto ou dado a ser codificado no QR Code. Se for nulo ou vazio, nada será exibido.
 * @param modifier O modificador a ser aplicado ao componente que contém a imagem.
 * @param size O tamanho da imagem do QR Code a ser gerada, em pixels (largura e altura).
 */
@Composable
fun QrCodeImage(
    content: String?,
    modifier: Modifier = Modifier,
    size: Int = 512 // Tamanho padrão da imagem gerada (alta resolução)
) {
    // Estado para armazenar o Bitmap do QR Code que será gerado.
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // LaunchedEffect é usado para iniciar uma corrotina quando o 'content' muda.
    // Se o conteúdo do QR Code for alterado, ele será gerado novamente.
    LaunchedEffect(content) {
        if (content.isNullOrBlank()) {
            qrCodeBitmap = null // Limpa a imagem se o conteúdo for inválido
            return@LaunchedEffect
        }

        // Lança a geração do QR Code em uma thread de I/O para não travar a UI.
        launch(Dispatchers.IO) {
            try {
                // Configuração do gerador de QR Code da biblioteca ZXing
                val writer = QRCodeWriter()
                val hints = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")

                // Gera a matriz de bits (a representação lógica do QR Code)
                val bitMatrix = writer.encode(
                    content,
                    BarcodeFormat.QR_CODE,
                    size,
                    size,
                    hints
                )

                // Cria um Bitmap vazio para desenhar o QR Code
                val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)

                // Itera sobre a matriz de bits e pinta os pixels do Bitmap
                for (x in 0 until size) {
                    for (y in 0 until size) {
                        // Se o bit for 'true', pinta de preto; senão, pinta de branco.
                        bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }

                // Atualiza o estado com o Bitmap gerado, o que fará a UI redesenhar.
                qrCodeBitmap = bmp
            } catch (e: Exception) {
                // Em caso de qualquer erro na geração, limpa o bitmap e imprime o erro.
                qrCodeBitmap = null
                e.printStackTrace()
            }
        }
    }

    // O Composable que efetivamente exibe a imagem na tela.
    Box(
        modifier = modifier.aspectRatio(1f), // Força o Composable a ter uma proporção quadrada.
        contentAlignment = Alignment.Center
    ) {
        // O 'qrCodeBitmap' só será desenhado se não for nulo.
        qrCodeBitmap?.let { bmp ->
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "QR Code para: $content",
                contentScale = ContentScale.Fit, // Garante que a imagem caiba no espaço definido.
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
