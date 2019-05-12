package com.hack.models

import org.mongodb.scala.bson.ObjectId

case class ApiKeyModel(
            _id: ObjectId,
            keyTitle: String,
            permissions: String,
            currentRequests: Int,
            dayLimit: Int
            )


//object KeyPermissions extends Enumeration {
//  type KeyPermissions = Value
//  val doHandle, checkMessages, checkKeys, createKeys = Value
//}

//object KeyPermissions extends Enumeration {
//  type KeyPermissions = Value
//  val STANDARD, MODERATOR, ADMIN = Value
//}
