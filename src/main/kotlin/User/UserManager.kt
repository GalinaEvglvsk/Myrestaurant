package org.example.User

object UserManager {
    private var users: List<User> = mutableListOf()

    fun getUsers(): List<User> = users

    fun setUsers(newUsers: List<User>) {
        users = newUsers
    }

}