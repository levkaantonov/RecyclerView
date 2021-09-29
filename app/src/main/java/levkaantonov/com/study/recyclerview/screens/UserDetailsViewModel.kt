package levkaantonov.com.study.recyclerview.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import levkaantonov.com.study.recyclerview.R
import levkaantonov.com.study.recyclerview.model.UserDetails
import levkaantonov.com.study.recyclerview.model.UserService
import levkaantonov.com.study.recyclerview.tasks.EmptyResult
import levkaantonov.com.study.recyclerview.tasks.PendingResult
import levkaantonov.com.study.recyclerview.tasks.Result
import levkaantonov.com.study.recyclerview.tasks.SuccessResult

class UserDetailsViewModel(
    private val userService: UserService
) : BaseViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private val _actionShowToast = MutableLiveData<Event<Int>>()
    val actionShowToast: LiveData<Event<Int>> = _actionShowToast

    private val _actionGoBack = MutableLiveData<Event<Unit>>()
    val actionGoBack: LiveData<Event<Unit>> = _actionGoBack

    private val currentState: State get() = state.value!!

    init {
        _state.value = State(
            userDetailsResult = EmptyResult(),
            deletingInProgress = false
        )
    }

    fun loadUser(userId: Long) {
        if (currentState.userDetailsResult !is EmptyResult) return

        _state.value = currentState.copy(userDetailsResult = PendingResult())
        userService.getById(userId)
            .onSuccess {
                _state.value = currentState.copy(userDetailsResult = SuccessResult(it))
            }
            .onError {
                _actionShowToast.value = Event(R.string.error_loading_of_user_details)
                _actionGoBack.value = Event(Unit)
            }
            .autoCancel()
    }

    fun deleteUser() {
        val userDetailsResult = currentState.userDetailsResult
        if (userDetailsResult !is SuccessResult) return
        _state.value = currentState.copy(deletingInProgress = true)
        userService.deleteUser(userDetailsResult.data.user)
            .onSuccess {
                _actionShowToast.value = Event(R.string.success_deleting_user)
                _actionGoBack.value = Event(Unit)
            }
            .onError {
                _state.value = currentState.copy(deletingInProgress = false)
                _actionShowToast.value = Event(R.string.error_deleting_user)
            }
            .autoCancel()
    }

    data class State(
        val userDetailsResult: Result<UserDetails>,
        private val deletingInProgress: Boolean
    ) {
        val showContent: Boolean get() = userDetailsResult is SuccessResult
        val showProgress: Boolean get() = userDetailsResult is PendingResult || deletingInProgress
        val enableDeleteButton: Boolean get() = !deletingInProgress
    }
}