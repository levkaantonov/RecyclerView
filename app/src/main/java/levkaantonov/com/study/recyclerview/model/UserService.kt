package levkaantonov.com.study.recyclerview.model

import com.github.javafaker.Faker
import levkaantonov.com.study.recyclerview.UserNotFoundException
import levkaantonov.com.study.recyclerview.tasks.SimpleTask
import levkaantonov.com.study.recyclerview.tasks.Task
import java.util.*
import java.util.concurrent.Callable

typealias UsersListener = (users: List<User>) -> Unit

class UserService {

    private var users = mutableListOf<User>()
    private var loaded = false
    private val listeners = mutableSetOf<UsersListener>()

    fun loadUsers(): Task<Unit> = SimpleTask<Unit>(Callable {
        Thread.sleep(2000L)
        val faker = Faker.instance()
        IMAGES.shuffle()
        users = (1..100).map {
            User(
                id = it.toLong(),
                name = faker.name().name(),
                company = faker.company().name(),
                photo = IMAGES[it % IMAGES.size]
            )
        }.toMutableList()
        loaded = true
        notifyChanges()
    })

    fun getById(id: Long): Task<UserDetails> = SimpleTask<UserDetails>(Callable {
        Thread.sleep(2000L)
        val user = users.firstOrNull { it.id == id } ?: UserNotFoundException()
        return@Callable UserDetails(
            user = user as User,
            details = Faker.instance().lorem().paragraphs(3).joinToString("\n\n")
        )
    })

    fun deleteUser(user: User): Task<Unit> = SimpleTask<Unit>(Callable {
        Thread.sleep(2000L)
        val index = users.indexOfFirst { it.id == user.id }
        if (index != -1) {
            users.removeAt(index)
            notifyChanges()
        }
    })

    fun moveUser(user: User, moveBy: Int): Task<Unit> = SimpleTask<Unit>(Callable {
        val oldIndex = users.indexOfFirst { it.id == user.id }
        if (oldIndex == -1) return@Callable
        val newIndex = oldIndex + moveBy
        if (newIndex < 0 || newIndex >= users.size) return@Callable
        Collections.swap(users, oldIndex, newIndex)
        notifyChanges()
    })

    fun addListener(listener: UsersListener) {
        listeners.add(listener)
        if (loaded) {
            listener.invoke(users)
        }
    }

    fun removeListener(listener: UsersListener) {
        listeners.remove(listener)
    }

    private fun notifyChanges() {
        if(!loaded) {
            return
        }
        listeners.forEach { it.invoke(users) }
    }

    companion object {
        private val IMAGES = mutableListOf(
            "https://images.unsplash.com/photo-1600267185393-e158a98703de?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NjQ0&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1579710039144-85d6bdffddc9?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0Njk1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0ODE0&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1620252655460-080dbec533ca?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzQ1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1613679074971-91fc27180061?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzUz&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1485795959911-ea5ebf41b6ae?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzU4&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1545996124-0501ebae84d0?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzY1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/flagged/photo-1568225061049-70fb3006b5be?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0Nzcy&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1567186937675-a5131c8a89ea?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0ODYx&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1546456073-92b9f0a8d413?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0ODY1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800"
        )
    }
}