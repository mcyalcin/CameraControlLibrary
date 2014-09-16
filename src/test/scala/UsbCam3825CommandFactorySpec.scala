import com.mikrotasarim.camera.command.factory.UsbCam3825CommandFactory
import com.mikrotasarim.camera.device.MockDeviceInterface
import org.scalatest._

class UsbCam3825CommandFactorySpec extends FlatSpec with Matchers {

  "A UsbCam3825 command factory" should "create the correct sequence of base commands for sending data to roic memory" in {
    val outputBuffer = new StringBuilder
    val device = new MockDeviceInterface(outputBuffer)

    val commandFactory = new UsbCam3825CommandFactory(device)
    val command = commandFactory.MakeWriteToAsicMemoryTopCommand(0, 0)
    command.Execute()

    val expectedOutput = "Wire 1 set to value 192\nWire 2 set to value 0\nWire 3 set to value 0\nWire Ins Updated\nTrigger 64 set to value 0\n"

    outputBuffer.toString() should be(expectedOutput)
  }

  it should "create the correct sequence of base commands for block writing to roic memory" in {
    val outputBuffer = new StringBuilder
    val device = new MockDeviceInterface(outputBuffer)

    val commandFactory = new UsbCam3825CommandFactory(device)

    val blocks = Array(1, 2, 3)
    val command = commandFactory.MakeBlockWriteToRoicMemoryCommand(0, blocks)
    command.Execute()

    val expectedOutput = "Wire 1 set to value 192\nWire 2 set to value 0\nWire 3 set to value 1\nWire Ins Updated\nTrigger 64 set to value 0\nWire 1 set to value 192\nWire 2 set to value 1\nWire 3 set to value 2\nWire Ins Updated\nTrigger 64 set to value 0\nWire 1 set to value 192\nWire 2 set to value 2\nWire 3 set to value 3\nWire Ins Updated\nTrigger 64 set to value 0\nWire 1 set to value 199\nWire Ins Updated\nTrigger 64 set to value 0\n"

    outputBuffer.toString() should be(expectedOutput)
  }

  it should "create the correct sequence of base commands for writing to flash memory" in {
    val outputBuffer = new StringBuilder
    val device = new MockDeviceInterface(outputBuffer)

    val commandFactory = new UsbCam3825CommandFactory(device)

    val blocks = new Array[Byte](1000)
    val command = commandFactory.MakeWriteToFlashMemoryCommand(1000, blocks)
    command.Execute()

    val expectedOutput = "Trigger 64 set to value 1\nA data array of size 256 claimed to be of size 256 written to pipe 128\nWire 4 set to value 6\nWire Ins Updated\nTrigger 64 set to value 0\nWire 1 set to value 241\nWire 2 set to value 1000\nWire 3 set to value 256\nWire 4 set to value 18\nWire Ins Updated\nTrigger 64 set to value 0\nTrigger 64 set to value 1\nA data array of size 256 claimed to be of size 256 written to pipe 128\nWire 4 set to value 6\nWire Ins Updated\nTrigger 64 set to value 0\nWire 1 set to value 241\nWire 2 set to value 1256\nWire 3 set to value 256\nWire 4 set to value 18\nWire Ins Updated\nTrigger 64 set to value 0\nTrigger 64 set to value 1\nA data array of size 256 claimed to be of size 256 written to pipe 128\nWire 4 set to value 6\nWire Ins Updated\nTrigger 64 set to value 0\nWire 1 set to value 241\nWire 2 set to value 1512\nWire 3 set to value 256\nWire 4 set to value 18\nWire Ins Updated\nTrigger 64 set to value 0\nTrigger 64 set to value 1\nA data array of size 232 claimed to be of size 232 written to pipe 128\nWire 4 set to value 6\nWire Ins Updated\nTrigger 64 set to value 0\nWire 1 set to value 241\nWire 2 set to value 1768\nWire 3 set to value 232\nWire 4 set to value 18\nWire Ins Updated\nTrigger 64 set to value 0\n"

    outputBuffer.toString() should be(expectedOutput)
  }

  it should "create the correct sequence of base commands for writing to flash memory in a border case" in {
    val outputBuffer = new StringBuilder
    val device = new MockDeviceInterface(outputBuffer)

    val commandFactory = new UsbCam3825CommandFactory(device)

    val blocks = new Array[Byte](1024)
    val command = commandFactory.MakeWriteToFlashMemoryCommand(1000, blocks)
    command.Execute()

    val expectedOutput = "Trigger 64 set to value 1\nA data array of size 256 claimed to be of size 256 written to pipe 128\nWire 4 set to value 6\nWire Ins Updated\nTrigger 64 set to value 0\nWire 1 set to value 241\nWire 2 set to value 1000\nWire 3 set to value 256\nWire 4 set to value 18\nWire Ins Updated\nTrigger 64 set to value 0\nTrigger 64 set to value 1\nA data array of size 256 claimed to be of size 256 written to pipe 128\nWire 4 set to value 6\nWire Ins Updated\nTrigger 64 set to value 0\nWire 1 set to value 241\nWire 2 set to value 1256\nWire 3 set to value 256\nWire 4 set to value 18\nWire Ins Updated\nTrigger 64 set to value 0\nTrigger 64 set to value 1\nA data array of size 256 claimed to be of size 256 written to pipe 128\nWire 4 set to value 6\nWire Ins Updated\nTrigger 64 set to value 0\nWire 1 set to value 241\nWire 2 set to value 1512\nWire 3 set to value 256\nWire 4 set to value 18\nWire Ins Updated\nTrigger 64 set to value 0\nTrigger 64 set to value 1\nA data array of size 256 claimed to be of size 256 written to pipe 128\nWire 4 set to value 6\nWire Ins Updated\nTrigger 64 set to value 0\nWire 1 set to value 241\nWire 2 set to value 1768\nWire 3 set to value 256\nWire 4 set to value 18\nWire Ins Updated\nTrigger 64 set to value 0\n"

    outputBuffer.toString() should be(expectedOutput)
  }
}
