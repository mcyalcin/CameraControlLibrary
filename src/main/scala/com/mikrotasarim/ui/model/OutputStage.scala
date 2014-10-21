package com.mikrotasarim.ui.model

import spire.implicits._
import scala.collection.immutable.ListMap
import scalafx.beans.property.{DoubleProperty, StringProperty, IntegerProperty, BooleanProperty}
import scalafx.collections.ObservableBuffer
import DeviceInterfaceModel.{MemoryLocation, CommitMemoryLocation}

abstract class Pad {
  val cmosLvdsLabels: ObservableBuffer[String]
  val selectedCmosLvds: StringProperty
  val cmosSelected: BooleanProperty
  val singleDifferentialLabels: ObservableBuffer[String]
  val singleSelected: BooleanProperty
  val terminationResolutionLabels: ObservableBuffer[String]
  val selectedLvdsCurrent: StringProperty
  val label: String
  val changed: BooleanProperty
  val power: IntegerProperty
  val enableTermination: BooleanProperty
  val powerDown: BooleanProperty

  def Commit(): Unit
  def Reset(): Unit
}

class BiasCurrent(override val address: Int, val initValue: Int, val maxValue: Double) extends MemoryLocation {
  val resolutions = ListMap(
    3.125 -> "01",
    6.25 -> "10",
    9.375 -> "11"
  )

  val resLabels = ObservableBuffer(resolutions.keys.toList)

  val resolution = DoubleProperty(6.25)

  val displayValue = DoubleProperty(resolution.value * initValue)

  val sliderValue = IntegerProperty(initValue)

  resolution.onChange({
    sliderValue.value = displayValue.value / resolution.value
    UpdateDisplayValue()
  })

  sliderValue.onChange(
    UpdateDisplayValue()
  )

  def UpdateDisplayValue() = {
    changed.value = true
    displayValue.value = sliderValue.value * resolution.value
    if (displayValue.value > maxValue) {
      sliderValue.value = maxValue / resolution.value
      displayValue.value = sliderValue.value * resolution.value
    }
  }

  val changed = BooleanProperty(value = false)

  def Commit() = {
    CommitMemoryLocation(this)
    changed.value = false
  }

  def Reset() = {
    sliderValue.value = initValue
    Commit()
  }

  override def memoryValue = {
    sliderValue.value * 2 + Integer.parseInt(resolutions(resolution.value), 2) * (2 pow 7)
  }
}

class OutputStage {
  def TurnOnLvds(): Unit = {
    for (pad <- pads) {
      pad.enableTermination.value = true
      pad.powerDown.value = false
      pad.selectedLvdsCurrent.value = "7.0 mA"
      pad.Commit()
    }
    delayWord.clkOutDlySel.value = 3
    delayWord.CommitCod()
  }

  class PadImpl(val label: String) extends Pad {

    def UpdateChanged(): Unit = {
      changed.value = committedCmosSelected != cmosSelected.value ||
        committedEnableTermination != enableTermination.value ||
        committedPower != power.value || committedSingleSelected != singleSelected.value ||
        committedPowerDown != powerDown.value || committedHighResolution != highTerminationResolution.value
    }

    val cmosLvdsLabels = ObservableBuffer("CMOS", "LVDS")
    val cmosSelected = BooleanProperty(value = false)
    val selectedCmosLvds = StringProperty("LVDS")
    selectedCmosLvds.onChange(cmosSelected.value = selectedCmosLvds.value == "CMOS")
    cmosSelected.onChange(UpdateChanged())
    var committedCmosSelected = false

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

    val powerDown = BooleanProperty(value = true)
    powerDown.onChange(UpdateChanged())
    var committedPowerDown = true

    val terminationResolutionLabels = ObservableBuffer("3.5 mA", "7.0 mA")
    val highTerminationResolution = BooleanProperty(value = false)
    val selectedLvdsCurrent = StringProperty("3.5 mA")
    selectedLvdsCurrent.onChange(highTerminationResolution.value = selectedLvdsCurrent.value == "7.0 mA")
    highTerminationResolution.onChange(UpdateChanged())
    var committedHighResolution = false

    val changed = BooleanProperty(value = false)

    def Commit() {
      if (committedCmosSelected != cmosSelected.value || committedEnableTermination != enableTermination.value) {
        committedCmosSelected = cmosSelected.value
        committedEnableTermination = enableTermination.value
        CommitMemoryLocation(termCmosWord)
      }
      if (committedPower != power.value || committedSingleSelected != singleSelected.value) {
        committedSingleSelected = singleSelected.value
        committedPower = power.value
        CommitMemoryLocation(memoryLocations(padMap(this)._1))
      }
      if (committedPowerDown != powerDown.value || committedHighResolution != highTerminationResolution.value) {
        committedPowerDown = powerDown.value
        committedHighResolution = highTerminationResolution.value
        CommitMemoryLocation(pdResWord)
      }
      changed.value = false
    }

    def Reset() = {
      selectedCmosLvds.value = "LVDS"
      enableTermination.value = false
      power.value = 3
      singleSelected.value = false
      powerDown.value = false
      selectedLvdsCurrent.value = "3.5 mA"
      Commit()
    }
  }


  val padMap = ListMap(
    new PadImpl("Serial Output 0") ->(30, 0, 0),
    new PadImpl("Serial Output 1") ->(30, 3, 1),
    new PadImpl("Serial Output 2") ->(29, 0, 6),
    new PadImpl("Serial Output 3") ->(29, 3, 7),
    new PadImpl("Frame Valid") ->(30, 6, 2),
    new PadImpl("Data Valid") ->(29, 9, 5),
    new PadImpl("Data Frame Clock") ->(30, 9, 3),
    new PadImpl("Output Data Clock") ->(29, 6, 4)
  )

  val pads = padMap.keys.toList

  val testDataLabels = ObservableBuffer("Test", "Data")
  val testSelected = new BooleanProperty()
  testSelected.onChange(CommitMemoryLocation(muxWord))

  val msbLsbLabels = ObservableBuffer("Least Significant Bit", "Most Significant Bit")
  val selectedMsb = StringProperty("Most Significant Bit")
  val msbSelected = BooleanProperty(value = true)
  selectedMsb.onChange(msbSelected.value = selectedMsb.value == "Most Significant Bit")
  msbSelected.onChange(CommitMemoryLocation(muxWord))

  val inputs = ListMap(
    "ADC0" -> "00",
    "ADC1" -> "01",
    "ADC2" -> "10",
    "ADC3" -> "11"
  )

  val inputLabels = ObservableBuffer(inputs.keys.toList)

  val dataFrameClock = StringProperty("ADC0")
  dataFrameClock.onChange(CommitMemoryLocation(muxWord))

  val selectedOutputs = ObservableBuffer(
    StringProperty("ADC0"),
    StringProperty("ADC1"),
    StringProperty("ADC2"),
    StringProperty("ADC3")
  )
  selectedOutputs(0).onChange(CommitMemoryLocation(muxWord))
  selectedOutputs(1).onChange(CommitMemoryLocation(muxWord))
  selectedOutputs(2).onChange(CommitMemoryLocation(muxWord))
  selectedOutputs(3).onChange(CommitMemoryLocation(muxWord))

  msbSelected.onChange(
    msbLsbHelpText.value =
      if (msbSelected.value)
        "fval dval 13..0"
      else
        "0..13 dval fval"
  )

  val msbLsbHelpText = StringProperty("fval dval 13..0")

  val adcTestMemMdacLabels = ObservableBuffer("Memory", "MDac")
  val adcTestMemMdacSel = BooleanProperty(value = false)
  adcTestMemMdacSel.onChange(CommitMemoryLocation(muxWord))

  val adcTestSelTopLabels = ObservableBuffer("ADC1 Test", "ADC0 Test")
  val adcTestSelTop = BooleanProperty(value = false)
  adcTestSelTop.onChange(CommitMemoryLocation(muxWord))

  val adcTestSelBotLabels = ObservableBuffer("ADC3 Test", "ADC2 Test")
  val adcTestSelBot = BooleanProperty(value = false)
  adcTestSelBot.onChange(CommitMemoryLocation(muxWord))

  val opAmpBias = new BiasCurrent(33, 8, 67.22)
  val driverBias = new BiasCurrent(34, 16, 163)

  class drivePowerMemoryLocation(override val address: Int) extends MemoryLocation {
    override def memoryValue: Long = {
      padMap.keys.toList.filter(padMap(_)._1 == address).map(p => p.committedPower * (2 pow padMap(p)._2)).sum
    }
  }

  object termCmosWord extends MemoryLocation {
    override val address = 31

    override def memoryValue = {
      padMap.keys.toList.filter(_.committedEnableTermination).map(p => 2 pow (8 + padMap(p)._3)).sum +
        padMap.keys.toList.filter(_.committedCmosSelected).map(2 pow padMap(_)._3).sum
    }
  }

  object pdResWord extends MemoryLocation {
    override val address = 32

    override def memoryValue = {
      padMap.keys.toList.filter(_.committedPowerDown).map(p => 2 pow (8 + padMap(p)._3)).sum +
        padMap.keys.toList.filter(_.committedHighResolution).map(2 pow padMap(_)._3).sum
    }
  }

  object muxWord extends MemoryLocation {
    override val address = 35

    override def memoryValue = {
      (if (adcTestMemMdacSel.value) 2 pow 14 else 0) +
        (if (msbSelected.value) 2 pow 13 else 0) +
        (if (testSelected.value) 2 pow 12 else 0) +
        (if (adcTestSelTop.value) 2 pow 11 else 0) +
        (if (adcTestSelBot.value) 2 pow 10 else 0) +
        Integer.parseInt(inputs(dataFrameClock.value), 2) * (2 pow 8) +
        Integer.parseInt(inputs(selectedOutputs(3).value), 2) * (2 pow 6) +
        Integer.parseInt(inputs(selectedOutputs(2).value), 2) * (2 pow 4) +
        Integer.parseInt(inputs(selectedOutputs(1).value), 2) * (2 pow 2) +
        Integer.parseInt(inputs(selectedOutputs(0).value), 2)
    }
  }

  object delayWord extends MemoryLocation {

    val clkOutDlySel = IntegerProperty(15)
    clkOutDlySel.onChange(clkOutDlyChanged.value = committedClkOutDly != clkOutDlySel.value)
    val clkOutDlyChanged = BooleanProperty(value = false)
    var committedClkOutDly = 15

    val fvalDlySel = IntegerProperty(1)
    fvalDlySel.onChange(fvalDlyChanged.value = committedFval != fvalDlySel.value)
    val fvalDlyChanged = BooleanProperty(value = false)
    var committedFval = 1

    val dvalDlySel = IntegerProperty(1)
    dvalDlySel.onChange(dvalDlyChanged.value = committedDval != dvalDlySel.value)
    val dvalDlyChanged = BooleanProperty(value = false)
    var committedDval = 1

    override val address: Int = 36

    override def memoryValue: Long =
      clkOutDlySel.value * (2 pow 8) +
        fvalDlySel.value * (2 pow 4) +
        dvalDlySel.value

    def CommitCod() = {
      committedClkOutDly = clkOutDlySel.value
      CommitMemoryLocation(this)
      clkOutDlyChanged.value = false
    }

    def CommitFval() = {
      committedFval = fvalDlySel.value
      CommitMemoryLocation(this)
      fvalDlyChanged.value = false
    }

    def CommitDval() = {
      committedDval = dvalDlySel.value
      CommitMemoryLocation(this)
      dvalDlyChanged.value = false
    }

    def ResetCod() = {
      clkOutDlySel.value = 15
      CommitCod()
    }

    def ResetFval() = {
      fvalDlySel.value = 1
      CommitFval()
    }

    def ResetDval() = {
      dvalDlySel.value = 1
      CommitDval()
    }
  }

  object moreDelayWord extends MemoryLocation {

    val coarseDlySel = IntegerProperty(2)
    coarseDlySel.onChange(coarseDlyChanged.value = committedCoarseDly != coarseDlySel.value)
    val coarseDlyChanged = BooleanProperty(value = false)
    var committedCoarseDly = 2

    val clkInDlySel = IntegerProperty(5)
    clkInDlySel.onChange(clkInDlyChanged.value = committedClkInDelay != clkInDlySel.value)
    val clkInDlyChanged = BooleanProperty(value = false)
    var committedClkInDelay = 5

    override val address: Int = 37

    override def memoryValue: Long =
      coarseDlySel.value * (2 pow 8) + clkInDlySel.value

    def CommitCoarseDelay() = {
      committedCoarseDly = coarseDlySel.value
      coarseDlyChanged.value = false
      CommitMemoryLocation(this)
    }

    def CommitClkInDelay() = {
      committedClkInDelay = clkInDlySel.value
      clkInDlyChanged.value = false
      CommitMemoryLocation(this)
    }

    def ResetCoarseDelay() = {
      coarseDlySel.value = 2
      CommitCoarseDelay()
    }

    def ResetClkInDelay() = {
      clkInDlySel.value = 5
      CommitClkInDelay()
    }
  }

  val memoryLocations = Map(
    29 -> new drivePowerMemoryLocation(29),
    30 -> new drivePowerMemoryLocation(30)
  )

  val adcTestMemoryTop = new AdcTestMemory("Top", 28)
  val adcTestMemoryBot = new AdcTestMemory("Bottom", 38)
}

class AdcTestMemory(val label: String, override val address: Int) extends MemoryLocation {

  val hexString = StringProperty("0000")
  val binaryString = StringProperty("0000 0000 0000 0000")

  def Commit() = {
    val string = String.format("%16s", Integer.parseInt(hexString.value, 16).toBinaryString).replace(' ', '0')
    binaryString.value =
      string.substring(0, 4) + " " +
        string.substring(4, 8) + " " +
        string.substring(8, 12) + " " +
        string.substring(12, 16) + " "
    CommitMemoryLocation(this)
  }

  override def memoryValue = Integer.parseInt(hexString.value, 16)
}
