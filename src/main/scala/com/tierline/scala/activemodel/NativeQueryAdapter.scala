package com.tierline.scala.activemodel

trait NativeQueryAdapter {

  def autoInclementSql(table: String): String

  def printSchemaSql = {

    "select schema_name, schema_owner, is_default from information_schema.schemata order by schema_name;"
  }
}

case object UnKnoneAdapter extends NativeQueryAdapter {

  def autoInclementSql(table: String): String = throw new IllegalAccessException("not support oparation")
}

case object MySqlDBAdapter extends NativeQueryAdapter {
  def autoInclementSql(table: String): String = s"ALTER TABLE $table AUTO_INCREMENT = 1;"
}

case object H2DBAdapter extends NativeQueryAdapter {
  def autoInclementSql(table: String): String = s"ALTER TABLE $table ALTER COLUMN id RESTART WITH 1;"
}

case object MSSqlAdapter extends NativeQueryAdapter {
  def autoInclementSql(table: String): String = s"ALTER TABLE $table ALTER COLUMN id RESTART WITH 1;"

}
