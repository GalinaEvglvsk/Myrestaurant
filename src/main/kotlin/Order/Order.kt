package org.example.Order

import org.example.data.DatabaseConfig
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

data class Order(val id: Int, var status: String, val userId: Int)
data class OrderDetail(val orderId: Int, val dishId: Int, var quantity: Int, var status: String)

object OrderManager {
    private val orders = ConcurrentHashMap<Int, Order>()
    private val orderDetails = ConcurrentHashMap<Pair<Int, Int>, OrderDetail>()

    fun createOrder(): Int {
        var orderId = 0

        val sql = "INSERT INTO Orders(status, createdAt, isPaid) VALUES('pending', datetime('now'), 0)"

        try {
            DatabaseConfig.getConnection().use { conn ->
                val pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)
                pstmt.executeUpdate()
                val rs = pstmt.generatedKeys
                if (rs.next()) {
                    orderId = rs.getInt(1)
                }
                conn.commit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return orderId
    }

    fun addDishToOrder(orderId: Int, dishId: Int, quantity: Int) {
        val sql = """
        INSERT INTO OrderDetails (order_id, dish_id, quantity, status) 
        VALUES (?, ?, ?, ?)
        ON CONFLICT(order_id, dish_id) DO UPDATE SET quantity = quantity + ?
    """.trimIndent()

        try {
            DatabaseConfig.getConnection().use { conn ->
                val pstmt: PreparedStatement = conn.prepareStatement(sql)
                pstmt.setInt(1, orderId)
                pstmt.setInt(2, dishId)
                pstmt.setInt(3, quantity)
                pstmt.setString(4, "pending")
                pstmt.setInt(5, quantity)

                pstmt.executeUpdate()
                conn.commit()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun cancelOrder(orderId: Int) {
        val sql = "DELETE FROM Orders WHERE orderId = ?"
        try {
            val conn = DatabaseConfig.getConnection()
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setInt(1, orderId)
                pstmt.executeUpdate()
            }
            println("Заказ $orderId отменен.")
        } catch (e: SQLException) {
            println("Ошибка при отмене заказа: ${e.message}")
        }
    }

    fun getOrderStatus(orderId: Int): String {
        val sql = "SELECT status FROM Orders WHERE orderId = ?"
        var status = "Не найдено"

        try {
            DatabaseConfig.getConnection().use { conn ->
                val pstmt: PreparedStatement = conn.prepareStatement(sql)
                pstmt.setInt(1, orderId)

                val rs: ResultSet = pstmt.executeQuery()
                if (rs.next()) {
                    status = rs.getString("status") // Получаем статус заказа
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return status
    }

    fun processOrder(orderId: Int) {
        thread {
            try {

                Thread.sleep(5000)

                // После "приготовления" обновляем статус заказа на "готов"
                val sql = "UPDATE Orders SET status = ? WHERE orderId = ?"
                DatabaseConfig.getConnection().use { conn ->
                    val pstmt: PreparedStatement = conn.prepareStatement(sql)
                    pstmt.setString(1, "готов")
                    pstmt.setInt(2, orderId)
                    pstmt.executeUpdate()
                    conn.commit()
                }
            } catch (e: InterruptedException) {
                println("Процесс приготовления был прерван: ${e.message}")
            } catch (e: SQLException) {
                println("Ошибка при обновлении статуса заказа: ${e.message}")
            }
        }
    }

    fun payForOrder(orderId: Int) {
        val sqlUpdateOrder = "UPDATE Orders SET status = 'оплачено' WHERE orderId = ?"

        try {
            DatabaseConfig.getConnection().use { conn ->
                val pstmtOrder: PreparedStatement = conn.prepareStatement(sqlUpdateOrder)
                pstmtOrder.setInt(1, orderId)
                pstmtOrder.executeUpdate()




                conn.commit()
            }
        } catch (e: SQLException) {
            println("Ошибка при обновлении статуса заказа на 'оплачено': ${e.message}")
            try {
                DatabaseConfig.getConnection().rollback()
            } catch (rollbackEx: SQLException) {
                println("Ошибка при откате транзакции: ${rollbackEx.message}")
            }
        }
    }
}