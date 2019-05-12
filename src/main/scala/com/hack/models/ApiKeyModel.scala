package com.hack.models

import slick.driver.PostgresDriver.api._
import com.hack.models.KeyPermissions.KeyPermissions

case class ApiKeyModel(
            id: Long,
            keyTitle: String,
            permissions: KeyPermissions,
            currentRequests: Int,
            dayLimit: Int
            )

case class ApiKeyParamsModel(
                keyTitle: String,
                permissions: List[KeyPermissions],
                dayLimit: Int
                      )

//object KeyPermissions extends Enumeration {
//  type KeyPermissions = Value
//  val doHandle, checkMessages, checkKeys, createKeys = Value
//}

object KeyPermissions extends Enumeration {
  type KeyPermissions = Value
  val STANDARD, MODERATOR, ADMIN = Value
}
