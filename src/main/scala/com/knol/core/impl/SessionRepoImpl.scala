package com.knol.core.impl

import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement

import scala.collection.mutable.ListBuffer

import com.knol.core.Session
import com.knol.core.SessionRepo
import com.knol.db.connection.DBConnection

class SessionRepoImpl extends SessionRepo with DBConnection {

  //Method to create a record in the knolder table.

  def create(session: Session): Option[Int] = {

    //Get connection to the database using method defined in DBConnection

    val connection = getConnection(None)

    try {
      connection match {
        case Some(conn) => {
          val query = "INSERT INTO knolx(Topic,Date,Knol_Id) VALUES(?,?,?)"

          // Allow the statement to return the auto generated keys

          val preparedStatement: PreparedStatement =
            conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)

          preparedStatement.setString(1, session.topic)
          preparedStatement.setString(2, session.date)
          preparedStatement.setInt(3, session.knolId)
          preparedStatement.execute()

          logger.debug("Created a Session with insert")

          //Return the auto incremented Id from the knolder table
          val rs = preparedStatement.getGeneratedKeys()
          rs.next()
          Some(rs.getInt(1))
        }
        case None => None
      }
    } catch {
      case sqlEx: SQLException =>
        {
          logger.error("\nError Occured: Insertion unsuccessful\n")
          None
        }
    } finally {
      connection match {
        case Some(conn) => conn.close()
        case None       =>
      }
    }

  }

  //Method to update already existing record in the knolder table

  def update(session: Session): Int =
    {

      //Get connection to the database using method defined in DBConnection

      val connection = getConnection(None)

      try {

        connection match {
          case Some(conn) => {
            val preparedStatement = conn.prepareStatement("UPDATE knolx SET Topic='New Topic', Date='12/10/2015' "
              + "WHERE Knol_Id =?")
            preparedStatement.setInt(1, session.knolId)
            logger.debug("Updating a Session record")
            preparedStatement.executeUpdate();
          }

          case None => -1
        }
      } catch {
        case sqlx: SQLException => {
          logger.error("Error Occured: update failed")
          0
        }
      } finally {
        connection match {
          case Some(conn) => conn.close()
          case None       =>
            }
        }
    }

  //Method to delete a record from the knolder table

  def delete(id: Int): Int =
    {

      //Get connection to the database using method defined in DBConnection

      val connection = getConnection(None)

      try {

        connection match {
          case Some(conn) => {
            val preparedStatement: PreparedStatement =
              conn.prepareStatement("DELETE FROM knolx WHERE Id=?")
            preparedStatement.setInt(1, id)
            logger.debug("Deleting a Session record")
            preparedStatement.executeUpdate()
          }
          case None => -1
        }
      } catch {
        case sqlEx: SQLException => {
          logger.error("Error Occured: Cannot delete the record")
          0
        }
      } finally {
        connection match {
          case Some(conn) => conn.close()
          case None       =>
            }
        }
    }

  //Method to fetch a particular record from the knolder table

  def getSession(Id: Int): Option[Session] =
    {
      //Get connection to the database using method defined in DBConnection

      val connection = getConnection(None)

      try {

        connection match {
          case Some(conn) => {
            val preparedStatement: PreparedStatement =
              conn.prepareStatement("SELECT * FROM knolx WHERE Id=?")

            preparedStatement.setInt(1, Id)

            val rs = preparedStatement.executeQuery()

            val buf = new ListBuffer[Session]

            while (rs.next()) {

              buf += Session((rs.getString("Topic")),
                (rs.getString("Date")),
                (rs.getInt("Knol_Id")),
                Some((rs.getInt("Id"))))
            }
            val Ls = buf.toList
            logger.debug("Fetching a Session record")
            Ls.headOption
          }
          case None => None
        }
      } catch {
        case sqlEx: SQLException =>
          logger.error("Error Occured: Cannot fetch the record")
          None
      } finally {
        connection match {
          case Some(conn) => conn.close()
          case None       =>
            }
        }
    }

  //Method to fetch all the records from knolder table

  def getList(): List[Session] =
    {

      //Get connection to the database using method defined in DBConnection

      val connection = getConnection(None)

      try {

        connection match {
          case Some(conn) => {
            val statement: Statement = conn.createStatement()
            val rs = statement.executeQuery("SELECT * FROM knolx")
            val buf = new ListBuffer[Session]

            while (rs.next()) {
              buf += Session((rs.getString("Topic")),
                (rs.getString("Date")),
                (rs.getInt("Knol_Id")),
                Some((rs.getInt("Id"))))
            }
            logger.debug("Fetching a List of all available Session records")

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
