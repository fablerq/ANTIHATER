package com.hack.configs

import slick.driver.PostgresDriver.api._
import com.hack.configs.{Configs => C}

object Postgres {
  lazy val db = Database.forURL(
    url    = s"jdbc:postgresql://${C.pgHost}:${C.pgPort}/${C.pgDBName}",
    driver = C.pgDriver
  )

  implicit val session: Session = db.createSession()

}