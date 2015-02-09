package com.knol.core

trait SessionRepo {
  def create(session:Session):Option[Int]
  def update(session:Session):Int
  def delete(id:Int):Int
  def getSession(Id:Int):Option[Session]
  def getList():List[Session]
}

case class Session(topic:String,date:String,knolId:Int,id:Option[Int]=None)
