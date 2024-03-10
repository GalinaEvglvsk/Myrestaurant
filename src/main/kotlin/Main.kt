package org.example

import org.example.Dish.Dish
import org.example.Dish.deleteDishFromDb
import org.example.Dish.loadDishesFromDb
import org.example.Dish.saveDishToDb
import org.example.Order.*
import org.example.Order.OrderManager.addDishToOrder
import org.example.Order.OrderManager.cancelOrder
import org.example.Order.OrderManager.createOrder
import org.example.Order.OrderManager.getOrderStatus
import org.example.Order.OrderManager.payForOrder
import org.example.Revenue.loadRevenueFromDb
import org.example.User.BasicAuthenticationService
import org.example.User.User
import org.example.User.UserType
import org.example.data.*

fun main() {
    DatabaseConfig.initializeDatabase()
    val authenticationService = BasicAuthenticationService()
    // Загрузка пользователей из БД при старте приложения
    authenticationService.loadUsersFromDb()
    loadDishesFromDb()
    loadRevenueFromDb()

    println("Добро пожаловать в систему управления заказами ресторана!")
    println("Выберите действие:")
    println("1. Регистрация")
    println("2. Вход")
    val action = readLine()

    when (action) {
        "1" -> {
            println("Регистрация нового пользователя")
            println("Введите имя пользователя:")
            val username = readLine()!!
            println("Введите пароль:")
            val password = readLine()!!
            println("Выберите тип пользователя (1 - Посетитель, 2 - Администратор):")
            val userType = when (readLine()) {
                "1" -> UserType.VISITOR
                "2" -> UserType.ADMINISTRATOR
                else -> null
            }
            if (userType != null) {
                authenticationService.register(username, password, userType)
            } else {
                println("Неверный выбор типа пользователя.")
            }
        }
        "2" -> {
            println("Вход в систему")
            println("Введите имя пользователя:")
            val username = readLine()!!
            println("Введите пароль:")
            val password = readLine()!!
            val user = User(username, password, "") // Пустой тип, так как для аутентификации он не требуется
            if (authenticationService.authenticate(user)) {
                val userType = authenticationService.getUserType(username)
                when (userType) {
                    UserType.ADMINISTRATOR -> {
                        println("Добро пожаловать, администратор $username!")
                        adminMenu()
                    }
                    UserType.VISITOR -> {
                        println("Добро пожаловать, посетитель $username!")
                        visitorMenu()
                        // Здесь можно добавить логику для посетителя
                    }
                    else -> println("Ошибка определения типа пользователя.")
                }
            } else {
                println("Неверное имя пользователя или пароль.")
            }
        }
        else -> println("Неверное действие.")
    }
}

fun adminMenu() {
    println("Выберите действие:")
    println("1. Добавить блюдо")
    println("2. Удалить блюдо")
    println("3. Показать выручку")
    val adminAction = readLine()
    when (adminAction) {
        "1" -> {
            println("Добавление нового блюда")
            println("Введите название блюда:")
            val name = readLine()!!
            println("Введите описание блюда:")
            val description = readLine()
            println("Введите цену блюда:")
            val price = readLine()!!.toDouble()
            println("Введите время приготовления блюда (в минутах):")
            val preparationTime = readLine()!!.toInt()
            println("Введите сложность приготовления блюда (от 1 до 5):")
            val difficulty = readLine()!!.toInt()
            val newDish = Dish(0, name, description, price, preparationTime, difficulty)
            val success = saveDishToDb(newDish)
            if (success) {
                println("Блюдо успешно добавлено в меню.")
            } else {
                println("Ошибка при добавлении блюда.")
            }
        }
        "2" -> {
            println("Удаление блюда")
            println("Введите id блюда для удаления:")
            val dishId = readLine()!!.toInt()
            val success = deleteDishFromDb(dishId)
            if (success) {
                println("Блюдо успешно удалено из меню.")
            } else {
                println("Ошибка при удалении блюда.")
            }
        }
        "3" -> {
            val totalRevenue = loadRevenueFromDb()
            println("Выручка: $totalRevenue")
        }
        else -> println("Неверное действие.")
    }
}

fun visitorMenu() {
    var userAction = 0
    do {
        println("Выберите действие:")
        println("1. Создать новый заказ (будет присвоен временный ID)")
        println("2. Добавить блюдо в существующий заказ")
        println("3. Отменить заказ")
        println("4. Оплатить заказ")
        println("5. Проверить статус заказа")
        println("6. Выйти")

        userAction = readLine()?.toIntOrNull() ?: 0

        when (userAction) {
            1 -> {
                // Создание нового заказа и сохранение его в базе данных
                val orderId = createOrder()
                println("Заказ с временным ID $orderId успешно создан.")
            }
            2 -> {
                println("Введите ID заказа:")
                val orderId = readLine()?.toIntOrNull() ?: 0
                println("Введите ID блюда:")
                val dishId = readLine()?.toIntOrNull() ?: 0
                println("Введите количество:")
                val quantity = readLine()?.toIntOrNull() ?: 0
                addDishToOrder(orderId, dishId, quantity)
                println("Блюдо добавлено в заказ.")
            }
            3 -> {
                println("Введите ID заказа для отмены:")
                val orderId = readLine()?.toIntOrNull() ?: 0
                cancelOrder(orderId)
                println("Заказ $orderId отменен.")
            }
            4 -> {
                println("Введите ID заказа для оплаты:")
                val orderId = readLine()?.toIntOrNull() ?: 0
                payForOrder(orderId)
                println("Заказ $orderId оплачен.")
            }
            5 -> {
                println("Введите ID заказа для проверки статуса:")
                val orderId = readLine()?.toIntOrNull() ?: 0
                val status = getOrderStatus(orderId)
                println("Статус заказа: $status")
            }
        }
    } while (userAction != 6)

    println("Вы вышли из меню посетителя.")
}