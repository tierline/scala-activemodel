package com.tierline.scala.activemodel.multitenant.shared

import com.tierline.scala.activemodel.multitenant.MultitenancyException
import org.squeryl.Session
import org.squeryl.dsl.ast.{OrderByExpression, QueryExpressionElements}
import org.squeryl.internals.{DatabaseAdapter, StatementWriter}

import scala.collection.mutable.ListBuffer

trait SelectAdapter extends DatabaseAdapter {
  self: SharedDatabaseAdapter =>

  override def writeQuery(qen: QueryExpressionElements, sw: StatementWriter, inverseOrderBy: Boolean, topHint: Option[String]): Unit = {
    if (!Multitenancy.hook) {
      super.writeQuery(qen, sw, inverseOrderBy, topHint)
    } else {

      var tableNames = ListBuffer[String]()

      sw.write("Select")

      topHint.foreach(" " + sw.write(_) + " ")

      if (qen.selectDistinct)
        sw.write(" distinct")

      sw.nextLine
      sw.writeIndented {
        sw.writeNodesWithSeparator(qen.selectList.filter(e => !e.inhibited), ",", true)
      }
      sw.nextLine
      sw.write("From")
      sw.nextLine

      if (!qen.isJoinForm) {
        sw.writeIndented {
          for (z <- qen.tableExpressions.zipi) {
            z.element.write(sw)
            sw.write(" ")
            val tableName = sw.quoteName(z.element.alias)
            tableNames += tableName
            sw.write(tableName)
            if (!z.isLast) {
              sw.write(",")
              sw.nextLine
            }
          }
          sw.pushPendingNextLine
        }
      }
      else {
        val singleNonJoinTableExpression = qen.tableExpressions.filter(!_.isMemberOfJoinList)
        assert(singleNonJoinTableExpression.size == 1, "join query must have exactly one FROM argument, got : " + qen.tableExpressions)
        val firstJoinExpr = singleNonJoinTableExpression.head
        val restOfJoinExpr = qen.tableExpressions.filter(_.isMemberOfJoinList)
        firstJoinExpr.write(sw)
        sw.write(" ")
        sw.write(sw.quoteName(firstJoinExpr.alias))
        sw.nextLine

        for (z <- restOfJoinExpr.zipi) {
          writeJoin(z.element, sw)
          if (z.isLast)
            sw.unindent
          sw.pushPendingNextLine
        }
      }

      writeEndOfFromHint(qen, sw)


      if (qen.hasUnInhibitedWhereClause) {
        sw.write("Where")
        sw.nextLine
        sw.write(s" (${tableNames(0)}.$tenantIdColumnName = ?) and ")
        sw.nextLine
        sw.writeIndented {
          qen.whereClause.get.write(sw)
        }
        sw.pushPendingNextLine
      }

      if (!qen.groupByClause.isEmpty) {
        sw.write("Group By")
        sw.nextLine
        sw.writeIndented {
          sw.writeNodesWithSeparator(qen.groupByClause.filter(e => !e.inhibited), ",", true)
        }
        sw.pushPendingNextLine
      }

      if (!qen.havingClause.isEmpty) {
        sw.write("Having")
        sw.nextLine
        sw.writeIndented {
          sw.writeNodesWithSeparator(qen.havingClause.filter(e => !e.inhibited), ",", true)
        }
        sw.pushPendingNextLine
      }

      if (!qen.orderByClause.isEmpty && qen.parent == None) {
        sw.write("Order By")
        sw.nextLine
        val ob0 = qen.orderByClause.filter(e => !e.inhibited)
        val ob = if (inverseOrderBy) ob0.map(_.asInstanceOf[OrderByExpression].inverse) else ob0
        sw.writeIndented {
          sw.writeNodesWithSeparator(ob, ",", true)
        }
        sw.pushPendingNextLine
      }

      writeEndOfQueryHint(qen, sw)

      writePaginatedQueryDeclaration(qen, sw)

    }
  }

  override def executeQuery(s: Session, sw: StatementWriter) = exec(s, sw) { params =>
    if (!Multitenancy.hook(sw)) {
      super.executeQuery(s, sw)
    } else {
      Multitenancy.currentTenantValue match {
        case Some(tenantId) =>
          val st = prepareStatement(s.connection, sw.statement)
          fillParamsInto(addHeadTenantParams(tenantId, params), st)
          (st.executeQuery, st)
        case None =>
          throw new MultitenancyException()
      }
    }
  }

}
