package com.tierline.scala.activemodel


import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import com.mchange.v2.c3p0.ComboPooledDataSource
import com.tierline.scala.activemodel.util.Companion
import grizzled.slf4j.Logging

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

  protected def createTable[T <: ActiveModelBase[_]](func: => Table[T])(implicit manifestT: Manifest[T]): Table[T] = {
    Companion.of[T] match {
      case Some(companion) => {
        val t = func
        companion.asInstanceOf[RepositoryBase[_, T]].set(this, t)
        t
      }
      case None => throw new IllegalStateException(s"${manifestT.runtimeClass.getSimpleName}:コンパニオンオブジェクトが見つかりません")
    }
  }

  override def drop() = inTransaction {
    debug(s"Drop:$schemaName")
    super.drop
  }

  override def create() = inTransaction {
    debug(s"Create:$schemaName")
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

  def sessionFactory: () => Session = { () =>
    this.createSession
  }

  def setLogger(debug: String) = Session.currentSession.setLogger(s => println(s"$debug $s"))

}
