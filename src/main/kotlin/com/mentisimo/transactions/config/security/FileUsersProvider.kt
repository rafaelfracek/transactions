package com.mentisimo.transactions.config.security

import de.siegmar.fastcsv.reader.NamedCsvReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.nio.charset.StandardCharsets

@Component
class FileUsersProvider(@Value("\${USERS_FILE}") private val usersFile: File) : UsersProvider {

    override fun provide(): List<UserCredentials> {
        val userCredentials = ArrayList<UserCredentials>();
        NamedCsvReader.builder().build(usersFile.toPath(), StandardCharsets.UTF_8).forEach { row ->
            userCredentials.add(UserCredentials(row.getField("username"), row.getField("password_hash")))
        }
        return userCredentials
    }
}