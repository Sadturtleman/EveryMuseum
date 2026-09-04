package com.sadturtleman.androidsampleproject.store.domain

import com.sadturtleman.androidsampleproject.common.domain.navigation.NavRoute
import com.sadturtleman.androidsampleproject.common.domain.navigation.Page

object StorePage : Page {
    const val PATH = "/store"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
