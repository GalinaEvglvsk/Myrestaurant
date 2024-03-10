    package org.example.Revenue

    import org.example.data.DatabaseConfig
    import java.sql.SQLException

    data class Revenue(
        val id: Int,
        val totalRevenue: Double
    )

    fun saveRevenueToDb(revenue: Revenue): Boolean {
        return try {
            DatabaseConfig.getConnection().use { conn ->
                val sql = "INSERT INTO Revenue(total_revenue) VALUES(?)"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setDouble(1, revenue.totalRevenue)
                pstmt.executeUpdate()
                conn.commit()
                true
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun loadRevenueFromDb(): List<Revenue> {
        val revenues = mutableListOf<Revenue>()
        DatabaseConfig.getConnection().use { conn ->
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("SELECT * FROM Revenue")
            while (rs.next()) {
                val id = rs.getInt("id")
                val totalRevenue = rs.getDouble("total_revenue")
                revenues.add(Revenue(id, totalRevenue))
            }
        }
        return revenues
    }

    fun saveRevenue(totalRevenue: Double) {
        val sql = "INSERT INTO Revenue(total_revenue) VALUES(?)"

        try {
            DatabaseConfig.getConnection().use { conn ->
                val pstmt = conn.prepareStatement(sql)
                pstmt.setDouble(1, totalRevenue)
                pstmt.executeUpdate()
                conn.commit() // Завершаем транзакцию, подтверждая изменения
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
