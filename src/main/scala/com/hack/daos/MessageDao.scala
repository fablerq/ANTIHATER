package com.hack.daos

import com.hack.models.{ApiKeyModel, MessageModel}
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.{Completed, MongoCollection}
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.result.DeleteResult

import scala.concurrent.Future

trait MessageDao {
  def getAll: Future[Seq[MessageModel]]
  def getSingle(id: ObjectId): Future[MessageModel]
  def create(key: MessageModel): Future[Completed]
  def deleteSingle(id: ObjectId): Future[DeleteResult]
}

class MessageDaoImpl(messageCollection: MongoCollection[MessageModel]) extends MessageDao {

  def getAll =
    messageCollection.find().toFuture()

  def getSingle(id: ObjectId): Future[MessageModel] =
    messageCollection.find(equal("_id", id))
      .first()
      .toFuture()

  def create(key: MessageModel): Future[Completed] =
    messageCollection.insertOne(key).toFuture()

  def deleteSingle(id: ObjectId): Future[DeleteResult] =
    messageCollection.deleteOne(Document("_id" -> id)).toFuture()


}
