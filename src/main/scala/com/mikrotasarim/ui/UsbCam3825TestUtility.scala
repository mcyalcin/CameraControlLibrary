package com.mikrotasarim.ui

import com.mikrotasarim.camera.Model

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{TextField, Button}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.stage.FileChooser

object UsbCam3825TestUtility extends JFXApp {

  stage = new PrimaryStage {
    resizable = false
    title = "Mikro-Tasarım UsbCam3825 Test Utility"
    scene = new Scene(800, 600) {
      content = new VBox {
        padding = Insets(10)
        spacing = 20
        content = createSelectBitfileHBox
      }
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

  private def createTextField: TextField =
    new TextField {
      prefColumnCount = 40
      promptText = "Enter bitfile path"
      text <==> Model.bitfilePath
      disable <== Model.bitfileDeployed
    }

  private def createDeployBitfileButton: Button = {
    new Button("Deploy") {
      id = "deployBitfileButton"
      disable <== Model.bitfileDeployed
      onAction = handle {
        Model.bitfileDeployed.value = true
      }
    }
  }

  private def createDisconnectFromFpgaButton: Button = {
    new Button("Disconnect") {
      id = "disconnectButton"
      disable <== !Model.bitfileDeployed
      onAction = handle {
        Model.bitfileDeployed.value = false
      }
    }
  }

  private def createSelectBitfileButton: Button = {
    new Button("Select Bitfile") {
      id = "selectBitfileButton"
      disable <== Model.bitfileDeployed
      onAction = handle {
        val fileChooser = new FileChooser() {
          title = "Pick an FPGA Bitfile"
        }

        val bitfile = fileChooser.showOpenDialog(stage)

        if (bitfile != null) {
          Model.bitfilePath.value = bitfile.getAbsolutePath
        }
      }
    }
  }
}
