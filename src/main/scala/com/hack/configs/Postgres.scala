package com.hack.configs

import slick.driver.PostgresDriver.api._
import com.hack.configs.{Configs => C}

object Postgres {
  lazy val db = Database.forConfig("db.default")

  //implicit val session: Session = db.createSession()

}