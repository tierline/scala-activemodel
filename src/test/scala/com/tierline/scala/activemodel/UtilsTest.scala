package com.tierline.scala.activemodel

import org.scalatest.FunSuite
import com.tierline.scala.activemodel.domain.Cart

class UtilsTest extends FunSuite {

  test("Get Companion object from class manifest") {
    assert(Utils.companionOf[Cart].isDefined)
  }
}
