package com.plcoding.stockmarketapp.presentation.company_listing

import com.plcoding.stockmarketapp.domain.model.CompanyListing

data class CompanyListingState(
    val companies: List<CompanyListing> = emptyList(),
    val isRefreshing : Boolean = false,
    val isLoading : Boolean = false,
    val searchQuery : String = ""
)