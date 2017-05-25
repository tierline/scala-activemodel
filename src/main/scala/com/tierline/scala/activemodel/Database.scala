package com.tierline.scala.activemodel

import grizzled.slf4j.Logging
import scala.collection.mutable._

object __Database extends Logging {

  val schema: ArrayBuffer[ActiveModelSchema] = ArrayBuffer()

  def setSchema(newSchema: ActiveModelSchema*) {
    schema.clear()
    schema ++= newSchema
    info("set schema:" + schema)
    //    schema.foreach { s => s.init() }
  }

  def close() {
    OldActiveModelSessionFactory.clear()
    schema.clear()
  }

}
