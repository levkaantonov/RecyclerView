package levkaantonov.com.study.recyclerview

import levkaantonov.com.study.recyclerview.model.User

interface Navigator {

    fun showDetails(user: User)

    fun goBack()

    fun toast(messagesRes: Int)
}