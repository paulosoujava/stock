package com.meu.stock.views.ui.routes

object AppRoutes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val MAIN = "main"
    const val CLIENT_LIST = "client_list?isSelectionMode={isSelectionMode}&searchQuery={searchQuery}"
    const val CLIENT_FORM = "client_form"
    const val SALES_YEAR_LIST = "sales_year_list"
    const val SALE_FORM = "sale_form"
    const val PRODUCT_FORM = "product_form"
    const val LIST_BY_CATEGORY = "list_by_category"
    const val CATEGORY_LIST = "category_list"
    const val CATEGORY_FORM = "category_form"
    const val PRODUCT_LIST = "product_list"
    const val ADD_LIVE = "add_live"
    const val LIVE_LIST = "live_list"
    const val REGISTER = "register"
    const val BEST_SELLERS = "sellers"
    const val PRINT_SCREEN = "print"
    const val SCANNER = "scanner"
    const val NOTES = "notes"
    const val PROMO_LIST = "list_promo"
    const val PROMO_CREATE = "create_promo"
    private const val PROMO_FORM_ROUTE = "promoForm"
    const val PROMO_ID_ARG = "promoId"
    //    Isso vai gerar rotas como "promoForm/123" ou "promoForm"
    val PROMO_FORM = "$PROMO_FORM_ROUTE?${PROMO_ID_ARG}={${PROMO_ID_ARG}}"
    fun buildPromoFormRoute(promoId: Long?): String {
        return if (promoId != null) {
            "$PROMO_FORM_ROUTE?$PROMO_ID_ARG=$promoId"
        } else {
            PROMO_FORM_ROUTE
        }
    }

}
