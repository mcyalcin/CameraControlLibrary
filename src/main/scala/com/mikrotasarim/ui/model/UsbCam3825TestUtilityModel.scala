package com.mikrotasarim.ui.model

import com.mikrotasarim.camera.command.factory.UsbCam3825CommandFactory
import com.mikrotasarim.camera.device.{ConsoleMockDeviceInterface, OpalKellyInterface}

import scalafx.beans.property.{BooleanProperty, StringProperty}
import scalafx.collections.ObservableBuffer

object UsbCam3825TestUtilityModel {

  var commandFactory: UsbCam3825CommandFactory = _

  def DeployBitfile() {
    val device = new OpalKellyInterface(bitfilePath.value)
    commandFactory = new UsbCam3825CommandFactory(device)
    UsbCam3825TestUtilityModel.bitfileDeployed.value = true
  }

  def DisconnectFromDevice() {
    commandFactory.MakeDisconnectCommand().Execute()
    UsbCam3825TestUtilityModel.bitfileDeployed.value = false
  }

  val bitfilePath: StringProperty = new StringProperty()
  val bitfileDeployed: BooleanProperty = new BooleanProperty() {value = false}
  val testMode: BooleanProperty = new BooleanProperty() {value = false}

  testMode.onChange(if (testMode.value) {
    bitfileDeployed.value = true
    commandFactory = new UsbCam3825CommandFactory(new ConsoleMockDeviceInterface)
  } else {
    bitfileDeployed.value = false
  })

  private def commitDac(dac: DacControlModel) {
    commandFactory.MakeWriteToAsicMemoryTopCommand(dac.address, dac.memoryValue).Execute()
  }

  private def createTimingGeneratorCurrentDac(label: String, address: Int, defaultValue: Double) =
    new DacControlModel(label, defaultValue, (0, 140), address, 7, commitDac)

  private def createTimingGeneratorVoltageDac(label: String, address: Int, defaultValue: Double) =
    new DacControlModel(label, defaultValue, (0, 3.3), address, 12, commitDac)

  def createBiasGeneratorCurrentDac(label: String, address: Int, defaultValue: Int) =
    new DacControlModel(label, defaultValue, (0, 128), address, 7, commitDac)

  def createBiasGeneratorVoltageDac(label: String, address: Int, defaultValue: Double) =
    new DacControlModel(label, defaultValue, (0, 3), address, 12, commitDac)

  val timingGeneratorVoltageDacs = ObservableBuffer(
    createTimingGeneratorVoltageDac("vpcas_cp", 9, 1.65),
    createTimingGeneratorVoltageDac("vncas_cp", 8, 1.65),
    createTimingGeneratorVoltageDac("vctrl", 7, 1.65)
  )

  val timingGeneratorCurrentDacs = ObservableBuffer(
    createTimingGeneratorCurrentDac("ibias_n_cp", 6, 32),
    createTimingGeneratorCurrentDac("ibias_p_cp", 5, 32),
    createTimingGeneratorCurrentDac("ibias_vi", 4, 1),
    createTimingGeneratorCurrentDac("ibias_vcdl", 3, 6)
  )

  val biasGeneratorVoltageDacs = ObservableBuffer(
    createBiasGeneratorVoltageDac("vin_p_ref", 80, 1.65),
    createBiasGeneratorVoltageDac("vin_n_ref", 79, 1.65),
    createBiasGeneratorVoltageDac("vpcas_predrv", 78, 1.8),
    createBiasGeneratorVoltageDac("vncas_predrv", 77, 1.5),
    createBiasGeneratorVoltageDac("vpcas_pga", 76, 1.8),
    createBiasGeneratorVoltageDac("vncas_pga", 75, 1.5),
    createBiasGeneratorVoltageDac("vcm_pga", 74, 1.65),
    createBiasGeneratorVoltageDac("vpcas_cm_pga", 73, 1.65),
    createBiasGeneratorVoltageDac("vncas_cm_pga", 72, 1.65),
    createBiasGeneratorVoltageDac("vpcas_1_adc", 71, 1.8),
    createBiasGeneratorVoltageDac("vncas_1_adc", 70, 1.5),
    createBiasGeneratorVoltageDac("vncas_p_1_adc", 69, 1.55),
    createBiasGeneratorVoltageDac("vpcas_2_adc", 68, 1.8),
    createBiasGeneratorVoltageDac("vncas_2_adc", 67, 1.5),
    createBiasGeneratorVoltageDac("vncas_p_2_adc", 66, 1.55),
    createBiasGeneratorVoltageDac("vpcas_3_adc", 65, 1.8),
    createBiasGeneratorVoltageDac("vncas_3_adc", 64, 1.5),
    createBiasGeneratorVoltageDac("vcm_adc", 63, 1.65),
    createBiasGeneratorVoltageDac("vpcas_cm_adc", 62, 1.65),
    createBiasGeneratorVoltageDac("vncas_cm_adc", 61, 1.65),
    createBiasGeneratorVoltageDac("vref_high", 60, 2.65),
    createBiasGeneratorVoltageDac("vpcas_ref_high", 59, 1.65),
    createBiasGeneratorVoltageDac("vncas_ref_high", 58, 1.65),
    createBiasGeneratorVoltageDac("vref_low", 57, 0.65),
    createBiasGeneratorVoltageDac("vpcas_ref_low", 56, 1.65),
    createBiasGeneratorVoltageDac("vncas_ref_low", 55, 1.65),
    createBiasGeneratorVoltageDac("vref_mid", 54, 1.65),
    createBiasGeneratorVoltageDac("vpcas_ref_mid", 53, 1.65),
    createBiasGeneratorVoltageDac("vncas_ref_mid", 52, 1.65)
  )

  val biasGeneratorCurrentDacs = ObservableBuffer(
    createBiasGeneratorCurrentDac("ibias_predrv", 51, 40),
    createBiasGeneratorCurrentDac("ibi_pga", 50, 18),
    createBiasGeneratorCurrentDac("ibias_cm_pga", 49, 18),
    createBiasGeneratorCurrentDac("ibias_adc", 48, 16),
    createBiasGeneratorCurrentDac("ibias_cm_adc", 47, 16),
    createBiasGeneratorCurrentDac("ibias_ref_high", 46, 16),
    createBiasGeneratorCurrentDac("ibias_ref_low", 45, 18),
    createBiasGeneratorCurrentDac("ibias_ref_mid", 44, 16)
  )
}