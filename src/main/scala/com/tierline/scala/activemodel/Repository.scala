package com.tierline.scala.activemodel

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import org.squeryl.dsl.ast._
import org.squeryl.dsl.boilerplate.FromSignatures

import grizzled.slf4j.Logging

trait Repository[T <: ActiveModelBase[Long]] extends RepositoryBase[Long, T] {

  override def idToTypedExpressionNode(id: Long): TypedExpressionNode[_] = id

}

trait StringKeyRepository[T <: ActiveModelBase[String]] extends RepositoryBase[String, T] {

  override def idToTypedExpressionNode(id: String): TypedExpressionNode[_] = id

}

trait RepositoryBase[K, T <: ActiveModelBase[K]] extends TableSupport[K, T] with FromSignatures with Logging {

  var schema: ActiveModelSchema = _

  protected var _table: Option[Table[T]] = None

  def schemaName = schema.schemaName

  def table: Table[T] = _table.getOrElse(throw new IllegalAccessException("Set Table!!"))

  def repo: Table[T] = this.table

  def tableName = table.name

  def set(schema: ActiveModelSchema, table: Table[T]) {
    this.schema = schema
    this._table = Some(table)
  }

  def idToTypedExpressionNode(v: K): TypedExpressionNode[_]

  def resetAutoIncrement() {
    transaction {
      val query = schema.dbAdapter.autoInclementSql(tableName)
      val con = Session.currentSession.connection
      val statement = con.prepareStatement(query)
      statement.execute()
      con.commit()
    }
  }

  def exists(id: K): Boolean = inTransaction {
    repo.lookup(id) match {
      case Some(value) => true
      case _ => false
    }
  }

  def findById(id: K): Option[T] = inTransaction {
    debug(s"find $tableName by id = $id")
    val e = repo.lookup(id)
    e.asInstanceOf[Option[T]]
  }

  def find(whereClauseFunctor: T => LogicalBoolean)(implicit dsl: QueryDsl): Seq[T] = inTransaction {
    val query = repo.where(whereClauseFunctor)(dsl)
    debug(query.statement)
    query.toSeq
  }

  def find(whereClauseFunctor: T => LogicalBoolean, orderByFunctor: T => ExpressionNode)(implicit dsl: QueryDsl): Seq[T] = inTransaction {
    val query = from(repo)(e =>
      where(whereClauseFunctor(e))
        select (e)
        orderBy (orderByFunctor(e)))
    debug(query.statement)
    query.toSeq
  }

  def find(
    whereClauseFunctor: T => LogicalBoolean,
    orderByFunctor1: T => ExpressionNode,
    orderByFunctor2: T => ExpressionNode)(implicit dsl: QueryDsl): Seq[T] = inTransaction {
    val query = from(repo)(e =>
      where(whereClauseFunctor(e))
        select (e)
        orderBy(orderByFunctor1(e), orderByFunctor2(e)))

    debug(query.statement)
    query.toSeq
  }

  def all: Seq[T] = inTransaction {
    debug(repo.statement)
    repo.toSeq
  }

  def first: Option[T] = inTransaction {
    val query = repo.where(e => 1 === 1).page(0, 1)
    debug(query.statement)
    val e = query.single
    if (e != null) {
      Option(e)
    } else {
      None
    }
  }

  def deleteAll(): Long = inTransaction {
    repo.deleteWhere(e => 1 === 1)
  }

  def deleteAll(whereClauseFunctor: T => LogicalBoolean): Long = inTransaction {
    repo.deleteWhere(whereClauseFunctor)
  }

  def fetch(page: Int, pageLength: Int): Seq[T] = inTransaction {
    val query = from(repo)(e =>
      where(1 === 1)
        select (e)
        orderBy (idToTypedExpressionNode(e.id) asc))
      .page((page - 1) * pageLength, pageLength)
    debug(query.statement)
    query.toSeq
  }

  def fetch(whereClauseFunctor: T => LogicalBoolean)(page: Int, pageLength: Int)(implicit dsl: QueryDsl): Seq[T] = inTransaction {
    val query = from(repo)(e =>
      where(whereClauseFunctor(e))
        select (e)
        orderBy (idToTypedExpressionNode(e.id) asc))
      .page((page - 1) * pageLength, pageLength)
    debug(query.statement)
    query.toSeq
  }

  def fetch(whereClauseFunctor: T => LogicalBoolean, orderByFunctor: T => ExpressionNode)(page: Int, pageLength: Int)(implicit dsl: QueryDsl): Seq[T] = inTransaction {
    val query = from(repo)(e =>
      where(whereClauseFunctor(e))
        select (e)
        orderBy (orderByFunctor(e)))
      .page((page - 1) * pageLength, pageLength)
    debug(query.statement)
    query.toSeq
  }

  def countAll: Long = inTransaction {
    val query = from(repo)(e => compute(count(idToTypedExpressionNode(e.id))))
    debug(query.statement)
    query.toLong
  }

  def countBy(whereClauseFunctor: T => LogicalBoolean): Long = inTransaction {
    val query = from(repo)(e => where(whereClauseFunctor(e)) compute (count(idToTypedExpressionNode(e.id))))
    debug(query.statement)
    val result: Long = query
    result
  }

  //  def scalar[A, T1](whereClauseFunctor: T => LogicalBoolean)(e1: => TypedExpressionNode[T1]): Option[A] = {
  //    val count = countBy(whereClauseFunctor)
  //    if (count > 0) {
  //      return None
  //    }
  //    var query = from(repo)(e =>
  //      where(whereClauseFunctor(e))
  //        compute (e1))
  //
  //    debug(query.statement)
  //    query.headOption match {
  //      case Some(r) => Some(r.measures.asInstanceOf[A])
  //      case None => None
  //    }
  //  }

}
