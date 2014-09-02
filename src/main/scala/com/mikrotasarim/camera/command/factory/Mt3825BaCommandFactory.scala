package com.mikrotasarim.camera.command.factory

import com.mikrotasarim.camera.command._
import com.mikrotasarim.camera.device._

class Mt3825BaCommandFactory(device: DeviceInterface) extends Mt3825BaConstants {

  def MakeWriteToAsicMemoryTopCommand(address: Int, value: Int): Command = {
    val commandList = List(
      MakeSetWireInValueCommand(CommandWire, WriteToAsicMemoryTopCommand),
      MakeSetWireInValueCommand(AddressWire, address),
      MakeSetWireInValueCommand(DataWire, value),
      MakeUpdateWireInsCommand(),
      MakeActivateTriggerInCommand(WriteTrigger, WriteTriggerBit)
    )
    new CompositeCommand(commandList)
  }

  def MakeWriteToAsicMemoryBotCommand(address: Int, value: Int): Command = {
    val commandList = List(
      MakeSetWireInValueCommand(CommandWire, WriteToAsicMemoryBotCommand),
      MakeSetWireInValueCommand(AddressWire, address),
      MakeSetWireInValueCommand(DataWire, value),
      MakeUpdateWireInsCommand(),
      MakeActivateTriggerInCommand(WriteTrigger, WriteTriggerBit)
    )
    new CompositeCommand(commandList)
  }

  def MakeSetWireInValueCommand(wireNumber: Int, value: Int): Command = {
    new SimpleCommand(() => device.SetWireInValue(wireNumber, value))
  }

  def MakeUpdateWireInsCommand(): Command = {
    new SimpleCommand(() => device.UpdateWireIns())
  }

  def MakeActivateTriggerInCommand(address: Int, bit: Int): Command = {
    new SimpleCommand(() => device.ActivateTriggerIn(address, bit))
  }

}

trait Mt3825BaConstants {

  // Endpoint addresses
  // 0x00 - 0x1F WireIn
  val ResetWire: Int = 0x00
  val CommandWire: Int = 0x01
  val AddressWire: Int = 0x02
  val DataWire: Int = 0x03

  // 0x20 - 0x3F WireOut
  // 0x40 - 0x5F TriggerIn
  val WriteTrigger: Int = 0x40

  // 0x60 - 0x7F TriggerOut
  // 0x80 - 0x9F PipeIn
  // 0xA0 - 0xBF PipeOut

  // Commands to be used on CommandWire
  val WriteToAsicMemoryTopCommand: Int = 0xc0
  val ReadFromAsicMemoryTopCommand: Int = 0xc1
  val WriteToAsicMemoryBotCommand: Int = 0xc2
  val ReadFromAsicMemoryBotCommand: Int = 0xc3
  val UpdateAsicMemoryCommand: Int = 0xc4
  val WriteToRoicMemoryCommand: Int = 0xc5
  val ReadFromRoicMemoryCommand: Int = 0xc6
  val BlockWriteToRoicMemoryCommand: Int = 0xc7
  val ReadFromRoicNucMemoryCommand: Int = 0xc8
  val ReadFromFlashMemoryCommand: Int = 0xf0
  val WriteToFlashMemoryCommand: Int = 0xf1
  val SoftResetAsicCommand: Int = 0xa0
  val SoftResetRoicCommand: Int = 0xa1

  // Trigger to be used on WriteTrigger. The bit doesn't matter at the moment, the wire has only one purpose.
  val WriteTriggerBit: Int = 0x00
}