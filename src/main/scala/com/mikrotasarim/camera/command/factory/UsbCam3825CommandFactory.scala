package com.mikrotasarim.camera.command.factory

import com.mikrotasarim.camera.command._
import com.mikrotasarim.camera.device._

object UsbCam3825CommandFactory {

}

class UsbCam3825CommandFactory(device: DeviceInterface) extends UsbCam3825Constants {

  // TODO: Work out how to implement read commands.
  def MakeReadFromAsicMemoryTopCommand() = ???

  def MakeReadFromAsicMemoryBottomCommand() = ???

  def MakeReadFromRoicMemoryCommand() = ???

  def MakeReadFromFlashMemoryCommand() = ???

  def MakeReadFromRoicNucMemoryCommand() = ???

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

  def MakeWriteToAsicMemoryTopCommand(address: Int, value: Int): Command = {
    if (address < 0 || address > 255) throw new Exception("Illegal address")
    if (value < 0 || value > 65535) throw new Exception("Illegal value")
    new CompositeCommand(
      GenerateWriteWireInCommands(address, value, WriteToAsicMemoryTopCommand) ++ GenerateCommitWireInCommands
    )
  }

  def MakeWriteToAsicMemoryBotCommand(address: Int, value: Int): Command = {
    if (address < 0 || address > 127) throw new Exception("Illegal address")
    if (value < 0 || value > 65535) throw new Exception("Illegal value")
    new CompositeCommand(
      GenerateWriteWireInCommands(address, value, WriteToAsicMemoryBotCommand) ++ GenerateCommitWireInCommands
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

  private def GenerateWriteWireInCommands(address: Long, value: Int, command: Int): List[Command] = {
    List(MakeSetWireInValueCommand(AsicCommandWire, command),
      MakeSetWireInValueCommand(AddressWire, address),
      MakeSetWireInValueCommand(DataWire, value))
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
  val AsicCommandWire = 0x01
  val FlashCommandWire = 0x04
  val AddressWire = 0x02
  val DataWire = 0x03

  // 0x20 - 0x3F WireOut
  // 0x40 - 0x5F TriggerIn
  val TriggerWire = 0x40

  // 0x60 - 0x7F TriggerOut
  val ReadTriggerWire = 0x60

  // 0x80 - 0x9F PipeIn
  val FlashInFifoPipe = 0x80

  // 0xA0 - 0xBF PipeOut

  // Reset bits - these are supposed to set one bit on a 32 bit wire, so we need to use powers of two
  val FpgaReset = 1
  val RoicReset = 2
  val FlashInFifoReset = 4

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
  val FlashMemoryMaxAddress = 0x1000000-1

  val Mt3825BaBiasGeneratorMemoryAddresses = Map(
    "ibias_predrv" -> 51,
    "vin_p_ref" -> 80,
    "vin_n_ref" -> 79,
    "vpcas_predrv" -> 78,
    "vncas_predrv" -> 77,
    "ibi_pga" -> 50,
    "vpcas_pga" -> 76,
    "vncas_pga" -> 75,
    "ibias_cm_pga" -> 49,
    "vcm_pga" -> 74,
    "vpcas_cm_pga" -> 73,
    "vncas_cm_pga" -> 72,
    "ibias_adc" -> 48,
    "vpcas_1_adc" -> 71,
    "vncas_1_adc" -> 70,
    "vncas_p_1_adc" -> 69,
    "vpcas_2_adc" -> 68,
    "vncas_2_adc" -> 67,
    "vncas_p_2_adc" -> 66,
    "vpcas_3_adc" -> 65,
    "vncas_3_adc" -> 64,
    "ibias_cm_adc" -> 47,
    "vcm_adc" -> 63,
    "vpcas_cm_adc" -> 62,
    "vncas_cm_adc" -> 61,

    "ibias_ref_high" -> 46,
    "vref_high" -> 60,
    "vpcas_ref_high" -> 59,
    "vncas_ref_high" -> 58,

    "ibias_ref_low" -> 45,
    "vref_low" -> 57,
    "vpcas_ref_low" -> 56,
    "vncas_ref_low" -> 55,

    "ibias_ref_mid" -> 44,
    "vref_mid" -> 54,
    "vpcas_ref_mid" -> 53,
    "vncas_ref_mid" -> 52
  )
}
