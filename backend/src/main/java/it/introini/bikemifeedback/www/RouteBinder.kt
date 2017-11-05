package it.introini.bikemifeedback.www

import com.google.inject.Inject
import com.google.inject.Injector
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Vertx
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CookieHandler
import io.vertx.ext.web.handler.LoggerHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.impl.Utils
import it.introini.bikemifeedback.www.handlers.ApplicationPageHandler
import org.pmw.tinylog.Logger


class RouteBinder @Inject constructor(         val vertx: Vertx,
                                       private val router: Router,
                                       private val injector: Injector ) {

    private val base = "/bikemifeedback"
    private val apiRoot = "$base/api/v1"

    fun bindRoutes() {
        val routes = Route.values()
        router.route("$apiRoot/*").handler(BodyHandler.create())
        router.route("$apiRoot/*").handler(LoggerHandler.create())
        router.route("$apiRoot/*").handler(CookieHandler.create())
        routes.forEach {
            router.route(it.method, "$apiRoot${it.endpoint}").blockingHandler(injector.getInstance(it.handler))
        }
        router.route("$base/static/*").handler(StaticHandler.create("static").setCachingEnabled(false))
        router.get("/bmf*").handler(ApplicationPageHandler)
        router.route("$apiRoot/*").failureHandler {
            Logger.error(it.failure(), "Unknown exception")
            it.response().statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR.code()
            it.response().end()
        }
    }

}