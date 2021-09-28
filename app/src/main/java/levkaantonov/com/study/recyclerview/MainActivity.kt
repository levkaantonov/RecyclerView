package levkaantonov.com.study.recyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import levkaantonov.com.study.recyclerview.databinding.ActivityMainBinding
import levkaantonov.com.study.recyclerview.model.User
import levkaantonov.com.study.recyclerview.model.UserService
import levkaantonov.com.study.recyclerview.model.UsersListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var adapter: UsersAdapter? = null
    private val usersService: UserService
        get() = (application as App).userService
    private val listener: UsersListener = {
        adapter?.users = it
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UsersAdapter(object : UserActionListener {
            override fun onUserMove(user: User, moveBy: Int) {
                usersService.moveUser(user, moveBy)
            }

            override fun onUserDelete(user: User) {
                usersService.deleteUser(user)
            }

            override fun onUserDetails(user: User) {
                Toast.makeText(this@MainActivity, "User ${user.name}", Toast.LENGTH_SHORT).show()
            }
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        usersService.addListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        usersService.removeListener(listener)
    }
}