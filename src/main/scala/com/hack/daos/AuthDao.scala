package com.hack.daos

import com.hack.models.{ApiKeyModel, ApiKeyParamsModel, KeyPermissions}
import slick.driver.PostgresDriver.api._
import com.hack.models.KeyPermissions.KeyPermissions
import com.hack.configs.Json4sSupport._
import slick.sql.FixedSqlAction

import scala.concurrent.Future

trait AuthDao {
  def getAll: Future[Seq[ApiKeyModel]]
  def getSingle(title: String): Future[Option[ApiKeyModel]]
  def deleteSingle(title: String) : Future[Int]
  def create(key: ApiKeyModel): Future[Unit]
  def createSchema(): Future[Unit]
  def dropSchema(): FixedSqlAction[Unit, NoStream, Effect.Schema]
}

class AuthDaoImpl(authDb: Database) extends AuthDao {

  implicit val fuelEnumMapper =
    MappedColumnType.base[KeyPermissions, String](_.toString, KeyPermissions.withName)

  class Keys(tag: Tag) extends Table[ApiKeyModel](tag, "keys") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def keyTitle = column[String]("keyTitle")
    def permissions = column[KeyPermissions]("permissions")
    def currentRequests = column[Int]("currentRequests")
    def dayLimit = column[Int]("dayLimit")

    def * =
      (id, keyTitle, permissions,
        currentRequests, dayLimit) <> (ApiKeyModel.tupled, ApiKeyModel.unapply)
  }

  val apiKeys = TableQuery[Keys]

  def getAll: Future[Seq[ApiKeyModel]] = {
    val query = apiKeys.sortBy(_.id).result
    authDb.run(query)
  }

  def getSingle(title: String): Future[Option[ApiKeyModel]] = {
    val query = apiKeys.filter(_.keyTitle === title).result.headOption
    authDb.run(query)
  }

  def deleteSingle(title: String): Future[Int] = {
    val query =  apiKeys.filter(_.keyTitle === title).delete
    authDb.run(query)
  }

  def create(key: ApiKeyModel): Future[Unit] = {
    val query = DBIO.seq(apiKeys += key)
    authDb.run(query)
  }

  def createSchema(): Future[Unit] =
    authDb.run(apiKeys.schema.create)

  def dropSchema(): FixedSqlAction[Unit, NoStream, Effect.Schema] =
    apiKeys.schema.drop
}
