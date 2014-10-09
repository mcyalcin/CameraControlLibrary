package com.mikrotasarim.ui.model

import com.mikrotasarim.ui.model.DeviceInterfaceModel.{MemoryLocation, CommitMemoryLocation}

import spire.implicits._
import scala.collection.immutable.ListMap
import scalafx.beans.property.{StringProperty, IntegerProperty, BooleanProperty}
import scalafx.collections.ObservableBuffer

object AdcChannel {
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
