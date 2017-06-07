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

  protected def beforeSave(func: => Unit) = beforeSaveFanc = Some(func _)

  protected def afterSave(func: => Unit) = afterSaveFanc = Some(func _)

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

  protected def beforeUpdate(func: => Unit) = beforeUpdateFanc = Some(func _)

  protected def afterUpdate(func: => Unit) = afterUpdateFanc = Some(func _)

  @Transient
  protected var beforeUpdatePartialFanc: Option[() => Unit] = None
  @Transient
  protected var afterUpdatePartialFanc: Option[() => Unit] = None

  protected def beforeUpdatePartial(func: => Unit) = beforeUpdatePartialFanc = Some(func _)

  protected def afterUpdatePartial(func: => Unit) = afterUpdatePartialFanc = Some(func _)

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

  override def updatePartial(func: this.type => UpdateAssignment): this.type = {
    inTransaction {
      beforeUpdatePartialFanc match {
        case Some(f) => f()
        case None =>
      }
      super.updatePartial(func)
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

  protected def beforeDelete(func: => Unit) = beforeDeleteFanc = Some(func _)

  protected def afterDelete(func: => Unit) = afterDeleteFanc = Some(func _)

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
