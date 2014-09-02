package com.mikrotasarim.camera.device

import com.opalkelly.frontpanel._

class OpalKellyInterface(bitFileName: String) extends DeviceInterface {
  System.loadLibrary("okjFrontPanel")

  val panel = new okCFrontPanel()
  if (okCFrontPanel.ErrorCode.NoError != panel.OpenBySerial("")) {
    throw new Exception("A device could not be opened.  Is one connected?\n")
  }

  // Setup the PLL from defaults.
  panel.LoadDefaultPLLConfiguration()

  // Download the configuration file.
  if (okCFrontPanel.ErrorCode.NoError != panel.ConfigureFPGA(bitFileName)) {
    throw new Exception("FPGA configuration failed.\n")
  }

  override def SetWireInValue(wireNumber: Int, value: Int): Unit = panel.SetWireInValue(wireNumber, value)

  override def ActivateTriggerIn(address: Int, bit: Int): Unit = panel.ActivateTriggerIn(address, bit)

  override def UpdateWireIns(): Unit = panel.UpdateWireIns()

  def IsFrontPanel3Supported(): Boolean = panel.IsFrontPanel3Supported()

  def IsFrontPanelEnabled(): Boolean = panel.IsFrontPanelEnabled()
}
