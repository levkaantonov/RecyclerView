package levkaantonov.com.study.recyclerview

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import levkaantonov.com.study.recyclerview.databinding.ItemUserBinding
import levkaantonov.com.study.recyclerview.model.User


interface UserActionListener {

    fun onUserMove(user: User, moveBy: Int)

    fun onUserDelete(user: User)

    fun onUserDetails(user: User)
}

class UsersAdapter(private val actionListener: UserActionListener) :
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>(), View.OnClickListener {

    var users: List<User> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onClick(v: View) {
        val user = v.tag as User
        when (v.id) {
            R.id.ivMoreButton -> {
                showPopupMenu(v)
            }
            else -> {
                actionListener.onUserDetails(user)
            }
        }
    }

    private fun showPopupMenu(v: View) {
        val popupMenu = PopupMenu(v.context, v)
        val context = v.context
        val user = v.tag as User
        val position = users.indexOfFirst { it.id == user.id }

        popupMenu.apply {
            menu.add(0, ID_MOVE_UP, Menu.NONE, context.getString(R.string.popup_menu_move_up))
                .apply {
                    isEnabled = position > 0
                }
            menu.add(
                0,
                ID_MOVE_DOWN,
                Menu.NONE,
                context.getString(R.string.popup_menu_move_down)
            ).apply {
                isEnabled = position < users.size - 1
            }
            menu.add(
                0,
                ID_REMOVE,
                Menu.NONE,
                context.getString(R.string.popup_menu_remove)
            )
                .apply {

                }

            setOnMenuItemClickListener {
                when (it.itemId) {
                    ID_MOVE_UP -> {
                        actionListener.onUserMove(user, -1)
                        true
                    }
                    ID_MOVE_DOWN -> {
                        actionListener.onUserMove(user, 1)
                        true
                    }
                    ID_REMOVE -> {
                        actionListener.onUserDelete(user)
                        true
                    }
                    else -> true
                }
            }

            show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)
        binding.ivMoreButton.setOnClickListener(this)

        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = users.size

    class UserViewHolder(
        private val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            this.itemView.tag = user
            with(binding) {
                ivMoreButton.tag = user
                tvUserName.text = user.name
                tvUserCompany.text = user.company
                if (user.photo.isNotBlank()) {
                    Glide.with(ivPhoto.context)
                        .load(user.photo)
                        .circleCrop()
                        .placeholder(R.drawable.ic_user_avatar)
                        .into(ivPhoto)
                } else {
                    Glide.with(ivPhoto.context)
                        .clear(ivPhoto)
                    ivPhoto.setImageResource(R.drawable.ic_user_avatar)
                }
            }
        }
    }

    companion object {
        private const val ID_MOVE_UP = 1
        private const val ID_MOVE_DOWN = 2
        private const val ID_REMOVE = 3
    }
}