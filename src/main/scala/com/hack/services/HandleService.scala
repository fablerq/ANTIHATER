package com.hack.services

import com.hack.models._

import sys.process._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

trait HandleService {
  def handleRequest(request: RequestModel): Future[Either[ServiceResponse,
    List[MessageResponseModel]]]
  def calcClassifier(messages: List[String]): List[MessageCalcRequestModel]
}


class HandleServiceImpl(authService: AuthService,
                        messageService: MessageService) extends HandleService {
  def handleRequest(request: RequestModel): Future[Either[ServiceResponse,
                                                          List[MessageResponseModel]
                                                          ]] = {
    authService.checkKey(request.apiKey.keyTitle, KeyPermissions.STANDARD).map {
      case x if x.status == StatusType.Success =>
        val messages: List[String] = request.messages.map(x => x.body)
        val data = calcClassifier(messages)
        val response: List[MessageResponseModel] =
          for {
            requestMessage <- request.messages
            responseMessage <- data
            if requestMessage.body == responseMessage.body
          } yield MessageResponseModel(responseMessage.classifier,
            responseMessage.body,
            requestMessage.extraSettings)
          data.map { message =>
            messageService.addMessage(request.apiKey.keyTitle,
                                      message.body,
                                      message.classifier)
          }
        Right(response)
      case x => Left(x)
    }
  }

  def calcClassifier(messages: List[String]): List[MessageCalcRequestModel] = {
    //write to file some data
    //
    s"python hackathon.py".!
    val filename = "data2.txt"
    var list: List[MessageCalcRequestModel] = List()
    for (line <- Source.fromFile(filename).getLines) {
      val data = line.split(" ")
      val message = MessageCalcRequestModel(data(0).toInt, data(1))
      list = list :+ message
    }
    list
  }
}
