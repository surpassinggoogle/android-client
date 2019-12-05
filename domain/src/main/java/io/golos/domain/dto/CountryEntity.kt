package io.golos.domain.dto

import io.golos.domain.Entity

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-09.
 */
data class CountryEntity(
    val countryPhoneCode: Int,
    val countryCode: String,
    val countryName: String,
    val thumbNailUrl: String,
    val countryPhoneLength: Int
) : Entity

data class CountriesList(val countries: List<CountryEntity>) : List<CountryEntity> by countries, Entity