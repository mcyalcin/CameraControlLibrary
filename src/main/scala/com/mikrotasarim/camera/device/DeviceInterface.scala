package com.mikrotasarim.camera.device

trait DeviceInterface {
  def SetWireInValue(wireNumber: Int, value: Long)
  def ActivateTriggerIn(address: Int, bit: Int)
  def WriteToPipeIn(address: Int, size: Int, data: Array[Byte])
  def WriteToBlockPipeIn(address: Int, blockSize: Int, size: Int, data: Array[Byte])
  def UpdateWireIns()
}
