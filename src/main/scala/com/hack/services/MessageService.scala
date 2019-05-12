package com.hack.services

import com.hack.models._
import com.hack.daos.MessageDao

import org.mongodb.scala.bson.ObjectId

import sys.process._
import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MessageService {
  def getAll(key: String): Future[Either[ServiceResponse, Seq[MessageModel]]]
  def getSingle(id: String, key: String): Future[Either[ServiceResponse, MessageModel]]
  def addMessage(key: String,
                 body: String,
                 classifier: Int): Future[ServiceResponse]
  def deleteMessage(key: String, id: String): Future[ServiceResponse]
}

class MessageServiceImpl(messageDao: MessageDao,
                         authService: AuthService) extends MessageService {

  def getAll(key: String): Future[Either[ServiceResponse, Seq[MessageModel]]] = {
    authService.checkKey(key, "admin").flatMap {
      case x if x.status =>
        messageDao.getAll.map {
          case x: Seq[MessageModel] if x.nonEmpty => Right(x)
          case _ =>
            Left(ServiceResponse(false, Some("Error. There are no messages")))
        }
      case x =>
        Future.successful(Left(x))
    }
  }

  def getSingle(id: String, key: String): Future[Either[ServiceResponse, MessageModel]] = {
    val trueId = new ObjectId(id)
    authService.checkKey(key, "admin").flatMap {
      case x if x.status =>
        messageDao.getSingle(trueId).map {
          case x if x != null => Right(x)
          case _ =>
            Left(ServiceResponse(false, None))
        }
      case x =>
        Future.successful(Left(x))
    }
  }

  def addMessage(key: String,
                 body: String,
                 classifier: Int): Future[ServiceResponse] = {
    authService.checkKey(key, "moderator").flatMap {
      case x if x.status =>
          val newMessage = MessageModel(new ObjectId(), classifier, body)
          messageDao.create(newMessage).map { x =>
            ServiceResponse(true, None)
          }
      case x => Future.successful(x)
    }
  }

  def deleteMessage(key: String, id: String): Future[ServiceResponse] = {
    val trueId = new ObjectId(id)
    authService.checkKey(key, "admin").flatMap {
      case x if x.status =>
        messageDao.getSingle(trueId).flatMap {
          case x if x.isInstanceOf[MessageModel] =>
            messageDao.deleteSingle(trueId).map { message =>
              ServiceResponse(true, None)
            }
          case _ =>
            Future.successful(ServiceResponse(false,
                                              Some("Error. Not found")))
        }
      case x => Future.successful(x)
    }
  }

}
