package org.example.Dish

import org.example.data.DatabaseConfig
import java.sql.SQLException

class Dish(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val preparationTime: Int,
    val difficulty: Int
)

fun saveDishToDb(dish: Dish): Boolean {
    DatabaseConfig.getConnection().use { conn ->
        val sql = """
            INSERT INTO Dishes(name, description, price, preparation_time, difficulty) 
            VALUES(?, ?, ?, ?, ?)
        """.trimIndent()
        val pstmt = conn.prepareStatement(sql)
        pstmt.setString(1, dish.name)
        pstmt.setString(2, dish.description)
        pstmt.setDouble(3, dish.price)
        pstmt.setInt(4, dish.preparationTime)
        pstmt.setInt(5, dish.difficulty)
        pstmt.executeUpdate()
        conn.commit()
    }
    return true
}

fun loadDishesFromDb(): List<Dish> {
    val dishes = mutableListOf<Dish>()
    DatabaseConfig.getConnection().use { conn ->
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery("SELECT * FROM Dishes")
        while (rs.next()) {
            val id = rs.getInt("id")
            val name = rs.getString("name")
            val description = rs.getString("description")
            val price = rs.getDouble("price")
            val preparationTime = rs.getInt("preparation_time")
            val difficulty = rs.getInt("difficulty")
            dishes.add(Dish(id, name, description, price, preparationTime, difficulty))
        }
    }
    return dishes
}

fun deleteDishFromDb(dishId: Int): Boolean {
    return try {
        DatabaseConfig.getConnection().use { conn ->
            val sql = "DELETE FROM Dishes WHERE id = ?"
            val pstmt = conn.prepareStatement(sql)
            pstmt.setInt(1, dishId)
            pstmt.executeUpdate()
            conn.commit()
            true
        }
    } catch (e: SQLException) {
        e.printStackTrace()
        false
    }
}