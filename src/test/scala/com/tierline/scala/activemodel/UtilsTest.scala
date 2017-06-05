package com.tierline.scala.activemodel

import com.tierline.scala.activemodel.singletenant.domain.Cart
import org.scalatest.FunSuite
import com.tierline.scala.activemodel.util.Companion

class UtilsTest extends FunSuite {

  test("Get Companion object from class manifest") {
    assert(Companion.of[Cart].isDefined)
  }
}
