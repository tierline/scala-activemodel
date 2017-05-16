package com.tierline.scala.activemodel

import org.scalatest.FunSuite
import com.tierline.scala.activemodel.domain.Cart
import com.tierline.scala.activemodel.util.Companion

class UtilsTest extends FunSuite {

  test("Get Companion object from class manifest") {
    assert(Companion.of[Cart].isDefined)
  }
}
