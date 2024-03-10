package org.example.data

import org.example.User.User

fun registerUser(username: String, password: String, userType: String) {
    DatabaseConfig.getConnection().use { conn ->
        val sql = "INSERT INTO Users(username, password, userType) VALUES(?, ?, ?)"
        val pstmt = conn.prepareStatement(sql)
        pstmt.setString(1, username)
        pstmt.setString(2, password)
        pstmt.setString(3, userType)
        pstmt.executeUpdate()
        conn.commit()
    }
}

fun updateUserPassword(username: String, newPassword: String) {
    DatabaseConfig.getConnection().use { conn ->
        val sql = "UPDATE Users SET password = ? WHERE username = ?"
        val pstmt = conn.prepareStatement(sql)
        pstmt.setString(1, newPassword)
        pstmt.setString(2, username)
        pstmt.executeUpdate()
        conn.commit()
    }
}

fun getAllUsers(): List<User> {
    val users = mutableListOf<User>()
    DatabaseConfig.getConnection().use { conn ->
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery("SELECT username, password, userType FROM Users")
        while (rs.next()) {
            users.add(User(rs.getString("username"), rs.getString("password"), rs.getString("userType")))
        }
    }
    return users
}


fun deleteUser(username: String) {
    DatabaseConfig.getConnection().use { conn ->
        val sql = "DELETE FROM Users WHERE username = ?"
        val pstmt = conn.prepareStatement(sql)
        pstmt.setString(1, username)
        pstmt.executeUpdate()
        conn.commit()
    }
}