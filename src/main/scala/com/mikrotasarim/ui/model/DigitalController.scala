package com.mikrotasarim.ui.model

import spire.implicits._
import scala.collection.immutable.ListMap
import scalafx.beans.property.{StringProperty, BooleanProperty, IntegerProperty}
import DeviceInterfaceModel.{CommitMemoryLocation, MemoryLocation}

import scalafx.collections.ObservableBuffer

abstract class DigPadDrive {
  val label: String
  val strength: IntegerProperty
  val changed: BooleanProperty
  def Commit(): Unit
  def Reset(): Unit
}

class DigitalController {

  class DigPadDriveImpl(val label: String, val address: Int, val offset: Int, val defaultValue: Int) extends DigPadDrive{

    def this(label: String, address: Int, offset: Int) = this(label, address, offset, 3)

    val strength = IntegerProperty(defaultValue)
    var committedStrength = defaultValue
    val changed = BooleanProperty(value = false)
    strength.onChange(changed.value = committedStrength != strength.value)

    def Commit() = {
      committedStrength = strength.value
      CommitDriveStrengthWord(address)
      changed.value = false
    }

    def Reset() = {
      strength.value = defaultValue
      if (committedStrength != defaultValue) Commit()
    }
  }

  private def CommitDriveStrengthWord(addr: Int) = {
    val value = digPadDrives.filter(_.address == addr).map(d => (2 pow d.offset) * d.committedStrength).sum
    val word = new MemoryLocation {
      override def memoryValue: Long = value

      override val address: Int = addr
    }
    CommitMemoryLocation(word)
  }

  val digPadDrives = List(
    new DigPadDriveImpl("sb_flash", 95, 0),
    new DigPadDriveImpl("sio_flash_out<3>", 95, 3),
    new DigPadDriveImpl("sio_flash_out<2>", 95, 6),
    new DigPadDriveImpl("sio_flash_out<1>", 95, 9),
    new DigPadDriveImpl("sio_flash_out<0>", 95, 12),
    new DigPadDriveImpl("clk_flash", 94, 0),
    new DigPadDriveImpl("fsync_roic", 94, 3),
    new DigPadDriveImpl("rstb_roic", 94, 6),
    new DigPadDriveImpl("slatch_roic", 94, 9),
    new DigPadDriveImpl("sdin_roic<3>", 94, 12),
    new DigPadDriveImpl("sdin_roic<2>", 93, 0),
    new DigPadDriveImpl("sdin_roic<1>", 93, 3),
    new DigPadDriveImpl("sdin_roic<0>", 93, 6),
    new DigPadDriveImpl("sysclk_roic", 93, 9),
    new DigPadDriveImpl("sdout_asic", 93, 12, 7)
  ).reverse

  val digTest0Options = ListMap(
    "Logic 0" -> "00000",
    "Logic 1" -> "00001",
    "OS_dval" -> "00010",
    "OS_fval" -> "00011",
    "TGD_signal0" -> "00100",
    "TGD_signal1" -> "00101",
    "TGD_signal2" -> "00110",
    "TGD_signal3" -> "00111",
    "SPI_command_ready" -> "01000",
    "SPI_address_ready" -> "01001",
    "SPI_data_ready" -> "01010",
    "SPI_data_readyx2" -> "01011",
    "SPI_data_readyx4" -> "01100",
    "SPI_data_readyx8" -> "01101",
    "MEM_write_pt1" -> "01110",
    "MEM_write_pt2" -> "01111",
    "MEM_read_pt1" -> "10000",
    "MEM_read_pt2" -> "10001",
    "MEM_update_pt1" -> "10010",
    "MEM_update_pt2" -> "10011",
    "ASIC_frame_start" -> "10100",
    "ASIC_frame_end" -> "10101",
    "ROIC_send_data" -> "10110",
    "ROIC_queue_empty" -> "10111",
    "TGD_counter0" -> "11000",
    "TGD_counter16" -> "11001",
    "ROIC_pixclk" -> "11010",
    "TG_clk" -> "11011",
    "OS_clk_fast" -> "11100",
    "ROIC_sys_clk" -> "11101",
    "FM_clk" -> "11110",
    "ASIC_sys_clk" -> "11111"
  )

  val digTest1Options = ListMap(
    "Logic 0" -> "00000",
    "Logic 1" -> "00001",
    "OS_dval" -> "00010",
    "OS_fval" -> "00011",
    "TGD_signal0" -> "00100",
    "TGD_signal1" -> "00101",
    "TGD_signal2" -> "00110",
    "TGD_signal3" -> "00111",
    "CMD_write_pt1" -> "01000",
    "CMD_read_pt1" -> "01001",
    "CMD_write_pt2" -> "01010",
    "CMD_read_pt2" -> "01011",
    "CMD_update" -> "01100",
    "CMD_write_roic" -> "01101",
    "CMD_read_roic" -> "01110",
    "CMD_write_roic_nuc" -> "01111",
    "CMD_read_roic_nuc" -> "10000",
    "CMD_reset_asic" -> "10001",
    "CMD_reset_roic" -> "10010",
    "CMD_read_flash_mem" -> "10011",
    "CMD_write_flash_mem" -> "10100",
    "SPI_stop_cmd_dec" -> "10101",
    "MEM_read_data_ready" -> "10110",
    "FM_dq_inout" -> "10111",
    "TGD_counter0" -> "11000",
    "TGD_counter16" -> "11001",
    "ROIC_pixclk" -> "11010",
    "TG_clk" -> "11011",
    "OS_clk_fast" -> "11100",
    "ROIC_sys_clk" -> "11101",
    "FM_clk" -> "11110",
    "ASIC_sys_clk" -> "11111"
  )

  val digTestOptions = List(digTest0Options, digTest1Options)

  val digTestSelection = ObservableBuffer(new StringProperty("Logic 0"), new StringProperty("Logic 0"))
  digTestSelection(0).onChange(CommitDigTest())
  digTestSelection(1).onChange(CommitDigTest())

  def CommitDigTest() = {
    val value = Integer.parseInt(digTestOptions(0)(digTestSelection(0).value), 2) * (2 pow 8) + Integer.parseInt(digTestOptions(1)(digTestSelection(1).value), 2)
    val word = new MemoryLocation {
      override def memoryValue: Long = value

      override val address: Int = 106
    }
    CommitMemoryLocation(word)
  }

  val internalRoicLabels = ObservableBuffer("Internal", "ROIC")
  val internalPixelClock = BooleanProperty(value = false)
  internalPixelClock.onChange(CommitSwitches())
  val internalDvalFval = BooleanProperty(value = false)
  internalDvalFval.onChange(CommitSwitches())

  val clockSpeedLabels = ObservableBuffer("Slow", "Fast")
  val slowFlashClock = BooleanProperty(value = false)
  slowFlashClock.onChange(CommitSwitches())

  val frameStartSelected = BooleanProperty(value = false)
  frameStartSelected.onChange(CommitSwitches())
  val autoSelected = BooleanProperty(value = false)
  autoSelected.onChange(CommitSwitches())

  def CommitSwitches() = {
    val value = (if (frameStartSelected.value) 16 else 0) +
      (if (!slowFlashClock.value) 8 else 0) +
      (if (internalDvalFval.value) 4 else 0) +
      (if (internalPixelClock.value) 2 else 0) +
      (if (autoSelected.value) 1 else 0)
    val word = new MemoryLocation {
      override def memoryValue: Long = value

      override val address: Int = 105
    }
    CommitMemoryLocation(word)
  }
}
