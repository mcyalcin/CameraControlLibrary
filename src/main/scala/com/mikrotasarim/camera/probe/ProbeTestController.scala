package com.mikrotasarim.camera.probe

import com.mikrotasarim.camera.command.factory.UsbCam3825CommandFactory
import com.mikrotasarim.ui.model.MtAs1410x2MemoryMap

import scala.collection.mutable

class ProbeTestController(commandFactory: UsbCam3825CommandFactory) {

  val defaultValueHexes = new Array[String](127)

  val defaultValues = new Array[String](127)

  class MemoryTestResult(val pass: Boolean, val errorCount: Int, val errors: List[(Int,Int)])

  def RunAsicMemoryDefaultValueTest(): MemoryTestResult = {

    // Reset

    val errors:mutable.SortedSet[(Int, Int)] = mutable.SortedSet()

    MtAs1410x2MemoryMap.ReadAsicMemory()

    for (i <- 0 to 127)
      for (j <- 0 to 15)
        if (MtAs1410x2MemoryMap.memoryModel(i).text.value(j) != defaultValues(i)(j))
          errors += Tuple2(i,j)

    new MemoryTestResult(errors.isEmpty, errors.toList.length, errors.toList)
  }

  def RunAsicMemoryToggleTest(): MemoryTestResult = {

    // Reset

    val errors:mutable.SortedSet[(Int, Int)] = mutable.SortedSet()

    FillMemWithOnes()
    FillMemWithZeroes()
    for (i <- 0 to 127)
      for (j <- 0 to 15)
        if (MtAs1410x2MemoryMap.memoryModel(i).text.value(j) != defaultValues(i)(j))
          errors += Tuple2(i,j)
    FillMemWithOnes()
    for (i <- 0 to 127)
      for (j <- 0 to 15)
        if (MtAs1410x2MemoryMap.memoryModel(i).text.value(j) != defaultValues(i)(j))
          errors += Tuple2(i,j)

    new MemoryTestResult(errors.isEmpty, errors.toList.length, errors.toList)
  }

  def FillMemWithOnes(): Unit = {
    for (i <- 0 to 255) {
      commandFactory.MakeWriteToAsicMemoryTopCommand(i, 0xffff)
    }
    for (i <- 0 to 127) {
      commandFactory.MakeWriteToAsicMemoryTopCommand(i, 0xffff)
    }
  }

  def FillMemWithZeroes(): Unit = {
    for (i <- 0 to 255) {
      commandFactory.MakeWriteToAsicMemoryTopCommand(i, 0)
    }
    for (i <- 0 to 127) {
      commandFactory.MakeWriteToAsicMemoryTopCommand(i, 0)
    }
  }

  class PowerConsumptionTestResult(
    val resetCurrent: Double,
    val runningCurrent: Double,
    val biasGenCurrent: Double,
    val timingGenCurrent: Double,
    val adc0Current: Double,
    val adc1Current: Double,
    val adc2Current: Double,
    val adc3Current: Double
  )

  def RunPowerConsumptionTest(): PowerConsumptionTestResult = {

    // Reset

    // ResetChip()

    // resetCurrent = MeasureCurrent()
    // LiftReset()
    // runningCurrent = MeasureCurrent()
    // EnableBiasGenerator()
    // biasGenCurrent = MeasureCurrent()
    // EnableTimingGenerator()
    // timingGenCurrent = MeasureCurrent()
    // EnableAdcChannel0()
    // adc0Current = MeasureCurrent()
    // EnableAdcChannel1()
    // adc1Current = MeasureCurrent()
    // EnableAdcChannel2()
    // adc2Current = MeasureCurrent()
    // EnableAdcChannel3()
    // adc3Current = MeasureCurrent()

    /*new PowerConsumptionTestResult(
      resetCurrent,
      runningCurrent,
      biasGenCurrent,
      timingGenCurrent,
      adc0Current,
      adc1Current,
      adc2Current,
      adc3Current
    )*/
    ???
  }

  var dieNumber = 0

  def RunFlashInterfaceTest(): Boolean = {

    // Reset

    val testData = new Array[Byte](256)
    commandFactory.MakeWriteToFlashMemoryCommand(dieNumber,testData).Execute()
    val output = commandFactory.ReadFromFlashMemory(dieNumber, 256)

    for (i <- 0 to 255) if (output(i) != testData(i)) return false

    true
  }

  def RunFlashSectorErase(): Unit = {
    commandFactory.MakeFlashSectorEraseCommand(0).Execute()
  }

  def RunRoicInterfaceTest(): Boolean = {

    // Reset

    var pass = true

    commandFactory.MakeWriteToRoicMemoryCommand(15, 0x4321)
    commandFactory.MakeReadFromRoicMemoryCommand(15, o => if (o != 0x4321) pass = false)
    commandFactory.MakeWriteToRoicMemoryCommand(15, 0x1234)
    commandFactory.MakeReadFromRoicMemoryCommand(15, o => if (o != 0x1234) pass = false)

    pass
  }

  def RunOutputStageTest(): Boolean = {

    // Reset

    val testPatternTop = 0x1234
    val testPatternBot = 0x4321

    commandFactory.MakeWriteToAsicMemoryTopCommand(31, 0xff00)
    commandFactory.MakeWriteToAsicMemoryTopCommand(32, 0x00ff)
    commandFactory.MakeWriteToAsicMemoryTopCommand(35, 0x70e4)
    commandFactory.MakeWriteToAsicMemoryTopCommand(28, testPatternTop)
    commandFactory.MakeWriteToAsicMemoryTopCommand(35, testPatternBot)

    commandFactory.MakeReadOutputCommand(4)

    val buf0 = Array.ofDim[Byte](4)
    commandFactory.device.ReadFromBlockPipeOut(commandFactory.DigitalOutputPipe0, buf0.length, buf0)
    val buf2 = Array.ofDim[Byte](4)
    commandFactory.device.ReadFromBlockPipeOut(commandFactory.DigitalOutputPipe0, buf2.length, buf2)

    commandFactory.ConvertToWords(buf0)(0) == testPatternTop && commandFactory.ConvertToWords(buf2)(0) == testPatternBot
  }

}