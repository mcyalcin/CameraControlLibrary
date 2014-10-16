package com.mikrotasarim.camera.command.factory

import com.mikrotasarim.camera.command._
import com.mikrotasarim.camera.device._

class UsbCam3825CommandFactory(device: DeviceInterface) extends UsbCam3825Constants {

  def MakeReadOutputChunkCommand(): Command = {
    val buf0 = Array.ofDim[Byte](4096)
    val buf1 = Array.ofDim[Byte](4096)
    val buf2 = Array.ofDim[Byte](4096)
    val buf3 = Array.ofDim[Byte](4096)
    device.ReadFromPipeOut(DigitalOutputPipe0, buf0.length, buf0)
    println(System.currentTimeMillis())
    device.ReadFromPipeOut(DigitalOutputPipe1, buf1.length, buf1)
    println(System.currentTimeMillis())
    device.ReadFromPipeOut(DigitalOutputPipe2, buf2.length, buf2)
    println(System.currentTimeMillis())
    device.ReadFromPipeOut(DigitalOutputPipe3, buf3.length, buf3)
    println(System.currentTimeMillis())
    println("TROLOLO0")
    for (i <- 0 until buf0.length by 4) {
      print((buf0(i+3) + 256) % 256)
      print(" ")
      print((buf0(i+2) + 256) % 256)
      print(" ")
      print((buf0(i+1) + 256) % 256)
      print(" ")
      print((buf0(i) + 256) % 256)
      println()
    }
    println("TROLOLO1")
    for (i <- 0 until buf1.length by 4) {
      print((buf1(i+3) + 256) % 256)
      print(" ")
      print((buf1(i+2) + 256) % 256)
      print(" ")
      print((buf1(i+1) + 256) % 256)
      print(" ")
      print((buf1(i) + 256) % 256)
      println()
    }
    println("TROLOLO2")
    for (i <- 0 until buf2.length by 4) {
      print((buf2(i+3) + 256) % 256)
      print(" ")
      print((buf2(i+2) + 256) % 256)
      print(" ")
      print((buf2(i+1) + 256) % 256)
      print(" ")
      print((buf2(i) + 256) % 256)
      println()
    }
    println("TROLOLO3")
    for (i <- 0 until buf3.length by 4) {
      print((buf3(i+3) + 256) % 256)
      print(" ")
      print((buf3(i+2) + 256) % 256)
      print(" ")
      print((buf3(i+1) + 256) % 256)
      print(" ")
      print((buf3(i) + 256) % 256)
      println()
    }
    new SimpleCommand(() => println("Ok"))
  }

  def MakeFpgaDvalFvalSelectionCommand(embeddedDvalFval: Boolean): Command = new CompositeCommand(List(
    MakeFpgaConfigurationCommand(if (embeddedDvalFval) EmbeddedDvalFval else 0, EmbeddedDvalFval),
    MakeUpdateWireInsCommand()
  ))

  def MakeChannelEnableCommand(index: Integer, enable: Boolean): Command = new CompositeCommand(List(
    MakeFpgaConfigurationCommand(if (enable) EnableChannel(index) else 0, EnableChannel(index)),
    MakeUpdateWireInsCommand()
  ))

  def MakeEnableTestFeedCommand(enable: Boolean): Command = new CompositeCommand(List(
    MakeFpgaConfigurationCommand(if (enable) EnableTestFeedOnChannels else 0, EnableTestFeedOnChannels),
    MakeUpdateWireInsCommand()
  ))

  def MakeReadFromAsicMemoryTopCommand(address: Int, callback: (Long) => Unit): Command = {
    new CompositeCommand(
      GenerateReadWireOutCommands(address, ReadFromAsicMemoryTopCommand) ++
        GenerateCommitWireInCommands :+
        new SimpleCommand(() => {
          device.UpdateWireOuts()
          val data = device.GetWireOutValue(ReadWire)
          callback(data)
        })
    )
  }

  def MakeReadFromAsicMemoryBottomCommand(address: Int, callback: (Long) => Unit): Command = {
    new CompositeCommand(
      GenerateReadWireOutCommands(address, ReadFromAsicMemoryBotCommand) ++
        GenerateCommitWireInCommands :+
        new SimpleCommand(() => {
          device.UpdateWireOuts()
          val data = device.GetWireOutValue(ReadWire)
          callback(data)
        })
    )
  }

  def MakeReadFromRoicMemoryCommand(address: Int, callback: (Long) => Unit): Command = {
    new CompositeCommand(
      GenerateReadWireOutCommands(address, ReadFromRoicMemoryCommand) ++
        GenerateCommitWireInCommands :+
        new SimpleCommand(() => {
          device.UpdateWireOuts()
          val data = device.GetWireOutValue(ReadWire)
          callback(data)
        })
    )
  }

  def MakeReadFromFlashMemoryCommand(startAddress: Int, data: Array[Byte]): Command = {
    if (startAddress < 0) throw new Exception("Illegal start address")
    if (startAddress + data.length > FlashMemoryMaxAddress) throw new Exception("Illegal end address")

    val numberOfFullBlocks = data.length / FlashBlockSize

    MakeActivateTriggerInCommand(TriggerWire, ResetFlashInFifoTriggerBit)

    // TODO: Read flash memory
//    val blockWriteCommandList = (
//      for (i <- 0 to numberOfFullBlocks) yield
//        MakeWriteFlashBlockCommand(
//          startAddress + i * FlashBlockSize,
//          data.slice(i * FlashBlockSize, (i + 1) * FlashBlockSize)
//        )
//      ).toList
//    new CompositeCommand(blockWriteCommandList)
  }

  def MakeReadFromRoicNucMemoryCommand() = ???

  def MakeDisconnectCommand(): Command = {
    new SimpleCommand(() => device.Disconnect())
  }

  def MakeFpgaResetCommand(reset: Boolean): Command = {
    new CompositeCommand(List(
      MakeResetCommand(if (!reset) FpgaReset else 0, FpgaReset),
      MakeUpdateWireInsCommand()
    ))
  }

  def MakeRoicResetCommand(reset: Boolean): Command = {
    new CompositeCommand(List(
      MakeResetCommand(if (!reset) ChipReset else 0, ChipReset),
      MakeUpdateWireInsCommand()
    ))
  }

  def MakeResetCommand(bit: Long, mask: Long): Command = {
    new SimpleCommand(() => device.SetWireInValue(ResetWire, bit, mask))
  }

  def MakeFpgaConfigurationCommand(bit: Long, mask: Long): Command = {
    new SimpleCommand(() => device.SetWireInValue(FpgaConfigWire, bit, mask))
  }

  def MakeWriteToFlashMemoryCommand(startAddress: Long, data: Array[Byte]): Command = {
    if (startAddress < 0) throw new Exception("Illegal start address")
    if (startAddress + data.length > FlashMemoryMaxAddress) throw new Exception("Illegal end address")

    val numberOfFullBlocks = data.length / FlashBlockSize

    MakeActivateTriggerInCommand(TriggerWire, ResetFlashInFifoTriggerBit)

    val blockWriteCommandList = (
      for (i <- 0 to numberOfFullBlocks) yield
        MakeWriteFlashBlockCommand(
          startAddress + i * FlashBlockSize,
          data.slice(i * FlashBlockSize, (i + 1) * FlashBlockSize)
        )
      ).toList

    new CompositeCommand(blockWriteCommandList)
  }

  def MakeBlockWriteToRoicMemoryCommand(startAddress: Int, blocks: Array[Int]): Command = {
    if (startAddress < 0) throw new Exception("Illegal start address")
    if (startAddress + blocks.length > 127) throw new Exception("Illegal end address")
    if (blocks.min < 0 || blocks.max > 65536) throw new Exception("Illegal value")

    val writeToAsicCommands = (for (i <- 0 until blocks.length) yield MakeWriteToAsicMemoryTopCommand(startAddress + i, blocks(i))).toList

    new CompositeCommand(
      (writeToAsicCommands :+
        MakeSetWireInValueCommand(AsicCommandWire, BlockWriteToRoicMemoryCommand)) ++
        GenerateCommitWireInCommands
    )
  }

  def MakeSoftResetAsicCommand(): Command = {
    new CompositeCommand(
      MakeSetWireInValueCommand(AsicCommandWire, SoftResetAsicCommand) +: GenerateCommitWireInCommands
    )
  }

  def MakeSoftResetRoicCommand(): Command = {
    new CompositeCommand(
      MakeSetWireInValueCommand(AsicCommandWire, SoftResetRoicCommand) +: GenerateCommitWireInCommands
    )
  }

  def MakeUpdateAsicMemoryCommand(): Command = {
    new CompositeCommand(
      MakeSetWireInValueCommand(AsicCommandWire, UpdateAsicMemoryCommand) +: GenerateCommitWireInCommands
    )
  }

  def MakeWriteToAsicMemoryTopCommand(address: Int, value: Long): Command = {
    if (address < 0 || address > 255) throw new Exception("Illegal address")
    if (value < 0 || value > 65535) throw new Exception("Illegal value")
    new CompositeCommand(
      GenerateWriteWireInCommands(address, value, WriteToAsicMemoryTopCommand) ++ GenerateCommitWireInCommands ++
        (MakeSetWireInValueCommand(AsicCommandWire, UpdateAsicMemoryCommand) +: GenerateCommitWireInCommands)
    )
  }

  def MakeWriteToAsicMemoryBotCommand(address: Int, value: Long): Command = {
    if (address < 0 || address > 127) throw new Exception("Illegal address")
    if (value < 0 || value > 65535) throw new Exception("Illegal value")
    new CompositeCommand(
      GenerateWriteWireInCommands(address, value, WriteToAsicMemoryBotCommand) ++ GenerateCommitWireInCommands ++
        (MakeSetWireInValueCommand(AsicCommandWire, UpdateAsicMemoryCommand) +: GenerateCommitWireInCommands)
    )
  }

  def MakeWriteToRoicMemoryCommand(address: Int, value: Int): Command = {
    if (address < 0 || address > 127) throw new Exception("Illegal address")
    if (value < 0 || value > 65535) throw new Exception("Illegal value")

    new CompositeCommand(
      GenerateWriteWireInCommands(address, value, WriteToRoicMemoryCommand) ++ GenerateCommitWireInCommands
    )
  }

  private def MakeWriteFlashBlockCommand(address: Long, data: Array[Byte]): Command = {
    if (data.size > FlashBlockSize) throw new Exception("Data block exceeds maximum flash write block size")

    if (data.size > 0)
      new CompositeCommand(
        List(
          MakeResetFlashInFifoCommand(),
          MakeSetFlashInFifoCommand(data),
          MakeFlashWriteLatchEnableCommand(),
          MakeSendDataToFlashFromFlashInFifoCommand(address, data.length)
        )
      )
    else new SimpleCommand(() => ())
  }

  private def MakeSendDataToFlashFromFlashInFifoCommand(address: Long, dataLength: Int): Command = {
    new CompositeCommand(
      List(MakeSetWireInValueCommand(AsicCommandWire, WriteToFlashMemoryCommand),
        MakeSetWireInValueCommand(AddressWire, address),
        MakeSetWireInValueCommand(DataWire, dataLength),
        MakeSetWireInValueCommand(FlashCommandWire, FlashWriteCommand)) ++ GenerateCommitWireInCommands
    )
  }

  private def MakeFlashWriteLatchEnableCommand(): Command = {
    new CompositeCommand(
      MakeSetWireInValueCommand(FlashCommandWire, FlashWriteLatchEnableCommand) +: GenerateCommitWireInCommands
    )
  }

  private def MakeResetFlashInFifoCommand(): Command = {
    MakeActivateTriggerInCommand(TriggerWire, ResetFlashInFifoTriggerBit)
  }

  private def MakeSetFlashInFifoCommand(data: Array[Byte]): Command = {
    new SimpleCommand(() => device.WriteToPipeIn(FlashInFifoPipe, data.length, data))
  }

  private def GenerateWriteWireInCommands(address: Long, value: Long, command: Int): List[Command] = {
    List(MakeSetWireInValueCommand(AsicCommandWire, command),
      MakeSetWireInValueCommand(AddressWire, address),
      MakeSetWireInValueCommand(DataWire, value))
  }

  private def GenerateReadWireOutCommands(address: Long, command: Int): List[Command] = {
    List(MakeSetWireInValueCommand(AsicCommandWire, command),
      MakeSetWireInValueCommand(AddressWire, address))
  }

  private def GenerateCommitWireInCommands: List[Command] = {
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

trait UsbCam3825Constants {

  // Endpoint addresses
  // 0x00 - 0x1F WireIn
  val ResetWire = 0x00
  val FpgaConfigWire = 0x05

  val AsicCommandWire = 0x01
  val FlashCommandWire = 0x04
  val AddressWire = 0x02
  val DataWire = 0x03

  // 0x20 - 0x3F WireOut
  val ReadWire = 0x20

  // 0x40 - 0x5F TriggerIn
  val TriggerWire = 0x40

  // 0x60 - 0x7F TriggerOut
  val ReadTriggerWire = 0x60

  // 0x80 - 0x9F PipeIn
  val FlashInFifoPipe = 0x80

  // 0xA0 - 0xBF PipeOut
  val ReadPipe = 0xa0
  val DigitalOutputPipe0 = 0xa1
  val DigitalOutputPipe1 = 0xa2
  val DigitalOutputPipe2 = 0xa3
  val DigitalOutputPipe3 = 0xa4

  // Reset bits. These are supposed to set one bit on a 32 bit wire, so powers of two are assigned.
  val FpgaReset = 1
  val ChipReset = 2
  val FlashInFifoReset = 4

  // Fpga configuration switch bits on fpga configuration wire. These are supposed to set one bit on a 32 bit wire, so powers of two are assigned.
  val EmbeddedDvalFval = 16
  val EnableChannel = Array(1,2,4,8)
  val EnableTestFeedOnChannels = 32

  // Commands to be used on AsicCommandWire
  val WriteToAsicMemoryTopCommand = 0xc0
  val ReadFromAsicMemoryTopCommand = 0xc1
  val WriteToAsicMemoryBotCommand = 0xc2
  val ReadFromAsicMemoryBotCommand = 0xc3
  val UpdateAsicMemoryCommand = 0xc4
  val WriteToRoicMemoryCommand = 0xc5
  val ReadFromRoicMemoryCommand = 0xc6
  val BlockWriteToRoicMemoryCommand = 0xc7
  val ReadFromRoicNucMemoryCommand = 0xc8
  val ReadFromFlashMemoryCommand = 0xf0
  val WriteToFlashMemoryCommand = 0xf1
  val SoftResetAsicCommand = 0xa0
  val SoftResetRoicCommand = 0xa1

  // Commands to be used on FlashCommandWire, along with read or write to flash memory asic commands
  val FlashReadCommand = 0x0b
  val FlashWriteCommand = 0x12
  val UnreliableFlashWriteCommand = 0x32
  val FlashWriteLatchEnableCommand = 0x06
  val FlashSectorErase = 0xd8
  val FlashBulkErase = 0xc7

  val ExecuteCommandTriggerBit = 0
  val ResetFlashInFifoTriggerBit = 1
  val ReadNucTriggerBit = 3

  val FlashBlockSize = 256
  val FlashMemoryMaxAddress = 0x1000000 - 1
}
