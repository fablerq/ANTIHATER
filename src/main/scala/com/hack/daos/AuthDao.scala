package com.hack.daos

import com.hack.models.ApiKeyModel
import org.mongodb.scala.{Completed, MongoCollection}
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.result.DeleteResult
import org.mongodb.scala.model.Filters.equal
import scala.concurrent.Future

trait AuthDao {
  def getAll: Future[Seq[ApiKeyModel]]
  def getSingle(title: String): Future[ApiKeyModel]
  def create(key: ApiKeyModel): Future[Completed]
  def deleteSingle(title: String): Future[DeleteResult]
}

class AuthDaoImpl(authCollection: MongoCollection[ApiKeyModel]) extends AuthDao {

  def getAll =
    authCollection.find().toFuture()

  def getSingle(title: String): Future[ApiKeyModel] = {
    println("q"+title)
    authCollection.find(equal("keyTitle", title))
      .first()
      .toFuture()
  }


  def create(key: ApiKeyModel): Future[Completed] =
    authCollection.insertOne(key).toFuture()

  def deleteSingle(title: String): Future[DeleteResult] =
    authCollection.deleteOne(Document("title" -> title)).toFuture()

}
