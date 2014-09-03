package com.mikrotasarim.camera.command.factory

import com.mikrotasarim.camera.command._
import com.mikrotasarim.camera.device._

class Mt3825BaCommandFactory(device: DeviceInterface) extends Mt3825BaConstants {

  // TODO: Work out how to implement read commands.
  def MakeReadFromAsicMemoryTopCommand() = ???

  def MakeReadFromAsicMemoryBottomCommand() = ???

  def MakeReadFromRoicMemoryCommand() = ???

  def MakeReadFromFlashMemoryCommand() = ???

  def MakeReadFromRoicNucMemoryCommand() = ???

  // TODO: Check if startAddress should be Long
  def MakeWriteToFlashMemoryCommand(startAddress: Int, data: Array[Byte]): Command = {
    // TODO: Add input validation
    val numberOfFullBlocks = data.length/FlashBlockSize

    MakeActivateTriggerInCommand(TriggerWire, ResetFlashInFifoTriggerBit)

    (for (i <- 0 until numberOfFullBlocks)
      yield
        new CompositeCommand(
          List(
            MakeWriteToFlashMemoryCommand(FlashBlockSize, data.slice(i*FlashBlockSize, (i+1)*FlashBlockSize)),
            MakeFlashInFifoCommitCommand()
          )
        )
      ).toList

    MakeWriteToFlashMemoryCommand(data.length - numberOfFullBlocks * FlashBlockSize, data.drop(numberOfFullBlocks * FlashBlockSize))
    MakeFlashInFifoCommitCommand()

    ???
  }

  private def MakeFlashInFifoCommitCommand(): Command = {
    new CompositeCommand(List(MakeSetWireInValueCommand(0x03, 0x02000000),
      MakeSetWireInValueCommand(0x04, 0x00000006),
      MakeUpdateWireInsCommand(),
      MakeActivateTriggerInCommand(0x40, 0x2)))
  }

  private def MakeWriteToFlashInFifoCommand(size: Int, data: Array[Byte]): Command = {
    if (size == 0) new SimpleCommand(() => ()) else
    new SimpleCommand(() => device.WriteToPipeIn(FlashInFifoPipe, size, data))
  }

  def MakeBlockWriteToRoicMemoryCommand(startAddress: Int, blocks: Array[Int]): Command = {
    if (startAddress < 0) throw new Exception("Illegal start address")
    if (startAddress + blocks.length > 127) throw new Exception("Illegal end address")
    if (blocks.min < 0 || blocks.max > 65536) throw new Exception("Illegal value")

    val writeToAsicCommands = (for (i <- 0 until blocks.length) yield MakeWriteToAsicMemoryTopCommand(startAddress + i, blocks(i))).toList

    new CompositeCommand(
      (writeToAsicCommands :+
      MakeSetWireInValueCommand(CommandWire, BlockWriteToRoicMemoryCommand)) ++
      GenerateCommitWireInsCommands
    )
  }

  def MakeSoftResetAsicCommand(): Command = {
    new CompositeCommand(
      MakeSetWireInValueCommand(CommandWire, SoftResetAsicCommand) +: GenerateCommitWireInsCommands
    )
  }

  def MakeSoftResetRoicCommand(): Command = {
    new CompositeCommand(
      MakeSetWireInValueCommand(CommandWire, SoftResetRoicCommand) +: GenerateCommitWireInsCommands
    )
  }

  def MakeUpdateAsicMemoryCommand(): Command = {
    new CompositeCommand(
      MakeSetWireInValueCommand(CommandWire, UpdateAsicMemoryCommand) +: GenerateCommitWireInsCommands
    )
  }

  def MakeWriteToAsicMemoryTopCommand(address: Int, value: Int): Command = {
    if (address < 0 || address > 255) throw new Exception("Illegal address")
    if (value < 0 || value > 65535) throw new Exception("Illegal value")
    new CompositeCommand(
      GenerateWriteWireInCommands(address, value, WriteToAsicMemoryTopCommand) ++ GenerateCommitWireInsCommands
    )
  }

  def MakeWriteToAsicMemoryBotCommand(address: Int, value: Int): Command = {
    if (address < 0 || address > 127) throw new Exception("Illegal address")
    if (value < 0 || value > 65535) throw new Exception("Illegal value")
    new CompositeCommand(
      GenerateWriteWireInCommands(address, value, WriteToAsicMemoryBotCommand) ++ GenerateCommitWireInsCommands
    )
  }

  def MakeWriteToRoicMemoryCommand(address: Int, value: Int): Command = {
    if (address < 0 || address > 127) throw new Exception("Illegal address")
    if (value < 0 || value > 65535) throw new Exception("Illegal value")
    new CompositeCommand(
      GenerateWriteWireInCommands(address, value, WriteToRoicMemoryCommand) ++ GenerateCommitWireInsCommands
    )
  }

  private def GenerateWriteWireInCommands(address: Int, value: Int, command: Int): List[Command] = {
    List(MakeSetWireInValueCommand(CommandWire, command),
      MakeSetWireInValueCommand(AddressWire, address),
      MakeSetWireInValueCommand(DataWire, value))
  }

  private def GenerateCommitWireInsCommands: List[Command] = {
    List(
      MakeUpdateWireInsCommand(),
      MakeActivateTriggerInCommand(TriggerWire, ExecuteCommandTriggerBit)
    )
  }

  private def MakeSetWireInValueCommand(wireNumber: Int, value: Long): Command = {
    new SimpleCommand(() => device.SetWireInValue(wireNumber, value))
  }

  private def MakeUpdateWireInsCommand(): Command = {
    new SimpleCommand(() => device.UpdateWireIns())
  }

  private def MakeActivateTriggerInCommand(address: Int, bit: Int): Command = {
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
  val TriggerWire: Int = 0x40

  // 0x60 - 0x7F TriggerOut
  // 0x80 - 0x9F PipeIn
  val FlashInFifoPipe = 0x80

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

  val ExecuteCommandTriggerBit: Int = 0
  val ResetFlashInFifoTriggerBit: Int = 1

  val FlashBlockSize = 256
}