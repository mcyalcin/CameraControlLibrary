package com.mikrotasarim.ui.model

import java.util.concurrent.Executor

import com.mikrotasarim.camera.command.factory.UsbCam3825CommandFactory
import com.mikrotasarim.camera.device.{ConsoleMockDeviceInterface, OpalKellyInterface}
import com.mikrotasarim.ui.view.UsbCam3825TestUtility

import scala.concurrent.{Future, ExecutionContext}
import scalafx.application.Platform
import scalafx.beans.property.{BooleanProperty, StringProperty}
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

  val outputFormatOptions = ObservableBuffer("Binary", "Decimal", "Hexadecimal")
  val selectedOutputFormat = StringProperty("Decimal")

  def ReadOutputIntoFile(): Unit = {
    ReadOutputIntoFile(sampleCount.value.toInt, outFilePath.value, sixteenBitMode.value)
  }

  def ReadOutputIntoFile(length: Int, filename: String, sixteenBitMode: Boolean): Unit = {
    val radix = if (selectedOutputFormat.value == "Binary") 2 else if (selectedOutputFormat.value == "Decimal") 10 else 16
    commandFactory.ReadOutputIntoFile(length, filename, sixteenBitMode, radix)
  }

  val dacSweepTest1OutFilePath = StringProperty("")
  val dacSweepTest2OutFilePath = StringProperty("")

  val ec = JavaFXExecutionContext.javaFxExecutionContext

  def RunDacSweepTest1(): Unit = {
    Future {
      commandFactory.RunDacSweepTest1(dacSweepTest1OutFilePath.value)
    }(ec)
  }

  def RunDacSweepTest2(): Unit = commandFactory.RunDacSweepTest2(dacSweepTest2OutFilePath.value)

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

  selectedSpeedFactor.onChange(commandFactory.ChangeSpeedFactor(selectedSpeedFactor.value.toInt))

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
}
