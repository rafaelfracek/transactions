package com.mentisimo.transactions.domain.model

import java.math.BigDecimal

data class CommissionThreshold(val threshold: BigDecimal, private val commissionPercentage: BigDecimal) {
    val multiplier: BigDecimal = commissionPercentage.scaleByPowerOfTen(-2)
}