package com.mikrotasarim.ui.model

import com.mikrotasarim.camera.command.factory.UsbCam3825CommandFactory
import com.mikrotasarim.camera.device.{ConsoleMockDeviceInterface, OpalKellyInterface}

import spire.implicits._
import scala.collection.immutable.ListMap
import scalafx.beans.property.{IntegerProperty, BooleanProperty, StringProperty}
import scalafx.beans.value.ObservableValue
import scalafx.collections.ObservableBuffer

// TODO: Divide up sub-module models
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

  val testMode: BooleanProperty = new BooleanProperty() {
    value = false
    onChange(if (testMode.value) {
      bitfileDeployed.value = true
      commandFactory = new UsbCam3825CommandFactory(new ConsoleMockDeviceInterface)
    } else {
      bitfileDeployed.value = false
    })
  }

  object ResetControls {
    val fpgaReset = new BooleanProperty() {
      value = true
      onChange(commandFactory.MakeFpgaResetCommand(this.value).Execute())
    }

    val roicReset = new BooleanProperty() {
      value = true
      onChange(commandFactory.MakeRoicResetCommand(this.value).Execute())
    }
  }

  // TODO: Move this into MemoryLocation class
  private def CommitMemoryLocation(memoryLocation: MemoryLocation) =
    commandFactory.MakeWriteToAsicMemoryTopCommand(memoryLocation.address, memoryLocation.memoryValue).Execute()

  object TimingGeneratorMainControls extends MemoryLocation {
    val enable = new BooleanProperty(this, "enable", false) {
      onChange(CommitMemoryLocation(TimingGeneratorMainControls))
    }

    val pwRefValues = ListMap(
      "50%" -> 0,
      "10n" -> 1,
      "30n" -> 2,
      "50n" -> 3
    )

    val pwRefLabels = ObservableBuffer(pwRefValues.keys.toList)

    val pwOutValues = ListMap(
      "50%" -> 0,
      "15n" -> 1
    )

    val pwOutLabels = ObservableBuffer(pwOutValues.keys.toList)

    val selectedPwRef = new StringProperty("30n") {
      onChange(CommitMemoryLocation(TimingGeneratorMainControls))
    }

    val selectedPwOut = new StringProperty("15n") {
      onChange(CommitMemoryLocation(TimingGeneratorMainControls))
    }

    override val address: Int = 19

    override def memoryValue: Long = (if (enable.value) 8 else 0) +
                                     pwOutValues(selectedPwOut.value) * 4 +
                                     pwRefValues(selectedPwRef.value)
  }

  object TimingGeneratorVcdlControls extends MemoryLocation {

    val pdCp = new BooleanProperty(this, "pdCp", false) {
      onChange(CommitMemoryLocation(TimingGeneratorVcdlControls))
    }

    val pdVcdl = new BooleanProperty(this, "pdVcdl", false) {
      onChange(CommitMemoryLocation(TimingGeneratorVcdlControls))
    }

    val vpBiasVcdl = new IntegerProperty(this, "vp", 32) {
      onChange(CommitMemoryLocation(TimingGeneratorVcdlControls))
    }

    val vnBiasVcdl = new IntegerProperty(this, "vn", 32) {
      onChange(CommitMemoryLocation(TimingGeneratorVcdlControls))
    }

    override val address: Int = 11

    override def memoryValue: Long =
      (if (pdCp.value) 2 pow 15 else 0) +
      (if (pdVcdl.value) 2 pow 14 else 0) +
      vpBiasVcdl.value * (2 pow 7) +
      vnBiasVcdl.value
  }

  private def CreateTimingGeneratorCurrentDac(label: String, address: Int, defaultValue: Double) =
    new DacControlModel(label, defaultValue, (0, 140), address, 7, CommitMemoryLocation)

  private def CreateTimingGeneratorVoltageDac(label: String, address: Int, defaultValue: Double) =
    new DacControlModel(label, defaultValue, (0, 3.3), address, 12, CommitMemoryLocation)

  private def CreateBiasGeneratorCurrentDac(label: String, address: Int, defaultValue: Int) =
    new DacControlModel(label, defaultValue, (0, 128), address, 7, CommitMemoryLocation)

  private def CreateBiasGeneratorVoltageDac(label: String, address: Int, defaultValue: Double) =
    new DacControlModel(label, defaultValue, (0, 3), address, 12, CommitMemoryLocation)

  val timingGeneratorVoltageDacs = ObservableBuffer(
    CreateTimingGeneratorVoltageDac("vpcas_cp", 9, 1.65),
    CreateTimingGeneratorVoltageDac("vncas_cp", 8, 1.65),
    CreateTimingGeneratorVoltageDac("vctrl", 7, 1.65)
  )

  val timingGeneratorCurrentDacs = ObservableBuffer(
    CreateTimingGeneratorCurrentDac("ibias_n_cp", 6, 32),
    CreateTimingGeneratorCurrentDac("ibias_p_cp", 5, 32),
    CreateTimingGeneratorCurrentDac("ibias_vi", 4, 1),
    CreateTimingGeneratorCurrentDac("ibias_vcdl", 3, 6)
  )

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

  val phaseSignals = ObservableBuffer(
    CreatePhaseSignal("pga_s", "10000101111110", 13),
    CreatePhaseSignal("pga_a", "00001000111100", 14),
    CreatePhaseSignal("adc_odd_s", "10000101111110", 15),
    CreatePhaseSignal("adc_odd_a", "10000101111110", 16),
    CreatePhaseSignal("adc_even_s", "10001001111100", 17),
    CreatePhaseSignal("adc_even_a", "00000100111110", 18)
  )

  val lockPhaseSignals = new BooleanProperty(this, "lockPhaseSignals", false)
  val phaseSignalsChanged = new BooleanProperty(this, "phaseSignalsChanged", false)

  class PhaseSignal(val label: String, val fallen : Int, val risen : Int, val address: Int) {
    val fall = new IntegerProperty(this, "fall", fallen)
    val rise = new IntegerProperty(this, "rise", risen)

    fall.onChange((changed, oldValue, newValue) => {
      sliderChangedOnLock(changed, newValue.intValue - oldValue.intValue)
      phaseSignalsChanged.value = true
    })

    rise.onChange((changed, oldValue, newValue) => {
      sliderChangedOnLock(changed, newValue.intValue - oldValue.intValue)
      phaseSignalsChanged.value = true
    })

    def sliderChangedOnLock(changed: ObservableValue[Int, Number], offset: Int) {
      if (lockPhaseSignals.value) {
        lockPhaseSignals.value = false

        for (phaseSignal <- phaseSignals) {
          if (phaseSignal.fall != changed) {
            phaseSignal.fall.value = (phaseSignal.fall.value + offset + 128) % 128
          }
          if (phaseSignal.rise != changed) {
            phaseSignal.rise.value = (phaseSignal.rise.value + offset + 128) % 128
          }
        }
        lockPhaseSignals.value = true
      }
    }

    def reset() {
      fall.value = fallen
      rise.value = risen
    }

    def memoryValue = fall.value * 128 + rise.value
  }

  private def CreatePhaseSignal(label: String, defaultValueString: String, address: Int): PhaseSignal = {
    new PhaseSignal(label, Integer.parseInt(defaultValueString.substring(0,7),2), Integer.parseInt(defaultValueString.substring(7),2), address)
  }

  def CommitPhaseSignals() {
    for (phaseSignal <- phaseSignals) {
      commandFactory.MakeWriteToAsicMemoryTopCommand(phaseSignal.address, phaseSignal.memoryValue).Execute()
    }
    phaseSignalsChanged.value = false
  }

  def ResetPhaseSignals(): Unit = {
    lockPhaseSignals.value = false
    for (phaseSignal <- phaseSignals) {
      phaseSignal.reset()
    }
    CommitPhaseSignals()
  }

  val powerReferences = ObservableBuffer("20", "50", "40%", "99%")

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
      "No current test" -> "0",
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

  abstract class MemoryLocation {
    val address: Int
    def memoryValue: Long
  }
}