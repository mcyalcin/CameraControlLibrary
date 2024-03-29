package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.{DeviceInterfaceModel, MtAs1410x2MemoryMap}

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.{Node, Scene}
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, VBox}

object UsbCam3825TestUtility extends JFXApp {

  def Reset(): Unit = {
    stage.scene = new Scene(stage.scene.width.intValue(), stage.scene.height.intValue()) {
      root = new BorderPane {
        center = createTabs
      }
    }
  }

  stage = new PrimaryStage {
    title = "Mikro-Tasarım UsbCam3825 Test Utility"
  }

  stage.scene = new Scene {
    root = new BorderPane {
      center = createTabs
    }
  }

  stage.setMaximized(true)

  private def createReadOutputControl: Node = new VBox {
    spacing = 10
    content = List(
      new Label("Save Channel Output"),
      new ChoiceBox(DeviceInterfaceModel.channelOptions) {
        value <==> DeviceInterfaceModel.selectedChannel
      },
      new TextField {
        promptText = "File path"
        text <==> DeviceInterfaceModel.outFilePath
      },
      new TextField {
        promptText = "Sample count"
        text <==> DeviceInterfaceModel.sampleCount
      },
      new CheckBox("16 bit mode") {
        tooltip = "Output 16 LSBs instead of full 32"
        selected <==> DeviceInterfaceModel.sixteenBitMode
      },
      new ChoiceBox(DeviceInterfaceModel.outputFormatOptions) {
        value <==> DeviceInterfaceModel.selectedOutputFormat
      },
      new Button("Read") {
        onAction = handle { DeviceInterfaceModel.ReadOutputIntoFile()}
      }
    )
  }

  private def createTabs: TabPane = {
    new TabPane {
      tabs = List(
        new Tab {
          text = "FPGA"
          content = UsbCam3825FpgaControls.createFpgaControlPanel
          closable = false
        },
        new Tab {
          text = "ASIC"
          closable = false
          disable <== !DeviceInterfaceModel.bitfileDeployed
          content = new BorderPane {
            left = new VBox {
              padding = Insets(10)
              spacing = 10
              content = List(
                createReadOutputControl
              )
            }
            center = new TabPane {
              tabs = List(
                new Tab {
                  text = "Digital Controller"
                  content = UsbCam3825DigitalControllerControls.createDigitalControllerTab
                  closable = false
                },
                new Tab {
                  text = "Output Stage"
                  content = UsbCam3825OutputStageControls.createOutputStageTab
                  closable = false
                },
                new Tab {
                  text = "Bias Generator"
                  content = UsbCam3825BiasGeneratorControls.createBiasGeneratorTab
                  closable = false
                },
                new Tab {
                  text = "ADC Channel"
                  content = UsbCam3825AdcChannelControls.createAdcChannelTab
                  closable = false
                }
              )
            }
          }
        }
      )
    }
  }
}
