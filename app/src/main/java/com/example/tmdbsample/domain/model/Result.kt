package com.example.tmdbsample.domain.model

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class Result<out T>(val status: Status, val data: T?, val error: Throwable?) {

    fun <R> map(mapping: (T?) -> R): Result<R> {
        return Result(this.status, mapping(this.data), this.error)
    }

    fun <R> mapData(mapping: (T) -> R): Result<R> {
        return Result(this.status, data?.let { mapping(it) }, this.error)
    }

    companion object {
        fun <T> success(data: T?): Result<T> {
            return Result(Status.SUCCESS, data, null)
        }

        fun <T> error(error: Throwable, data: T? = null): Result<T> {
            return Result(Status.ERROR, data, error)
        }

        fun <T> loading(data: T? = null): Result<T> {
            return Result(Status.LOADING, data, null)
        }
    }
}

fun <T> Result<T>?.isLoading(): Boolean = this?.status == Status.LOADING
fun <T> Result<T>?.isSuccess(): Boolean = this?.status == Status.SUCCESS
fun <T> Result<T>?.isError(): Boolean = this?.status == Status.ERROR

fun <T> Result<T>?.dataIsNull(): Boolean = this?.data == null

fun <T> Result<T>?.doOnError(action: (error: Throwable) -> Unit) {
    this?.error?.let { action(it) }
}

fun <T> Result<T>?.doOnData(action: (data: T) -> Unit) {
    this?.data?.let { action(it) }
}
