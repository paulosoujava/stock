package com.meu.stock

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.meu.stock.ui.theme.StockTheme
import com.meu.stock.views.ui.print.PrintScreen
import com.meu.stock.views.ui.readQrcode.ScannerScreen
import com.meu.stock.views.ui.routes.AppRoutes
import com.meu.stock.views.ui.screens.clients.create.ClientScreen
import com.meu.stock.views.ui.screens.clients.list.ClientListScreen
import com.meu.stock.views.ui.screens.home.HomeScreen
import com.meu.stock.views.ui.screens.live.create.AddLiveScreen
import com.meu.stock.views.ui.screens.live.list.ListLiveScreen
import com.meu.stock.views.ui.screens.login.LoginScreen
import com.meu.stock.views.ui.screens.products.category.create.CategoryFormScreen
import com.meu.stock.views.ui.screens.products.category.list.CategoryListScreen
import com.meu.stock.views.ui.screens.history.YearListScreen
import com.meu.stock.views.ui.screens.notes.create.NoteFormScreen
import com.meu.stock.views.ui.screens.notes.list.NoteListScreen
import com.meu.stock.views.ui.screens.products.product.create.ProductFormScreen
import com.meu.stock.views.ui.screens.products.product.listCategories.ListCategoriesScreen
import com.meu.stock.views.ui.screens.products.product.listProductsByCategory.ListProductsByCategoryScreen
import com.meu.stock.views.ui.screens.register.RegisterScreen
import com.meu.stock.views.ui.screens.sales.SaleFormScreen
import com.meu.stock.views.ui.screens.sellers.BestSellersScreen
import com.meu.stock.views.ui.screens.splash.SplashScreen
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockTheme {
                AppNavigation()
            }
        }
    }
}

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.SPLASH
    ) {
        composable(AppRoutes.SPLASH) {
            SplashScreen(navController = navController)
        }

        composable(AppRoutes.LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(AppRoutes.MAIN) {
            HomeScreen(navController = navController)
        }

        composable(
            AppRoutes.CLIENT_LIST, arguments = listOf(
                navArgument("isSelectionMode") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val isSelectionMode = backStackEntry.arguments?.getBoolean("isSelectionMode") ?: false
            ClientListScreen(navController = navController, isSelectionMode)
        }

        composable(
            route = "${AppRoutes.CLIENT_FORM}?clientId={clientId}",
            arguments = listOf(navArgument("clientId") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getString("clientId")
            ClientScreen(navController = navController, clientId = clientId)
        }
        composable(AppRoutes.SALES_YEAR_LIST) {
            YearListScreen(navController = navController)
        }

        //navController.navigate("${AppRoutes.PRODUCT_FORM}/${categoryId}?productId=${productId}")
        composable(
            route = "${AppRoutes.PRODUCT_FORM}?productId={productId}&categoryId={categoryId}",
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType; nullable = true },
                navArgument("categoryId") { type = NavType.StringType; nullable = true }
            )
        ) {
            ProductFormScreen(navController = navController)
        }

        composable(AppRoutes.LIST_BY_CATEGORY) {
            ListCategoriesScreen(navController = navController)
        }
        composable(AppRoutes.CATEGORY_LIST) {
            CategoryListScreen(navController = navController)
        }

        composable(
            route = "${AppRoutes.CATEGORY_FORM}?categoryId={categoryId}",
            arguments = listOf(navArgument("categoryId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) {
            CategoryFormScreen(navController = navController)
        }
        composable(
            route = AppRoutes.PRODUCT_LIST + "/{categoryId}?name={categoryName}",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.LongType },
                navArgument("categoryName") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            ListProductsByCategoryScreen(navController = navController)
        }
        composable(AppRoutes.LIVE_LIST) {
            ListLiveScreen(navController = navController)
        }
        composable(AppRoutes.ADD_LIVE) {
            AddLiveScreen(navController = navController)
        }
        composable(AppRoutes.SALE_FORM) {
            SaleFormScreen(navController = navController)
        }

        composable(AppRoutes.REGISTER) {
            RegisterScreen(navController = navController)
        }

        composable(AppRoutes.BEST_SELLERS) {
            BestSellersScreen(navController = navController)
        }
        composable(
            route = "${AppRoutes.PRINT_SCREEN}?name={name}&desc={desc}&price={price}&id={id}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType; nullable = true },
                navArgument("desc") { type = NavType.StringType; nullable = true },
                navArgument("price") { type = NavType.StringType; nullable = true },
                navArgument("id") { type = NavType.StringType; nullable = true },
                navArgument("categoryId") { type = NavType.StringType; nullable = true },
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
            val desc = backStackEntry.arguments?.getString("desc")?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
            val price = backStackEntry.arguments?.getString("price")
            val id = backStackEntry.arguments?.getString("id")
            val categoryId = backStackEntry.arguments?.getString("categoryId")

            PrintScreen(
                navController = navController,
                productName = name,
                productDescription = desc,
                productPrice = price,
                productId = id,
                categoryId= categoryId
            )
        }

        composable(AppRoutes.SCANNER) {
            ScannerScreen(
                onQrCodeScanned = { route ->
                    val params = route.split("&").associate {
                        val (key, value) = it.split("=")
                        key to value
                    }
                    val productId = params["productId"]
                    val categoryId = params["categoryId"]

                    Log.d("ScannerScreen", "${AppRoutes.PRODUCT_FORM}?productId=$productId&categoryId=$categoryId")

                    navController.navigate("${AppRoutes.PRODUCT_FORM}?productId=$productId&categoryId=$categoryId") {
                        popUpTo(AppRoutes.SCANNER) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.NOTES) {
            NoteListScreen(navController = navController)
        }

        composable(
            route = "NOTE_FORM?noteId={noteId}",
            arguments = listOf(navArgument("noteId") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
             NoteFormScreen(
                 navController = navController,
            )
        }
    }
}
