package com.knol.core.impl

import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite
import com.knol.core.Knol
import com.knol.core.Session
import com.knol.db.connection.DBConnection
import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigResolveOptions

class SessionRepoImplTest extends FunSuite with BeforeAndAfter with DBConnection {
  
  val config = ConfigFactory.load("NoneTesting.conf",
          ConfigParseOptions.defaults.setAllowMissing(false), ConfigResolveOptions.defaults)

  val Knolrepoimpl = new KnolRepoImpl

  val MySession = new SessionRepoImpl

  val testdata = Session("My test topic", "11/03/2015", 1, Some(1))

  val testerror = Session("My test topic", "11/03/2015", 2, Some(1))

  val knol = Knol("Bharat Singh", "Bharat@knoldus.com", "9716719367", Some(1))

  /*
   * Create table knolder in the test database before every test case that executes so that we can 
   * have auto incremented keys generated from 1.
   */
  before {
    val conection = getConnection(None)

    conection match {
      case Some(conn) =>
        {
          val stmt = conn.createStatement()
          stmt.execute("create table knolder(Id int PRIMARY KEY AUTO_INCREMENT,"
            + "Name VARCHAR(25),Email VARCHAR(30) NOT NULL UNIQUE KEY, Mob VARCHAR(12))")

          stmt.execute("CREATE TABLE knolx(Id int PRIMARY KEY AUTO_INCREMENT,Topic VARCHAR(50), "
            + "Date VARCHAR(15),Knol_Id int, FOREIGN KEY (Knol_Id) REFERENCES knolder(Id) ON DELETE CASCADE)")

          Knolrepoimpl.create(knol)
          MySession.create(testdata)
          stmt.close()
          conn.close()
        }
      case None => None
    }
  }

  /*
   * Drop the table knolder after every test case that executes so that redundant
   * records/table do not exist in the database
   */
  after {

    val conn = getConnection(None)

    conn match {
      case Some(cnctn) => {
        val stmt = cnctn.createStatement()
        stmt.execute("drop table knolx")
        stmt.execute("drop table knolder")
        stmt.close()
        cnctn.close()
      }
      case None => None

    }
  }

  test("checking error in creating a session") {

    val result = MySession.create(testerror)
    assert(result === None)
  }

  test("Create a Session") {

    val result = MySession.create(testdata)
    assert(result === Some(2))
  }

  test("checking error in updating session info") {

    {
      val conn = getConnection(None)

      conn match {
        case Some(cnctn) => {
          val stmt = cnctn.createStatement()
          stmt.execute("drop table knolx")
          stmt.close()
          cnctn.close()
        }
        case None => None
      }
    }

    val result = MySession.update(testdata)
    assert(result === 0)

    {
      val conection = getConnection(None)

      conection match {
        case Some(conn) =>
          {
            val stmt = conn.createStatement()
            stmt.execute("create table knolx(Testdata int)")
            stmt.close()
            conn.close()
          }
        case None => None
      }
    }

  }

  test("Update a Session") {

    val result = MySession.update(testdata)
    assert(result === 1)

  }

  test("checking error in deleting a session") {

    {
      val conn = getConnection(None)

      conn match {
        case Some(cnctn) => {
          val stmt = cnctn.createStatement()
          stmt.execute("drop table knolx")
          stmt.close()
          cnctn.close()
        }
        case None => None
      }
    }

    val result = MySession.delete(1)
    assert(result === 0)

    {
      val conection = getConnection(None)

      conection match {
        case Some(conn) =>
          {
            val stmt = conn.createStatement()
            stmt.execute("create table knolx(Testdata int)")
            stmt.close()
            conn.close()
          }
        case None => None
      }
    }

  }

  test("Delete a Session") {

    val result = MySession.delete(1)
    assert(result === 1)

  }

  test("checking error in fetching a sessoion record") {

    {
      val conn = getConnection(None)

      conn match {
        case Some(cnctn) => {
          val stmt = cnctn.createStatement()
          stmt.execute("drop table knolx")
          stmt.close()
          cnctn.close()
        }
        case None => None
      }
    }

    val result = MySession.getSession(1)
    assert(result === None)

    {
      val conection = getConnection(None)

      conection match {
        case Some(conn) =>
          {
            val stmt = conn.createStatement()
            stmt.execute("create table knolx(Testdata int)")
            stmt.close()
            conn.close()
          }
        case None => None
      }
    }

  }

  test("Get a particular Session info") {

    val result = MySession.getSession(1)
    assert(result === Some(testdata))

  }

  test("checking error in fetching a list of all available sessions") {

    {
      val conn = getConnection(None)

      conn match {
        case Some(cnctn) => {
          val stmt = cnctn.createStatement()
          stmt.execute("drop table knolx")
          stmt.close()
          cnctn.close()
        }
        case None => None
      }
    }

    val result: List[Session] = MySession.getList()
    assert(result === List())

    {
      val conection = getConnection(None)

      conection match {
        case Some(conn) =>
          {
            val stmt = conn.createStatement()
            stmt.execute("create table knolx(Testdata int)")
            stmt.close()
            conn.close()
          }
        case None => None
      }
    }

  }

  test("Get a list of all available Sessions") {

    val result: List[Session] = MySession.getList()
    assert(result === List(testdata))

  }

}
