package com.plcoding.stockmarketapp.data.local

import androidx.room.*

@Dao
interface StockDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyListings(companyListings: List<CompanyListingEntity>)

    @Query("DELETE FROM companylistingentity")
    suspend fun clerAllListings()

    @Query(
        """
            SELECT * FROM companylistingentity
            WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR 
            UPPER(name) == symbol
        """
    )
    suspend fun searchCompanyListings(query: String): List<CompanyListingEntity>
}