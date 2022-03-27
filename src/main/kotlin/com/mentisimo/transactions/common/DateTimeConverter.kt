package com.mentisimo.transactions.common

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class DateTimeConverter(@Value("\${dateTimePattern}") private val dateTimePattern: String) {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern)

    fun toString(localDateTime: LocalDateTime): String {
        return dateTimeFormatter.format(localDateTime)
    }

    fun fromString(localDateTimeStr: String): LocalDateTime {
        return LocalDateTime.parse(localDateTimeStr, dateTimeFormatter)
    }
}