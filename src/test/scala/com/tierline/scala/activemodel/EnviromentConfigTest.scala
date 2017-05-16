package com.tierline.scala.activemodel

import org.scalatest.Assertions
import org.scalatest.BeforeAndAfter
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSpec
import org.scalatest.FunSuite
import org.squeryl.adapters.H2Adapter
import com.tierline.scala.activemodel.domain._
import com.tierline.sails.servicelocator.ComponentContext
import com.tierline.sails.servicelocator.logging.Slf4jLogger
import com.typesafe.config.ConfigFactory
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.SessionFactory
import org.squeryl.Session

object Context extends ComponentContext {

  val config = ConfigFactory.load("env-database.conf")
  production {
    val dbConfig = config.getConfig("production")
    val schema = EnviromentConfigTestSchema(H2(dbConfig))
    Database.setSchema(schema)
  }

  test {
    val dbConfig = config.getConfig("test")
    val schema = EnviromentConfigTestSchema(H2(dbConfig))
    Database.setSchema(schema)
  }
}

class EnviromentConfigTest extends FunSuite with BeforeAndAfterAll with BeforeAndAfter with Slf4jLogger {

  var session: Session = _

  override def afterAll {
    session.unbindFromCurrentThread
    Database.close()
  }

  test("test") {
    Context.testStage()
    Context.init()
    session = SessionFactory.newSession
    //    session.bindToCurrentThread
    transaction {
      EnviromentConfigTestSchema.drop
      EnviromentConfigTestSchema.create

      assert(EnviromentConfigTestSchema.databaseAdapter.url.contains("test") == true)
      val isSaved = new Cart("Note", 1000).save

      assert(isSaved, "didn't saved")
    }
    session.unbindFromCurrentThread
  }
  //
  //  test("pro") {
  //    Context.productionStage()
  //    Context.init()
  //
  //    var session = SessionFactory.newSession
  //    session.bindToCurrentThread
  //    transaction {
  //      EnviromentConfigTestSchema.drop
  //      EnviromentConfigTestSchema.create
  //
  //      assert(EnviromentConfigTestSchema.databaseAdapter.url.contains("production") == true)
  //      val isSaved = new Cart("Note", 1000).save
  //
  //      assert(isSaved, "didn't saved")
  //    }
  //    session.unbindFromCurrentThread
  //  }

}
