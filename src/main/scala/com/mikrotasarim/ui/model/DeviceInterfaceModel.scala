package com.mikrotasarim.ui.model

import java.util.concurrent.Executor

import com.mikrotasarim.camera.command.factory.UsbCam3825CommandFactory
import com.mikrotasarim.camera.device.{ConsoleMockDeviceInterface, OpalKellyInterface}
import com.mikrotasarim.ui.view.UsbCam3825TestUtility

import scala.concurrent.{Future, ExecutionContext}
import scalafx.application.Platform
import scalafx.beans.property.{IntegerProperty, BooleanProperty, StringProperty}
import scalafx.collections.ObservableBuffer

object JavaFXExecutionContext {
  implicit val javaFxExecutionContext: ExecutionContext =
    ExecutionContext.fromExecutor(new Executor {
      def execute(command: Runnable): Unit = Platform.runLater(command)
    })
}

object DeviceInterfaceModel {

  var commandFactory: UsbCam3825CommandFactory = new UsbCam3825CommandFactory(new ConsoleMockDeviceInterface)

  def DeployBitfile() {
    val device = new OpalKellyInterface(bitfilePath.value)
    commandFactory = new UsbCam3825CommandFactory(device)
    InitializeFpga()
    for (i <- 0 to 3) ChannelControls.channelEnabled(i).value = true
    DeviceInterfaceModel.bitfileDeployed.value = true
  }

  private def InitializeFpga(): Unit = {
    embeddedDvalFval.value = true
  }

  def DisconnectFromDevice() {
    commandFactory.MakeDisconnectCommand().Execute()
    DeviceInterfaceModel.bitfileDeployed.value = false
  }

  val bitfilePath: StringProperty = new StringProperty()
  val alternateBitfilePath: StringProperty = new StringProperty()
  val bitfileDeployed: BooleanProperty = BooleanProperty(value = false)

  val testMode: BooleanProperty = new BooleanProperty() {
    value = false
    onChange(if (testMode.value) {
      ResetControls.fpgaReset.value = true
      ResetControls.chipReset.value = true
      bitfileDeployed.value = true
      commandFactory = new UsbCam3825CommandFactory(new ConsoleMockDeviceInterface)
    } else {
      bitfileDeployed.value = false
      ResetControls.fpgaReset.value = true
      ResetControls.chipReset.value = true
    })
  }

  object DelayControls {
    val inc = BooleanProperty(value = false)
    val rst = BooleanProperty(value = false)
    val cal = BooleanProperty(value = false)

    inc.onChange(commandFactory.device.SetWireInValue(7, if (inc.value) 16 else 0, 16))
    rst.onChange(commandFactory.device.SetWireInValue(7, if (rst.value) 32 else 0, 32))
    cal.onChange(commandFactory.device.SetWireInValue(7, if (cal.value) 64 else 0, 64))

    def cen(): Unit = {
      commandFactory.device.ActivateTriggerIn(0x42, 0)
    }
  }

  object ChannelControls {
    val channelEnabled = List(
      BooleanProperty(value = false),
      BooleanProperty(value = false),
      BooleanProperty(value = false),
      BooleanProperty(value = false)
    )

    for (i <- 0 to 3) {
      channelEnabled(i).onChange(commandFactory.MakeChannelEnableCommand(i,channelEnabled(i).value).Execute())
    }

    val testFeedEnabled = BooleanProperty(value = false)

    testFeedEnabled.onChange(commandFactory.MakeEnableTestFeedCommand(testFeedEnabled.value).Execute())

    val sweepTestFeedEnabled = BooleanProperty(value = false)

    sweepTestFeedEnabled.onChange(commandFactory.EnableDacSweepTest(sweepTestFeedEnabled.value))
  }

  def ReadDigitalOutputChunk(): Unit = {
    commandFactory.MakeReadOutputChunkCommand().Execute()
  }

  val outFilePath = StringProperty("")
  val sampleCount = StringProperty("")
  val sixteenBitMode = BooleanProperty(value = false)

  val channelOptions = ObservableBuffer("All Channels", "Channel 0", "Channel 1", "Channel 2", "Channel 3", "Test Channel")
  val selectedChannel = StringProperty("All Channels")
  val channelMappings = Map(
    "Channel 0" -> 0xa1,
    "Channel 1" -> 0xa2,
    "Channel 2" -> 0xa3,
    "Channel 3" -> 0xa4,
    "Test Channel" -> 0xa5
  )

  val outputFormatOptions = ObservableBuffer("Binary", "Decimal", "Hexadecimal")
  val selectedOutputFormat = StringProperty("Decimal")

  def ReadOutputIntoFile(): Unit = {
    ReadOutputIntoFile(sampleCount.value.toInt, outFilePath.value, sixteenBitMode.value)
  }

  def ReadOutputIntoFile(length: Int, filename: String, sixteenBitMode: Boolean): Unit = {
    val radix = if (selectedOutputFormat.value == "Binary") 2 else if (selectedOutputFormat.value == "Decimal") 10 else 16
    if (selectedChannel.value.equals("All Channels")) {
      commandFactory.ReadOutputIntoFile(length, filename, sixteenBitMode, radix)
    } else {
      commandFactory.ReadChannelOutputIntoFile(length, filename, sixteenBitMode, radix, channelMappings(selectedChannel.value))
    }
  }

  def ReadOutputIntoFile(length: Int, filename: String, sixteenBitMode: Boolean, channel: Int): Unit = {
    val radix = if (selectedOutputFormat.value == "Binary") 2 else if (selectedOutputFormat.value == "Decimal") 10 else 16
    commandFactory.ReadChannelOutputIntoFile(length, filename, sixteenBitMode, radix, channel)
  }

  val dacSweepTest1OutFilePath = StringProperty("")
  val dacSweepTest2OutFilePath = StringProperty("")

  val ec = JavaFXExecutionContext.javaFxExecutionContext

  def RunDacSweepTest1(): Unit = {
    Future {
      commandFactory.RunInternalDacSweepTest1(dacSweepTest1OutFilePath.value)
    }(ec)
  }

  def RunDacSweepTest2(): Unit = commandFactory.RunInternalDacSweepTest2(dacSweepTest2OutFilePath.value)

  def RunExternalDacSweepTest1(): Unit = commandFactory.RunDacSweepTest1(dacSweepTest1OutFilePath.value)
  def RunExternalDacSweepTest2(): Unit = commandFactory.RunDacSweepTest2(dacSweepTest2OutFilePath.value)

  var adcChannel = new AdcChannel
  var biasGenerator = new BiasGenerator
  var digitalController = new DigitalController
  var outputStage = new OutputStage
  var timingGenerator = new TimingGenerator

  object ResetControls {
    def RestoreDefaults(): Unit = {
      adcChannel = new AdcChannel
      biasGenerator = new BiasGenerator
      digitalController = new DigitalController
      outputStage = new OutputStage
      timingGenerator = new TimingGenerator

      UsbCam3825TestUtility.Reset()
    }

    val fpgaReset = new BooleanProperty() {
      value = true
      onChange(commandFactory.MakeFpgaResetCommand(this.value).Execute())
    }

    val chipReset = new BooleanProperty() {
      value = true
      onChange({
        commandFactory.MakeChipResetCommand(this.value).Execute()
        RestoreDefaults()
      })
    }
  }

  val speedFactors = ObservableBuffer("1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024", "2048", "4096")
  val selectedSpeedFactor = StringProperty("1")

  selectedSpeedFactor.onChange(commandFactory.ChangeClockSpeedFactor(selectedSpeedFactor.value.toInt))

  trait MemoryLocation {
    val address: Int

    def memoryValue: Long
  }

  def CommitMemoryLocation(memoryLocation: MemoryLocation) = {
    commandFactory.MakeWriteToAsicMemoryTopCommand(memoryLocation.address, memoryLocation.memoryValue).Execute()
    MtAs1410x2MemoryMap.ReadAsicMemory(memoryLocation.address)
  }

  val embeddedAsicLabels = ObservableBuffer("Embedded", "ASIC")
  val embeddedDvalFval = BooleanProperty(value = false)

  embeddedDvalFval.onChange(DeviceInterfaceModel.commandFactory.MakeFpgaDvalFvalSelectionCommand(embeddedDvalFval.value).Execute())

  val positiveDacsValue = IntegerProperty(0)
  val negativeDacsValue = IntegerProperty(0)

  positiveDacsValue.onChange(commandFactory.SetPositiveDacs(positiveDacsValue.value))
  negativeDacsValue.onChange(commandFactory.SetNegativeDacs(negativeDacsValue.value))

}
