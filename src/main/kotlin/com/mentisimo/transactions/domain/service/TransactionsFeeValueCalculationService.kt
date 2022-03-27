package com.mentisimo.transactions.domain.service

import org.springframework.stereotype.Service
import com.mentisimo.transactions.domain.model.CommissionThreshold
import java.math.BigDecimal

@Service
class TransactionsFeeValueCalculationService {

    private val transactionValueThresholds: Set<CommissionThreshold> = linkedSetOf(
            CommissionThreshold(1000.toBigDecimal(), "3.5".toBigDecimal()),
            CommissionThreshold(2500.toBigDecimal(), "2.5".toBigDecimal()),
            CommissionThreshold(5000.toBigDecimal(), "1.1".toBigDecimal()),
            CommissionThreshold(10_000.toBigDecimal(), "0.1".toBigDecimal())
    )

    fun calculateTransactionsFeeValue(totalValueOfTransactions: BigDecimal): BigDecimal {
        for (threshold in transactionValueThresholds) {
            if (totalValueOfTransactions.compareTo(threshold.threshold) == -1) {
                return totalValueOfTransactions.multiply(threshold.multiplier)
            }
        }
        return BigDecimal.ZERO
    }
}