package com.hack.daos

import com.hack.models.{ApiKeyModel, MessageModel}
import slick.dbio.{Effect, NoStream}

import com.hack.models.KeyPermissions
import slick.driver.PostgresDriver.api._
import com.hack.models.KeyPermissions.KeyPermissions
import slick.sql.FixedSqlAction

import scala.concurrent.Future

trait MessageDao {
  def getAll: Future[Seq[MessageModel]]
  def getSingle(id: Long): Future[Option[MessageModel]]
  def deleteSingle(id: Long) : Future[Int]
  def create(key: MessageModel): Future[Unit]
  def createSchema(): Future[Unit]
  def dropSchema(): FixedSqlAction[Unit, NoStream, Effect.Schema]
}

class MessageDaoImpl(messageDb: Database) extends MessageDao {
  implicit val fuelEnumMapper =
    MappedColumnType.base[KeyPermissions, String](_.toString, KeyPermissions.withName)

  class Messages(tag: Tag) extends Table[MessageModel](tag, "messages") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def classifier = column[Int]("classifier")
    def body = column[String]("body")

    def * =
      (id, classifier, body) <> (MessageModel.tupled, MessageModel.unapply)
  }

  val messages = TableQuery[Messages]

  def getAll: Future[Seq[MessageModel]] = {
    val query = messages.sortBy(_.id).result
    messageDb.run(query)
  }

  def getSingle(id: Long): Future[Option[MessageModel]] = {
    val query = messages.filter(_.id === id).result.headOption
    messageDb.run(query)
  }

  def deleteSingle(id: Long): Future[Int] = {
    val query =  messages.filter(_.id === id).delete
    messageDb.run(query)
  }

  def create(message: MessageModel): Future[Unit] = {
    val query = DBIO.seq(messages += message)
    messageDb.run(query)
  }

  def createSchema(): Future[Unit] =
    messageDb.run(messages.schema.create)

  def dropSchema(): FixedSqlAction[Unit, NoStream, Effect.Schema] =
    messages.schema.drop
}
