package com.hack.models

import com.hack.models.StatusType.StatusType

case class ServiceResponse(
      status: StatusType,
      error: Option[String]
                     )

object StatusType extends Enumeration {
  type StatusType = Value
  val Success, Failure = Value
}