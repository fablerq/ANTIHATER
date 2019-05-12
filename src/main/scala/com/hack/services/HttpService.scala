package com.hack.services

import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import com.hack.daos.{AuthDao, AuthDaoImpl, MessageDaoImpl}
import com.hack.routes.{AuthRoutes, HandleRoutes, MessageRoutes}
import slick.driver.PostgresDriver.api._

class HttpService(db: Database) {

  val authService =
    new AuthServiceImpl(
      new AuthDaoImpl(db)
    )

  val authRoutes =
    new AuthRoutes(authService)

  val messageService =
    new MessageServiceImpl(
      new MessageDaoImpl(db),
      authService
    )

  val messageRoutes =
    new MessageRoutes(messageService)

  val handleRoutes =
    new HandleRoutes(
      new HandleServiceImpl(
        authService,
        messageService
      )
    )

  val routes =
    pathPrefix("api") {
      handleRoutes.route ~ messageRoutes.route ~ authRoutes.route
    }
}
