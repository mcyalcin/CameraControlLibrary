package com.mikrotasarim.ui.model

import spire.implicits._
import scalafx.beans.property.{BooleanProperty, DoubleProperty, ObjectProperty, StringProperty}

class DacControlModel(
                       label: String,
                       val defaultValue: Double,
                       val valueRange: (Double, Double),
                       val address: Int,
                       valueBits: Int,
                       CommitMethod: (DacControlModel) => Unit
                       ) {

  def Reset() = {
    value.value = defaultValue
    external.value = false
    lowPower.value = false
    powerDown.value = false
    Commit()
  }

  val name = new StringProperty(this, "name", label)
  val value = new DoubleProperty(this, "dval", defaultValue)
  val changed = new BooleanProperty(this, "changed", false)
  val external = new BooleanProperty(this, "external", false)
  val lowPower = new BooleanProperty(this, "external", false)
  val powerDown = new BooleanProperty(this, "external", false)

  value.onChange({
    changed.value = true
    external.value = true
  })
  external.onChange(changed.value = true)
  powerDown.onChange(changed.value = true)
  lowPower.onChange(changed.value = true)

  def Commit() = {
    CommitMethod(this)
    changed.value = false
  }

  def memoryValue: Long = {
    def convertNumericValueToSteps: Long = {
      if (value.value > valueRange._2 || value.value < valueRange._1) throw new Exception("Value out of bounds")
      val numberOfSteps = 2 pow valueBits
      val stepSize = (valueRange._2 - valueRange._1) / numberOfSteps
      val under: Long = ((value.value - valueRange._1) / stepSize).toLong
      val over = under + 1
      val result = if (stepSize * over - value.value < value.value - stepSize * under) over else under
      if (result == numberOfSteps) under - 1 else under
    }

    (if (external.value) 2 pow (valueBits + 2) else 0) +
    (if (powerDown.value) 2 pow (valueBits + 1) else 0) +
    (if (lowPower.value) 2 pow valueBits else 0) +
    convertNumericValueToSteps
  }
}
