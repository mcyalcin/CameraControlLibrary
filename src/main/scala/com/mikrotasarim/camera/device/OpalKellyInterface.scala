package com.mikrotasarim.camera.device

import com.opalkelly.frontpanel._

object OpalKellyInterface {
  val panel = new okCFrontPanel()
}

class OpalKellyInterface(bitFileName: String) extends DeviceInterface {

  System.loadLibrary("okjFrontPanel")

//  val panel = new okCFrontPanel()

  if (okCFrontPanel.ErrorCode.NoError != OpalKellyInterface.panel.OpenBySerial("")) {
    throw new Exception("A device could not be opened.  Is one connected?\n")
  }

  OpalKellyInterface.panel.LoadDefaultPLLConfiguration()

  if (okCFrontPanel.ErrorCode.NoError != OpalKellyInterface.panel.ConfigureFPGA(bitFileName)) {
//    OpalKellyInterface.panel.delete()
    throw new Exception("FPGA configuration failed.\n")
  }

  override def SetWireInValue(wireNumber: Int, value: Long): Unit = OpalKellyInterface.panel.SetWireInValue(wireNumber, value)

  override def SetWireInValue(wireNumber: Int, value: Long, mask: Long): Unit = OpalKellyInterface.panel.SetWireInValue(wireNumber, value, mask)

  override def ActivateTriggerIn(address: Int, bit: Int): Unit = OpalKellyInterface.panel.ActivateTriggerIn(address, bit)

  override def WriteToPipeIn(address: Int, size: Int, data: Array[Byte]): Unit = OpalKellyInterface.panel.WriteToPipeIn(address, size, data)

  override def ReadFromPipeOut(address: Int, size: Int, data: Array[Byte]): Unit = {
    OpalKellyInterface.panel.ReadFromPipeOut(address, size, data)
  }

  override def WriteToBlockPipeIn(address: Int, blockSize: Int, size: Int, data: Array[Byte]): Unit =
    OpalKellyInterface.panel.WriteToBlockPipeIn(address, blockSize, size, data)

  override def ReadFromBlockPipeOut(address: Int, size: Int, data: Array[Byte]): Unit = {
    OpalKellyInterface.panel.ReadFromBlockPipeOut(address, 64, size, data)
  }

  override def UpdateWireIns(): Unit = OpalKellyInterface.panel.UpdateWireIns()

  def IsFrontPanel3Supported(): Boolean = OpalKellyInterface.panel.IsFrontPanel3Supported()

  def IsFrontPanelEnabled(): Boolean = OpalKellyInterface.panel.IsFrontPanelEnabled()

  def Disconnect(): Unit = {
    //panel.delete()
  }

  override def GetWireOutValue(address: Int): Long = OpalKellyInterface.panel.GetWireOutValue(address)

  override def UpdateWireOuts(): Unit = OpalKellyInterface.panel.UpdateWireOuts()
}
