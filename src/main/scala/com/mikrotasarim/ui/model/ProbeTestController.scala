package com.mikrotasarim.ui.model

import java.io.{FileWriter, File}

import com.mikrotasarim.camera.command.factory.UsbCam3825CommandFactory
import com.mikrotasarim.camera.device.OpalKellyInterface
import jssc.{SerialPortList, SerialPort}

import scalafx.Includes._
import scala.collection.immutable.IndexedSeq
import scala.collection.immutable.Range.Inclusive
import scala.collection.mutable
import scalafx.beans.property.{BooleanProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scala.language.reflectiveCalls

object ProbeTestController {

  var serialPort: SerialPort = _

  def MeasureCurrent() = {
    serialPort.writeString("MEAS:CURR?\n")
    Thread.sleep(300)
    serialPort.readString().toDouble
  }

  val comPortList = ObservableBuffer(SerialPortList.getPortNames.toList)
  val selectedComPort = StringProperty("")

  selectedComPort.onChange(SetSerialPort())

  def SetSerialPort(): Unit = {
    serialPort = new SerialPort(selectedComPort.value)
    openPort()
  }

  def openPort(): Unit = {
    serialPort.openPort()
    serialPort.setParams(9600, 8, 1, 0)
  }

  def closePort(): Unit = {
    serialPort.closePort()
  }

  def setLocal(): Unit = {
    serialPort.writeString("SYST:LOC\n")
  }

  def setRemote(): Unit = {
    serialPort.writeString("SYST:REM\n")
  }

  def outputOn(): Unit = {
    serialPort.writeString("OUTP ON\n")
  }

  def outputOff(): Unit = {
    serialPort.writeString("OUTP OFF\n")
  }

  val outputPath = StringProperty("")
  val waferId = StringProperty("Wafer")
  val dieNumber = StringProperty("1")

  def RunTest(value: Int): Unit = value match {
    case 1 => RunCurrentTest()
    case 2 => RunSerialInterfaceTest()
    case 3 => RunAsicMemoryToggleTest()
    case 4 => RunPowerConsumptionTest()
    case 5 => RunFlashInterfaceTest()
    case 6 => RunOutputStageTest()
    case 7 => RunAdcFunctionalityTest()
    case 8 => RunPgaFunctionalityTest()
    case 9 => RunInputBufferFunctionalityTest()
    case 10 => RunPgaGainTest()
    case 11 => RunAdcLinearityTest()
    case 12 => RunAdcNoiseTest()
    case 13 => RunRoicInterfaceTest()
  }

  def RunAllTests(): Unit = {
    outputOn()
    Thread.sleep(1000)
    for (i <- 1 to 13) RunTest(i)
  }

  def SaveAndProceed(): Unit = {

    def using[A <: {def close() : Unit}, B](resource: A)(f: A => B): B =
      try f(resource) finally resource.close()

    def writeStringToFile(file: File, data: String, appending: Boolean = false) =
      using(new FileWriter(file, appending))(_.write(data))

    //writeStringToFile(new File(outputPath.value + "/" + waferId.value + "/Die" + dieNumber.value + "/testResult.txt"), "dummy")

    for (i <- 1 to 13) {
      pass(i).value = false; fail(i).value = false
    }

    outputOff()

    dieNumber.value = (dieNumber.value.toInt + 1).toString
  }

  val current = StringProperty("Current Off Reset: 0")
  val faultyBits = new StringProperty("Faulty Bits: 0")

  val labels = Array(
    "0.",
    "1. Current Test",
    "2. Serial Interface Test",
    "3. Memory Test",
    "4. Power Consumption Test",
    "5. Flash Memory Test",
    "6. Output Stage Test",
    "7. ADC Functionality Test",
    "8. PGA Functionality Test",
    "9. Input Buffer Test",
    "10. PGA Gain Test",
    "11. ADC Channel Linearity Test",
    "12. ADC Channel Noise Test",
    "13. Roic Memory Test"
  )

  val pass = ObservableBuffer(
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false)
  )

  val fail = ObservableBuffer(
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false),
    BooleanProperty(value = false)
  )

  class ProbeTestResult(
                         val currentTestResult: Option[CurrentTestResult],
                         val serialInterfaceTestResult: Option[Boolean],
                         val memoryTestResult: Option[MemoryTestResult],
                         val powerConsumptionTestResult: Option[PowerConsumptionTestResult],
                         val flashInterfaceTestResult: Option[Boolean],
                         val roicInterfaceTestResult: Option[Boolean],
                         val outputStageTestResult: Option[Boolean],
                         val adcFunctionalityTestResult: Option[Boolean],
                         val pgaFunctionalityTestResult: Option[Boolean],
                         val inputBufferTestResult: Option[Boolean],
                         val pgaGainTestResult: Option[Boolean],
                         val adcLinearityTestResult: Option[Boolean],
                         val adcNoiseTestResult: Option[AdcNoiseTestResult]
                         )

  var currentTestResult = new ProbeTestResult(None, None, None, None, None, None, None, None, None, None, None, None, None)

  val commandFactory = DeviceInterfaceModel.commandFactory

  val defaultValueHexes = new Array[String](127)

  val defaultValues = new Array[String](127)

  class MemoryTestResult(val pass: Boolean, val errorCount: Int, val errors: List[(Int, Int)])

  def Reset(): Unit = {
    DeviceInterfaceModel.commandFactory.MakeFpgaResetCommand(reset = true).Execute()
    DeviceInterfaceModel.commandFactory.MakeFpgaResetCommand(reset = false).Execute()
    DeviceInterfaceModel.commandFactory.ChangeSpeedFactor(2)
  }

  def RunAsicMemoryDefaultValueTest(): MemoryTestResult = {

    Reset()

    val errors: mutable.SortedSet[(Int, Int)] = mutable.SortedSet()

    MtAs1410x2MemoryMap.ReadAsicMemory()

    for (i <- 0 to 127)
      for (j <- 0 to 15)
        if (MtAs1410x2MemoryMap.memoryModel(i).text.value(j) != defaultValues(i)(j))
          errors += Tuple2(i, j)

    new MemoryTestResult(errors.isEmpty, errors.toList.length, errors.toList)
  }

  def RunAsicMemoryToggleTest(): MemoryTestResult = {

    Reset()

    val errors: mutable.SortedSet[(Int, Int)] = mutable.SortedSet()

    FillMemWithOnes()
    FillMemWithZeroes()
    for (i <- topMemoryIndexes) {
      val word = ("0000000000000000" + commandFactory.ReadFromAsicMemory(i).toBinaryString).takeRight(16)
      for (j <- 0 to 15)
        if (word(j) != '0')
          errors += Tuple2(i, j)
    }

    for (i <- botMemoryIndexes) {
      val word = ("0000000000000000" + commandFactory.ReadFromAsicMemoryBot(i).toBinaryString).takeRight(16)
      for (j <- 0 to 15)
        if (word(j) != '0')
          errors += Tuple2(256 + i, j)
    }

    FillMemWithOnes()

    for (i <- topMemoryIndexes) {
      val word = ("0000000000000000" + commandFactory.ReadFromAsicMemory(i).toBinaryString).takeRight(16)
      for (j <- 0 to 15)
        if (word(j) != '1')
          errors += Tuple2(i, j)
    }

    for (i <- botMemoryIndexes) {
      val word = ("0000000000000000" + commandFactory.ReadFromAsicMemoryBot(i).toBinaryString).takeRight(16)
      for (j <- 0 to 15)
        if (word(j) != '1')
          errors += Tuple2(256 + i, j)
    }

    if (errors.isEmpty) pass(3).value = true else fail(3).value = true

    new MemoryTestResult(errors.isEmpty, errors.toList.length, errors.toList)
  }

  val volatileAddresses = Set(93)

  var topMemoryIndexes: IndexedSeq[Int] = (0 to 255).filter(!volatileAddresses.contains(_))
  val botMemoryIndexes: Inclusive = 0 to 127

  def FillMemWithOnes(): Unit = {
    for (i <- topMemoryIndexes) {
      commandFactory.MakeWriteToAsicMemoryTopCommand(i, 0xffff).Execute()
    }
    for (i <- botMemoryIndexes) {
      commandFactory.MakeWriteToAsicMemoryTopCommand(i, 0xffff).Execute()
    }
  }

  def FillMemWithZeroes(): Unit = {
    for (i <- topMemoryIndexes) {
      commandFactory.MakeWriteToAsicMemoryTopCommand(i, 0).Execute()
    }
    for (i <- botMemoryIndexes) {
      commandFactory.MakeWriteToAsicMemoryTopCommand(i, 0).Execute()
    }
  }

  def within(current: Double, target: Double, margin: Double): Boolean =
    current > target - margin && current < target + margin

  class PowerConsumptionTestResult(
                                    val resetCurrent: Double,
                                    val runningCurrent: Double,
                                    val oneChannelCurrent: Double,
                                    val twoChannelCurrent: Double,
                                    val fourChannelCurrent: Double,
                                    val noLvdsCurrent: Double,
                                    val lvds0Current: Double,
                                    val lvds1Current: Double,
                                    val lvds2Current: Double,
                                    val lvds3Current: Double,
                                    val lvds4Current: Double,
                                    val lvds5Current: Double,
                                    val lvds6Current: Double,
                                    val lvds7Current: Double
                                    ) {

    def pass: Boolean = {
      within(resetCurrent, 200, 10) &&
        within(runningCurrent, 224, 10) &&
        within(oneChannelCurrent, 271, 10) &&
        within(twoChannelCurrent, 310, 10) &&
        within(fourChannelCurrent, 380, 10) &&
        within(noLvdsCurrent, 351, 10) &&
        within(lvds0Current, 357, 10) &&
        within(lvds1Current, 362, 10) &&
        within(lvds2Current, 362, 10) &&
        within(lvds3Current, 365, 10) &&
        within(lvds4Current, 367, 10) &&
        within(lvds5Current, 371, 10) &&
        within(lvds6Current, 375, 10) &&
        within(lvds7Current, 380, 10)
    }
  }

  class CurrentTestResult(
                           val pass: Boolean,
                           val currentValue: Double
                           )

  def RunCurrentTest(): CurrentTestResult = {

    Reset()

    commandFactory.MakeChipResetCommand(reset = false).Execute()
    val value = MeasureCurrent()

    val min = 0.200
    val max = 0.230

    if (value < max && value > min) pass(1).value = true else fail(1).value = true

    new CurrentTestResult(value < max && value > min, value)
  }

  def RunSerialInterfaceTest(): Boolean = {

    Reset()

    var pas = true

    commandFactory.MakeWriteToAsicMemoryTopCommand(0x59, 0xaaaa).Execute()
    commandFactory.MakeReadFromAsicMemoryTopCommand(0x59, (value) => pas = value == 0xaaaa).Execute()

    if (pas) pass(2).value = true else fail(2).value = true

    pas
  }

  def RunPowerConsumptionTest(): PowerConsumptionTestResult = {

    Reset()

    commandFactory.MakeChipResetCommand(reset = true).Execute()
    val resetCurrent = MeasureCurrent()
    commandFactory.MakeChipResetCommand(reset = false).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(40, 0x5).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(19, 0xe).Execute()
    val runningCurrent = MeasureCurrent()
    commandFactory.MakeWriteToAsicMemoryTopCommand(21, 0x14).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(24, 0x8).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(25, 0x8).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(26, 0x16).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(27, 0x152).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(32, 0xc200).Execute()
    val oneChannelCurrent = MeasureCurrent()
    commandFactory.MakeWriteToAsicMemoryTopCommand(21, 0x3c).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(24, 0x108).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(25, 0x108).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(26, 0x14).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(27, 0x6).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(32, 0xc000).Execute()
    val twoChannelCurrent = MeasureCurrent()
    commandFactory.MakeWriteToAsicMemoryTopCommand(82, 0x3c).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(85, 0x108).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(86, 0x108).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(87, 0x14).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(88, 0x6).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(32, 0x0).Execute()
    val fourChannelCurrent = MeasureCurrent()
    commandFactory.MakeWriteToAsicMemoryTopCommand(32, 0xff00).Execute()
    val noLvdsCurrent = MeasureCurrent()
    commandFactory.MakeWriteToAsicMemoryTopCommand(32, 0xfe00).Execute()
    val lvds0Current = MeasureCurrent()
    commandFactory.MakeWriteToAsicMemoryTopCommand(32, 0xfc00).Execute()
    val lvds1Current = MeasureCurrent()
    commandFactory.MakeWriteToAsicMemoryTopCommand(32, 0xf800).Execute()
    val lvds2Current = MeasureCurrent()
    commandFactory.MakeWriteToAsicMemoryTopCommand(32, 0xf000).Execute()
    val lvds3Current = MeasureCurrent()
    commandFactory.MakeWriteToAsicMemoryTopCommand(32, 0xe000).Execute()
    val lvds4Current = MeasureCurrent()
    commandFactory.MakeWriteToAsicMemoryTopCommand(32, 0xc000).Execute()
    val lvds5Current = MeasureCurrent()
    commandFactory.MakeWriteToAsicMemoryTopCommand(32, 0x8000).Execute()
    val lvds6Current = MeasureCurrent()
    commandFactory.MakeWriteToAsicMemoryTopCommand(32, 0x0000).Execute()
    val lvds7Current = MeasureCurrent()

    val res = new PowerConsumptionTestResult(
      resetCurrent,
      runningCurrent,
      oneChannelCurrent,
      twoChannelCurrent,
      fourChannelCurrent,
      noLvdsCurrent,
      lvds0Current,
      lvds1Current,
      lvds2Current,
      lvds3Current,
      lvds4Current,
      lvds5Current,
      lvds6Current,
      lvds7Current
    )

    if (res.pass) pass(4).value = true else fail(4).value = true

    res
  }

  def RunFlashInterfaceTest(): Boolean = {

    Reset()

    commandFactory.ChangeSpeedFactor(1)

    RunFlashSectorErase()

    Thread.sleep(1000)

    val output1 = commandFactory.ReadFromFlashMemory(dieNumber.value.toInt, 256)

    val testData = new Array[Byte](256)

    for (i <- 0 to 255) testData(i) = i.toByte

    commandFactory.MakeWriteToFlashMemoryCommand(dieNumber.value.toInt, testData).Execute()

    val output = commandFactory.ReadFromFlashMemory(dieNumber.value.toInt, 256)

    for (i <- 0 to 255) if (output1(i) != 255) {
      fail(5).value = true
      return false
    }

    for (i <- 0 to 255) if (output(i) != testData(i)) {
      fail(5).value = true
      return false
    }

    pass(5).value = true

    true
  }

  def RunFlashSectorErase(): Unit = {
    commandFactory.MakeFlashSectorEraseCommand(0).Execute()
  }

  def RunRoicInterfaceTest(): Boolean = {

    DisconnectFromDevice()
    Thread.sleep(100)
    DeployAtfile()

    Reset()

    var pas = true

    commandFactory.MakeWriteToRoicMemoryCommand(15, 0xaaaa).Execute()
    commandFactory.MakeReadFromRoicMemoryCommand(15, o => if (o != 0xaaaa) pas = false).Execute()

    DisconnectFromDevice()
    Thread.sleep(100)
    DeployBitfile()

    if (pas) pass(12).value = true else fail(12).value = true

    pas
  }

  def DeployAtfile() {
    val device = new OpalKellyInterface(DeviceInterfaceModel.alternateBitfilePath.value)
    DeviceInterfaceModel.commandFactory = new UsbCam3825CommandFactory(device)
    DeviceInterfaceModel.bitfileDeployed.value = true
  }

  def DeployBitfile() {
    val device = new OpalKellyInterface(DeviceInterfaceModel.bitfilePath.value)
    DeviceInterfaceModel.commandFactory = new UsbCam3825CommandFactory(device)
    DeviceInterfaceModel.bitfileDeployed.value = true
  }

  def DisconnectFromDevice() {
    commandFactory.MakeDisconnectCommand().Execute()
    DeviceInterfaceModel.bitfileDeployed.value = false
  }

  def RunOutputStageTest(): Boolean = {

    Reset()

    val testPatternTop = 0x3a28
    val testPatternBot = 0x2c14

    commandFactory.MakeWriteToAsicMemoryTopCommand(31, 0xff00).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(32, 0x00ff).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(35, 0x70e4).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(28, testPatternTop).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(38, testPatternBot).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(36, 0x0311).Execute()

    val values0 = readChannels()

    commandFactory.MakeWriteToAsicMemoryTopCommand(31, 0).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(32, 0).Execute()

    val values01 = readChannels()

    commandFactory.MakeWriteToAsicMemoryTopCommand(35, 0x50e4).Execute()
    val values1 = readChannels()

    commandFactory.MakeWriteToAsicMemoryTopCommand(40, 0x0005).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(3, 0x0202).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(4, 0x0200).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(10, 0x0002).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(12, 0xa000).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(19, 0x000e).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(21, 0x003c).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(22, 0x0233).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(23, 0x001e).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(24, 0x0108).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(25, 0x0108).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(26, 0x0000).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(27, 0x0006).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(82, 0x0030).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(83, 0x0233).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(84, 0x001e).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(85, 0x0084).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(86, 0x0084).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(87, 0x0017).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(88, 0x0066).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(35, 0x20e4).Execute()

    val values2 = readChannels()


    commandFactory.MakeWriteToAsicMemoryTopCommand(35, 0x2093).Execute()
    val values3 = readChannels()


    commandFactory.MakeWriteToAsicMemoryTopCommand(35, 0x204e).Execute()
    val values4 = readChannels()

    commandFactory.MakeWriteToAsicMemoryTopCommand(35, 0x2039).Execute()
    val values5 = readChannels()

    var result = true
    if (!(
      values0(0) == testPatternTop && values0(2) == testPatternBot &&
        values0(1) == testPatternTop && values0(3) == testPatternBot
      )) result = false
    if (!(
      values01(0) == testPatternTop && values01(2) == testPatternBot &&
        values01(1) == testPatternTop && values01(3) == testPatternBot
      )) result = false
    if (!(
      values1(0) == 0x145c && values1(2) == 0x2834 &&
        values1(1) == 0x145c && values1(3) == 0x2834
      )) result = false
    if (!(within(values2(0), 1432, 300) &&
      within(values2(1), 14952, 300) &&
      within(values2(2), 11571, 300) &&
      within(values2(3), 4812, 300))) result = false

    if (!(within(values3(1), 1432, 300) &&
      within(values3(2), 14952, 300) &&
      within(values3(3), 11571, 300) &&
      within(values3(0), 4812, 300))) result = false

    if (!(within(values4(2), 1432, 300) &&
      within(values4(3), 14952, 300) &&
      within(values4(0), 11571, 300) &&
      within(values4(1), 4812, 300))) result = false

    if (!(within(values5(3), 1432, 300) &&
      within(values5(0), 14952, 300) &&
      within(values5(1), 11571, 300) &&
      within(values5(2), 4812, 300))) result = false

    if (result) pass(6).value = true else fail(6).value = true

    result
  }

  def InitializeAdc(): Unit = {
    commandFactory.MakeWriteToAsicMemoryTopCommand(40, 0x0005).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(3, 0x0202).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(2, 0x0200).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(10, 0x0002).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(12, 0xa000).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(19, 0x000e).Execute()
  }

  def SetAdcBlock(baseIndex: Int, valueMap: Map[Int, Int]): Unit = {
    for (key <- valueMap.keys)
      commandFactory.MakeWriteToAsicMemoryTopCommand(baseIndex + key, valueMap(key)).Execute()
  }

  def  RunAdcFunctionalityTest(): Boolean = {

    Reset()
    InitializeAdc()

    val map = Map(
      0 -> 0x0030,
      1 -> 0x0233,
      2 -> 0x001e,
      3 -> 0x0084,
      4 -> 0x0084,
      5 -> 0x0017,
      6 -> 0x0066
    )

    SetAdcBlock(21, map)
    SetAdcBlock(82, map)

    val values = readChannels()

    val res = within(values(0), 4812, 500) &&
      within(values(1), 11571, 500) &&
      within(values(2), 11571, 500) &&
      within(values(3), 4812, 500)

    if (res) pass(7).value = true else fail(7).value = true

    res
  }

  def RunPgaFunctionalityTest(): Boolean = {

    Reset()
    InitializeAdc()

    val map = Map(
      0 -> 0x003c,
      1 -> 0x0233,
      2 -> 0x001e,
      3 -> 0x0108,
      4 -> 0x0108,
      5 -> 0x0014,
      6 -> 0x0006
    )

    SetAdcBlock(21, map)
    SetAdcBlock(82, map)

    val values = readChannels()

    val res = within(values(0), 3123, 500) &&
      within(values(1), 13260, 500) &&
      within(values(2), 13260, 500) &&
      within(values(3), 3123, 500)

    if (res) pass(8).value = true else fail(8).value = true

    res
  }

  def readChannels(): Array[Long] = {

    val buf0 = Array.ofDim[Byte](1024 * 4)
    val buf1 = Array.ofDim[Byte](1024 * 4)
    val buf2 = Array.ofDim[Byte](1024 * 4)
    val buf3 = Array.ofDim[Byte](1024 * 4)

    commandFactory.MakeReadOutputCommand().Execute()
    commandFactory.device.ReadFromBlockPipeOut(commandFactory.DigitalOutputPipe0, buf0.length, buf0)
    commandFactory.device.ReadFromBlockPipeOut(commandFactory.DigitalOutputPipe1, buf1.length, buf1)
    commandFactory.device.ReadFromBlockPipeOut(commandFactory.DigitalOutputPipe2, buf2.length, buf2)
    commandFactory.device.ReadFromBlockPipeOut(commandFactory.DigitalOutputPipe3, buf3.length, buf3)

    val val0 = commandFactory.ComputeStats(commandFactory.ConvertToWords(buf0))
    val val1 = commandFactory.ComputeStats(commandFactory.ConvertToWords(buf1))
    val val2 = commandFactory.ComputeStats(commandFactory.ConvertToWords(buf2))
    val val3 = commandFactory.ComputeStats(commandFactory.ConvertToWords(buf3))

    Array(val0.mean.toLong, val1.mean.toLong, val2.mean.toLong, val3.mean.toLong)
  }

  def readChannelStats(): Array[commandFactory.Stats] = {

    val buf0 = Array.ofDim[Byte](1024 * 4)
    val buf1 = Array.ofDim[Byte](1024 * 4)
    val buf2 = Array.ofDim[Byte](1024 * 4)
    val buf3 = Array.ofDim[Byte](1024 * 4)

    commandFactory.MakeReadOutputCommand().Execute()
    commandFactory.device.ReadFromBlockPipeOut(commandFactory.DigitalOutputPipe0, buf0.length, buf0)
    commandFactory.device.ReadFromBlockPipeOut(commandFactory.DigitalOutputPipe1, buf1.length, buf1)
    commandFactory.device.ReadFromBlockPipeOut(commandFactory.DigitalOutputPipe2, buf2.length, buf2)
    commandFactory.device.ReadFromBlockPipeOut(commandFactory.DigitalOutputPipe3, buf3.length, buf3)

    val val0 = commandFactory.ComputeStats(commandFactory.ConvertToWords(buf0))
    val val1 = commandFactory.ComputeStats(commandFactory.ConvertToWords(buf1))
    val val2 = commandFactory.ComputeStats(commandFactory.ConvertToWords(buf2))
    val val3 = commandFactory.ComputeStats(commandFactory.ConvertToWords(buf3))

    Array(val0, val1, val2, val3)
  }

  def RunInputBufferFunctionalityTest(): Boolean = {

    Reset()
    InitializeAdc()

    val map0 = Map(
      0 -> 0x0030,
      1 -> 0x0233,
      2 -> 0x0000,
      3 -> 0x0210,
      4 -> 0x0210,
      5 -> 0x0017,
      6 -> 0x0066
    )

    SetAdcBlock(21, map0)
    SetAdcBlock(82, map0)

    val values0 = readChannels()

    val map1 = Map(
      0 -> 0x003c,
      1 -> 0x0233,
      2 -> 0x0000,
      3 -> 0x0000,
      4 -> 0x0000,
      5 -> 0x0014,
      6 -> 0x0006
    )

    SetAdcBlock(21, map1)
    SetAdcBlock(82, map1)

    val values1 = readChannels()

    val res = within(values0(0), 4812, 500) &&
      within(values0(1), 11571, 500) &&
      within(values0(2), 11571, 500) &&
      within(values0(3), 4812, 500) &&
      within(values1(0), 3123, 500) &&
      within(values1(1), 13260, 500) &&
      within(values1(2), 13260, 500) &&
      within(values1(3), 3123, 500)

    if (res) pass(9).value = true else fail(9).value = true

    res
  }

  def RunPgaGainTest(): Boolean = {

    Reset()
    InitializeAdc()

    val map = Map(
      0 -> 0x003c,
      1 -> 0x0233,
      2 -> 0x001e,
      3 -> 0x0042,
      4 -> 0x0042,
      5 -> 0x003c,
      6 -> 0x0006
    )

    SetAdcBlock(21, map)
    SetAdcBlock(82, map)

    commandFactory.MakeWriteToAsicMemoryTopCommand(80, 0x5623).Execute()
    val values0 = readChannels()

    commandFactory.MakeWriteToAsicMemoryTopCommand(80, 0x58ba).Execute()
    val values1 = readChannels()

    commandFactory.MakeWriteToAsicMemoryTopCommand(80, 0x5b79).Execute()
    val values2 = readChannels()

    commandFactory.MakeWriteToAsicMemoryTopCommand(26, 0x0014).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(87, 0x0014).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(80, 0x5623).Execute()
    val values3 = readChannels()

    commandFactory.MakeWriteToAsicMemoryTopCommand(80, 0x58ba).Execute()
    val values4 = readChannels()

    commandFactory.MakeWriteToAsicMemoryTopCommand(80, 0x5b79).Execute()
    val values5 = readChannels()

    commandFactory.MakeWriteToAsicMemoryTopCommand(26, 0x0000).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(87, 0x0000).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(80, 0x5623).Execute()
    val values6 = readChannels()

    commandFactory.MakeWriteToAsicMemoryTopCommand(80, 0x58ba).Execute()
    val values7 = readChannels()

    commandFactory.MakeWriteToAsicMemoryTopCommand(80, 0x5b79).Execute()
    val values8 = readChannels()

    val res = values0.filter(!within(_, 6182, 300)).isEmpty &&
      values1.filter(!within(_, 8192, 300)).isEmpty &&
      values2.filter(!within(_, 10311, 300)).isEmpty &&
      values3.filter(!within(_, 5177, 300)).isEmpty &&
      values4.filter(!within(_, 8192, 300)).isEmpty &&
      values5.filter(!within(_, 11371, 300)).isEmpty &&
      values6.filter(!within(_, 4172, 300)).isEmpty &&
      values7.filter(!within(_, 8192, 300)).isEmpty &&
      values8.filter(!within(_, 12431, 300)).isEmpty

    if (res) pass(10).value = true else fail(10).value = true

    res
  }

  def adcOutput(i: Int): (Double, Double, Double, Double) = {

    commandFactory.MakeWriteToAsicMemoryTopCommand(80, i).Execute()

    Thread.sleep(10)

    val stats = readChannelStats()

    (stats(0).mean, stats(1).mean, stats(2).mean, stats(3).mean)
  }

  def RunAdcLinearityTest() = {

    Reset()
    InitializeAdc()

    val map = Map(
      0 -> 0x003c,
      1 -> 0x0233,
      2 -> 0x001e,
      3 -> 0x0042,
      4 -> 0x0042,
      5 -> 0x0000,
      6 -> 0x0006
    )

    SetAdcBlock(21, map)
    SetAdcBlock(82, map)

    val valueMap = (for (i <- 0x314 until 0xe88 by 10) yield i -> adcOutput(i)).toMap

    var adc0errors = 0
    var adc1errors = 0
    var adc2errors = 0
    var adc3errors = 0


    // diff 60+45

  }

  def RunAdcNoiseTest(): AdcNoiseTestResult = {

    Reset()
    InitializeAdc()

    commandFactory.MakeWriteToAsicMemoryTopCommand(79, 0x7000).Execute()
    commandFactory.MakeWriteToAsicMemoryTopCommand(80, 0x7000).Execute()

    val map1 = Map(
      0 -> 0x0028,
      1 -> 0x0233,
      2 -> 0x001f,
      3 -> 0x0040,
      4 -> 0x0040,
      5 -> 0x0015,
      6 -> 0x00ac
    )

    SetAdcBlock(21, map1)
    SetAdcBlock(82, map1)

    val values1 = readChannelStats()

    values1(0).stdev
    values1(3).stdev

    val map0 = Map(
      0 -> 0x0014,
      1 -> 0x0233,
      2 -> 0x001f,
      3 -> 0x0002,
      4 -> 0x0002,
      5 -> 0x0016,
      6 -> 0x0152
    )

    SetAdcBlock(21, map0)
    SetAdcBlock(82, map0)

    val values0 = readChannelStats()

    values0(1)
    values0(2)

    val pas =
      within(values1(0).mean, 8192, 5) &&
      within(values0(1).mean, 8192, 5) &&
      within(values0(2).mean, 8192, 5) &&
      within(values1(3).mean, 8192, 5) &&
      values1(0).stdev < 10 &&
      values0(1).stdev < 10 &&
      values0(2).stdev < 10 &&
      values1(3).stdev < 10

    if (pas) pass(12).value = true else fail(12).value

    new AdcNoiseTestResult(
      pas,
      values1(0).stdev, values0(1).stdev, values0(2).stdev, values1(3).stdev,
      values1(0).mean, values0(1).mean, values0(2).mean, values1(3).mean
    )
  }

  class AdcNoiseTestResult(
                            val pass: Boolean,
                            val noise0: Double,
                            val noise1: Double,
                            val noise2: Double,
                            val noise3: Double,
                            val mean0: Double,
                            val mean1: Double,
                            val mean2: Double,
                            val mean3: Double
                            )

}