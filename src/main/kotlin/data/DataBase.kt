package org.example.data

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


object DatabaseConfig {
    const val url = "jdbc:sqlite:C:/Users/nikit/OneDrive/Рабочий стол/restaurant.db" // Замените на путь к вашей базе данных

    fun getConnection(): Connection {
        return DriverManager.getConnection(url).also {
            it.autoCommit = false
        }
    }
    fun initializeDatabase() {
        getConnection().use { conn ->
            val sql = """
                CREATE TABLE IF NOT EXISTS Users (
                    username TEXT PRIMARY KEY,
                    password TEXT NOT NULL,
                    userType TEXT NOT NULL
                );
                CREATE TABLE IF NOT EXISTS Dishes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT,
                    price REAL NOT NULL,
                    preparation_time INTEGER NOT NULL,
                    difficulty INTEGER NOT NULL
                );
                CREATE TABLE IF NOT EXISTS Orders (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    status TEXT NOT NULL,
                    FOREIGN KEY (dish_id) REFERENCES Menu(id)
                );

                CREATE TABLE IF NOT EXISTS Revenue (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    total_revenue REAL NOT NULL
                );
                CREATE TABLE IF NOT EXISTS OrderDetails (
                    order_id INTEGER,
                    dish_id INTEGER,
                    quantity INTEGER NOT NULL,
                    status TEXT NOT NULL DEFAULT 'pending', -- 'pending', 'preparing', 'ready', 'cancelled'
                    PRIMARY KEY (order_id, dish_id),
                    FOREIGN KEY (order_id) REFERENCES Orders(id),
                    FOREIGN KEY (dish_id) REFERENCES Dishes(id)
                );
            """.trimIndent()
            conn.createStatement().executeUpdate(sql)
            conn.commit()
        }
    }
}

object DatabaseConnection {
    @JvmStatic
    fun main(args: Array<String>) {
        var connection: Connection? = null
        try {
            // Загружаем драйвер
            Class.forName("org.sqlite.JDBC")
            // Устанавливаем соединение с базой данных
            connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/nikit/OneDrive/Рабочий стол/restaurant.db")
            println("Соединение с базой данных установлено!")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            if (connection != null) {
                try {
                    // Закрываем соединение
                    connection.close()
                } catch (ex: SQLException) {
                    ex.printStackTrace()
                }
            }
        }
    }
}