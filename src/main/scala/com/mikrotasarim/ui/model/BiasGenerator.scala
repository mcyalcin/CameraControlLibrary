package com.mikrotasarim.ui.model

import DeviceInterfaceModel.{MemoryLocation, CommitMemoryLocation}

import scala.collection.immutable.ListMap
import scalafx.beans.property.{StringProperty, BooleanProperty}
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
      "H" -> "1000000",
      "L" -> "1000001",
      "DAC0" -> "1000010",
      "DAC1" -> "1000011",
      "DAC2"-> "1000100",
      "DAC3"-> "1000101",
      "DAC4"-> "1000110",
      "DAC5"-> "1000111",
      "DAC6"-> "1001000",
      "DAC7"-> "1001001",
      "DAC8"-> "1001010",
      "DAC9"-> "1001011",
      "DAC10"-> "1001100",
      "DAC11"-> "1001101",
      "DAC12"-> "1001110",
      "DAC13"-> "1001111",
      "DAC14"-> "1010000",
      "DAC15"-> "1010001",
      "DAC16"-> "1010010",
      "DAC17"-> "1010011",
      "DAC18"-> "1010100",
      "DAC19"-> "1010101",
      "DAC20"-> "1010110",
      "DAC21"-> "1010111",
      "DAC22"-> "1011000",
      "DAC23"-> "1011001",
      "DAC24"-> "1011010",
      "DAC25"-> "1011011",
      "DAC26"-> "1011100",
      "DAC27"-> "1011101",
      "DAC28"-> "1011110",
      "sub"-> "1011111",
      "vbg"-> "1100000",
      "vdda"-> "1100001",
      "vhigh"-> "1100010",
      "vncas"-> "1100011",
      "vpbias"-> "1100101",
      "vpcas"-> "1100101",
      "vssa"-> "1100110",
      "vtemp" -> "1100111"
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
    CreateBiasGeneratorVoltageDac("vin_p_ref", 80, 1.65),
    CreateBiasGeneratorVoltageDac("vin_n_ref", 79, 1.65),
    CreateBiasGeneratorVoltageDac("vpcas_predrv", 78, 1.8),
    CreateBiasGeneratorVoltageDac("vncas_predrv", 77, 1.5),
    CreateBiasGeneratorVoltageDac("vpcas_pga", 76, 1.8),
    CreateBiasGeneratorVoltageDac("vncas_pga", 75, 1.5),
    CreateBiasGeneratorVoltageDac("vcm_pga", 74, 1.65),
    CreateBiasGeneratorVoltageDac("vpcas_cm_pga", 73, 1.65),
    CreateBiasGeneratorVoltageDac("vncas_cm_pga", 72, 1.65),
    CreateBiasGeneratorVoltageDac("vpcas_1_adc", 71, 1.8),
    CreateBiasGeneratorVoltageDac("vncas_1_adc", 70, 1.5),
    CreateBiasGeneratorVoltageDac("vncas_p_1_adc", 69, 1.55),
    CreateBiasGeneratorVoltageDac("vpcas_2_adc", 68, 1.8),
    CreateBiasGeneratorVoltageDac("vncas_2_adc", 67, 1.5),
    CreateBiasGeneratorVoltageDac("vncas_p_2_adc", 66, 1.55),
    CreateBiasGeneratorVoltageDac("vpcas_3_adc", 65, 1.8),
    CreateBiasGeneratorVoltageDac("vncas_3_adc", 64, 1.5),
    CreateBiasGeneratorVoltageDac("vcm_adc", 63, 1.65),
    CreateBiasGeneratorVoltageDac("vpcas_cm_adc", 62, 1.65),
    CreateBiasGeneratorVoltageDac("vncas_cm_adc", 61, 1.65),
    CreateBiasGeneratorVoltageDac("vref_high", 60, 2.65),
    CreateBiasGeneratorVoltageDac("vpcas_ref_high", 59, 1.65),
    CreateBiasGeneratorVoltageDac("vncas_ref_high", 58, 1.65),
    CreateBiasGeneratorVoltageDac("vref_low", 57, 0.65),
    CreateBiasGeneratorVoltageDac("vpcas_ref_low", 56, 1.65),
    CreateBiasGeneratorVoltageDac("vncas_ref_low", 55, 1.65),
    CreateBiasGeneratorVoltageDac("vref_mid", 54, 1.65),
    CreateBiasGeneratorVoltageDac("vpcas_ref_mid", 53, 1.65),
    CreateBiasGeneratorVoltageDac("vncas_ref_mid", 52, 1.65)
  )

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
