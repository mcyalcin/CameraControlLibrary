package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.UsbCam3825TestUtilityModel
import com.mikrotasarim.ui.model.UsbCam3825TestUtilityModel.PhaseSignal
import com.mikrotasarim.utility.DialogMessageStage

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, HBox, VBox}
import scalafx.scene.{Node, Scene}
import scalafx.stage.FileChooser

object UsbCam3825TestUtility extends JFXApp {

  stage = new PrimaryStage {
    title = "Mikro-Tasarım UsbCam3825 Test Utility"
    scene = new Scene(800, 600) {
      root = new BorderPane {
        center = createTabs
      }
    }
  }

  private def createTabs: TabPane = {
    new TabPane {
      tabs = List(
        new Tab {
          text = "FPGA"
          content = createFpgaControlPanel
          closable = false
        },
        new Tab {
          text = "Memory"
          content = createMemoryTab
          closable = false
          disable <== !UsbCam3825TestUtilityModel.bitfileDeployed
        },
        new Tab {
          text = "Output Stage"
          content = createOutputStageTab
          closable = false
          disable <== !UsbCam3825TestUtilityModel.bitfileDeployed
        },
        new Tab {
          text = "Bias Generator"
          content = UsbCam3825BiasGeneratorControls.createBiasGeneratorTab
          closable = false
          disable <== !UsbCam3825TestUtilityModel.bitfileDeployed
        },
        new Tab {
          text = "Timing Generator"
          content = UsbCam3825TimingGeneratorControls.createTimingGeneratorTab
          closable = false
          disable <== !UsbCam3825TestUtilityModel.bitfileDeployed
        },
        new Tab {
          text = "ADC Channel"
          content = createAdcChannelTab
          closable = false
          disable <== !UsbCam3825TestUtilityModel.bitfileDeployed
        }
      )
    }
  }

  private def createOutputStageTab: Node = {
    new ScrollPane {
      content = new Label("Ton implemented yet.")
    }
  }

  private def createMemoryTab: Node = {
    new ScrollPane {
      content = new Label("Not implemented yet.")
    }
  }

  private def createAdcChannelTab: Node = {
    new ScrollPane {
      content = new Label("Not implemented tey.")
    }
  }


  private def createFpgaControlPanel: Node = {
    new ScrollPane {
      content = new VBox {
        padding = Insets(10)
        spacing = 20
        content = List(
          new CheckBox("Software Self Test Mode") {
            inner =>
            inner.selected <==> UsbCam3825TestUtilityModel.testMode
            tooltip = "On test mode, software works with a mock device interface instead of an actual device"
          },
          createSelectBitfileHBox,
          createResetControls
        )
      }
    }
  }

  private def createResetControls: Node = {
    new HBox {
      spacing = 20
      content = List(
        new CheckBox("FPGA Reset") {
          selected <==> UsbCam3825TestUtilityModel.ResetControls.fpgaReset
          disable <== !UsbCam3825TestUtilityModel.bitfileDeployed
        },
        new CheckBox("ROIC Reset") {
          selected <==> UsbCam3825TestUtilityModel.ResetControls.roicReset
          disable <== !UsbCam3825TestUtilityModel.bitfileDeployed ||
            UsbCam3825TestUtilityModel.ResetControls.fpgaReset
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
      text <==> UsbCam3825TestUtilityModel.bitfilePath
      disable <== UsbCam3825TestUtilityModel.bitfileDeployed
    }
  }

  private def createDeployBitfileButton: Button = {
    new Button("Deploy") {
      id = "deployBitfileButton"
      disable <== UsbCam3825TestUtilityModel.bitfileDeployed
      onAction = handle {
        try {
          UsbCam3825TestUtilityModel.DeployBitfile()
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
      disable <== (!UsbCam3825TestUtilityModel.bitfileDeployed || UsbCam3825TestUtilityModel.testMode)
      onAction = handle {
        UsbCam3825TestUtilityModel.DisconnectFromDevice()
      }
    }
  }

  private def createSelectBitfileButton: Button = {
    new Button("Select Bitfile") {
      id = "selectBitfileButton"
      disable <== UsbCam3825TestUtilityModel.bitfileDeployed
      onAction = handle {
        val fileChooser = new FileChooser() {
          title = "Pick an FPGA Bitfile"
        }

        val bitfile = fileChooser.showOpenDialog(stage)

        if (bitfile != null) {
          UsbCam3825TestUtilityModel.bitfilePath.value = bitfile.getAbsolutePath
        }
      }
    }
  }
}
