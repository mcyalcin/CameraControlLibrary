package com.mikrotasarim.ui.model

import com.mikrotasarim.ui.model.DeviceInterfaceModel.{MemoryLocation, CommitMemoryLocation}

import spire.implicits._
import scala.collection.immutable.ListMap
import scalafx.beans.property.{IntegerProperty, StringProperty, BooleanProperty}
import scalafx.beans.value.ObservableValue
import scalafx.collections.ObservableBuffer

object TimingGenerator {
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
      val word = new MemoryLocation {
        override def memoryValue: Long = phaseSignal.memoryValue

        override val address: Int = phaseSignal.address
      }
      CommitMemoryLocation(word)
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
}
