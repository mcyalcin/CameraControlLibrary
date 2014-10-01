package com.mikrotasarim.camera.device

import com.opalkelly.frontpanel._

class OpalKellyInterface(bitFileName: String) extends DeviceInterface {

  System.loadLibrary("okjFrontPanel")

  val panel = new okCFrontPanel()

  if (okCFrontPanel.ErrorCode.NoError != panel.OpenBySerial("")) {
    throw new Exception("A device could not be opened.  Is one connected?\n")
  }

  panel.LoadDefaultPLLConfiguration()

  if (okCFrontPanel.ErrorCode.NoError != panel.ConfigureFPGA(bitFileName)) {
    panel.delete()
    throw new Exception("FPGA configuration failed.\n")
  }

  override def SetWireInValue(wireNumber: Int, value: Long): Unit = panel.SetWireInValue(wireNumber, value)

  override def SetWireInValue(wireNumber: Int, value: Long, mask: Long): Unit = panel.SetWireInValue(wireNumber, value, mask)

  override def ActivateTriggerIn(address: Int, bit: Int): Unit = panel.ActivateTriggerIn(address, bit)

  override def WriteToPipeIn(address: Int, size: Int, data: Array[Byte]): Unit = panel.WriteToPipeIn(address, size, data)

  override def WriteToBlockPipeIn(address: Int, blockSize: Int, size: Int, data: Array[Byte]): Unit =
    panel.WriteToBlockPipeIn(address, blockSize, size, data)

  override def UpdateWireIns(): Unit = panel.UpdateWireIns()

  def IsFrontPanel3Supported(): Boolean = panel.IsFrontPanel3Supported()

  def IsFrontPanelEnabled(): Boolean = panel.IsFrontPanelEnabled()

  def Disconnect(): Unit = panel.delete()

  override def GetWireOutValue(address: Int): Long = panel.GetWireOutValue(address)

  override def UpdateWireOuts(): Unit = panel.UpdateWireOuts()
}
