package org.example.data

import java.sql.SQLException

fun addDish(name: String, description: String?, price: Double, preparationTime: Int, difficulty: Int): Boolean {
    val sql = """
        INSERT INTO Dishes (name, description, price, preparation_time, difficulty) 
        VALUES (?, ?, ?, ?, ?)
    """.trimIndent()
    return try {
        val conn = DatabaseConfig.getConnection()
        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setString(1, name)
            pstmt.setString(2, description)
            pstmt.setDouble(3, price)
            pstmt.setInt(4, preparationTime)
            pstmt.setInt(5, difficulty)
            pstmt.executeUpdate()
        }
        true
    } catch (e: SQLException) {
        e.printStackTrace()
        false
    }
}

fun deleteDish(dishId: Int): Boolean {
    val sql = "DELETE FROM Dishes WHERE id = ?"
    return try {
        val conn = DatabaseConfig.getConnection()
        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setInt(1, dishId)
            pstmt.executeUpdate()
        }
        true
    } catch (e: SQLException) {
        e.printStackTrace()
        false
    }
}