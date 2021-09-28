package levkaantonov.com.study.recyclerview

import android.app.Application
import levkaantonov.com.study.recyclerview.model.UserService

class App : Application() {

    val userService = UserService()
}