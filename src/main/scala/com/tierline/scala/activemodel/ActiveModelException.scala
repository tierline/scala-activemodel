package com.tierline.scala.activemodel

object ActiveModelException {

  def apply(message: String): ActiveModelException = new ActiveModelException(message)

  def tableNotFound(name: String): ActiveModelException = ActiveModelException("not found table " + name)

}

class ActiveModelException(message: String) extends RuntimeException(message)
  