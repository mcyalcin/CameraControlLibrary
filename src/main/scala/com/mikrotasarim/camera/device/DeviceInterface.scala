package com.mikrotasarim.camera.device

trait DeviceInterface {
  def SetWireInValue(wireNumber: Int, value: Int)
  def ActivateTriggerIn(address: Int, bit: Int)
  def UpdateWireIns()
}
