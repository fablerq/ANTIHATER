package com.hack.configs

import com.hack.models.{ApiKeyModel, MessageModel, ServiceResponse, extraSettingModel}
import com.mongodb.MongoCredential._
import com.mongodb.{MongoCredential, ServerAddress}
import com.mongodb.connection.{ClusterSettings, ConnectionPoolSettings}
import com.typesafe.config.ConfigFactory
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import sun.awt.SunHints.Value

import scala.collection.JavaConverters._
object Mongo {
  lazy val config = ConfigFactory.load()

  lazy val connectionPoolSettings: ConnectionPoolSettings =
    ConnectionPoolSettings
      .builder()
      .maxWaitQueueSize(3000)
      .build()

  lazy val clusterSettings: ClusterSettings =
    ClusterSettings
      .builder()
      .maxWaitQueueSize(2000)
      .hosts(List(
        new ServerAddress(config.getString("mongo.host"))
      ).asJava)
      .build()

  val host: String = config.getString("mongo.host")
  val port: Int = config.getInt("mongo.port")
  val dbName: String = config.getString("mongo.database")
  val user: String = config.getString("mongo.user")
  val password: String = config.getString("mongo.password")

  val credential: MongoCredential =
    createCredential(user, dbName, password.toCharArray)

  lazy val settings: MongoClientSettings =
    MongoClientSettings
      .builder()
      .clusterSettings(clusterSettings)
      .credentialList(List(credential).asJava)
      .connectionPoolSettings(connectionPoolSettings)
      .build()

  lazy val mongoClient: MongoClient =
    MongoClient(settings)

  lazy val customCodecs = fromProviders(
    classOf[ApiKeyModel],
    classOf[MessageModel],
    classOf[ServiceResponse]
  )

  lazy val codecRegistry = fromRegistries(customCodecs, DEFAULT_CODEC_REGISTRY)
  lazy val database: MongoDatabase =
    mongoClient
      .getDatabase(config.getString("mongo.database"))
      .withCodecRegistry(codecRegistry)

  val keysCollection: MongoCollection[ApiKeyModel] =
    database.getCollection("keys")

  val messagesCollection: MongoCollection[MessageModel] =
    database.getCollection("messages")

}