package com.knol.core

trait KnolSessionRepo {

  def getSessionById(id: Int): List[KnolSession]

}

case class KnolSession(name: String, email: String, mob: String, topic: String,
                       date: String, knolId: Int, id: Int)
