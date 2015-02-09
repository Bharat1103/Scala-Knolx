package com.knol.db.connection

import com.typesafe.config.ConfigFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import org.slf4j.LoggerFactory
import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.ConfigResolveOptions
import com.typesafe.config.Config

trait DBConnection {

  val logger = LoggerFactory.getLogger(this.getClass)

  def getConnection(conf: Option[Config]): Option[Connection] = {

    conf match {

      case Some(conf) =>
        {
          val url = conf.getString("db.url")
          val user = conf.getString("db.user")
          val password = conf.getString("db.password")
          val driverClass = conf.getString("db.driver")

          try {
            Class.forName(driverClass)
            val connection: Connection = DriverManager.getConnection(url, user, password)
            Some(connection)
          } catch {
            case ex: SQLException =>
              logger.error("ERROR: Unable to Connect to Database.")
              None
          }

        }

      case None => {
        val config = ConfigFactory.load("application.conf",
          ConfigParseOptions.defaults.setAllowMissing(false), ConfigResolveOptions.defaults)
        val url = config.getString("db.url")
        val user = config.getString("db.user")
        val password = config.getString("db.password")
        val driverClass = config.getString("db.driver")

        try {
          Class.forName(driverClass)
          val connection: Connection = DriverManager.getConnection(url, user, password)
          Some(connection)
        } catch {
          case ex: SQLException =>
            logger.error("ERROR: Unable to Connect to Database.")
            None
        }

      }

    }

  }
}
