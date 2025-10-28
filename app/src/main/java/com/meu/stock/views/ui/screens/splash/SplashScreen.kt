package com.meu.stock.views.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.meu.stock.R
import com.meu.stock.views.ui.routes.AppRoutes
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val navigationDestination by viewModel.navigationDestination.collectAsState()

    LaunchedEffect(navigationDestination) {
        delay(1500)
        if (navigationDestination != NavigationDestination.UNKNOWN) {


            val route = when (navigationDestination) {
                NavigationDestination.MAIN_SCREEN -> AppRoutes.MAIN
                NavigationDestination.LOGIN_SCREEN -> AppRoutes.LOGIN
                else -> null // Não deve acontecer, mas é uma segurança.
            }

            route?.let {
                navController.navigate(it) {

                    popUpTo(AppRoutes.SPLASH) { inclusive = true }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo"
        )
    }
}
