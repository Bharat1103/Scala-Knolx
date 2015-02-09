package com.knol.core.impl
import scala.collection.mutable.ListBuffer
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement
import org.slf4j.LoggerFactory
import com.knol.core.knolRepo
import com.knol.core.Knol
import com.knol.db.connection.DBConnection

class KnolRepoImpl extends knolRepo with DBConnection {

  //Method to create a record in the knolder table.

  def create(knolder: Knol): Option[Int] = {

    //Get connection to the database using method defined in DBConnection

    val connection = getConnection(None)

    try {
      connection match {
        case Some(conn) => {
          val query = "INSERT INTO knolder(Name,Email,Mob) VALUES(?,?,?)"

          // Allow the statement to return the auto generated keys

          val preparedStatement: PreparedStatement =
            conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)

          preparedStatement.setString(1, knolder.name)
          preparedStatement.setString(2, knolder.email)
          preparedStatement.setString(3, knolder.mob)
          preparedStatement.execute()

          logger.debug("Created a knolder with insert")

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

  def update(knolder: Knol): Int =
    {

      //Get connection to the database using method defined in DBConnection

      val connection = getConnection(None)

      try {

        connection match {
          case Some(conn) => {
            val preparedStatement = conn.prepareStatement("UPDATE knolder SET Name='Singh Bharat', Email='Test@dummy',"
              + "Mob='1234567890' WHERE Email like ?")
            preparedStatement.setString(1, knolder.email)
            logger.debug("Updating a knolder record")
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
              conn.prepareStatement("DELETE FROM knolder WHERE Id=?")
            preparedStatement.setInt(1, id)
            logger.debug("Deleting a knolder record")
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

  def getKnolder(Id: Int): Option[Knol] =
    {
      //Get connection to the database using method defined in DBConnection

      val connection = getConnection(None)

      try {

        connection match {
          case Some(conn) => {
            val preparedStatement: PreparedStatement =
              conn.prepareStatement("SELECT * FROM knolder WHERE Id=?")

            preparedStatement.setInt(1, Id)

            val rs = preparedStatement.executeQuery()

            val buf = new ListBuffer[Knol]

            while (rs.next()) {

              buf += Knol((rs.getString("Name")),
                (rs.getString("Email")),
                (rs.getString("Mob")),
                Some((rs.getInt("Id"))))
            }
            val Ls = buf.toList
            logger.debug("Fetching a knolder record")
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

  //Import for using the mutable ListBuffer

  import scala.collection.mutable.ListBuffer

  //Method to fetch all the records from knolder table

  def getList(): List[Knol] =
    {

      //Get connection to the database using method defined in DBConnection

      val connection = getConnection(None)

      try {

        connection match {
          case Some(conn) => {
            val statement: Statement = conn.createStatement()
            val rs = statement.executeQuery("SELECT * FROM knolder")
            val buf = new ListBuffer[Knol]

            while (rs.next()) {
              buf += Knol((rs.getString("Name")),
                (rs.getString("Email")),
                (rs.getString("Mob")),
                Some((rs.getInt("Id"))))
            }
            logger.debug("Fetching a List of all knolder records")

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
