package com.knol.core.impl

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import com.knol.core.Knol
import com.knol.db.connection.DBConnection
import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigResolveOptions

class KnolRepoImplTest extends FunSuite with BeforeAndAfter with DBConnection {

  val knolrepoimpl = new KnolRepoImpl
  
  val config = ConfigFactory.load("NoneTesting.conf",
          ConfigParseOptions.defaults.setAllowMissing(false), ConfigResolveOptions.defaults)

  val testdata = Knol("Test Me", "Test@dummy", "1234567890", Some(1))

  val knol = Knol("Bharat Singh", "Bharat@knoldus.com", "9716719367", Some(2))

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
          knolrepoimpl.create(testdata)
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

    val str = "DROP TABLE knolder"
    conn match {
      case Some(cnctn) => {
        val stmt = cnctn.createStatement()
        stmt.execute(str)
        stmt.close()
        cnctn.close()
      }
      case None => None

    }
  }
  
  
   test("checking error in getting connection with database")
  {
    val result=getConnection(Some(config))
    assert(result===None)
  }
   
  

  test("Checking error in create method") {

    val result = knolrepoimpl.create(testdata)
    assert(result === None)

  }

  test("Create a Knolder") {

    val result = knolrepoimpl.create(knol)
    assert(result === Some(2))
  }

  test("checking error in update method") {
    knolrepoimpl.create(knol)
    val result = knolrepoimpl.update(knol)
    assert(result === 0)
  }

  test("Update a Knolder") {

    val result = knolrepoimpl.update(testdata)
    assert(result === 1)

  }

  test("checking error in delete method") {

    {
      val conn = getConnection(None)

      conn match {
        case Some(cnctn) => {
          val stmt = cnctn.createStatement()
          stmt.execute("drop table knolder")
          stmt.close()
          cnctn.close()
        }
        case None => None
      }
    }

    val result = knolrepoimpl.delete(1)
    assert(result === 0)

    {
      val conection = getConnection(None)

      conection match {
        case Some(conn) =>
          {
            val stmt = conn.createStatement()
            stmt.execute("create table knolder(Testdata int)")
            stmt.close()
            conn.close()
          }
        case None => None
      }
    }

  }

  test("Delete a Knolder") {
    val result = knolrepoimpl.delete(1)
    assert(result === 1)
  }

  test("checking error in fetching a knolder record") {

    {
      val conn = getConnection(None)

      conn match {
        case Some(cnctn) => {
          val stmt = cnctn.createStatement()
          stmt.execute("drop table knolder")
          stmt.close()
          cnctn.close()
        }
        case None => None
      }
    }

    val result = knolrepoimpl.getKnolder(1)
    assert(result === None)

    {
      val conection = getConnection(None)

      conection match {
        case Some(conn) =>
          {
            val stmt = conn.createStatement()
            stmt.execute("create table knolder(Testdata int)")
            stmt.close()
            conn.close()
          }
        case None => None
      }
    }

  }

  test("Get a particular Knolder") {

    val result = knolrepoimpl.getKnolder(1)
    assert(result === Some(testdata))

  }

  test("checking error in fetching a list of knolders") {

    {
      val conn = getConnection(None)

      conn match {
        case Some(cnctn) => {
          val stmt = cnctn.createStatement()
          stmt.execute("drop table knolder")
          stmt.close()
          cnctn.close()
        }
        case None => None
      }
    }

    val result: List[Knol] = knolrepoimpl.getList()
    assert(result === List())

    {
      val conection = getConnection(None)

      conection match {
        case Some(conn) =>
          {
            val stmt = conn.createStatement()
            stmt.execute("create table knolder(Testdata int)")
            stmt.close()
            conn.close()
          }
        case None => None
      }
    }

  }

  test("Get a list of Knolders") {

    val result: List[Knol] = knolrepoimpl.getList()
    assert(result === List(testdata))

  }

}
