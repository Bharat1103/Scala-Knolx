package com.knol.core

trait knolRepo {

  def create(knolder: Knol): Option[Int]
  def update(knolder: Knol): Int
  def delete(id: Int): Int
  def getKnolder(Id: Int): Option[Knol]
  def getList(): List[Knol]

}

case class Knol(name: String, email: String, mob: String, id: Option[Int] = None)
