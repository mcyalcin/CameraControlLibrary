import com.mikrotasarim.camera.device.OpalKellyInterface
import org.scalatest._

class OpalKellyInterfaceSpec extends FlatSpec with Matchers {
  val bitFilePath = "c:\\users\\mcyalcin\\desktop\\top_module.bit"

  "An Opal Kelly Interface" should "create a hardware connection upon initialization" in {
    val device = new OpalKellyInterface(bitFilePath)
    device.IsFrontPanelEnabled() should be (right = true)
  }
}
