package com.mikrotasarim.ui.model

import com.mikrotasarim.camera.command.factory.UsbCam3825CommandFactory
import com.mikrotasarim.camera.device.{ConsoleMockDeviceInterface, OpalKellyInterface}

import spire.implicits._
import scala.collection.immutable.ListMap
import scalafx.beans.property.{IntegerProperty, BooleanProperty, StringProperty}
import scalafx.beans.value.ObservableValue
import scalafx.collections.ObservableBuffer

object UsbCam3825TestUtilityModel {

  var commandFactory: UsbCam3825CommandFactory = new UsbCam3825CommandFactory(new ConsoleMockDeviceInterface)

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
      ResetControls.fpgaReset.value = true
      ResetControls.chipReset.value = true
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

    val chipReset = new BooleanProperty() {
      value = true
      onChange(commandFactory.MakeRoicResetCommand(this.value).Execute())
    }
  }

  trait MemoryLocation {
    val address: Int
    def memoryValue: Long
  }

  def CommitMemoryLocation(memoryLocation: MemoryLocation) =
    commandFactory.MakeWriteToAsicMemoryTopCommand(memoryLocation.address, memoryLocation.memoryValue).Execute()

}
