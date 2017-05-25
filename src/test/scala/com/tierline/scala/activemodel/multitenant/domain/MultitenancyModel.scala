package com.tierline.scala.activemodel.multitenant.domain

import com.tierline.scala.activemodel.ActiveModel

trait MultitenancyModel {
  self: ActiveModel =>
  var tenantId: String = _
}

