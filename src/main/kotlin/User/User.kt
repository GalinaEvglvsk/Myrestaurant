package org.example.User

import java.io.Serializable

open class User(val username: String, val password: String, val userType: String): Serializable

enum class UserType : Serializable {
    ADMINISTRATOR, VISITOR
}

class Visitor(username: String, password: String) : User(username, password, UserType.VISITOR.toString())

class Administrator(username: String, password: String) : User(username, password, UserType.ADMINISTRATOR.toString())