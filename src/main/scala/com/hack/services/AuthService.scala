package com.hack.services

import com.hack.daos.AuthDao
import com.hack.models.{ApiKeyModel, ServiceResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.mongodb.scala.bson.ObjectId

import scala.util.Random

trait AuthService {
  def getAll(key: String): Future[Either[ServiceResponse, Seq[ApiKeyModel]]]
  def getSingle(title: String, key: String): Future[Either[ServiceResponse, ApiKeyModel]]
  def createKey(permission: String,
                dayLimit: Int,
                key: String): Future[Either[ServiceResponse, ApiKeyModel]]
  def deleteKey(key: String, title: String): Future[ServiceResponse]
  def checkKey(keyTitle: String, permission: String): Future[ServiceResponse]
}

class AuthServiceImpl(dao: AuthDao) extends AuthService {

  def getAll(key: String): Future[Either[ServiceResponse, Seq[ApiKeyModel]]] = {
    checkKey(key, "admin").flatMap {
      case x if x.status =>
        dao.getAll.map {
          case x: Seq[ApiKeyModel] if x.nonEmpty => Right(x)
          case _ =>
            Left(ServiceResponse(false, Some("Error. There are nokeys")))
        }
      case x =>
        Future.successful(Left(x))
    }
  }

  def getSingle(title: String, key: String): Future[Either[ServiceResponse, ApiKeyModel]] = {
    checkKey(key, "admin").flatMap {
      case x if x.status =>
        dao.getSingle(title).map {
          case x if x != null => Right(x)
          case _ =>
            Left(ServiceResponse(false, Some("Error. Not found")))
        }
      case x =>
        Future.successful(Left(x))
    }
  }

  def createKey(permission: String,
                dayLimit: Int,
                key: String): Future[Either[ServiceResponse, ApiKeyModel]] = {
    checkKey(key, "moderator").flatMap {
      case x if x.status =>
        dao.getAll.flatMap { elements =>
          val key: String = randomKey(50)
          val newKey = ApiKeyModel(new ObjectId(), key, permission, 0, dayLimit)
          dao.create(newKey).flatMap { x =>
            dao.getSingle(key).map { x => Right(x) }
          }
        }
      case x => Future.successful(Left(x))
    }
  }

  def deleteKey(key: String,
                title: String): Future[ServiceResponse] = {
    checkKey(key, "admin").flatMap {
      case x if x.status =>
        dao.getSingle(title).flatMap {
          case x if x.isInstanceOf[ApiKeyModel] =>
            dao.deleteSingle(title).map { message =>
              ServiceResponse(true, None)
            }
          case _ =>
            Future.successful(ServiceResponse(false,
              Some("Error. Key not found")))
        }
      case x => Future.successful(x)
    }
  }

  def checkKey(keyTitle: String, permission: String): Future[ServiceResponse] = {
    dao.getSingle(keyTitle).map {
      case key if key != null =>
        key match {
          case x if permission == x.permissions =>
            ServiceResponse(true, None)
          case x if x.permissions == "admin" =>
            ServiceResponse(true, None)
          case x if x.permissions == "moderator" && permission == "standard" =>
            ServiceResponse(true, None)
          case x if x.currentRequests > x.dayLimit =>
            ServiceResponse(false, Some("Error. Day limit has ended"))
          case _ =>
            ServiceResponse(true, None)
        }
      case x =>
        ServiceResponse(false, Some("Error. Code not found"))
    }
  }

  def randomKey(n: Int): String = {
    val alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    val size = alpha.size
    (1 to n).map(x => alpha(Random.nextInt.abs % size)).mkString
  }

}
