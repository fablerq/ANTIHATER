package com.hack.services

import com.hack.daos.AuthDao
import com.hack.models.KeyPermissions.KeyPermissions
import com.hack.models.{ApiKeyModel, KeyPermissions, ServiceResponse, StatusType}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.hack.models.KeyPermissions.KeyPermissions

import scala.util.Random

trait AuthService {
  def getAll(key: String): Future[Either[ServiceResponse, Seq[ApiKeyModel]]]
  def getSingle(title: String, key: String): Future[Either[ServiceResponse, ApiKeyModel]]
  def createKey(permission: String,
                dayLimit: Int,
                key: String): Future[Either[ServiceResponse, ApiKeyModel]]
  def deleteKey(key: String, title: String): Future[ServiceResponse]
  def checkKey(keyTitle: String, permission: KeyPermissions): Future[ServiceResponse]
}

class AuthServiceImpl(dao: AuthDao) extends AuthService {

  def getAll(key: String): Future[Either[ServiceResponse, Seq[ApiKeyModel]]] = {
    checkKey(key, KeyPermissions.ADMIN).flatMap {
      case x if x.status == StatusType.Success =>
        dao.getAll.map {
          case x: Seq[ApiKeyModel] if x.nonEmpty => Right(x)
          case _ =>
            Left(ServiceResponse(StatusType.Failure, Some("Error. There are nokeys")))
        }
      case x =>
        Future.successful(Left(x))
    }
  }

  def getSingle(title: String, key: String): Future[Either[ServiceResponse, ApiKeyModel]] = {
    checkKey(key, KeyPermissions.ADMIN).flatMap {
      case x if x.status == StatusType.Success =>
        dao.getSingle(title).map {
          case Some(x) => Right(x)
          case None =>
            Left(ServiceResponse(StatusType.Failure, None))
        }
      case x =>
        Future.successful(Left(x))
    }
  }


  def createKey(permission: String,
                dayLimit: Int,
                key: String): Future[Either[ServiceResponse, ApiKeyModel]] = {
    checkKey(key, KeyPermissions.MODERATOR).flatMap {
      case x if x.status == StatusType.Success =>
        dao.getAll.flatMap { elements =>
          val count: Long = elements.length + 1
          val key: String = randomKey(50)
          val exactPermission: KeyPermissions = permission match {
            case "standart" => KeyPermissions.STANDARD
            case "moderator" => KeyPermissions.MODERATOR
            case "admin" => KeyPermissions.ADMIN
          }
          val newKey = ApiKeyModel(count, key, exactPermission, 0, dayLimit)
          dao.create(newKey).flatMap { x =>
            dao.getSingle(key).map { x =>
              Right(x.get)
            }
          }
        }
      case x =>
        Future.successful(Left(ServiceResponse(StatusType.Failure,
                                          Some("Error. Not enough permissions"))))
    }
  }

  def deleteKey(key: String,
                title: String): Future[ServiceResponse] = {
    checkKey(key, KeyPermissions.ADMIN).flatMap {
      case x if x.status == StatusType.Success =>
        dao.getSingle(title).flatMap {
          case x if x.isInstanceOf[Option[ApiKeyModel]] =>
            dao.deleteSingle(title).map { message =>
              ServiceResponse(StatusType.Success, None)
            }
          case _ =>
            Future.successful(ServiceResponse(StatusType.Failure,
              Some("Error. Key not found")))
        }
      case x => Future.successful(x)
    }
  }

  def checkKey(keyTitle: String, permission: KeyPermissions): Future[ServiceResponse] = {
    dao.getSingle(keyTitle).map {
      case Some(key) =>
        key match {
          case x if permission != x.permissions =>
            ServiceResponse(StatusType.Failure, Some("Error. Not enough permissions"))
          case x if x.currentRequests > x.dayLimit =>
            ServiceResponse(StatusType.Failure, Some("Error. Day limit has ended"))
          case _ =>
            ServiceResponse(StatusType.Success, None)
        }
      case None =>
        ServiceResponse(StatusType.Failure, Some("Error. Code not found"))
    }
  }

  def randomKey(n: Int): String = {
    val alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    val size = alpha.size
    (1 to n).map(x => alpha(Random.nextInt.abs % size)).mkString
  }

}
