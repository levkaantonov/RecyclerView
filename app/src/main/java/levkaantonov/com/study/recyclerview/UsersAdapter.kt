package levkaantonov.com.study.recyclerview

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.CustomPopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import levkaantonov.com.study.recyclerview.databinding.ItemUserBinding
import levkaantonov.com.study.recyclerview.model.User
import levkaantonov.com.study.recyclerview.screens.UserListItem

interface UserActionListener {
    fun onUserMove(user: User, moveBy: Int)
    fun onUserDelete(user: User)
    fun onUserDetails(user: User)
    fun onUserFire(user: User)
}

class UsersAdapter(private val actionListener: UserActionListener) :
    RecyclerView.Adapter<UserViewHolder>(), View.OnClickListener {

    var userListItems: List<UserListItem> = emptyList()
        set(value) {
            val diffResult = DiffUtil.calculateDiff(UsersDiffCallback(field, value))
            diffResult.dispatchUpdatesTo(this)
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        binding.ivMoreButton.setOnClickListener(this)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val userListItem = userListItems[position]
        holder.bind(userListItem, this)
    }

    override fun getItemCount(): Int = userListItems.size

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
        val popupMenu = CustomPopupMenu(v.context, v)
        val context = v.context
        val user = v.tag as User
        val position = userListItems.indexOfFirst { it.user.id == user.id }

        popupMenu.apply {
            with(menu) {
                add(0, ID_MOVE_UP, Menu.NONE, context.getString(R.string.popup_menu_move_up))
                    .apply {
                        isEnabled = position > 0
                        setIcon(R.drawable.ic_action_up)
                    }
                add(0, ID_MOVE_DOWN, Menu.NONE, context.getString(R.string.popup_menu_move_down))
                    .apply {
                        isEnabled = position < userListItems.size - 1
                        setIcon(R.drawable.ic_action_down)
                    }
                add(0, ID_REMOVE, Menu.NONE, context.getString(R.string.popup_menu_remove)).apply {
                    setIcon(R.drawable.ic_action_delete)
                }
                if (user.company.isNotBlank()) {
                    add(0, ID_FIRE, Menu.NONE, context.getString(R.string.fire)).apply {
                        setIcon(R.drawable.ic_action_fire)
                    }
                }
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
                    ID_FIRE -> {
                        actionListener.onUserFire(user)
                        true
                    }
                    else -> true
                }
            }
            show()
        }
    }

    companion object {
        private const val ID_MOVE_UP = 1
        private const val ID_MOVE_DOWN = 2
        private const val ID_REMOVE = 3
        private const val ID_FIRE = 4
    }
}

class UserViewHolder(
    private val binding: ItemUserBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        userListItem: UserListItem,
        actionListener: View.OnClickListener
    ) {
        val user = userListItem.user
        binding.ivMoreButton.tag = user
        this.itemView.tag = user
        val context = this.itemView.context

        with(binding) {
            if (userListItem.isInProgress) {
                ivMoreButton.visibility = View.INVISIBLE
                progressBar.visibility = View.VISIBLE
                binding.root.setOnClickListener(null)
            } else {
                ivMoreButton.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                binding.root.setOnClickListener(actionListener)
            }

            tvUserName.text = user.name
            tvUserCompany.text =
                if (user.company.isNotBlank()) user.company
                else context.getString(R.string.unemployed)
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

class UsersDiffCallback(
    private val oldList: List<UserListItem>,
    private val newList: List<UserListItem>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem == newItem
    }
}
