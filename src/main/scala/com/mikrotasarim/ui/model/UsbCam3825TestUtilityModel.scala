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

  // TODO: The dependency to commandFactory here impedes partitioning of this. Think of a good refactoring.
  def CommitMemoryLocation(memoryLocation: MemoryLocation) =
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

  object TimingGeneratorAnalogTestControls extends MemoryLocation {
    val selLfCap = new BooleanProperty(this, "selLfCap", false)
    selLfCap.onChange(CommitMemoryLocation(this))
    val enVctrlExt = new BooleanProperty(this, "enVctrlExt", false)
    enVctrlExt.onChange(CommitMemoryLocation(this))
    val selVctrl = new IntegerProperty(this, "selVctrl", 2)
    selVctrl.onChange(CommitMemoryLocation(this))

    val analogCurrentTests = ListMap(
      "No current test" -> "0000",
      "itest<0>" -> "1000",
      "itest<1>" -> "1001",
      "itest<2>" -> "1010",
      "itest<3>" -> "1011",
      "itest_cp" -> "1100",
      "itest_vi" -> "1101",
      "itest_tune_p" -> "1110",
      "itest_tune_n" -> "1111"
    )

    val analogVoltageTests = ListMap(
      "No voltage test" -> "0000000",
      "vdda_dac" -> "1011110",
      "vssa_dac" -> "1011111",
      "vpbias" -> "1100000",
      "vpcas" -> "1100001",
      "vncas" -> "1100010",
      "vctrl_test" -> "1100011",
      "vncas_cp" -> "1100100",
      "vpcas_cp" -> "1100101",
      "L" -> "1100110",
      "H" -> "1100111"
    )

    val analogCurrentTestLabels = ObservableBuffer(analogCurrentTests.keys.toList)
    val analogVoltageTestLabels = ObservableBuffer(analogVoltageTests.keys.toList)

    val selectedCurrentTest = new StringProperty("No current test")
    selectedCurrentTest.onChange(CommitMemoryLocation(this))

    val selectedVoltageTest = new StringProperty("No voltage test")
    selectedVoltageTest.onChange(CommitMemoryLocation(this))

    override val address: Int = 12

    override def memoryValue: Long =
      (if (selLfCap.value) 2 pow 15 else 0) +
      selVctrl.value * (2 pow 12) +
      (if (enVctrlExt.value) 2 pow 11 else 0) +
      Integer.parseInt(analogCurrentTests(selectedCurrentTest.value),2) * (2 pow 7) +
      Integer.parseInt(analogVoltageTests(selectedVoltageTest.value),2)
  }

  object TimingGeneratorDigitalTestControls extends MemoryLocation {

    val testOutput0 = ListMap(
      "No signal selected" -> (0,0,0),
      "ref_clk" -> (1,0,0),
      "ref_clk_buf" -> (1,0,1),
      "pga_s_rise" -> (1,1,0),
      "pga_s_fall" -> (1,1,2)
    )
    val testOutput1 = ListMap(
      "No signal selected" -> (0,0,0),
      "pga_a_rise" -> (1,0,0),
      "pga_a_fall" -> (1,0,1),
      "adc_odd_s_rise" -> (1,1,0),
      "adc_odd_s_fall" -> (1,1,2)
    )
    val testOutput2 = ListMap(
      "No signal selected" -> (0,0,0),
      "adc_odd_a_rise" -> (1,0,0),
      "adc_odd_a_fall" -> (1,0,1),
      "adc_even_s_rise" -> (1,1,0),
      "adc_even_s_fall" -> (1,1,2)
    )
    val testOutput3 = ListMap(
      "No signal selected" -> (0,0,0),
      "adc_even_a_rise" -> (1,0,0),
      "adc_even_a_fall" -> (1,0,1),
      "phi_pga_s" -> (1,1,0),
      "phi_pga_a" -> (1,1,2)
    )
    val testOutput4 = ListMap(
      "No signal selected" -> (0,0,0),
      "phi_adc_odd_s" -> (1,0,0),
      "phi_adc_odd_a" -> (1,0,1),
      "phi_adc_even_s" -> (1,1,0),
      "phi_adc_even_a" -> (1,1,2)
    )

    val testOutput0Labels = ObservableBuffer(testOutput0.keys.toList)
    val testOutput1Labels = ObservableBuffer(testOutput1.keys.toList)
    val testOutput2Labels = ObservableBuffer(testOutput2.keys.toList)
    val testOutput3Labels = ObservableBuffer(testOutput3.keys.toList)
    val testOutput4Labels = ObservableBuffer(testOutput4.keys.toList)

    val selectedTestOutput0 = new StringProperty("No signal selected") {
      onChange(CommitMemoryLocation(TimingGeneratorDigitalTestControls))
    }
    val selectedTestOutput1 = new StringProperty("No signal selected"){
      onChange(CommitMemoryLocation(TimingGeneratorDigitalTestControls))
    }
    val selectedTestOutput2 = new StringProperty("No signal selected"){
      onChange(CommitMemoryLocation(TimingGeneratorDigitalTestControls))
    }
    val selectedTestOutput3 = new StringProperty("No signal selected"){
      onChange(CommitMemoryLocation(TimingGeneratorDigitalTestControls))
    }
    val selectedTestOutput4 = new StringProperty("No signal selected"){
      onChange(CommitMemoryLocation(TimingGeneratorDigitalTestControls))
    }

    override val address: Int = 20

    override def memoryValue: Long =
      List(testOutput0(selectedTestOutput0.value)._1,
           testOutput1(selectedTestOutput1.value)._1,
           testOutput2(selectedTestOutput2.value)._1,
           testOutput3(selectedTestOutput3.value)._1,
           testOutput4(selectedTestOutput4.value)._1).max * (2 pow 15) +
      testOutput4(selectedTestOutput4.value)._2 * (2 pow 14) +
      testOutput3(selectedTestOutput3.value)._2 * (2 pow 13) +
      testOutput2(selectedTestOutput2.value)._2 * (2 pow 12) +
      testOutput1(selectedTestOutput1.value)._2 * (2 pow 11) +
      testOutput0(selectedTestOutput0.value)._2 * (2 pow 10) +
      testOutput4(selectedTestOutput4.value)._3 * (2 pow 8) +
      testOutput3(selectedTestOutput3.value)._3 * (2 pow 6) +
      testOutput2(selectedTestOutput2.value)._3 * (2 pow 4) +
      testOutput1(selectedTestOutput1.value)._3 * (2 pow 2) +
      testOutput0(selectedTestOutput0.value)._3
  }

  object VcdlBiasCurrent extends MemoryLocation {
    val iBiasVcdl = new IntegerProperty(this, "i", 16) {
      onChange(CommitMemoryLocation(VcdlBiasCurrent))
    }

    override val address = 10

    override def memoryValue = iBiasVcdl.value
  }

  def CreateTimingGeneratorCurrentDac(label: String, address: Int, defaultValue: Double) =
    new DacControlModel(label, defaultValue, (0, 140), address, 7, CommitMemoryLocation)

  def CreateTimingGeneratorVoltageDac(label: String, address: Int, defaultValue: Double) =
    new DacControlModel(label, defaultValue, (0, 3.3), address, 12, CommitMemoryLocation)

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

  trait MemoryLocation {
    val address: Int
    def memoryValue: Long
  }

  class AdcChannelClockSettings(val address: Int) extends MemoryLocation {

    val selAdcClk =
      List(new BooleanProperty(this, "selAdcClk0", false),
      new BooleanProperty(this, "selAdcClk1", false))
    selAdcClk(0).onChange(CommitMemoryLocation(this))
    selAdcClk(1).onChange(CommitMemoryLocation(this))
    val selPgaClk =
      List(new BooleanProperty(this, "selPgaClk0", false),
      new BooleanProperty(this, "selPgaClk1", false))
    selPgaClk(0).onChange(CommitMemoryLocation(this))
    selPgaClk(1).onChange(CommitMemoryLocation(this))
    val enAdcClk =
      List(new BooleanProperty(this, "enAdcClk0", false),
      new BooleanProperty(this, "enAdcClk1", false))
    enAdcClk(0).onChange(CommitMemoryLocation(this))
    enAdcClk(1).onChange(CommitMemoryLocation(this))
    val enPgaClk =
      List(new BooleanProperty(this, "enPgaClk0", false),
      new BooleanProperty(this, "enPgaClk1", false))
    enPgaClk(0).onChange(CommitMemoryLocation(this))
    enPgaClk(1).onChange(CommitMemoryLocation(this))
    val selClkMode =
      List(new BooleanProperty(this, "selClkMode0", false),
      new BooleanProperty(this, "selClkMode1", false))
    selClkMode(0).onChange(CommitMemoryLocation(this))
    selClkMode(1).onChange(CommitMemoryLocation(this))

    override def memoryValue: Long =
      (if (selAdcClk(1).value) 2 pow 9 else 0) +
      (if (selAdcClk(0).value) 2 pow 8 else 0) +
      (if (selPgaClk(1).value) 2 pow 7 else 0) +
      (if (selPgaClk(0).value) 2 pow 6 else 0) +
      (if (enAdcClk(1).value) 2 pow 5 else 0) +
      (if (enAdcClk(0).value) 2 pow 4 else 0) +
      (if (enPgaClk(1).value) 2 pow 3 else 0) +
      (if (enPgaClk(0).value) 2 pow 2 else 0) +
      (if (selClkMode(1).value) 2 pow 1 else 0) +
      (if (selClkMode(0).value) 1 else 0)
  }

  class AdcChannelTimingSliders(override val address: Int) extends MemoryLocation {

    val saMargin = new IntegerProperty()
    saMargin.onChange(CommitMemoryLocation(this))
    val nod = new IntegerProperty()
    nod.onChange(CommitMemoryLocation(this))
    val delay = new IntegerProperty()
    delay.onChange(CommitMemoryLocation(this))

    override def memoryValue: Long =
      saMargin.value * (2 pow 8) +
      nod.value * (2 pow 4) +
      delay.value
  }

  class AdcChannelInputDriveControls(override val address: Int) extends MemoryLocation {

    val pdn = List(
      new BooleanProperty(this, "pdn0", false),
      new BooleanProperty(this, "pdn1", false)
    )
    pdn(0).onChange(CommitMemoryLocation(this))
    pdn(1).onChange(CommitMemoryLocation(this))

    val pdp = List(
      new BooleanProperty(this, "pdp0", false),
      new BooleanProperty(this, "pdp1", false)
    )
    pdp(0).onChange(CommitMemoryLocation(this))
    pdp(1).onChange(CommitMemoryLocation(this))

    val shortRefEnable = new BooleanProperty(this, "sre", false)

    val bwPredrv = new IntegerProperty()

    def memoryValue =
      bwPredrv.value * (2 pow 5) +
      (if (pdn(1).value) 2 pow 4 else 0) +
      (if (pdn(0).value) 2 pow 3 else 0) +
      (if (pdp(1).value) 2 pow 2 else 0) +
      (if (pdp(0).value) 2 else 0) +
      (if (shortRefEnable.value) 1 else 0)
  }

  class AdcChannelPgaSettings(override val address: Int) extends MemoryLocation {

    val bwPga = new IntegerProperty()
    bwPga.onChange(CommitMemoryLocation(this))

    val pdPga = List(
      new BooleanProperty(this, "pdPga0", false),
      new BooleanProperty(this, "pdPga1", false)
    )
    pdPga(0).onChange(CommitMemoryLocation(this))
    pdPga(1).onChange(CommitMemoryLocation(this))

    val gainSettings = ListMap(
      "2.000" -> "0000",
      "1.875" -> "0001",
      "1.760" -> "0010",
      "1.670" -> "0011",
      "1.580" -> "0100",
      "1.500" -> "0101",
      "1.430" -> "0110",
      "1.360" -> "0111",
      "1.300" -> "1000",
      "1.250" -> "1001",
      "1.200" -> "1010",
      "1.150" -> "1011",
      "1.110" -> "1100",
      "1.070" -> "1101",
      "1.030" -> "1110",
      "1.000" -> "1111"
    )

    val gainLabels = ObservableBuffer(gainSettings.keys.toList)
    val selectedGain = new StringProperty("1.500")

    selectedGain.onChange(CommitMemoryLocation(this))

    override def memoryValue =
      bwPga.value * (2 pow 6) +
      (Integer.parseInt(gainSettings(selectedGain.value),2) * (2 pow 2)) +
      (if (pdPga(1).value) 2 else 0) +
      (if (pdPga(0).value) 1 else 0)
  }

  class AdcChannelMiscControls(override val address: Int) extends MemoryLocation {

    var enableMdacTest = new BooleanProperty()
    enableMdacTest.onChange(CommitMemoryLocation(this))
    var extRefEnable = new BooleanProperty()
    extRefEnable.onChange(CommitMemoryLocation(this))
    var externalRefDacDrv = new BooleanProperty()
    externalRefDacDrv.onChange(CommitMemoryLocation(this))

    var pdAdcRefDrv = List(
      new BooleanProperty(),
      new BooleanProperty()
    )
    pdAdcRefDrv(0).onChange(CommitMemoryLocation(this))
    pdAdcRefDrv(1).onChange(CommitMemoryLocation(this))
    var pdPgaRefDrv = List(
      new BooleanProperty(),
      new BooleanProperty()
    )
    pdPgaRefDrv(0).onChange(CommitMemoryLocation(this))
    pdPgaRefDrv(1).onChange(CommitMemoryLocation(this))
    var pdAdc = List(
      new BooleanProperty(),
      new BooleanProperty()
    )
    pdAdc(0).onChange(CommitMemoryLocation(this))
    pdAdc(1).onChange(CommitMemoryLocation(this))
    var rstbDigCor = List(
      new BooleanProperty(),
      new BooleanProperty()
    )
    rstbDigCor(0).onChange(CommitMemoryLocation(this))
    rstbDigCor(1).onChange(CommitMemoryLocation(this))

    override def memoryValue =
      (if (enableMdacTest.value) 2 pow 10 else 0) +
      (if (extRefEnable.value) 2 pow 9 else 0) +
      (if (pdAdcRefDrv(1).value) 2 pow 8 else 0) +
      (if (pdAdcRefDrv(0).value) 2 pow 7 else 0) +
      (if (pdPgaRefDrv(1).value) 2 pow 6 else 0) +
      (if (pdPgaRefDrv(0).value) 2 pow 5 else 0) +
      (if (pdAdc(1).value) 2 pow 4 else 0) +
      (if (pdAdc(0).value) 2 pow 3 else 0) +
      (if (rstbDigCor(1).value) 2 pow 2 else 0) +
      (if (rstbDigCor(0).value) 2 else 0) +
      (if (externalRefDacDrv.value) 1 else 0)
  }

  class AdcChannelMode(override val address: Int) extends MemoryLocation {

    val channelModes = ListMap(
      "External Drive - PGA - ADC" -> "01000",
      "External Drive - ADC" -> "00100",
      "On-chip Drive - PGA - ADC" -> "00000",
      "On-chip Drive - ADC" -> "10000",
      "DAC reference input - PGA - ADC" -> "00010",
      "DAC reference input - ADC" -> "00001"
    )

    val channelModeLabels = ObservableBuffer(channelModes.keys.toList)

    val selectedMode = List(
      new StringProperty("On-chip Drive - PGA - ADC"),
      new StringProperty("On-chip Drive - PGA - ADC")
    )

    selectedMode(0).onChange(CommitMemoryLocation(this))
    selectedMode(1).onChange(CommitMemoryLocation(this))

    override def memoryValue =
      Integer.parseInt(channelModes(selectedMode(1).value),2) * (2 pow 5) +
      Integer.parseInt(channelModes(selectedMode(0).value),2)
  }

  class AdcChannelSettings(baseAddress: Int) {
    val clockSettings = new AdcChannelClockSettings(baseAddress)
    val timingSliders = new AdcChannelTimingSliders(baseAddress+1)
    val inputDriveControls = new AdcChannelInputDriveControls(baseAddress+2)
    val modePositive = new AdcChannelMode(baseAddress + 3)
    val modeNegative = new AdcChannelMode(baseAddress + 4)
    val pgaSettings = new AdcChannelPgaSettings(baseAddress+5)
    val miscControls = new AdcChannelMiscControls(baseAddress+6)
  }

  val AdcChannelTopSettings = new AdcChannelSettings(21)

  val AdcChannelBotSettings = new AdcChannelSettings(82)
}