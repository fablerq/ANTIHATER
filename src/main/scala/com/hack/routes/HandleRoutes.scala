package com.hack.routes

import akka.http.scaladsl.server.Directives.{as, complete, entity, path, post}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.hack.models.RequestModel
import com.hack.services.HandleService
import com.hack.configs.Json4sSupport._

class HandleRoutes(handleService: HandleService) {
  def route = cors() {
    path("handle") {
      post {
        entity(as[RequestModel]) { request =>
          complete(handleService.handleRequest(request))
        }
      }
    }
  }
}
