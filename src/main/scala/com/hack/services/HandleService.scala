package com.hack.services

import java.io.{BufferedWriter, File, FileWriter}

import com.hack.models._
import net.liftweb.json.{JsonAST, _}
import sys.process._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

trait HandleService {
  def handleRequest(request: RequestModel): Future[Either[ServiceResponse,
    List[MessageResponseModel]]]
  def writeToFile(messages: List[String]): Unit
  def calcClassifier(messages: List[String]): List[MessageCalcRequestModel]
}


class HandleServiceImpl(authService: AuthService,
                        messageService: MessageService) extends HandleService {
  def handleRequest(request: RequestModel): Future[Either[ServiceResponse,
                                                          List[MessageResponseModel]
                                                          ]] = {
    authService.checkKey(request.apiKey, "standard").map {
      case x if x.status =>
        val messages: List[String] = request.messages.map(x => x.body)
        writeToFile(messages)
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
            messageService.addMessage(request.apiKey,
                                      message.body,
                                      message.classifier)
          }
        Right(response)
      case x => Left(x)
    }
  }

  def writeToFile(messages: List[String]): Unit = {
    val file = new File("src/main/scala/data/data.txt")
    val bw = new BufferedWriter(new FileWriter(file))
    messages.map { x =>
      bw.write(x)
      bw.newLine()
    }
    bw.close()
  }

  def calcClassifier(messages: List[String]): List[MessageCalcRequestModel] = {
    s"python script.py".!
    val data =
      parse(Source.fromFile("src/main/scala/data/data2.txt").mkString)
    val list: List[MessageCalcRequestModel] = for {
      JObject(x) <- data
      JField("body", JString(body)) <- x
      JField("classifier", JString(classifier)) <- x
    } yield MessageCalcRequestModel(classifier.toInt, body)
    list
  }
}
