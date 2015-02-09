package com.knol.core.impl

import com.knol.core.KnolSessionRepo
import scala.collection.mutable.ListBuffer
import com.knol.db.connection.DBConnection
import java.sql.PreparedStatement
import com.knol.core.Session
import com.knol.core.KnolSession
import java.sql.SQLException

class KnolSessionRepoImpl extends KnolSessionRepo with DBConnection {

  val connection = getConnection(None)

  //Method to perform Join operation on knolder and knolx tables

  def getSessionById(id: Int): List[KnolSession] = {
    try {

      connection match {

        case Some(conn) => {
          val preparedStatement: PreparedStatement = conn.prepareStatement("SELECT * FROM knolder U, knolx S "
            + "WHERE U.Id=S.Knol_Id=?")
          preparedStatement.setInt(1, id)
          val result = preparedStatement.executeQuery()
          val buf = new ListBuffer[KnolSession]
          while (result.next()) {
            buf += KnolSession(result.getString("Name"),
              result.getString("Email"),
              result.getString("Mob"),
              result.getString("Topic"),
              result.getString("Date"),
              result.getInt("Knol_Id"),
              result.getInt("S.Id"))
          }
          buf.toList
        }
        case None => List()
      }
    } catch {
      case sqlEx: SQLException =>
        logger.error("Error Occured: Cannot fetch list")
        List()
    } finally {
      connection match {
        case Some(conn) => conn.close()
        case None       =>
      }
    }
  }
}
