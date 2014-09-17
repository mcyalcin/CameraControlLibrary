package com.mikrotasarim.ui

import com.mikrotasarim.camera.Model

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.{Node, Scene}
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, VBox}
import scalafx.stage.{StageStyle, Stage, FileChooser}

object UsbCam3825TestUtility extends JFXApp {

  stage = new PrimaryStage {
    title = "Mikro-Tasarım UsbCam3825 Test Utility"
    scene = new Scene {
      content = createTabs
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
          text = "Bias Generator"
          content = createBiasGeneratorTab
          closable = false
          disable <== !Model.bitfileDeployed
        },
        new Tab {
          text = "Timing Generator"
          content = createTimingGeneratorTab
          closable = false
          disable <== !Model.bitfileDeployed
        }
      )
    }
  }

  private def createBiasGeneratorTab: Node = {
    val treeView = new TreeView[String] {
      minWidth = 150
      showRoot = false
      editable = false
      root = new TreeItem[String] {
        value = "Root"
        children = List(
          new TreeItem("Animal") {
            children = List(
              new TreeItem("Lion"),
              new TreeItem("Tiger"),
              new TreeItem("Bear")
            )
          },
          new TreeItem("Mineral") {
            children = List(
              new TreeItem("Copper"),
              new TreeItem("Diamond"),
              new TreeItem("Quartz")
            )
          },
          new TreeItem("Vegetable") {
            children = List(
              new TreeItem("Arugula"),
              new TreeItem("Broccoli"),
              new TreeItem("Cabbage")
            )
          }
        )
      }
    }

    val listViewItems = new ObservableBuffer[String]()

    val listView = new ListView[String] {
      items = listViewItems
    }

    treeView.selectionModel().selectionMode = SelectionMode.SINGLE
    treeView.selectionModel().selectedItem.onChange(
      (_, _, newTreeItem) => {
        if (newTreeItem != null && newTreeItem.isLeaf) {
          listViewItems.clear()
          for (i <- 1 to 10000) {
            listViewItems += newTreeItem.getValue + " " + i
          }
        }
      }
    )

    new SplitPane {
      items ++= List(
        treeView,
        listView
      )
    }
  }

  private def createTimingGeneratorTab: VBox = createFpgaControlPanel

  private def createFpgaControlPanel: VBox = {
    new VBox {
      padding = Insets(10)
      spacing = 20
      content = createSelectBitfileHBox
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
      text <==> Model.bitfilePath
      disable <== Model.bitfileDeployed
    }
  }

  private def createDeployBitfileButton: Button = {
    new Button("Deploy") {
      id = "deployBitfileButton"
      disable <== Model.bitfileDeployed
      onAction = handle {
        try {
          Model.DeployBitfile()
        } catch {
          case e: Exception =>
            val dialog = new Stage
            dialog.initStyle(StageStyle.UTILITY)
            val scene = new Scene {
              content = new Label(e.getMessage)
            }
            dialog.setScene(scene)
            dialog.show()
        }
      }
    }
  }

  private def createDisconnectFromFpgaButton: Button = {
    new Button("Disconnect") {
      id = "disconnectButton"
      disable <== !Model.bitfileDeployed
      onAction = handle {
        Model.DisconnectFromDevice()
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
