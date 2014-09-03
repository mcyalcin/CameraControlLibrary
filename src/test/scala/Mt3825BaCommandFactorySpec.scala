import com.mikrotasarim.camera.command.factory.Mt3825BaCommandFactory
import org.scalatest._

class Mt3825BaCommandFactorySpec extends FlatSpec with Matchers {

  "A Mt3825Ba command factory" should "create the correct sequence of base commands for sending data to roic memory" in {
    val outputBuffer = new StringBuilder
    val device = new MockDeviceInterface(outputBuffer)

    val commandFactory = new Mt3825BaCommandFactory(device)
    val command = commandFactory.MakeWriteToAsicMemoryTopCommand(0, 0)
    command.Execute()

    val expectedOutput = "Wire 1 set to value 192\nWire 2 set to value 0\nWire 3 set to value 0\nWire Ins Updated\nTrigger 64 set to value 0\n"

    outputBuffer.toString() should be(expectedOutput)
  }

  it should "create the correct sequence of base commands for block writing to roic memory" in {
    val outputBuffer = new StringBuilder
    val device = new MockDeviceInterface(outputBuffer)

    val commandFactory = new Mt3825BaCommandFactory(device)

    val blocks = Array(1, 2, 3)
    val command = commandFactory.MakeBlockWriteToRoicMemoryCommand(0, blocks)
    command.Execute()

    val expectedOutput = "Wire 1 set to value 192\nWire 2 set to value 0\nWire 3 set to value 1\nWire Ins Updated\nTrigger 64 set to value 0\nWire 1 set to value 192\nWire 2 set to value 1\nWire 3 set to value 2\nWire Ins Updated\nTrigger 64 set to value 0\nWire 1 set to value 192\nWire 2 set to value 2\nWire 3 set to value 3\nWire Ins Updated\nTrigger 64 set to value 0\nWire 1 set to value 199\nWire Ins Updated\nTrigger 64 set to value 0\n"

    outputBuffer.toString() should be(expectedOutput)
  }
}
