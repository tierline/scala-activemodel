package com.tierline.scala.activemodel.singletenant.domain

import com.tierline.scala.activemodel.{StringKeyActiveModel, StringKeyRepository}
import org.squeryl.PrimitiveTypeMode._

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