package com.mikrotasarim.camera.components

import spire.implicits._
import squants.{MetricSystem, Quantity}
import squants.electro.ElectricCurrentUnit

class Dac[T <: Quantity[T]](label: String, memoryAddress: Int, defaultValue: T, range: (T,T), valueBits: Int) {

  var external: Boolean = false
  var powerDown: Boolean = false
  var lowPower: Boolean = false
  var value = defaultValue

  def memoryValue: Long = {
    def convertNumericValueToSteps: Long = {
      if (value > range._2 || value < range._1) throw new Exception("Value out of bounds")
      val numberOfSteps = 2 pow valueBits
      val stepSize = (range._2 - range._1) / numberOfSteps
      ((value - range._1) / stepSize).toLong
    }

    (if (external) 2 pow (valueBits + 2) else 0) +
    (if (powerDown) 2 pow (valueBits + 1) else 0) +
    (if (lowPower) 2 pow valueBits else 0) +
    convertNumericValueToSteps
  }
}

object Microamperes extends ElectricCurrentUnit {
  val symbol = "uA"
  val multiplier = MetricSystem.Micro
}
