package org.example.User

import org.example.data.DatabaseConfig

interface AuthenticationService {
    fun authenticate(user: User): Boolean
}

class BasicAuthenticationService : AuthenticationService {
    private val users = mutableMapOf<String, Pair<String, UserType>>()
    private val users1: MutableMap<String, User> = mutableMapOf()

    fun register(username: String, password: String, userType: UserType): Boolean {
        if (users.containsKey(username)) {
            println("Пользователь с таким именем уже существует.")
            return false
        }
        users[username] = Pair(password, userType)
        saveUserToDb(username, password, userType)
        println("Пользователь успешно зарегистрирован.")
        return true
    }
    private fun saveUserToDb(username: String, password: String, userType: UserType) {
        DatabaseConfig.getConnection().use { conn ->
            val sql = "INSERT INTO Users(username, password, userType) VALUES(?, ?, ?)"
            val pstmt = conn.prepareStatement(sql)
            pstmt.setString(1, username)
            pstmt.setString(2, password)
            pstmt.setString(3, userType.toString())
            pstmt.executeUpdate()
            conn.commit()
        }
    }


    fun loadUsersFromDb() {
        DatabaseConfig.getConnection().use { conn ->
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("SELECT username, password, userType FROM Users")
            while (rs.next()) {
                val username = rs.getString("username")
                val password = rs.getString("password")
                val userType = UserType.valueOf(rs.getString("userType"))
                users[username] = Pair(password, userType)
            }
        }
    }

    override fun authenticate(user: User): Boolean {
        val userInfo = users[user.username]
        return userInfo != null && userInfo.first.equals(user.password, ignoreCase = true)
    }
    fun getUserType(username: String): UserType? {
        return users[username]?.second
    }
    fun getUserByUsername(username: String): User? {
        return users1[username]
    }

}