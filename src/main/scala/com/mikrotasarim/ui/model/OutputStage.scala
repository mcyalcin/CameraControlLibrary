package com.mikrotasarim.ui.model

import spire.implicits._
import scala.collection.immutable.ListMap
import scalafx.beans.property.{DoubleProperty, StringProperty, IntegerProperty, BooleanProperty}
import scalafx.collections.ObservableBuffer
import UsbCam3825TestUtilityModel.{MemoryLocation, CommitMemoryLocation}

object OutputStage {

  class Pad(val label: String) {

    def UpdateChanged(): Unit = {
      changed.value = committedCmosSelected != cmosSelected.value ||
        committedEnableTermination != enableTermination.value ||
        committedPower != power.value || committedSingleSelected != singleSelected.value ||
        committedPowerDown != powerDown.value || committedLowResolution != lowTerminationResolution.value
    }

    val cmosLvdsLabels = ObservableBuffer("CMOS", "LVDS")
    val cmosSelected = BooleanProperty(value = true)
    cmosSelected.onChange(UpdateChanged())
    var committedCmosSelected = true

    val power = IntegerProperty(3)
    power.onChange(UpdateChanged())
    var committedPower = 3

    val singleDifferentialLabels = ObservableBuffer("Single", "Differential")
    val singleSelected = BooleanProperty(value = false)
    singleSelected.onChange(UpdateChanged())
    var committedSingleSelected = false

    val enableTermination = BooleanProperty(value = false)
    enableTermination.onChange(UpdateChanged())
    var committedEnableTermination = false

    val powerDown = BooleanProperty(value = false)
    powerDown.onChange(UpdateChanged())
    var committedPowerDown = false

    val terminationResolutionLabels = ObservableBuffer("3.5 mA", "7.0 mA")
    val lowTerminationResolution = BooleanProperty(value = false)
    lowTerminationResolution.onChange(UpdateChanged())
    var committedLowResolution = false

    val changed = BooleanProperty(value = false)

    def Commit() {
      if (committedCmosSelected != cmosSelected.value || committedEnableTermination != enableTermination.value) {
        committedCmosSelected = cmosSelected.value
        committedEnableTermination = enableTermination.value
        CommitMemoryLocation(memoryLocations(31))
      }
      if (committedPower != power.value || committedSingleSelected != singleSelected.value) {
        committedSingleSelected = singleSelected.value
        committedPower = power.value
        CommitMemoryLocation(memoryLocations(padMap(this)._1))
      }
      if (committedPowerDown != powerDown.value || committedLowResolution != lowTerminationResolution.value) {
        committedPowerDown = powerDown.value
        committedLowResolution = lowTerminationResolution.value
        CommitMemoryLocation(memoryLocations(32))
      }
      changed.value = false
    }

    def Reset() = {
      cmosSelected.value = true
      enableTermination.value = false
      power.value = 3
      singleSelected.value = false
      powerDown.value = false
      lowTerminationResolution.value = false
      Commit()
    }
  }

  class BiasCurrent(override val address: Int) extends MemoryLocation {
    val resolutions = ListMap(
      3.125 -> "10",
      6.25 -> "01",
      9.375 -> "11"
    )

    val resLabels = ObservableBuffer(resolutions.keys.toList)

    val resolution = DoubleProperty(3.125)

    override def memoryValue = _
  }

  val padMap = ListMap(
    new Pad("Serial Output 0") ->(30, 0, 0),
    new Pad("Serial Output 1") ->(30, 3, 1),
    new Pad("Serial Output 2") ->(29, 0, 6),
    new Pad("Serial Output 3") ->(29, 3, 7),
    new Pad("Frame Valid") ->(30, 6, 2),
    new Pad("Data Valid") ->(29, 9, 5),
    new Pad("Data Frame Clock") ->(30, 9, 3),
    new Pad("Output Data Clock") ->(29, 6, 4)
  )

  val pads = padMap.keys.toList

  // TODO: Study NumberStringConverter for test memory bindings

  val ibiasOp = new IntegerProperty()
  val ibiasDrv = new IntegerProperty()

  val testDataLabels = ObservableBuffer("Test", "Data")
  val testSelected = new BooleanProperty()

  val msbLsbLabels = ObservableBuffer("Least Significant Bit", "Most Significant Bit")
  val msbSelected = new BooleanProperty()

  val inputLabels = ObservableBuffer("ADC0", "ADC1", "ADC2", "ADC3")

  val dataFrameClock = StringProperty("ADC0")

  val selectedOutputs = ObservableBuffer(
    StringProperty("ADC0"),
    StringProperty("ADC1"),
    StringProperty("ADC2"),
    StringProperty("ADC3")
  )

  msbSelected.onChange(
    msbLsbHelpText.value =
      if (msbSelected.value)
        "0..13 dval fval"
      else
        "fval dval 13..0"
  )

  val msbLsbHelpText = StringProperty("fval dval 13..0")

  val adcTestMemMdacLabels = ObservableBuffer("Memory", "MDac")
  val adcTestMemMdacSel = BooleanProperty(value = false)

  val adcTestSelTopLabels = ObservableBuffer("ADC1 Test", "ADC0 Test")
  val adcTestSelTop = BooleanProperty(value = false)

  val adcTestSelBotLabels = ObservableBuffer("ADC3 Test", "ADC2 Test")
  val adcTestSelBot = BooleanProperty(value = false)

  val clkOutDlySel = IntegerProperty(0)
  val fvalDlySel = IntegerProperty(0)
  val dvalDlySel = IntegerProperty(0)
  val coarseDlySel = IntegerProperty(0)
  val clkInDlySel = IntegerProperty(0)

  val opAmpBias = new BiasCurrent(33)
  val driverBias = new BiasCurrent(34)

  class drivePowerMemoryLocation(override val address: Int) extends MemoryLocation {
    override def memoryValue: Long = {
      padMap.keys.toList.filter(padMap(_)._1 == address).map(p => p.power.value * (2 pow padMap(p)._2)).sum
    }
  }

  object termCmosWord extends MemoryLocation {
    override val address = 31

    override def memoryValue = {
      padMap.keys.toList.filter(_.enableTermination.value).map(p => 2 pow (8 + padMap(p)._3)).sum +
      padMap.keys.toList.filter(_.cmosSelected.value).map(2 pow padMap(_)._3).sum
    }
  }

  object pdResWord extends MemoryLocation {
    override val address = 32

    override def memoryValue = {
      padMap.keys.toList.filter(_.powerDown.value).map(p => 2 pow (8 + padMap(p)._3)).sum +
      padMap.keys.toList.filter(_.lowTerminationResolution.value).map(2 pow padMap(_)._3).sum
    }
  }

  // TODO: Memory location translations for output stage
  val memoryLocations = Map(
    29 -> new drivePowerMemoryLocation(29),
    30 -> new drivePowerMemoryLocation(30),
    31 -> termCmosWord,
    32 -> pdResWord
  )
}
