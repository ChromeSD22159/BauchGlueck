package de.frederikkohler.bauchglueck.utils

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.Dotenv
import org.jetbrains.exposed.sql.Database

class DatabaseService(
    var connection: Database? = null,
    private val dotenv: Dotenv
) {

    private fun getDatabaseInstance(
        port: Int,
        databaseName: String,
        host: String,
        username: String,
        password: String
    ): Database {
        println("Using database $databaseName on $host")
        val driverClassName = "com.mysql.cj.jdbc.Driver"
        val config = "jdbc:mysql://$host:$port/$databaseName?user=$username&password=$password"
        return Database.connect(provideDataSource(config,driverClassName))
    }

    private fun provideDataSource(url:String,driverClass:String): HikariDataSource {
        val hikariConfig= HikariConfig().apply {
            driverClassName=driverClass
            jdbcUrl=url
            maximumPoolSize=3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(hikariConfig)
    }

    init {
        connection = getDatabaseInstance(
            dotenv["DB_PORT"].toInt(),
            dotenv["DB_NAME"],
            dotenv["DB_HOST"],
            dotenv["DB_USERNAME"],
            dotenv["DB_PASSWORD"]
        )
    }
}