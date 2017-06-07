package com.tierline.scala.activemodel.util

import ch.qos.logback.classic.Level
import org.slf4j.LoggerFactory

trait XXLogger {
  val rootLogger: ch.qos.logback.classic.Logger =
    LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[ch.qos.logback.classic.Logger]

  //  val rootLogger =
  //    LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)

  def LoggerAll() = rootLogger.setLevel(Level.ALL)

  def LoggerError() = rootLogger.setLevel(Level.ERROR)

  def LoggerWarn() = rootLogger.setLevel(Level.WARN)

  def LoggerInfo() = rootLogger.setLevel(Level.INFO)

  def LoggerDebug() = rootLogger.setLevel(Level.DEBUG)

  def LoggerTrace() = rootLogger.setLevel(Level.TRACE)

  def LoggerOff() = rootLogger.setLevel(Level.OFF)

  def debug = rootLogger
}
