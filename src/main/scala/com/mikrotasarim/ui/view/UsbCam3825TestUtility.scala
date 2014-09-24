package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.DacControlModel
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
import scalafx.util.converter.DoubleStringConverter

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
          content = createBiasGeneratorTab
          closable = false
          disable <== !UsbCam3825TestUtilityModel.bitfileDeployed
        },
        new Tab {
          text = "Timing Generator"
          content = createTimingGeneratorTab
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

  private def createBiasGeneratorTab: Node = {
    new ScrollPane {
      content = new HBox {
        padding = Insets(10)
        spacing = 20
        content = List(
          new VBox {
            spacing = 20
            content = List(
              new CheckBox("Activate") {
                selected <==> UsbCam3825TestUtilityModel.BiasGeneratorActivator.switch
              },
              new HBox {
                spacing = 10
                content = List(
                  new CheckBox("Power Down Top") {
                    selected <==> UsbCam3825TestUtilityModel.BiasGeneratorPowerSettings.powerDownTop
                  },
                  new CheckBox("Power Down Bottom") {
                    selected <==> UsbCam3825TestUtilityModel.BiasGeneratorPowerSettings.powerDownBot
                  }
                )
              },
              new HBox {
                spacing = 20
                content = List(
                  new ChoiceBox(UsbCam3825TestUtilityModel.BiasGeneratorTestSettings.voltageTestLabels) {
                    selectionModel().selectFirst()
                    selectionModel().selectedItem.onChange(
                      (_, _, newValue) => UsbCam3825TestUtilityModel.BiasGeneratorTestSettings.selectedVoltageTest.value = newValue
                    )
                  },
                  new ChoiceBox(UsbCam3825TestUtilityModel.BiasGeneratorTestSettings.currentTestLabels){
                    selectionModel().selectFirst()
                    selectionModel().selectedItem.onChange(
                      (_, _, newValue) => UsbCam3825TestUtilityModel.BiasGeneratorTestSettings.selectedCurrentTest.value = newValue
                    )
                  }
                )
              },
              new VBox {
                spacing = 10
                content = List(new Label("Current DACs")) ++ createBiasGeneratorCurrentDacControls
              }
            )
          },
          new VBox {
            spacing = 10
            content = List(new Label("Voltage DACs")) ++ createBiasGeneratorVoltageDacControls
          }
        )
      }
    }
  }

  private def createBiasGeneratorCurrentDacControls: List[Node] = createDacControls(UsbCam3825TestUtilityModel.biasGeneratorCurrentDacs, "%3.0f", "uA", 1)

  private def createBiasGeneratorVoltageDacControls: List[Node] = createDacControls(UsbCam3825TestUtilityModel.biasGeneratorVoltageDacs, "%1.3f", "V", 0.001)

  private def createDacControls(dacList: Seq[DacControlModel], format: String, unitLabel: String, resolution: Double): List[Node] = {
    (for (dac <- dacList) yield new HBox {
      spacing = 10
      content = List(
        new Label {
          text = dac.name.value
          prefWidth = 100
        },
        new Slider {
          prefWidth = 200
          min = dac.valueRange._1
          max = dac.valueRange._2
          blockIncrement = resolution
          value <==> dac.value
          labelFormatter = new DoubleStringConverter
        },
        new Label {
          text <== (dac.value.asString(format) + " " + unitLabel)
          prefWidth = 50
        },
        new Button("Reset") {
          onAction = () => dac.Reset()
          tooltip = "Reset to " + dac.defaultValue + " " + unitLabel
        },
        new CheckBox("External") {
          inner =>
            inner.selected <==> dac.external
        },
        new CheckBox("Low Power") {
          inner =>
            inner.selected <==> dac.lowPower
        },
        new CheckBox("Power Down") {
          inner =>
            inner.selected <==> dac.powerDown
        },
        new Button("Commit") {
          onAction = () => {
            dac.Commit()
          }
          disable <== !dac.changed
        }
      )
    }).toList
  }

  private def createTimingGeneratorTab: Node = {
    new ScrollPane {
      content = new VBox {
        padding = Insets(10)
        spacing = 10
        content =
          (createTimingGeneratorMainControls +:
          new Label("Voltage DACs") +:
          createTimingGeneratorVoltageDacControls) ++
          (new Label("Current DACs") +:
          createTimingGeneratorCurrentDacControls) :+
          new Label("Phase Signals") :+
          createPhaseSignalControls
      }
    }
  }

  private def createTimingGeneratorMainControls: Node = {
    new HBox {
      spacing = 10
      content = List(
        new CheckBox("dll_enable"),
        new CheckBox("sel_pw_out"),
        new ChoiceBox(UsbCam3825TestUtilityModel.powerReferences) {
          selectionModel().selectFirst()
        }
      )
    }
  }

  private def createPhaseSignalControls: Node = {
    new HBox {
      spacing = 20
      content = List(
        createPhaseSignalSliders,
        new VBox {
          spacing = 20
          content = List(
            new CheckBox("Lock relative") { selected <==> UsbCam3825TestUtilityModel.lockPhaseSignals },
            new Button("Reset") {onAction = () => UsbCam3825TestUtilityModel.ResetPhaseSignals() },
            new Button("Commit") {
              disable <== !UsbCam3825TestUtilityModel.phaseSignalsChanged
              onAction = () => UsbCam3825TestUtilityModel.CommitPhaseSignals()
            }
          )
        }
      )
    }
  }

  private def createPhaseSignalSliders: Node = {
    new VBox {
      spacing = 10
      content = (for (phaseSignal <- UsbCam3825TestUtilityModel.phaseSignals) yield createPhaseSignalSliderPair(phaseSignal)).toList
    }
  }

  private def createPhaseSignalSliderPair(phaseSignal: PhaseSignal): Node = {
    new HBox {
      spacing = 20
      content = List(
        new HBox {
          spacing = 5
          content = List(
            new Label("sel_fall_" + phaseSignal.label) {
              prefWidth = 110
            },
            new Slider {
              min = 0
              max = 127
              value <==> phaseSignal.fall
            },
            new Label {
              prefWidth = 20
              text <== phaseSignal.fall.asString
            }
          )
        },
        new HBox {
          spacing = 10
          content = List(
            new Label("sel_rise_" + phaseSignal.label) {
              prefWidth = 110
            },
            new Slider {
              min = 0
              max = 127
              value <==> phaseSignal.rise
            },
            new Label {
              prefWidth = 20
              text <== phaseSignal.rise.asString
            }
          )
        }
      )
    }
  }

  private def createTimingGeneratorCurrentDacControls: List[Node] = createDacControls(UsbCam3825TestUtilityModel.timingGeneratorCurrentDacs, "%3.0f", "uA", 1)

  private def createTimingGeneratorVoltageDacControls: List[Node] = createDacControls(UsbCam3825TestUtilityModel.timingGeneratorVoltageDacs, "%1.3f", "V", 0.001)

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
          createSelectBitfileHBox
        )
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
