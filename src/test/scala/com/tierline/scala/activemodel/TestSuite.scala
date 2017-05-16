package com.tierline.scala.activemodel

import com.tierline.sails.servicelocator.logging.Slf4jLogger
import com.tierline.scala.activemodel.domain.TestSchema
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import org.squeryl.PrimitiveTypeMode.transaction
import org.squeryl.{Session, SessionFactory}
import org.squeryl.PrimitiveTypeMode._

trait TestSuite extends FunSuite with BeforeAndAfterAll with BeforeAndAfter with Slf4jLogger {

  var session: Session = _

  override def beforeAll {
    Database.setSchema(TestSchema)
    session = SessionFactory.newSession
    transaction {
      TestSchema.drop()
      TestSchema.create()
    }
    session.bindToCurrentThread
  }

  override def afterAll {
    session.unbindFromCurrentThread
  }
}
