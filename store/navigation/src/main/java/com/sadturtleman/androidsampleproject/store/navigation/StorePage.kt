package com.sadturtleman.androidsampleproject.store.navigation

import com.sadturtleman.androidsampleproject.common.navigation.NavRoute
import com.sadturtleman.androidsampleproject.common.navigation.Page

object StorePage : Page {
    const val PATH = "/store"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
