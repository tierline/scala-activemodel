package com.tierline.scala.activemodel

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.SessionFactory
import grizzled.slf4j.Logging
import java.sql.DriverManager
import com.mchange.v2.c3p0.DataSources
import scala.collection.mutable._
import org.squeryl.internals.DatabaseAdapter

object Database extends Logging {

  val schema: ArrayBuffer[ActiveModelSchema] = ArrayBuffer()

  def setSchema(newSchema: ActiveModelSchema*) {
    schema.clear()
    schema ++= newSchema
    info("set schema:" + schema)
    schema.foreach { s => s.init() }
  }

  def close() {
    ActiveModelSessionFactory.clear()
    schema.clear()

  }

}
