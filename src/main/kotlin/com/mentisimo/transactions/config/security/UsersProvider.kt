package com.mentisimo.transactions.config.security

interface UsersProvider {
    fun provide(): List<UserCredentials>
}