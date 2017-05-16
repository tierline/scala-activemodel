package com.tierline.scala.activemodel

import org.squeryl.annotations.Transient
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.ast.UpdateAssignment

trait BeforeAfterSupport extends SaveBeforeAfterSupport with UpdateBeforeAfterSupport with DeleteBeforeAfterSupport {

}

trait SaveBeforeAfterSupport extends ActiveModel {
  @Transient
  protected var beforeSaveFanc: Option[() => Unit] = None
  @Transient
  protected var afterSaveFanc: Option[() => Unit] = None

  protected def beforeSave(fanc: => Unit) = beforeSaveFanc = Some(fanc _)

  protected def afterSave(fanc: => Unit) = afterSaveFanc = Some(fanc _)

  override def create(): this.type = {
    inTransaction {
      beforeSaveFanc match {
        case Some(f) => f()
        case None =>
      }
      super.create()
      afterSaveFanc match {
        case Some(f) => f()
        case None =>
      }
    }
    this
  }

  override def save(): Boolean = {
    inTransaction {
      beforeSaveFanc match {
        case Some(f) => f()
        case None =>
      }
      val result = super.save()
      afterSaveFanc match {
        case Some(f) => f()
        case None =>
      }
      result
    }
  }
}

trait UpdateBeforeAfterSupport extends ActiveModel {
  @Transient
  protected var beforeUpdateFanc: Option[() => Unit] = None
  @Transient
  protected var afterUpdateFanc: Option[() => Unit] = None

  protected def beforeUpdate(fanc: => Unit) = beforeUpdateFanc = Some(fanc _)

  protected def afterUpdate(fanc: => Unit) = afterUpdateFanc = Some(fanc _)

  @Transient
  protected var beforeUpdatePartialFanc: Option[() => Unit] = None
  @Transient
  protected var afterUpdatePartialFanc: Option[() => Unit] = None

  protected def beforeUpdatePartial(fanc: => Unit) = beforeUpdatePartialFanc = Some(fanc _)

  protected def afterUpdatePartial(fanc: => Unit) = afterUpdatePartialFanc = Some(fanc _)

  override def update(): this.type = {
    inTransaction {
      beforeUpdateFanc match {
        case Some(f) => f()
        case None =>
      }
      super.update()
      afterUpdateFanc match {
        case Some(f) => f()
        case None =>
      }
    }
    this
  }

  override def updatePartial(fanc: this.type => UpdateAssignment): this.type = {
    inTransaction {
      beforeUpdatePartialFanc match {
        case Some(f) => f()
        case None =>
      }
      super.updatePartial(fanc)
      afterUpdatePartialFanc match {
        case Some(f) => f()
        case None =>
      }
    }
    this
  }
}

trait DeleteBeforeAfterSupport extends ActiveModel {
  @Transient
  protected var beforeDeleteFanc: Option[() => Unit] = None
  @Transient
  protected var afterDeleteFanc: Option[() => Unit] = None

  protected def beforeDelete(fanc: => Unit) = beforeDeleteFanc = Some(fanc _)

  protected def afterDelete(fanc: => Unit) = afterDeleteFanc = Some(fanc _)

  override def delete(): Boolean = {
    inTransaction {
      beforeDeleteFanc match {
        case Some(f) => f()
        case None =>
      }
      val result = super.delete()
      afterDeleteFanc match {
        case Some(f) => f()
        case None =>
      }
      result
    }

  }
}
