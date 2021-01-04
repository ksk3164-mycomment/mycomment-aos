package kr.beimsupicures.mycomment.api.loaders.base

abstract class BaseLoader<T : Any> {

    var page: Int = 0
    var isLoading: Boolean = false
    var isLast: Boolean = false
    lateinit var api: T

    open fun reset() {
        isLast = false
        page = 0
    }

    fun available(): Boolean {
        return !isLoading && !isLast
    }
}