package com.hack.models

case class ServiceResponse(
      status: Boolean,
      error: Option[String]
                     )

//object StatusType extends Enumeration {
//  type StatusType = Value
//  val Success, Failure = Value
//}