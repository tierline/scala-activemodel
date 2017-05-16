package com.tierline.scala.activemodel

import grizzled.slf4j.Logging

object Utils extends Logging {
  def companionOf[T: Manifest]: Option[AnyRef] = try {
    val classOfT = implicitly[Manifest[T]].runtimeClass
    val companionClassName = classOfT.getName + "$"
    val companionClass = Class.forName(companionClassName)
    val moduleField = companionClass.getField("MODULE$")
    Some(moduleField.get(null))
  } catch {
    case e: Throwable => {
      debug(s"$e ${e.getCause}")
      None
    }
  }
}
