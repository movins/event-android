package com.github.movins.event.kt;

import android.util.Log
import com.whale.sing.log.WLog

typealias EmptyListener = () -> Unit
inline fun EmptyListener.tryException(finally: EmptyListener = {}) {
    try {
        this()
    } catch (e: Exception) {
        WLog.e("HH", "${this.javaClass.name} : " + e.message)
    } finally {
        finally()
    }
}

fun Throwable.upload() {
    WLog.e("upload_throwable", Log.getStackTraceString(this))
}

fun String?.e(tag: String = "HH") = WLog.e(tag, this)
fun String?.w(tag: String = "HH") = WLog.w(tag, this)
fun String?.e(obj: Any) = WLog.e(obj.javaClass.simpleName, this)
fun String?.i(tag: String = "HH") = WLog.i(tag, this)

//只能用来返回非null对象,先.yes{}后.no{}
inline fun <T> Boolean.yes(crossinline yes: () -> T): T? = if (this) yes() else null

inline fun <T> T?.no(crossinline no: () -> T): T = this ?: no()

//类似三目运算符
infix fun <T> Boolean?.yes(returns: T): T? = if (this == true) returns else null

infix fun <T> T?.no(returns: T): T = this ?: returns
