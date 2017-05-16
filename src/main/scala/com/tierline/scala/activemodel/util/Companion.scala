package com.tierline.scala.activemodel.util

import grizzled.slf4j.Logging

object Companion extends Logging {
  def of[T: Manifest]: Option[AnyRef] = try {
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
