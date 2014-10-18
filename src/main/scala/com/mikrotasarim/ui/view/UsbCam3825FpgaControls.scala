package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.DeviceInterfaceModel._
import com.mikrotasarim.ui.view.UsbCam3825TestUtility._
import com.mikrotasarim.ui.view.UsbCam3825UiHelper._
import com.mikrotasarim.utility.DialogMessageStage

import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, VBox}
import scalafx.stage.FileChooser

object UsbCam3825FpgaControls {
  def createFpgaControlPanel: Node = {
    new ScrollPane {
      content = new VBox {
        padding = Insets(10)
        spacing = 20

        content = List(
          new CheckBox("Software Self Test Mode") {
            inner =>
            inner.selected <==> testMode
            tooltip = "On test mode, software works with a mock device interface instead of an actual device"
          },
          createSelectBitfileHBox,
          createResetControls,
          createFvalDvalSelector,
          createChannelControls,
          createCameraFeedButton
        )
      }
    }
  }

  private def createChannelControls: Node = {
    new HBox {
      spacing = 10
      disable <== !bitfileDeployed
      content =
        (for (i <- 0 to 3) yield createChannelCheckBox(i)).toList :+
        new CheckBox("Test Feed on Channels") {
          selected <==> ChannelControls.testFeedEnabled
        } :+
        new CheckBox("Dac Sweep Test Feed on Channels") {
          selected <==> ChannelControls.sweepTestFeedEnabled
        } :+
        new Button("Read") {
          onAction = handle {
            ReadDigitalOutputChunk()
          }
        }
    }
  }

  private def createChannelCheckBox(index: Int): Node = {
    new CheckBox("Channel " + index) {
      selected <==> ChannelControls.channelEnabled(index)
    }
  }

  private def createCameraFeedButton: Node = {
    new Button("Camera Feed") {
      tooltip = "Not implemented yet."
      disable = true
      onAction = handle {
        VideoFeedStage.show()
      }
    }
  }

  private def createFvalDvalSelector: Node = {
    new HBox {
      disable <== !bitfileDeployed
      spacing = 10
      content = createLabeledBooleanDropdown("Dval/Fval", embeddedAsicLabels, embeddedDvalFval)
    }
  }

  private def createResetControls: Node = {
    new HBox {
      spacing = 20
      content = List(
        new CheckBox("FPGA Reset") {
          selected <==> ResetControls.fpgaReset
          disable <== !bitfileDeployed
        },
        new CheckBox("Chip Reset") {
          selected <==> ResetControls.chipReset
          disable <== !bitfileDeployed ||
            ResetControls.fpgaReset
        }
      )
    }
  }

  private def createSelectBitfileHBox: HBox = {
    new HBox {
      spacing = 10
      content = List(
        createSelectBitfileButton,
        createTextField,
        createDeployBitfileButton,
        createDisconnectFromFpgaButton
      )
    }
  }

  private def createTextField: TextField = {
    new TextField {
      prefColumnCount = 40
      promptText = "Enter bitfile path"
      text <==> bitfilePath
      disable <== bitfileDeployed
    }
  }

  private def createDeployBitfileButton: Button = {
    new Button("Deploy") {
      id = "deployBitfileButton"
      disable <== bitfileDeployed
      onAction = handle {
        try {
          DeployBitfile()
        } catch {
          case e: Exception =>
            val dialog = new DialogMessageStage("Error",
              e.getMessage, 320, 100, null)
            dialog.show()
        }
      }
    }
  }

  private def createDisconnectFromFpgaButton: Button = {
    new Button("Disconnect") {
      id = "disconnectButton"
      disable <== (!bitfileDeployed || testMode)
      onAction = handle {
        DisconnectFromDevice()
      }
    }
  }

  private def createSelectBitfileButton: Button = {
    new Button("Select Bitfile") {
      id = "selectBitfileButton"
      disable <== bitfileDeployed
      onAction = handle {
        val fileChooser = new FileChooser() {
          title = "Pick an FPGA Bitfile"
        }

        val bitfile = fileChooser.showOpenDialog(stage)

        if (bitfile != null) {
          bitfilePath.value = bitfile.getAbsolutePath
        }
      }
    }
  }
}
