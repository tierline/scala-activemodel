package com.tierline.scala.activemodel

import com.tierline.scala.activemodel.domain.TestSchema
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import org.squeryl.{Session, SessionFactory}
import org.squeryl.PrimitiveTypeMode._


trait TestSuite extends FunSuite with BeforeAndAfterAll with BeforeAndAfter {
  var session: Session = _
  var schema: ActiveModelSchema = _

  override def beforeAll {
    schema = TestSchema
    Database.setSchema(schema)
    session = SessionFactory.newSession

    session.bindToCurrentThread

    transaction {
      schema.create()
    }
    showTables()
  }

  override def afterAll {
    transaction {
      schema.drop()
    }
    showTables()
    session.unbindFromCurrentThread
  }


  //only using debug print
  def showTables() = {
    println("\n\n########## SHOW TABLES ##########")

    NativeQuery(schema).execute("SHOW TABLES") {
      rs =>
        println(s"=> ${
          rs.getString(1)
        } ${
          rs.getString(2)
        }")
    }
    println("#################################\n\n")
  }
}
