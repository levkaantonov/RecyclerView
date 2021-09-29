package levkaantonov.com.study.recyclerview

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import levkaantonov.com.study.recyclerview.databinding.ActivityMainBinding
import levkaantonov.com.study.recyclerview.model.User
import levkaantonov.com.study.recyclerview.screens.UserDetailsFragment
import levkaantonov.com.study.recyclerview.screens.UsersListFragment

class MainActivity : AppCompatActivity(), Navigator {
    private lateinit var binding: ActivityMainBinding

    private val actions = mutableListOf<() -> Unit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(binding.fragmentContainerView.id, UsersListFragment())
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        actions.forEach { it() }
        actions.clear()
    }

    private fun runWhenActive(action: () -> Unit) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            action()
        } else {
            actions += action
        }
    }

    override fun showDetails(user: User) {
        runWhenActive {
            supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(binding.fragmentContainerView.id, UserDetailsFragment.newInstance(user.id))
                .commit()
        }
    }

    override fun goBack() {
        runWhenActive { onBackPressed() }
    }

    override fun toast(messagesRes: Int) {
        Toast.makeText(this, messagesRes, Toast.LENGTH_SHORT).show()
    }
}