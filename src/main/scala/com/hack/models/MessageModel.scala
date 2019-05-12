package com.hack.models

import org.mongodb.scala.bson.ObjectId

case class MessageModel(
                  id: ObjectId,
                  classifier: Int,
                  body: String,
                       )

case class MessageResponseModel(
                   classifier: Int,
                   body: String,
                   extraSettings: Option[extraSettingModel]
                               )

case class MessageParamsModel(
                   body: String,
                   extraSettings: Option[extraSettingModel]
                             )

case class MessageCalcRequestModel(
                   classifier: Int,
                   body: String
                                  )
