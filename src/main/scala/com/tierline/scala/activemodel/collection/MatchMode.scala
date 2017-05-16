package com.tierline.scala.activemodel.collection

trait MatchMode {
  def apply(value: String): String
}

object Start extends MatchMode {
  def apply(value: String): String = "%" + value
}

object End extends MatchMode {
  def apply(value: String): String = value + "%"
}

object AnyWhere extends MatchMode {
  def apply(value: String): String = "%" + value + "%"
}

object Entire extends MatchMode {
  def apply(value: String): String = value
}