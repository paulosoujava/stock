package com.meu.stock.views.ui.print

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.compose.material.icons.filled.Print
import com.meu.stock.views.ui.routes.AppRoutes
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import android.graphics.Bitmap.CompressFormat
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.meu.stock.ui.theme.StockTheme
import com.meu.stock.views.ui.components.DefaultTabBar
import com.meu.stock.views.ui.components.QrCodeImage

// ZXing imports (necessário adicionar dependência no build.gradle)
// implementation "com.google.zxing:core:3.5.1"
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import androidx.core.content.FileProvider

@Preview(showBackground = true)
@Composable
fun PrintScreenPreview() {
    val navController = rememberNavController()
    StockTheme {
        PrintScreen(
            navController = navController,
            productName = "Camisa Social Slim Fit de Algodão",
            productDescription = "Camisa masculina de manga longa com corte ajustado, feita com algodão egípcio.",
            productPrice = "380.99",
            productId = "6"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrintScreen(
    navController: NavController,
    productName: String?,
    productDescription: String?,
    productPrice: String?,
    productId: String?
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            DefaultTabBar(
                title = "Imprimi Etiquetas",
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            )
            {
                // Simula a aparência de uma etiqueta
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                )
                {
                    QrCodeImage(
                        content = productId,
                        size = 180,
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botão para enviar somente o QR code como imagem
                    Button(onClick = {
                        // Gera bitmap do QR
                        val qrBitmap = try {
                            generateQrCodeBitmap(productId ?: "")
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }

                        if (qrBitmap != null) {
                            val uri = saveBitmapAndGetUri(context, qrBitmap, "qrcode_${productId ?: "produto"}.png")
                            if (uri != null) {
                                sendImageIntent(context, uri, "Imprimir QR Code")
                            } else {
                                // Falha ao salvar
                                Log.e("PrintScreen", "Não foi possível salvar o bitmap do QR")
                            }
                        } else {
                            Log.e("PrintScreen", "Erro ao gerar QR bitmap")
                        }
                    }) {
                        Icon(
                            Icons.Default.Print,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Imprimir somente qrcode")
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            )
            {
                // Simula a aparência de uma etiqueta
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, MaterialTheme.colorScheme.outline)
                        .padding(24.dp),
                )
                {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = productName ?: "Produto sem nome",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.weight(1f)
                        )
                        QrCodeImage(
                            content = productId,
                            size = 180,
                            modifier = Modifier.size(80.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = productDescription ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "R$ $productPrice",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botão para enviar QR + dados do produto
                Button(onClick = {
                    // Gera bitmap do QR
                    val qrBitmap = try {
                        generateQrCodeBitmap(productId ?: "")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }

                    // Monta texto com os dados
                    val productInfo = """
                        Nome: ${productName ?: "Produto sem nome"}
                        Descrição: ${productDescription ?: ""}
                        Preço: R$ ${productPrice ?: ""}
                        ID: ${productId ?: ""}
                    """.trimIndent()

                    if (qrBitmap != null) {
                        val uri = saveBitmapAndGetUri(context, qrBitmap, "qrcode_${productId ?: "produto"}.png")
                        if (uri != null) {
                            sendImageAndTextIntent(context, uri, productInfo, "Imprimir produto")
                        } else {
                            Log.e("PrintScreen", "Não foi possível salvar o bitmap do QR")
                        }
                    } else {
                        // Se não conseguir gerar bitmap, tenta enviar só o texto
                        sendTextIntent(context, productInfo, "Imprimir produto")
                    }
                }) {
                    Icon(
                        Icons.Default.Print,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Imprimir")
                }
            }
        }
    }
}

/**
 * Gera um Bitmap de QR code usando ZXing.
 * Adicione no build.gradle (module):
 * implementation "com.google.zxing:core:3.5.1"
 */
fun generateQrCodeBitmap(content: String, size: Int = 512): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
        }
    }
    return bmp
}

/**
 * Salva o bitmap em um arquivo temporário no cache e retorna um Uri via FileProvider.
 * Necessário configurar FileProvider no AndroidManifest + res/xml/file_paths.xml
 */
fun saveBitmapAndGetUri(context: Context, bitmap: Bitmap, filename: String): Uri? {
    return try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, filename)
        val fos = FileOutputStream(file)
        bitmap.compress(CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()
        // Authority deve ser: "${applicationId}.fileprovider"
        val authority = "${context.packageName}.fileprovider"
        val uri = FileProvider.getUriForFile(context, authority, file)
        uri
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Envia apenas a imagem (EXTRA_STREAM) — abre chooser de apps que aceitam imagem.
 */
fun sendImageIntent(context: Context, imageUri: Uri, chooserTitle: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, imageUri)
        type = "image/png"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val chooser = Intent.createChooser(sendIntent, chooserTitle)
    // startActivity de um Context normal
    context.startActivity(chooser)
}

/**
 * Envia texto (EXTRA_TEXT)
 */
fun sendTextIntent(context: Context, text: String, chooserTitle: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val chooser = Intent.createChooser(sendIntent, chooserTitle)
    context.startActivity(chooser)
}

/**
 * Envia imagem + texto (EXTRA_STREAM + EXTRA_TEXT)
 */
fun sendImageAndTextIntent(context: Context, imageUri: Uri, text: String, chooserTitle: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_STREAM, imageUri)
        type = "image/png"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val chooser = Intent.createChooser(sendIntent, chooserTitle)
    context.startActivity(chooser)
}

