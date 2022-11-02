package com.plcoding.stockmarketapp.presentation.company_listing

sealed class CompanyListingEvent {

    object Refresh : CompanyListingEvent()
    class OnSearchQueryChange(val query: String) : CompanyListingEvent()
}
