package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.{DeviceInterfaceModel, MtAs1410x2MemoryMap}

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, VBox}

object UsbCam3825TestUtility extends JFXApp {

  stage = new PrimaryStage {
    title = "Mikro-Tasarım UsbCam3825 Test Utility"
    scene = new Scene(800, 600) {
      root = new BorderPane {
        center = createTabs
      }
    }
  }

  stage.setMaximized(true)

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
                new Button("Reset") {
                  disable = true
                  tooltip = "Not Implemented Yet"
                },
                new Button("Memory Map") {
                  onAction = () => {
                    MtAs1410x2MemoryMap.ReadAsicMemory()
                    val dialog = MtAs1410x2MemoryMapStage
                    dialog.show()
                  }
                }
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
                  text = "Timing Generator"
                  content = UsbCam3825TimingGeneratorControls.createTimingGeneratorTab
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
        },
        new Tab {
          text = "ROIC"
          closable = false
          disable <== !DeviceInterfaceModel.bitfileDeployed
          tooltip = "Not Implemented Yet"
          content = new BorderPane {
            left = new VBox {
              padding = Insets(10)
              spacing = 10
              content = List(
                new Button("Reset"),
                new Button("Memory Map")
              )
            }
            center = new TabPane {
              tabs = List(
                new Tab {
                  text = "Digital Controller"
                  closable = false
                  content = new ScrollPane
                },
                new Tab {
                  text = "Bias Generator"
                  closable = false
                  content = new ScrollPane
                },
                new Tab {
                  text = "RefMem/NUC"
                  closable = false
                  content = new ScrollPane
                },
                new Tab {
                  text = "Readout"
                  closable = false
                  content = new ScrollPane
                }
              )
            }
          }
        }
      )
    }
  }
}
