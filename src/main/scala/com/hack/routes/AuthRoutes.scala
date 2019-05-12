package com.hack.routes

import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.hack.models.RequestModel
import com.hack.services.AuthService
import com.hack.configs.Json4sSupport._

class AuthRoutes(authService: AuthService) {
  def route = cors() {
    path("auth") {
      parameters("key".as[String]) {
        key =>
          post {
            complete(authService.getAll(key))
          }
      } ~
      parameters("key".as[String], "title".as[String]) {
        (key, title) =>
          get {
            complete(authService.getSingle(title, key))
          }
          delete {
            complete(authService.deleteKey(key, title))
          }
      } ~
        parameters("key".as[String], "permission".as[String], "dayLimit".as[Int]) {
          (key, permission, dayLimit) =>
            post {
              complete(authService.createKey(permission, dayLimit, key))
            }
        }
    }
  }
}
