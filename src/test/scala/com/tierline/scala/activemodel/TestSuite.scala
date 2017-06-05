package com.tierline.scala.activemodel

import com.tierline.scala.activemodel.singletenant.ActiveModelSessionFactory
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import org.squeryl.Session
import org.squeryl.PrimitiveTypeMode._


trait TestSuite extends FunSuite with BeforeAndAfterAll with BeforeAndAfter {

  var session: Session = _
  val schema: ActiveModelSchema = TestSchema

  override def beforeAll {
    ActiveModelSessionFactory.concreteFactory = schema.sessionFactory
    session = ActiveModelSessionFactory.newSession
    session.bindToCurrentThread

    transaction {
      schema.drop()
      schema.create()
    }

    //    showTables()
  }

  override def afterAll {
    transaction {
      schema.drop()
    }

    //    showTables()
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

  def select(table: String, id: Long, columns: Seq[String]): Seq[Map[String, String]] = {
    val query = s"select * from $table where id = $id"
    val results = NativeQuery(schema).execute(query) {
      rs =>
        val tableMap = scala.collection.mutable.Map[String, String]()
        columns.foreach(name => tableMap(name) = rs.getString(name))
        tableMap.toMap
    }
    results
  }

  def selectHead(table: String, id: Long, columns: Seq[String]): Map[String, String] = {
    val query = s"select * from $table where id = $id"
    val results = NativeQuery(schema).execute(query) {
      rs =>
        val tableMap = scala.collection.mutable.Map[String, String]()
        columns.foreach(name => tableMap(name) = rs.getString(name))
        tableMap.toMap
    }
    results.head
  }
}
