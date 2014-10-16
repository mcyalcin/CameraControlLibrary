package com.mikrotasarim.camera.device

trait DeviceInterface {
  def UpdateWireOuts()
  def GetWireOutValue(address: Int): Long
  def SetWireInValue(wireNumber: Int, value: Long)
  def SetWireInValue(wireNumber: Int, value: Long, mask: Long)
  def ActivateTriggerIn(address: Int, bit: Int)
  def WriteToPipeIn(address: Int, size: Int, data: Array[Byte])
  def ReadFromPipeOut(address: Int, size: Int, data: Array[Byte])
  def WriteToBlockPipeIn(address: Int, blockSize: Int, size: Int, data: Array[Byte])
  def UpdateWireIns()
  def Disconnect()
}
