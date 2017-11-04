package runtime.wrappers

external fun require(module: String): dynamic

inline fun <T> jsObject(builder: T.() -> Unit): T {
    val obj: T = js("({})")
    return obj.apply {
        builder()
    }
}

inline fun js(builder: dynamic.() -> Unit): dynamic = jsObject(builder)

@Suppress("UNUSED_VARIABLE")
fun Any.getOwnPropertyNames(): Array<String> {
    val me = this
    return js("Object.getOwnPropertyNames(me)")
}

fun toPlainObjectStripNull(me: Any): dynamic {
    val obj = js("({})")
    for (p in me.getOwnPropertyNames().filterNot { it == "__proto__" || it == "constructor" }) {
        js("if (me[p] != null) { obj[p]=me[p] }")
    }
    return obj
}

fun jsObjectOf(vararg pairs: Pair<String, dynamic>): dynamic {
    val obj: dynamic = Any()
    pairs.forEach {
        (key, value) ->
        obj[key] = value
    }
    return obj
}

fun jsObjectOf(map: Map<String, dynamic>): dynamic = jsObjectOf(*map.toList().toTypedArray())


fun jsstyle(builder: dynamic.() -> Unit): String = js(builder)

fun msToHMS(ms: Int): String {
    var seconds = ms / 1000
    seconds %= 3600
    val minutes = seconds / 60
    seconds %= 60
    return "$minutes:${if (seconds < 10) "0$seconds" else seconds}"
}