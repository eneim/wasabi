package tel.schich.rfc5988.parsing

import tel.schich.rfc5988.parsing.Result.Error
import tel.schich.rfc5988.parsing.Result.Success

sealed interface Result<out T> {

  val rest: StringSlice

  data class Success<T>(
    val value: T,
    override val rest: StringSlice
  ) : Result<T>

  data class Error(
    val message: String,
    override val rest: StringSlice
  ) : Result<Nothing>
}

fun <T : Any> Result<T>.getOrNull(): T? = when (this) {
  is Success -> value
  is Error -> null
}
