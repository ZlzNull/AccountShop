package com.zlz

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import io.ktor.gson.*
import io.ktor.features.*
import com.fasterxml.jackson.databind.*
import com.zlz.Api.sendMail
import io.ktor.jackson.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import java.text.DateFormat

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }

        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }


        val client = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }
    runBlocking {
        // Sample for making a HTTP Client request
        /*
        val message = client.post<JsonSampleClass> {
            url("http://127.0.0.1:8080/path/to/endpoint")
            contentType(ContentType.Application.Json)
            body = JsonSampleClass(hello = "world")
        }
        */
    }

    routing {
        get("/") {
            call.response.header("Access-Control-Allow-Origin","*")
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/html-dsl") {
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }

        get("/styles.css") {
            call.respondCss {
                body {
                    backgroundColor = Color.red
                }
                p {
                    fontSize = 2.em
                }
                rule("p.myclass") {
                    color = Color.blue
                }
            }
        }


        data class HelloWorld(val hello: String)

        options("/route"){
            call.response.header("Access-Control-Allow-Origin","*")
            call.response.header("Access-Control-Allow-Method","*")
            call.response.header("Access-Control-Allow-Headers","Content-Type")
            call.response.header("Access-Control-Max-Age","1728000")

            val map = HashMap<String,Any>()
            map["code"] = 200
            call.respond(map)
        }

        post("/route") {
            call.response.header("Access-Control-Allow-Origin","*")
            call.response.header("Access-Control-Allow-Method","*")
            call.response.header("Access-Control-Allow-Headers","Content-Type")
            call.response.header("Access-Control-Max-Age","1728000")

            val helloWorld = call.receive<HelloWorld>()

            println(helloWorld)
            val map = HashMap<String,Any>()
            map["code"] = 200
            call.respond(map)
        }

//        get("/json/gson") {
//            call.respond(mapOf("hello" to "world"))
//        }
//
//        get("/json/jackson") {
//            call.respond(mapOf("hello" to "world"))
//        }

        get("/code/{qq}"){
            call.response.header("Access-Control-Allow-Origin","*")
            val qq  = call.parameters["qq"] ?: ""
            println(qq)
            val map = HashMap<String,Any>()
            sendMail(qq)
            map["code"] = 200
            map["msg"] = "验证码发送成功"
            call.respond(map)
        }

        get("/json/{}"){
            call.response.header("Access-Control-Allow-Origin","*")
            val data = call.request.queryParameters
            println(data["json"])
        }

    }
}

data class JsonSampleClass(val hello: String)

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
