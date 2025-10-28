package com.meu.stock.views.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.rotate

@Composable
fun StepPill(
    number: String = "01",
    title: String = "LOREM IPSUM",
    description: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam vitae orci in massa pellentesque hendrerit eu hendrerit lectus.",
    modifier: Modifier = Modifier,
    height: Dp = 80.dp,
    leftWidth: Dp = 140.dp,
    cornerRadius: Dp = 40.dp
) {
    // cores aproximadas à imagem
    val bgYellow = Color(0xFFDAB24A) // ajuste fino se quiser
    val bgGrey = Color(0xFFEFEFEF) // sombra sutil da parte branca
    val pillShape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Sombra + forma geral (usamos Surface para sombra mais natural)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .shadow(elevation = 8.dp, shape = pillShape, clip = false),
            color = Color.Transparent,
            shape = pillShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(pillShape)
            ) {
                // Parte esquerda branca (com cantos arredondados)
                Box(
                    modifier = Modifier
                        .width(leftWidth)
                        .fillMaxHeight()
                        .background(Color.White)
                        .clip(RoundedCornerShape(topStart = cornerRadius, bottomStart = cornerRadius)),
                    contentAlignment = Alignment.Center
                ) {
                    // Conteúdo da parte esquerda: número e texto vertical
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(start = 12.dp)
                        ) {
                            Text(
                                text = number,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF222222)
                            )
                        }

                        // "STEP" vertical (rotacionado)
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "STEP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF222222),
                                modifier = Modifier.rotate(-90f) // rotaciona para ficar vertical
                            )
                        }
                    }
                }

                // divisória fina entre os blocos
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(Color(0xFFF1C96A)) // tom próximo ao amarelo para suavizar
                )

                // Parte direita amarela
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(bgYellow)
                        .clip(RoundedCornerShape(topEnd = cornerRadius, bottomEnd = cornerRadius))
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = description,
                            fontSize = 12.sp,
                            color = Color(0xFFFFF8E6), // texto branco levemente amarelado
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 900)
@Composable
fun PreviewStepPill() {
    MaterialTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBC3CA)) // fundo parecido com a imagem (gradiente não aplicado)
            .padding(24.dp)
        ) {
            StepPill(
                number = "01",
                title = "LOREM IPSUM",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam vitae orci in massa pellentesque hendrerit eu hendrerit lectus."
            )
        }
    }
}
