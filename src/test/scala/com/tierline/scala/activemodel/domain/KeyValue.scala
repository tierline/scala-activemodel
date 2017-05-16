package com.tierline.scala.activemodel.domain

import org.squeryl.PrimitiveTypeMode._
import com.tierline.scala.activemodel.StringKeyActiveModel
import com.tierline.scala.activemodel.StringKeyRepository

object KeyValue extends StringKeyRepository[KeyValue] {
  def countByValue(value: String): Long = {
    val r = from(repo)(e =>
      where(e.value === value)
        compute (count(e.id)))
    debug(r.statement)
    r
  }
}

case class KeyValue(var id: String, var value: String) extends StringKeyActiveModel {

  def this() {
    this("", "")
  }
}