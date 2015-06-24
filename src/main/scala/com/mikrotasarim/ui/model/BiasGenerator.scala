package com.mikrotasarim.ui.model

import DeviceInterfaceModel.{MemoryLocation, CommitMemoryLocation}

import scala.collection.immutable.ListMap
import scalafx.beans.property.{DoubleProperty, StringProperty, BooleanProperty}
import scalafx.collections.ObservableBuffer

class BiasGenerator {
  object BiasGeneratorPowerSettings extends MemoryLocation {
    val powerDownTop = new BooleanProperty(this, "top", false) {onChange(CommitMemoryLocation(BiasGeneratorPowerSettings))}
    val powerDownBot = new BooleanProperty(this, "bot", false) {onChange(CommitMemoryLocation(BiasGeneratorPowerSettings))}

    override val address = 81

    override def memoryValue = (if (powerDownBot.value) 1 else 0) + (if (powerDownTop.value) 2 else 0)
  }

  object BiasGeneratorActivator extends MemoryLocation {
    val switch = new BooleanProperty(this, "switch", false) {onChange(CommitMemoryLocation(BiasGeneratorActivator))}

    override val address = 40

    override def memoryValue = if (switch.value) 5 else 4
  }

  object BiasGeneratorTestSettings extends MemoryLocation {

    val voltageTests = ListMap(
      "No voltage test" -> "0",
      "H" -> "1100111",
      "L" -> "1100110",
      "DAC0" -> "1100101",
      "DAC1" -> "1100100",
      "DAC2"-> "1100011",
      "DAC3"-> "1100010",
      "DAC4"-> "1100001",
      "DAC5"-> "1100000",
      "DAC6"-> "1011111",
      "DAC7"-> "1011110",
      "DAC8"-> "1011101",
      "DAC9"-> "1011100",
      "DAC10"-> "1011011",
      "DAC11"-> "1011010",
      "DAC12"-> "1011001",
      "DAC13"-> "1011000",
      "DAC14"-> "1010111",
      "DAC15"-> "1010110",
      "DAC16"-> "1010101",
      "DAC17"-> "1010100",
      "DAC18"-> "1010011",
      "DAC19"-> "1010010",
      "DAC20"-> "1010001",
      "DAC21"-> "1010000",
      "DAC22"-> "1001111",
      "DAC23"-> "1001110",
      "DAC24"-> "1001101",
      "DAC25"-> "1001100",
      "DAC26"-> "1001011",
      "DAC27"-> "1001010",
      "DAC28"-> "1001001",
      "sub"-> "1001000",
      "vbg"-> "1000111",
      "vdda"-> "1000110",
      "vhigh"-> "1000101",
      "vncas"-> "1000100",
      "vpbias"-> "1000011",
      "vpcas"-> "1000010",
      "vssa"-> "1000001",
      "vtemp" -> "1000000"
    )

    val voltageTestLabels = ObservableBuffer(voltageTests.keys.toList)

    var selectedVoltageTest = StringProperty("No voltage test")

    selectedVoltageTest.onChange(CommitMemoryLocation(this))

    var selectedCurrentTest = StringProperty("No current test")

    selectedCurrentTest.onChange(CommitMemoryLocation(this))

    val currentTests = ListMap (
      "No current test" -> "0000",
      "itest_bg" -> "1000",
      "itest<0>" -> "1001",
      "itest<1>" -> "1010",
      "itest<2>" -> "1011"
    )

    val currentTestLabels = ObservableBuffer(currentTests.keys.toList)

    override val address = 39

    override def memoryValue = Integer.parseInt(voltageTests(selectedVoltageTest.value), 2) * 128 +
      Integer.parseInt(currentTests(selectedCurrentTest.value), 2)
  }

  def CreateBiasGeneratorCurrentDac(label: String, address: Int, defaultValue: Int) =
    new DacControlModel(label, defaultValue, (0, 128), address, 7, CommitMemoryLocation)

  def CreateBiasGeneratorVoltageDac(label: String, address: Int, defaultValue: Double) =
    new DacControlModel(label, defaultValue, (0, 3), address, 12, CommitMemoryLocation)

  val biasGeneratorVoltageDacs = ObservableBuffer(
    CreateBiasGeneratorVoltageDac("vin_p_test", 80, 1.65),
    CreateBiasGeneratorVoltageDac("vin_n_test", 79, 1.65),
    CreateBiasGeneratorVoltageDac("vcm_pga", 74, 1.65),
    CreateBiasGeneratorVoltageDac("vcm_adc", 63, 1.65),
    CreateBiasGeneratorVoltageDac("vref_high", 60, 2.65),
    CreateBiasGeneratorVoltageDac("vref_low", 57, 0.65),
    CreateBiasGeneratorVoltageDac("vref_mid", 54, 1.65)
  )

  val power = DoubleProperty(4)
  val powerDown = BooleanProperty(value = false)
  val lowPower = BooleanProperty(value = false)
  val powerChanged = BooleanProperty(value = false)

  power.onChange(powerChanged.set(true))
  powerDown.onChange(powerChanged.set(true))
  lowPower.onChange(powerChanged.set(true))

  def CommitPower(): Unit = {
    for (b <- biasGeneratorCurrentDacs) b.powerDown.set(powerDown.value)
    for (b <- biasGeneratorCurrentDacs) b.lowPower.set(lowPower.value)
    biasGeneratorCurrentDacs(0).value.set(power.value * 10)
    biasGeneratorCurrentDacs(1).value.set(power.value * 5)
    biasGeneratorCurrentDacs(2).value.set(power.value * 5)
    biasGeneratorCurrentDacs(3).value.set(power.value * 4)
    biasGeneratorCurrentDacs(4).value.set(power.value * 4)
    biasGeneratorCurrentDacs(5).value.set(power.value * 4)
    biasGeneratorCurrentDacs(6).value.set(power.value * 5)
    biasGeneratorCurrentDacs(7).value.set(power.value * 4)
    biasGeneratorCurrentDacs(0).Commit()
    biasGeneratorCurrentDacs(1).Commit()
    biasGeneratorCurrentDacs(2).Commit()
    biasGeneratorCurrentDacs(3).Commit()
    biasGeneratorCurrentDacs(4).Commit()
    biasGeneratorCurrentDacs(5).Commit()
    biasGeneratorCurrentDacs(6).Commit()
    biasGeneratorCurrentDacs(7).Commit()
    powerChanged.set(false)
  }

  def ResetPower(): Unit = {
    power.set(4)
    powerDown.set(false)
    lowPower.set(false)
    CommitPower()
  }

  val biasGeneratorCurrentDacs = ObservableBuffer(
    CreateBiasGeneratorCurrentDac("ibias_predrv", 51, 40),
    CreateBiasGeneratorCurrentDac("ibi_pga", 50, 18),
    CreateBiasGeneratorCurrentDac("ibias_cm_pga", 49, 18),
    CreateBiasGeneratorCurrentDac("ibias_adc", 48, 16),
    CreateBiasGeneratorCurrentDac("ibias_cm_adc", 47, 16),
    CreateBiasGeneratorCurrentDac("ibias_ref_high", 46, 16),
    CreateBiasGeneratorCurrentDac("ibias_ref_low", 45, 18),
    CreateBiasGeneratorCurrentDac("ibias_ref_mid", 44, 16)
  )
}
