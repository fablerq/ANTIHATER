package com.hack.routes

import akka.http.scaladsl.server.Directives._
import com.hack.configs.Json4sSupport._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import com.hack.services.MessageService
import org.mongodb.scala.bson.ObjectId

class MessageRoutes(messageService: MessageService) {
  def route = cors() {
    path("messages") {
      parameters("key".as[String]) {
        key =>
          post {
            complete(messageService.getAll(key))
          }
      } ~
        parameters("key".as[String], "id".as[String]) {
          (key, id) =>
            get {
              complete(messageService.getSingle(id, key))
            }
            delete {
              complete(messageService.deleteMessage(key, id))
            }
        }
    }
  }
}