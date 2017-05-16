package com.tierline.scala.activemodel

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import com.mchange.v2.c3p0.ComboPooledDataSource
import grizzled.slf4j.Logging
import com.typesafe.config.ConfigFactory

trait ActiveModelSchema extends Schema with Logging {

  var schemaName: String = getClass.getSimpleName.replace("$", "")

  var _databaseAdapter: Option[ActiveModelDatabaseAdapter] = None

  def databaseAdapter_=(adapter: ActiveModelDatabaseAdapter) = _databaseAdapter = Some(adapter)

  def databaseAdapter: ActiveModelDatabaseAdapter = _databaseAdapter.getOrElse(throw new IllegalStateException)

  def dbAdapter: NativeQueryAdapter = databaseAdapter.nativeQueryAdapter

  def dataSource: ComboPooledDataSource = databaseAdapter.dataSource

  def Table[T <: ActiveModelBase[_]]()(implicit manifestT: Manifest[T]): Table[T] = createTable {
    super.table()
  }

  def Table[T <: ActiveModelBase[_]](name: String)(implicit manifestT: Manifest[T]): Table[T] = createTable {
    super.table(name)
  }

  def newSession = SessionFactory.newSession

  def apply(adapter: ActiveModelDatabaseAdapter): this.type = {
    this.databaseAdapter = adapter
    this
  }

  protected def createTable[T <: ActiveModelBase[_]](fanc: => Table[T])(implicit manifestT: Manifest[T]): Table[T] = {
    Utils.companionOf[T] match {
      case Some(companion) => {
        val repo = companion.asInstanceOf[RepositoryBase[_, T]]
        val t = fanc
        repo.set(this, t)
        t
      }
      case None => throw new IllegalStateException(s"${manifestT.runtimeClass.getSimpleName}:コンパニオンオブジェクトが見つかりません")
    }
  }

  override def drop() = inTransaction {
    debug(s"Drop:${schemaName}")
    super.drop
  }

  override def create() = inTransaction {
    debug(s"Create:${schemaName}")
    printDdl
    super.create
  }

  def printSchema() {
    transaction {
      val query = dbAdapter.printSchemaSql
      val con = Session.currentSession.connection
      val statement = con.createStatement()
      val result = statement.executeQuery(query)
      while (result.next()) {
        println(s"${result.getString(1)} ${result.getString(2)} ${result.getString(3)}")
      }
    }
  }

  def createSession: Session = {
    debug(s"Create Session of ${dataSource.getJdbcUrl}")
    Session.create(dataSource.getConnection, databaseAdapter.adapter)
  }

  def init() {
    ActiveModelSessionFactory.setDefault(this)
  }

  init()

}
