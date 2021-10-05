package levkaantonov.com.study.recyclerview.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import levkaantonov.com.study.recyclerview.R
import levkaantonov.com.study.recyclerview.UserActionListener
import levkaantonov.com.study.recyclerview.model.User
import levkaantonov.com.study.recyclerview.model.UserService
import levkaantonov.com.study.recyclerview.model.UsersListener
import levkaantonov.com.study.recyclerview.tasks.*

data class UserListItem(
    val id: Long,
    val user: User,
    val isInProgress: Boolean
)

class UsersListViewModel(
    private val userService: UserService
) : BaseViewModel(), UserActionListener {

    private val _users = MutableLiveData<Result<List<UserListItem>>>()
    val users: LiveData<Result<List<UserListItem>>> = _users

    private val _actionShowDetails = MutableLiveData<Event<User>>()
    val actionShowDetails: LiveData<Event<User>> = _actionShowDetails

    private val _actionShowToast = MutableLiveData<Event<Int>>()
    val actionShowToast: LiveData<Event<Int>> = _actionShowToast

    private val userIdsInProgress = mutableListOf<Long>()
    private var usersResult: Result<List<User>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }
    private val usersListener: UsersListener = {
        usersResult = if (it.isEmpty())
            EmptyResult()
        else
            SuccessResult(it)
    }

    init {
        userService.addListener(usersListener)
        loadUsers()
    }

    private fun loadUsers() {
        usersResult = PendingResult()
        userService.loadUsers()
            .onError { usersResult = ErrorResult(it) }
            .autoCancel()
    }

    override fun onUserMove(user: User, moveBy: Int) {
        if (isInProgress(user)) return
        addProgressTo(user)
        userService.moveUser(user, moveBy)
            .onSuccess { removeProgressFrom(user) }
            .onError {
                removeProgressFrom(user)
                _actionShowToast.value = Event(R.string.cant_move_user)
            }
            .autoCancel()
    }

    override fun onUserDelete(user: User) {
        if (isInProgress(user)) {
            return
        }
        addProgressTo(user)
        userService
            .deleteUser(user)
            .onSuccess { removeProgressFrom(user) }
            .onError {
                removeProgressFrom(user)
                _actionShowToast.value = Event(R.string.error_deleting_user)
            }
            .autoCancel()
    }

    override fun onUserFire(user: User) {
        if (isInProgress(user)) {
            return
        }
        addProgressTo(user)
        userService
            .fireUser(user)
            .onSuccess { removeProgressFrom(user) }
            .onError {
                removeProgressFrom(user)
                _actionShowToast.value = Event(R.string.cant_fire)
            }.autoCancel()
    }

    override fun onUserDetails(user: User) {
        _actionShowDetails.value = Event(user)
    }

    private fun addProgressTo(user: User) {
        userIdsInProgress.add(user.id)
        notifyUpdates()
    }

    private fun removeProgressFrom(user: User) {
        userIdsInProgress.remove(user.id)
        notifyUpdates()
    }

    private fun isInProgress(user: User): Boolean {
        return userIdsInProgress.contains(user.id)
    }

    private fun notifyUpdates() {
        _users.postValue(usersResult.map { users ->
            users.map { user ->
                UserListItem(user.id, user, isInProgress(user))
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        userService.removeListener(usersListener)
    }
}