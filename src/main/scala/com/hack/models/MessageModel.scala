package com.hack.models

case class MessageModel(
                  id: Long,
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
