package com.hack.services

import com.hack.models._
import com.hack.daos.MessageDao
import com.hack.models.KeyPermissions.KeyPermissions

import sys.process._
import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MessageService {
  def getAll(key: String): Future[Either[ServiceResponse, Seq[MessageModel]]]
  def getSingle(id: Long, key: String): Future[Either[ServiceResponse, MessageModel]]
  def addMessage(key: String,
                 body: String,
                 classifier: Int): Future[ServiceResponse]
  def deleteMessage(key: String, id: Long): Future[ServiceResponse]
}

class MessageServiceImpl(messageDao: MessageDao,
                         authService: AuthService) extends MessageService {

  def getAll(key: String): Future[Either[ServiceResponse, Seq[MessageModel]]] = {
    authService.checkKey(key, KeyPermissions.ADMIN).flatMap {
      case x if x.status == StatusType.Success =>
        messageDao.getAll.map {
          case x: Seq[MessageModel] if x.nonEmpty => Right(x)
          case _ =>
            Left(ServiceResponse(StatusType.Failure, Some("Error. There are no messages")))
        }
      case x =>
        Future.successful(Left(x))
    }
  }

  def getSingle(id: Long, key: String): Future[Either[ServiceResponse, MessageModel]] = {
    authService.checkKey(key, KeyPermissions.ADMIN).flatMap {
      case x if x.status == StatusType.Success =>
        messageDao.getSingle(id).map {
          case Some(x) => Right(x)
          case None =>
            Left(ServiceResponse(StatusType.Failure, None))
        }
      case x =>
        Future.successful(Left(x))
    }
  }

  def addMessage(key: String,
                 body: String,
                 classifier: Int): Future[ServiceResponse] = {
    authService.checkKey(key, KeyPermissions.MODERATOR).flatMap {
      case x if x.status == StatusType.Success =>
        messageDao.getAll.flatMap { messages =>
          val count: Long = messages.length + 1
          val newMessage = MessageModel(count, classifier, body)
          messageDao.create(newMessage).map { x =>
            ServiceResponse(StatusType.Success, None)
          }
        }
      case x => Future.successful(x)
    }
  }

  def deleteMessage(key: String, id: Long): Future[ServiceResponse] = {
    authService.checkKey(key, KeyPermissions.ADMIN).flatMap {
      case x if x.status == StatusType.Success =>
        messageDao.getSingle(id).flatMap {
          case x if x.isInstanceOf[Option[MessageModel]] =>
            messageDao.deleteSingle(id).map { message =>
              ServiceResponse(StatusType.Success, None)
            }
          case _ =>
            Future.successful(ServiceResponse(StatusType.Failure,
                                              Some("Error. Not found")))
        }
      case x => Future.successful(x)
    }
  }

}
