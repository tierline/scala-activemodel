package com.tierline.scala.activemodel

import org.scalatest.BeforeAndAfter
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSuite
import com.tierline.scala.activemodel.domain._
import com.typesafe.config.ConfigFactory
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.SessionFactory
import org.squeryl.Session

object Context {

  val config = ConfigFactory.load("env-database.conf")

  def production(): Unit = {
    val dbConfig = config.getConfig("production")
    val schema = EnvironmentConfigTestSchema(H2(dbConfig))
    Database.setSchema(schema)
  }

  def test(): Unit = {
    val dbConfig = config.getConfig("test")
    val schema = EnvironmentConfigTestSchema(H2(dbConfig))
    Database.setSchema(schema)
  }
}

class EnvironmentConfigTest extends FunSuite with BeforeAndAfterAll with BeforeAndAfter {

  var session: Session = _

  override def afterAll {
    session.unbindFromCurrentThread
    Database.close()
  }

  test("test") {
    Context.test()
    session = SessionFactory.newSession
    transaction {
      EnvironmentConfigTestSchema.drop()
      EnvironmentConfigTestSchema.create()

      assert(EnvironmentConfigTestSchema.databaseAdapter.url.contains("test"))
      val isSaved = new Cart("Note", 1000).save()

      assert(isSaved, "didn't saved")
    }
    session.unbindFromCurrentThread
  }

  test("production") {
    Context.production()

    val session = SessionFactory.newSession
    session.bindToCurrentThread
    transaction {
      EnvironmentConfigTestSchema.drop()
      EnvironmentConfigTestSchema.create()

      assert(EnvironmentConfigTestSchema.databaseAdapter.url.contains("production"))
      val isSaved = new Cart("Note", 1000).save()

      assert(isSaved, "didn't saved")
    }
    session.unbindFromCurrentThread
  }

}
