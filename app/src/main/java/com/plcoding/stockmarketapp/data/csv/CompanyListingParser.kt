package com.plcoding.stockmarketapp.data.csv

import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CompanyListingParser @Inject constructor(

) : CSVParser<CompanyListing> {

    override suspend fun parse(stream: InputStream): List<CompanyListing> {

        val csvReader = CSVReader(InputStreamReader(stream))
       return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(0)
                .mapNotNull { line ->
                    val symbol = line.getOrNull(0) ?: return@mapNotNull null
                    val name = line.getOrNull(1) ?: return@mapNotNull null
                    val exchange = line.getOrNull(2) ?: return@mapNotNull null

                    CompanyListing(
                        name = name,
                        symbol = symbol,
                        exchange = exchange
                    )
                }
                .also {
                    csvReader.close()
                }
        }
    }
}