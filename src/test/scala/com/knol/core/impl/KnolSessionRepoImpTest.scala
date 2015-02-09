package com.knol.core.impl

import org.scalatest.BeforeAndAfter
import com.knol.db.connection.DBConnection
import com.knol.core.KnolSessionRepo
import org.scalatest.FunSuite
import com.knol.core.KnolSession
import com.knol.core.Knol
import com.knol.core.Session
import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigResolveOptions

class KnolSessionRepoImpTest extends FunSuite with DBConnection with BeforeAndAfter {
  
  val config = ConfigFactory.load("NoneTesting.conf",
          ConfigParseOptions.defaults.setAllowMissing(false), ConfigResolveOptions.defaults)

  val Knolrepoimpl = new KnolRepoImpl

  val MySession = new SessionRepoImpl

  val MyKnolSession = new KnolSessionRepoImpl

  val TestSession = Session("My test topic", "11/03/2015", 1, Some(1))

  val TestKnol = Knol("Bharat Singh", "Bharat@knoldus.com", "9716719367", Some(1))

  val TestKnolSession = KnolSession("Bharat Singh", "Bharat@knoldus.com", "9716719367",
    "My test topic", "11/03/2015", 1, 1)

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

          val knolResult = Knolrepoimpl.create(TestKnol)
          val sessionResult = MySession.create(TestSession)
          stmt.close()
          conn.close()
        }
      case None => None
    }
  }

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

  //Method that returns knolder and his session info searched by his Id

  test("Get a list of KnolSession") {

    val result: List[KnolSession] = MyKnolSession.getSessionById(1)
    assert(result === List(TestKnolSession))

  }

  test("checking error in fetching a list of KnolSession") {

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

    val result: List[KnolSession] = MyKnolSession.getSessionById(1)
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

}
