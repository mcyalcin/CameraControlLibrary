package com.mikrotasarim.camera

import com.mikrotasarim.camera.command.factory.UsbCam3825CommandFactory
import com.mikrotasarim.camera.device.{MockDeviceInterface, OpalKellyInterface}

import scalafx.beans.property.{BooleanProperty, StringProperty}

object Model {

  var commandFactory: UsbCam3825CommandFactory = _

  def DeployBitfile() {
    val device = new OpalKellyInterface(bitfilePath.value)
    commandFactory = new UsbCam3825CommandFactory(device)
    Model.bitfileDeployed.value = true
  }

  def DisconnectFromDevice() {
    commandFactory.MakeDisconnectCommand().Execute()
    Model.bitfileDeployed.value = false
  }

  val bitfilePath: StringProperty = new StringProperty()
  val bitfileDeployed: BooleanProperty = new BooleanProperty() {value = false}
  val testMode: BooleanProperty = new BooleanProperty() {value = false}

  testMode.onChange(if (testMode.value) {
    bitfileDeployed.value = true
    commandFactory = new UsbCam3825CommandFactory(new MockDeviceInterface(new StringBuilder()))
  } else {
    bitfileDeployed.value = false
  })
}