package com.tierline.scala.activemodel.collection

import scala.reflect._
import scala.reflect.runtime.universe._
import org.joda.time.format.DateTimeFormat
import java.util.Date
import java.sql.Timestamp

object FindMap {
  def apply(map: Map[String, String]) = {
    new FindMap(map)
  }
}

class FindMap(val params: Map[String, String]) {

  def get(key: String): Option[String] = {
    params.get(key) match {
      case Some("") => None
      case Some(value) => Some(value)
      case _ => None
    }
  }

  def getAs[T <: AnyVal : TypeTag](key: String): Option[T] = {
    params.get(key) match {
      case Some("") => None
      case Some(value) => {
        typeOf[T] match {
          case t if t =:= typeOf[Char] => Some(value.toString.asInstanceOf[T])
          case t if t =:= typeOf[Byte] => Some(value.toByte.asInstanceOf[T])
          case t if t =:= typeOf[Short] => Some(value.toShort.asInstanceOf[T])
          case t if t =:= typeOf[Int] => Some(value.toInt.asInstanceOf[T])
          case t if t =:= typeOf[Long] => Some(value.toLong.asInstanceOf[T])
          case t if t =:= typeOf[Float] => Some(value.toFloat.asInstanceOf[T])
          case t if t =:= typeOf[Double] => Some(value.toDouble.asInstanceOf[T])
          case t if t =:= typeOf[Boolean] => Some(value.toBoolean.asInstanceOf[T])
          case _ => None
        }
      }
      case _ => None
    }
  }

  def getOrElse(key: String, default: String): String = {
    get(key) match {
      case Some(value) => value
      case _ => default
    }
  }

  def getAsOrElse[T <: AnyVal : TypeTag](key: String, default: T): T = {
    getAs[T](key) match {
      case Some(value) => value
      case _ => default
    }
  }

  def like(key: String, mode: MatchMode = Entire): Option[String] = {
    params.get(key) match {
      case Some("") => None
      case Some(value) => {
        Some(mode(value))
      }
      case _ => None
    }
  }

  def getAsDate(key: String, pattern: String): Option[Date] = {
    params.get(key) match {
      case Some("") => None
      case Some(value) => {
        val date = DateTimeFormat.forPattern(pattern).parseDateTime(value)
        Some(date.toDate)
      }
      case _ => None
    }
  }

  def getAsTimestamp(key: String, pattern: String): Option[Timestamp] = {
    params.get(key) match {
      case Some("") => None
      case Some(value) => {
        val date = DateTimeFormat.forPattern(pattern).parseDateTime(value)
        Some(new Timestamp(date.getMillis))
      }
      case _ => None
    }
  }

}
