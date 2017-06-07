package com.tierline.scala.activemodel.multitenant.shared.db

import com.tierline.scala.activemodel.ActiveModelSchema
import com.tierline.scala.activemodel.multitenant.shared.{DeleteActiveModelTest, InsertActiveModelTest, SelectActiveModelTest, UpdateActiveModelTest}

class H2ActiveModelTest
  extends InsertActiveModelTest
    with UpdateActiveModelTest
    with DeleteActiveModelTest
    with SelectActiveModelTest {

  override val schema: ActiveModelSchema = H2SharedSchema
}
