package com.tierline.scala.activemodel

import java.sql.{Connection, ResultSet}

import grizzled.slf4j.Logging

object NativeQuery {
  implicit def stringMapToSeqStringMap(vMap: Map[String, String]): Map[String, Seq[String]] = vMap.map { case (k, v) => k -> Seq(v) }

  implicit def stringToSeqString(v: String): Seq[String] = Seq(v)
}

case class NativeQuery(schema: ActiveModelSchema) extends Logging {


  def using[Closeable <: {def close() : Unit}, B](closeable: Closeable)(func: Closeable => B): B = {
    try {
      func(closeable)
    } finally {
      closeable.close
    }
  }

  def connection: Connection = schema.newSession.connection

  import scala.collection.mutable.ListBuffer

  def toList[T](exists: => Boolean)(func: => T): List[T] = {
    val buff = new ListBuffer[T]
    while (exists) buff += func
    buff.toList
  }

  private def query[T](sql: String)(resultToObj: ResultSet => T): T = {
    using(connection) { con =>
      using(con.createStatement) { statement =>
        debug("execute query:" + sql)
        using(statement.executeQuery(sql)) { results =>
          resultToObj(results)
        }
      }
    }
  }

  def escape(vSeq: Seq[String]): Seq[String] = vSeq.map { v => v.replaceAll("'", "\"").replaceAll(";", "") }

  def addQuote(v: Seq[String]) = s"'${v.mkString("','")}'"

  private def replacement(sql: String, params: Map[String, Seq[String]]): String = {
    if (params.isEmpty) {
      sql
    } else {
      var sqlHolder = sql
      params.foreach { param =>
        sqlHolder = sqlHolder.replaceAll("%" + param._1, addQuote(escape(param._2)))
      }
      sqlHolder
    }
  }

  def queryEach[T](sql: String, params: Map[String, Seq[String]] = Map())(resultToObj: ResultSet => T): Seq[T] = {
    val sqlQuery = replacement(sql, params)
    query(sqlQuery) { results =>
      toList(results.next) {
        resultToObj(results)
      }
    }
  }

  def querySingle[T](sql: String, params: Map[String, Seq[String]] = Map())(resultToObj: ResultSet => T): Option[T] = {
    val sqlQuery = replacement(sql, params)
    query(sqlQuery) { results =>
      if (results.next) {
        Some(resultToObj(results))
      } else {
        None
      }

    }
  }

  def update(sql: String): Int = {
    using(connection) { con =>
      using(con.createStatement) { statement =>
        debug("execute update:" + sql)
        statement.executeUpdate(sql)
      }
    }
  }

  def execute[T](sql: String)(resultToObj: ResultSet => T): Seq[T] = {
    query(sql) { results =>
      toList(results.next) {
        resultToObj(results)
      }
    }
  }

}
