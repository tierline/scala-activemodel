package com.tierline.scala.activemodel.collection

import org.scalatest.FunSpec
import org.squeryl._
import org.squeryl.adapters.H2Adapter
import com.tierline.scala.activemodel.singletenant.domain._
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.Level
import org.scalatest.Assertions

class FindMapTest extends FunSpec {

  val rootLogger: Logger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger]
  rootLogger.setLevel(Level.DEBUG)

  val map = new FindMap(
    Map("stringVal" -> "char",
      "byteVal" -> "127",
      "shortVal" -> "32760",
      "intVal" -> "2147483647",
      "longVal" -> "92233720368547758",
      "floatVal" -> "1.2",
      "doubleVal" -> "1.2",
      "boolVal" -> "true"))

  describe("get") {
    it("String value") {
      assert(map.get("stringVal").get.isInstanceOf[String])
    }
    it("None value") {
      assert(map.get("stringNone") === None)
    }
  }

  describe("getAs") {

    it("None value") {
      assert(map.getAs("none") === None)
    }

    it("Byte value") {
      assert(map.getAs[Byte]("byteVal").get.isInstanceOf[Byte])
    }

    it("Short type value") {
      assert(map.getAs[Short]("shortVal").get.isInstanceOf[Short])
    }

    it("Int type value") {
      assert(map.getAs[Int]("intVal").get.isInstanceOf[Int])
    }

    it("Long type value") {
      assert(map.getAs[Long]("longVal").get.isInstanceOf[Long])
    }

    it("Float type value") {
      assert(map.getAs[Float]("floatVal").get.isInstanceOf[Float])
    }

    it("Double type value") {
      assert(map.getAs[Double]("doubleVal").get.isInstanceOf[Double])
    }

    it("Boolean type value") {
      assert(map.getAs[Boolean]("boolVal").get.isInstanceOf[Boolean])
    }

  }

  describe("getAsOrElse") {

    it("String type value") {
      assert(map.getOrElse("stringVal", "100") === "char")
      assert(map.getOrElse("none", "100") === "100")
    }

    it("Short type value") {
      assert(map.getAsOrElse[Short]("shortVal", 100) === 32760)
      assert(map.getAsOrElse[Short]("none", 100) === 100)
    }

    it("Int type value") {
      assert(map.getAsOrElse[Int]("intVal", 100) === 2147483647)
      assert(map.getAsOrElse[Int]("none", 100) === 100)
    }

    it("Long type value") {
      assert(map.getAsOrElse[Long]("longVal", 100) === 92233720368547758L)
      assert(map.getAsOrElse[Long]("none", 100) === 100)
    }

    it("Float type value") {
      assert(map.getAsOrElse[Float]("floatVal", 100) === 1.2f)
      assert(map.getAsOrElse[Float]("none", 10.0f) === 10.0f)
    }

    it("Double type value") {
      assert(map.getAsOrElse[Double]("doubleVal", 100) === 1.2d)
      assert(map.getAsOrElse[Double]("none", 10.00d) === 10.00d)
    }

    it("Boolean type value") {
      assert(map.getAsOrElse[Boolean]("boolVal", false) == true)
      assert(map.getAsOrElse[Boolean]("noen", false) == false)
    }

  }
}