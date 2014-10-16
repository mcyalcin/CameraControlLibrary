package com.mikrotasarim.ui.model

import com.mikrotasarim.camera.command.factory.UsbCam3825CommandFactory
import com.mikrotasarim.camera.device.{ConsoleMockDeviceInterface, OpalKellyInterface}

import scalafx.beans.property.{BooleanProperty, StringProperty}
import scalafx.collections.ObservableBuffer

object DeviceInterfaceModel {

  var commandFactory: UsbCam3825CommandFactory = new UsbCam3825CommandFactory(new ConsoleMockDeviceInterface)

  def DeployBitfile() {
    val device = new OpalKellyInterface(bitfilePath.value)
    commandFactory = new UsbCam3825CommandFactory(device)
    InitializeFpga()
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
  }

  def ReadDigitalOutputChunk(): Unit = {
    commandFactory.MakeReadOutputChunkCommand().Execute()
  }

  object ResetControls {
    val fpgaReset = new BooleanProperty() {
      value = true
      onChange(commandFactory.MakeFpgaResetCommand(this.value).Execute())
    }

    val chipReset = new BooleanProperty() {
      value = true
      onChange(commandFactory.MakeRoicResetCommand(this.value).Execute())
    }
  }

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
