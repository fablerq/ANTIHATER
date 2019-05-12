package com.hack.services

import akka.http.scaladsl.server.Directives._
import com.hack.daos.{AuthDao, AuthDaoImpl, MessageDao, MessageDaoImpl}
import com.hack.routes.{AuthRoutes, HandleRoutes, MessageRoutes}
import org.mongodb.scala.MongoDatabase


class HttpService(database: MongoDatabase) {

  val authDao = new AuthDaoImpl(database.getCollection("keys"))

  val authService =
    new AuthServiceImpl(authDao)

  val authRoutes =
    new AuthRoutes(authService)

  val messageDao = new MessageDaoImpl(database.getCollection("messages"))

  val messageService =
    new MessageServiceImpl(
      messageDao,
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
