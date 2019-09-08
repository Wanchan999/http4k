package tutorials.self_documenting_apis_with_openapi

import org.http4k.contract.ContractRoute
import org.http4k.contract.Tag
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.ContentType.Companion.TEXT_PLAIN
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.Path
import org.http4k.lens.Query
import org.http4k.lens.int

data class Drink(val name: String) {
    init {
        require(name.isNotEmpty())
    }
}

object Greetings {
    private val age = Query.int().map(::Age).required("age", "Your age")
    private val favouriteDrink = Query.map(::Drink).optional("drink", "Your favourite beverage")

    private fun handler(name: String): HttpHandler = { req: Request ->
        val drinkToOffer: Drink? = favouriteDrink(req)
        val beverage: String = drinkToOffer?.name ?: if (age(req).value >= 18) "beer" else "lemonade"
        Response(OK).body("Hello $name, would you like some $beverage?")
    }

    operator fun invoke(): ContractRoute = "/greet" / Path.of("name", "Your name") meta {
        summary = "Send greetings"
        description = "Greets the stupid human by offering them a beverage suitable for their age"
        tags += Tag("query")
        queries += favouriteDrink
        queries += age
        produces += TEXT_PLAIN
        returning(OK)
    } bindContract GET to ::handler
}
