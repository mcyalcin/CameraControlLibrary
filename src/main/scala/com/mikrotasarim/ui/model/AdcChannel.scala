package com.mikrotasarim.ui.model

import com.mikrotasarim.ui.model.DeviceInterfaceModel._

import spire.implicits._
import scala.collection.immutable.ListMap
import scalafx.beans.property.{StringProperty, IntegerProperty, BooleanProperty}
import scalafx.collections.ObservableBuffer

class AdcChannel {

  val AdcChannelTopSettings = new AdcChannelSettings(21)

  val AdcChannelBotSettings = new AdcChannelSettings(82)

  val AdcTestPresetLabels = ObservableBuffer(
    "Stage 1", "Stage 2", "Stage 3", "Stage 4", "Stage 5", "Stage 6",
    "Probe 5", "Probe 6", "Probe 7", "Probe 8"
  )

  val AdcTestPresetVersionLabels = ObservableBuffer("V1", "V2")

  def ApplyPresets(stage: String, version: String): Unit = {

    // TODO: Put all this into a lookup table, rather than methods.
    def setClockPresetsS5V1(settings: AdcChannelSettings): Unit = {
      settings.clockSettings.selPgaClk(0).value = false
      settings.clockSettings.selPgaClk(1).value = false
      settings.clockSettings.selAdcClk(0).value = false
      settings.clockSettings.selAdcClk(1).value = false
      settings.clockSettings.enAdcClk(0).value = true
      settings.clockSettings.enAdcClk(1).value = false
      settings.clockSettings.enPgaClk(0).value = true
      settings.clockSettings.enPgaClk(1).value = false
      settings.clockSettings.enClkMode(0).value = false
      settings.clockSettings.enClkMode(1).value = false
    }

    def setClockPresetsP5(settings: AdcChannelSettings): Unit = {
      settings.clockSettings.selPgaClk(0).value = false
      settings.clockSettings.selPgaClk(1).value = false
      settings.clockSettings.selAdcClk(0).value = false
      settings.clockSettings.selAdcClk(1).value = false
      settings.clockSettings.enAdcClk(0).value = true
      settings.clockSettings.enAdcClk(1).value = true
      settings.clockSettings.enPgaClk(0).value = true
      settings.clockSettings.enPgaClk(1).value = true
      settings.clockSettings.enClkMode(0).value = false
      settings.clockSettings.enClkMode(1).value = false
    }

    def setClockPresetsS6V1(settings: AdcChannelSettings): Unit = {
      settings.clockSettings.selPgaClk(0).value = false
      settings.clockSettings.selPgaClk(1).value = false
      settings.clockSettings.selAdcClk(0).value = false
      settings.clockSettings.selAdcClk(1).value = false
      settings.clockSettings.enAdcClk(0).value = true
      settings.clockSettings.enAdcClk(1).value = false
      settings.clockSettings.enPgaClk(0).value = false
      settings.clockSettings.enPgaClk(1).value = false
      settings.clockSettings.enClkMode(0).value = false
      settings.clockSettings.enClkMode(1).value = false
    }

    def setClockPresetsS5V2(settings: AdcChannelSettings): Unit = {
      settings.clockSettings.selPgaClk(0).value = false
      settings.clockSettings.selPgaClk(1).value = false
      settings.clockSettings.selAdcClk(0).value = false
      settings.clockSettings.selAdcClk(1).value = false
      settings.clockSettings.enAdcClk(0).value = false
      settings.clockSettings.enAdcClk(1).value = true
      settings.clockSettings.enPgaClk(0).value = false
      settings.clockSettings.enPgaClk(1).value = true
      settings.clockSettings.enClkMode(0).value = false
      settings.clockSettings.enClkMode(1).value = false
    }

    def setClockPresetsS6V2(settings: AdcChannelSettings): Unit = {
      settings.clockSettings.selPgaClk(0).value = false
      settings.clockSettings.selPgaClk(1).value = false
      settings.clockSettings.selAdcClk(0).value = false
      settings.clockSettings.selAdcClk(1).value = false
      settings.clockSettings.enAdcClk(0).value = false
      settings.clockSettings.enAdcClk(1).value = true
      settings.clockSettings.enPgaClk(0).value = false
      settings.clockSettings.enPgaClk(1).value = false
      settings.clockSettings.enClkMode(0).value = false
      settings.clockSettings.enClkMode(1).value = false
    }

    def setModePresetsS5V1(settings: AdcChannelSettings): Unit = {
      settings.modePositive.selectedMode(0).value = "DAC reference input - PGA - ADC"
      settings.modePositive.selectedMode(1).value = "On-chip Drive - PGA - ADC"
      settings.modeNegative.selectedMode(0).value = "DAC reference input - PGA - ADC"
      settings.modeNegative.selectedMode(1).value = "On-chip Drive - PGA - ADC"
    }

    def setModePresetsP5(settings: AdcChannelSettings): Unit = {
      settings.modePositive.selectedMode(0).value = "DAC reference input - PGA - ADC"
      settings.modePositive.selectedMode(1).value = "DAC reference input - PGA - ADC"
      settings.modeNegative.selectedMode(0).value = "DAC reference input - PGA - ADC"
      settings.modeNegative.selectedMode(1).value = "DAC reference input - PGA - ADC"
    }

    def setModePresetsS5V2(settings: AdcChannelSettings): Unit = {
      settings.modePositive.selectedMode(0).value = "On-chip Drive - PGA - ADC"
      settings.modePositive.selectedMode(1).value = "DAC reference input - PGA - ADC"
      settings.modeNegative.selectedMode(0).value = "On-chip Drive - PGA - ADC"
      settings.modeNegative.selectedMode(1).value = "DAC reference input - PGA - ADC"
    }

    def setModePresetsS6V1(settings: AdcChannelSettings): Unit = {
      settings.modePositive.selectedMode(0).value = "DAC reference input - ADC"
      settings.modePositive.selectedMode(1).value = "On-chip Drive - PGA - ADC"
      settings.modeNegative.selectedMode(0).value = "DAC reference input - ADC"
      settings.modeNegative.selectedMode(1).value = "On-chip Drive - PGA - ADC"
    }

    def setModePresetsS6V2(settings: AdcChannelSettings): Unit = {
      settings.modePositive.selectedMode(0).value = "On-chip Drive - PGA - ADC"
      settings.modePositive.selectedMode(1).value = "DAC reference input - ADC"
      settings.modeNegative.selectedMode(0).value = "On-chip Drive - PGA - ADC"
      settings.modeNegative.selectedMode(1).value = "DAC reference input - ADC"
    }

    def setPgaPresetsP5(settings: AdcChannelSettings): Unit = {
      settings.pgaSettings.bwPga.value = 0
      settings.pgaSettings.selectedGain.value = "1.500"
      settings.pgaSettings.pdPga(0).value = true
      settings.pgaSettings.pdPga(1).value = true
    }

    def setPgaPresetsS5V1(settings: AdcChannelSettings): Unit = {
      settings.pgaSettings.bwPga.value = 0
      settings.pgaSettings.selectedGain.value = "1.500"
      settings.pgaSettings.pdPga(0).value = false
      settings.pgaSettings.pdPga(1).value = true
    }

    def setPgaPresetsS5V2(settings: AdcChannelSettings): Unit = {
      settings.pgaSettings.bwPga.value = 0
      settings.pgaSettings.selectedGain.value = "1.500"
      settings.pgaSettings.pdPga(0).value = true
      settings.pgaSettings.pdPga(1).value = false
    }

    def setPgaDefaults(settings: AdcChannelSettings): Unit = {
      settings.pgaSettings.bwPga.value = 0
      settings.pgaSettings.selectedGain.value = "1.500"
      settings.pgaSettings.pdPga(0).value = true
      settings.pgaSettings.pdPga(1).value = true
    }

    def setMiscPresetsS5V2(settings: AdcChannelSettings): Unit = {
      settings.miscControls.enableMdacTest.value = false
      settings.miscControls.extRefEnable.value = false
      settings.miscControls.pdAdcRefDrv(0).value = true
      settings.miscControls.pdAdcRefDrv(1).value = false
      settings.miscControls.pdPgaRefDrv(0).value = true
      settings.miscControls.pdPgaRefDrv(1).value = false
      settings.miscControls.pdAdc(0).value = true
      settings.miscControls.pdAdc(1).value = false
      settings.miscControls.rstbDigCor(0).value = false
      settings.miscControls.rstbDigCor(1).value = true
      settings.miscControls.externalRefDacDrv.value = false
    }

    def setMiscPresetsS5V1(settings: AdcChannelSettings): Unit = {
      settings.miscControls.enableMdacTest.value = false
      settings.miscControls.extRefEnable.value = false
      settings.miscControls.pdAdcRefDrv(0).value = false
      settings.miscControls.pdAdcRefDrv(1).value = true
      settings.miscControls.pdPgaRefDrv(0).value = false
      settings.miscControls.pdPgaRefDrv(1).value = true
      settings.miscControls.pdAdc(0).value = false
      settings.miscControls.pdAdc(1).value = true
      settings.miscControls.rstbDigCor(0).value = true
      settings.miscControls.rstbDigCor(1).value = false
      settings.miscControls.externalRefDacDrv.value = false
    }

    def setMiscPresetsP5(settings: AdcChannelSettings): Unit = {
      settings.miscControls.enableMdacTest.value = false
      settings.miscControls.extRefEnable.value = false
      settings.miscControls.pdAdcRefDrv(0).value = false
      settings.miscControls.pdAdcRefDrv(1).value = false
      settings.miscControls.pdPgaRefDrv(0).value = false
      settings.miscControls.pdPgaRefDrv(1).value = false
      settings.miscControls.pdAdc(0).value = false
      settings.miscControls.pdAdc(1).value = false
      settings.miscControls.rstbDigCor(0).value = true
      settings.miscControls.rstbDigCor(1).value = true
      settings.miscControls.externalRefDacDrv.value = false
    }

    def setMiscPresetsS6V2(settings: AdcChannelSettings): Unit = {
      settings.miscControls.enableMdacTest.value = false
      settings.miscControls.extRefEnable.value = false
      settings.miscControls.pdAdcRefDrv(0).value = true
      settings.miscControls.pdAdcRefDrv(1).value = false
      settings.miscControls.pdPgaRefDrv(0).value = true
      settings.miscControls.pdPgaRefDrv(1).value = true
      settings.miscControls.pdAdc(0).value = true
      settings.miscControls.pdAdc(1).value = false
      settings.miscControls.rstbDigCor(0).value = false
      settings.miscControls.rstbDigCor(1).value = true
      settings.miscControls.externalRefDacDrv.value = false
    }

    def setMiscPresetsS6V1(settings: AdcChannelSettings): Unit = {
      settings.miscControls.enableMdacTest.value = false
      settings.miscControls.extRefEnable.value = false
      settings.miscControls.pdAdcRefDrv(0).value = false
      settings.miscControls.pdAdcRefDrv(1).value = true
      settings.miscControls.pdPgaRefDrv(0).value = true
      settings.miscControls.pdPgaRefDrv(1).value = true
      settings.miscControls.pdAdc(0).value = false
      settings.miscControls.pdAdc(1).value = true
      settings.miscControls.rstbDigCor(0).value = true
      settings.miscControls.rstbDigCor(1).value = false
      settings.miscControls.externalRefDacDrv.value = false
    }

    def setTimingDefaults(settings: AdcChannelSettings): Unit = {
      settings.timingSliders.delay.value = 3
      settings.timingSliders.nod.value = 3
      settings.timingSliders.saMargin.value = 2
    }

    def setInputDriveDefaults(settings: AdcChannelSettings): Unit = {
      settings.inputDriveControls.bwPredrv.value = 0
      settings.inputDriveControls.pdp(0).value = true
      settings.inputDriveControls.pdp(1).value = true
      settings.inputDriveControls.pdn(0).value = true
      settings.inputDriveControls.pdn(1).value = true
      settings.inputDriveControls.shortRefEnable.value = false
    }

    if (stage == "Stage 1") {

    } else if (stage == "Stage 2") {
      // TODO
    } else if (stage == "Stage 3") {
      // TODO
    } else if (stage == "Stage 4") {
      // TODO
    } else if (stage == "Stage 5") {
      if (version == "V1") {
        setClockPresetsS5V1(AdcChannelTopSettings)
        setClockPresetsS5V1(AdcChannelBotSettings)
        setTimingDefaults(AdcChannelTopSettings)
        setTimingDefaults(AdcChannelBotSettings)
        setInputDriveDefaults(AdcChannelTopSettings)
        setInputDriveDefaults(AdcChannelBotSettings)
        setModePresetsS5V1(AdcChannelTopSettings)
        setModePresetsS5V1(AdcChannelBotSettings)
        setPgaPresetsS5V1(AdcChannelTopSettings)
        setPgaPresetsS5V1(AdcChannelBotSettings)
        setMiscPresetsS5V1(AdcChannelTopSettings)
        setMiscPresetsS5V1(AdcChannelBotSettings)
      } else if (version == "V2") {
        setClockPresetsS5V2(AdcChannelTopSettings)
        setClockPresetsS5V2(AdcChannelBotSettings)
        setTimingDefaults(AdcChannelTopSettings)
        setTimingDefaults(AdcChannelBotSettings)
        setInputDriveDefaults(AdcChannelTopSettings)
        setInputDriveDefaults(AdcChannelBotSettings)
        setModePresetsS5V2(AdcChannelTopSettings)
        setModePresetsS5V2(AdcChannelBotSettings)
        setPgaPresetsS5V2(AdcChannelTopSettings)
        setPgaPresetsS5V2(AdcChannelBotSettings)
        setMiscPresetsS5V2(AdcChannelTopSettings)
        setMiscPresetsS5V2(AdcChannelBotSettings)
      }
    } else if (stage == "Stage 6") {
      if (version == "V1") {
        setTimingDefaults(AdcChannelTopSettings)
        setTimingDefaults(AdcChannelBotSettings)
        setInputDriveDefaults(AdcChannelTopSettings)
        setInputDriveDefaults(AdcChannelBotSettings)
        setClockPresetsS6V1(AdcChannelTopSettings)
        setClockPresetsS6V1(AdcChannelBotSettings)
        setModePresetsS6V1(AdcChannelTopSettings)
        setModePresetsS6V1(AdcChannelBotSettings)
        setPgaDefaults(AdcChannelTopSettings)
        setPgaDefaults(AdcChannelBotSettings)
        setMiscPresetsS6V1(AdcChannelTopSettings)
        setMiscPresetsS6V1(AdcChannelBotSettings)
      } else if (version == "V2") {
        setTimingDefaults(AdcChannelTopSettings)
        setTimingDefaults(AdcChannelBotSettings)
        setInputDriveDefaults(AdcChannelTopSettings)
        setInputDriveDefaults(AdcChannelBotSettings)
        setClockPresetsS6V2(AdcChannelTopSettings)
        setClockPresetsS6V2(AdcChannelBotSettings)
        setModePresetsS6V2(AdcChannelTopSettings)
        setModePresetsS6V2(AdcChannelBotSettings)
        setPgaDefaults(AdcChannelTopSettings)
        setPgaDefaults(AdcChannelBotSettings)
        setMiscPresetsS6V2(AdcChannelTopSettings)
        setMiscPresetsS6V2(AdcChannelBotSettings)
      }
    } else if (stage == "Probe 5") {
      // TODO
    } else if (stage == "Probe 6") {
      setClockPresetsP5(AdcChannelTopSettings)
      setClockPresetsP5(AdcChannelBotSettings)
      setTimingDefaults(AdcChannelTopSettings)
      setTimingDefaults(AdcChannelBotSettings)
      setInputDriveDefaults(AdcChannelTopSettings)
      setInputDriveDefaults(AdcChannelBotSettings)
      setModePresetsP5(AdcChannelTopSettings)
      setModePresetsP5(AdcChannelBotSettings)
      setPgaPresetsP5(AdcChannelTopSettings)
      setPgaPresetsP5(AdcChannelBotSettings)
      setMiscPresetsP5(AdcChannelTopSettings)
      setMiscPresetsP5(AdcChannelBotSettings)
    } else if (stage == "Probe 7") {
      // TODO
    } else if (stage == "Probe 8") {
      // TODO
    }
  }
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
  val enClkMode =
    List(new BooleanProperty(this, "selClkMode0", false),
      new BooleanProperty(this, "selClkMode1", false))
  enClkMode(0).onChange(CommitMemoryLocation(this))
  enClkMode(1).onChange(CommitMemoryLocation(this))

  override def memoryValue: Long =
    (if (selAdcClk(1).value) 2 pow 9 else 0) +
      (if (selAdcClk(0).value) 2 pow 8 else 0) +
      (if (selPgaClk(1).value) 2 pow 7 else 0) +
      (if (selPgaClk(0).value) 2 pow 6 else 0) +
      (if (enAdcClk(1).value) 2 pow 5 else 0) +
      (if (enAdcClk(0).value) 2 pow 4 else 0) +
      (if (enPgaClk(1).value) 2 pow 3 else 0) +
      (if (enPgaClk(0).value) 2 pow 2 else 0) +
      (if (enClkMode(1).value) 2 pow 1 else 0) +
      (if (enClkMode(0).value) 1 else 0)
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
  pdn.head.onChange(CommitMemoryLocation(this))
  pdn(1).onChange(CommitMemoryLocation(this))

  val pdp = List(
    new BooleanProperty(this, "pdp0", false),
    new BooleanProperty(this, "pdp1", false)
  )
  pdp.head.onChange(CommitMemoryLocation(this))
  pdp(1).onChange(CommitMemoryLocation(this))

  val shortRefEnable = new BooleanProperty(this, "sre", false)

  shortRefEnable.onChange({
    CommitMemoryLocation(this)
    biasGenerator.biasGeneratorVoltageDacs(0).powerDown.value = shortRefEnable.value
    biasGenerator.biasGeneratorVoltageDacs(0).Commit()
    biasGenerator.biasGeneratorVoltageDacs(1).powerDown.value = shortRefEnable.value
    biasGenerator.biasGeneratorVoltageDacs(1).Commit()
  })

  val bwPredrv = new IntegerProperty()

  bwPredrv.onChange(CommitMemoryLocation(this))

  def memoryValue =
    bwPredrv.value * (2 pow 5) +
      (if (pdn(1).value) 2 pow 4 else 0) +
      (if (pdn.head.value) 2 pow 3 else 0) +
      (if (pdp(1).value) 2 pow 2 else 0) +
      (if (pdp.head.value) 2 else 0) +
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
      (Integer.parseInt(gainSettings(selectedGain.value), 2) * (2 pow 2)) +
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
    Integer.parseInt(channelModes(selectedMode(1).value), 2) * (2 pow 5) +
      Integer.parseInt(channelModes(selectedMode(0).value), 2)
}

class AdcChannelSettings(baseAddress: Int) {
  val powerDown = ObservableBuffer(
    BooleanProperty(value = false),
    BooleanProperty(value = false)
  )
  val selectedMode = ObservableBuffer(
    StringProperty("PAD - PGA - ADC"),
    StringProperty("PAD - PGA - ADC")
  )

  powerDown(0).onChange(handlePowerDownChange(0))
  powerDown(1).onChange(handlePowerDownChange(1))
  selectedMode(0).onChange(handleModeChange(0))
  selectedMode(1).onChange(handleModeChange(1))

  def handlePowerDownChange(index: Int): Unit = {
    if (powerDown(index).value) {
      clockSettings.enAdcClk(index).set(false)
      clockSettings.selAdcClk(index).set(false)
      clockSettings.enPgaClk(index).set(false)
      clockSettings.selPgaClk(index).set(false)
      clockSettings.enClkMode(index).set(false)
      inputDriveControls.pdn(index).set(true)
      inputDriveControls.pdp(index).set(true)
      pgaSettings.pdPga(index).set(true)
      miscControls.pdAdc(index).set(true)
      miscControls.pdAdcRefDrv(index).set(true)
      miscControls.pdPgaRefDrv(index).set(true)
      miscControls.rstbDigCor(index).set(false)
    } else {
      handleModeChange(index)
    }
  }

  def handleModeChange(index: Int): Unit = {
    val sm = selectedMode(index).value
    if (sm == "PAD - PGA - ADC") {
      clockSettings.enAdcClk(index).set(false)
      clockSettings.selAdcClk(index).set(true)
      clockSettings.enPgaClk(index).set(false)
      clockSettings.selPgaClk(index).set(true)
      clockSettings.enClkMode(index).set(true)
      inputDriveControls.pdn(index).set(true)
      inputDriveControls.pdp(index).set(true)
      pgaSettings.pdPga(index).set(false)
      miscControls.pdAdc(index).set(false)
      miscControls.pdAdcRefDrv(index).set(false)
      miscControls.pdPgaRefDrv(index).set(false)
      miscControls.rstbDigCor(index).set(true)
    } else if (sm == "PAD - ADC") {
      clockSettings.enAdcClk(index).set(false)
      clockSettings.selAdcClk(index).set(true)
      clockSettings.enPgaClk(index).set(false)
      clockSettings.selPgaClk(index).set(false)
      clockSettings.enClkMode(index).set(true)
      inputDriveControls.pdn(index).set(true)
      inputDriveControls.pdp(index).set(true)
      pgaSettings.pdPga(index).set(true)
      miscControls.pdAdc(index).set(false)
      miscControls.pdAdcRefDrv(index).set(false)
      miscControls.pdPgaRefDrv(index).set(true)
      miscControls.rstbDigCor(index).set(true)
    } else if (sm == "PAD - Input Buffer - PGA - ADC") {
      clockSettings.enAdcClk(index).set(false)
      clockSettings.selAdcClk(index).set(true)
      clockSettings.enPgaClk(index).set(false)
      clockSettings.selPgaClk(index).set(true)
      clockSettings.enClkMode(index).set(true)
      inputDriveControls.pdn(index).set(false)
      inputDriveControls.pdp(index).set(false)
      pgaSettings.pdPga(index).set(false)
      miscControls.pdAdc(index).set(false)
      miscControls.pdAdcRefDrv(index).set(false)
      miscControls.pdPgaRefDrv(index).set(false)
      miscControls.rstbDigCor(index).set(true)
    } else if (sm == "PAD - Input Buffer - ADC") {
      clockSettings.enAdcClk(index).set(false)
      clockSettings.selAdcClk(index).set(true)
      clockSettings.enPgaClk(index).set(false)
      clockSettings.selPgaClk(index).set(false)
      clockSettings.enClkMode(index).set(true)
      inputDriveControls.pdn(index).set(false)
      inputDriveControls.pdp(index).set(false)
      pgaSettings.pdPga(index).set(true)
      miscControls.pdAdc(index).set(false)
      miscControls.pdAdcRefDrv(index).set(false)
      miscControls.pdPgaRefDrv(index).set(true)
      miscControls.rstbDigCor(index).set(true)
    } else if (sm == "Test DAC - PGA - ADC") {
      clockSettings.enAdcClk(index).set(false)
      clockSettings.selAdcClk(index).set(true)
      clockSettings.enPgaClk(index).set(false)
      clockSettings.selPgaClk(index).set(true)
      clockSettings.enClkMode(index).set(true)
      inputDriveControls.pdn(index).set(true)
      inputDriveControls.pdp(index).set(true)
      pgaSettings.pdPga(index).set(false)
      miscControls.pdAdc(index).set(false)
      miscControls.pdAdcRefDrv(index).set(false)
      miscControls.pdPgaRefDrv(index).set(false)
      miscControls.rstbDigCor(index).set(true)
    } else if (sm == "Test DAC - ADC") {
      clockSettings.enAdcClk(index).set(false)
      clockSettings.selAdcClk(index).set(true)
      clockSettings.enPgaClk(index).set(false)
      clockSettings.selPgaClk(index).set(false)
      clockSettings.enClkMode(index).set(true)
      inputDriveControls.pdn(index).set(true)
      inputDriveControls.pdp(index).set(true)
      pgaSettings.pdPga(index).set(true)
      miscControls.pdAdc(index).set(false)
      miscControls.pdAdcRefDrv(index).set(false)
      miscControls.pdPgaRefDrv(index).set(true)
      miscControls.rstbDigCor(index).set(true)
    }
    modePositive.selectedMode(index).set(modeMap(sm))
    modeNegative.selectedMode(index).set(modeMap(sm))
  }

  selectedMode.onChange()

  val modeMap = ListMap(
    "PAD - PGA - ADC" -> "External Drive - PGA - ADC",
    "PAD - ADC" -> "External Drive - ADC",
    "PAD - Input Buffer - PGA - ADC" -> "On-chip Drive - PGA - ADC",
    "PAD - Input Buffer - ADC" -> "On-chip Drive - ADC",
    "Test DAC - PGA - ADC" -> "DAC reference input - PGA - ADC",
    "Test DAC - ADC" -> "DAC reference input - ADC"
  )

  val modeList = ObservableBuffer(modeMap.keys.toList)

  val clockSettings = new AdcChannelClockSettings(baseAddress)
  val timingSliders = new AdcChannelTimingSliders(baseAddress + 1)
  val inputDriveControls = new AdcChannelInputDriveControls(baseAddress + 2)
  val modePositive = new AdcChannelMode(baseAddress + 3)
  val modeNegative = new AdcChannelMode(baseAddress + 4)
  val pgaSettings = new AdcChannelPgaSettings(baseAddress + 5)
  val miscControls = new AdcChannelMiscControls(baseAddress + 6)
}
