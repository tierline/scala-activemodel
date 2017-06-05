package com.tierline.scala.activemodel.singletenant

import org.squeryl.{Session, SessionFactory}

object ActiveModelSessionFactory {

  def newSession: Session = SessionFactory.newSession

  def concreteFactory_=(func: () => Session) = SessionFactory.concreteFactory = Some(func)

  def concreteFactory: Option[() => Session] = SessionFactory.concreteFactory
}
