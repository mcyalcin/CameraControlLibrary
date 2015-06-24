package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.{Pad, AdcTestMemory}
import com.mikrotasarim.ui.model.DeviceInterfaceModel.outputStage

import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, VBox}

object UsbCam3825OutputStageControls {

  private def createTestMemoryControl(label: String, model: AdcTestMemory) = {
    List(
      new Label("ADC Test Memory " + label) {
        prefWidth = 150
      },
      new TextField {
        text.onChange({
          text.value = text.value.replaceAll("[^a-fA-F0-9]", "")
          if (text.value.length > 4) text.value = text.value.substring(0, 4)
        })
        text <==> model.hexString
        prefColumnCount = 4
      },
      new Button("Commit") {
        onAction = () => model.Commit()
      },
      new Label {
        text <== model.binaryString
      }
    )
  }

  def createOutputStageTab: Node = {
    new ScrollPane {
      content = new VBox {
        padding = Insets(10)
        spacing = 15
        content = List(
          new VBox {
            spacing = 10
            content = List(
              new HBox {
                spacing = 10
                content = List(
                  createPadControls(outputStage.pads.head),
                  createPadControls(outputStage.pads(1)),
                  createPadControls(outputStage.pads(2)),
                  createPadControls(outputStage.pads(3))
                )
              },
              new HBox {
                spacing = 10
                content = List(
                  createPadControls(outputStage.pads(4)),
                  createPadControls(outputStage.pads(5)),
                  createPadControls(outputStage.pads(6)),
                  createPadControls(outputStage.pads(7))
                )
              },
              new Button("Turn On LVDS") {
                onAction = handle { outputStage.TurnOnLvds() }
              }
            )
          },
          new VBox {
            spacing = 30
            content = List(
              new VBox {
                spacing = 10
                content = List(
                  new HBox {
                    spacing = 10
                    content = List(
                      new ChoiceBox(outputStage.testDataLabels) {
                        selectionModel().selectLast()
                        selectionModel().selectedItem.onChange(
                          (_, _, newValue) => {
                            outputStage.testSelected.value = "Test" == newValue
                            outputStage.adcTestMemMdacSel.set(true)
                          }
                        )
                      }
                    )
                  },
                  new HBox {
                    spacing = 20
                    content = List(
                      new HBox {
                        disable <== !outputStage.testSelected || !outputStage.adcTestMemMdacSel
                        spacing = 10
                        content = createTestMemoryControl("Top", outputStage.adcTestMemoryTop)
                      }
                    )
                  },
                  new HBox {
                    spacing = 20
                    content = List(
                      new HBox {
                        disable <== !outputStage.testSelected || !outputStage.adcTestMemMdacSel
                        spacing = 10
                        content = createTestMemoryControl("Bottom", outputStage.adcTestMemoryBot)
                      }
                    )
                  },
                  new HBox {
                    spacing = 10
                    content = List(
                      new ChoiceBox(outputStage.msbLsbLabels) {
                        value <==> outputStage.selectedMsb
                      },
                      new Label {
                        text <== outputStage.msbLsbHelpText
                      }
                    )
                  }
                )
              },
              new VBox {
                spacing = 10
                content = List(
                  new VBox {
                    spacing = 10
                    content = List(
                      new HBox {
                        spacing = 10
                        content = List(
                          new Label("Data Frame Clock"),
                          new ChoiceBox(outputStage.inputLabels) {
                            value <==> outputStage.dataFrameClock
                          }
                        )
                      },
                      new HBox {
                        spacing = 10
                        content = List(
                          createMuxControl(0),
                          createMuxControl(1),
                          createMuxControl(2),
                          createMuxControl(3)
                        )
                      }
                    )
                  }
                )
              },
              new VBox {
                spacing = 5
                content = List(
                  UsbCam3825UiHelper.createDelaySliderGroup("Clock Out Delay", outputStage.delayWord.clkOutDlySel, 0, 63, outputStage.delayWord.CommitCod, outputStage.delayWord.ResetCod, outputStage.delayWord.clkOutDlyChanged),
                  UsbCam3825UiHelper.createDelaySliderGroup("Frame Valid Delay", outputStage.delayWord.fvalDlySel, 0, 15, outputStage.delayWord.CommitFval, outputStage.delayWord.ResetFval, outputStage.delayWord.fvalDlyChanged),
                  UsbCam3825UiHelper.createDelaySliderGroup("Data Valid Delay", outputStage.delayWord.dvalDlySel, 0, 15, outputStage.delayWord.CommitDval, outputStage.delayWord.ResetDval, outputStage.delayWord.dvalDlyChanged)
                )
              }
            )
          }
        )
      }
    }
  }

  private def createMuxControl(channel: Int): Node = {
    new VBox {
      padding = Insets(5)
      spacing = 10
      style = "-fx-border-color: darkgrey;-fx-border-radius: 10;"
      content = List(
        new Label("Serial Output " + channel),
        new ChoiceBox(outputStage.inputLabels) {
          value <==> outputStage.selectedOutputs(channel)
        }
      )
    }
  }

  private def createPadControls(pad: Pad): Node = {
    val cmosLvdsChoiceBox = new ChoiceBox(pad.cmosLvdsLabels) {
      value <==> pad.selectedCmosLvds
    }
    val singleDifferentialChoiceBox = new ChoiceBox(pad.singleDifferentialLabels) {
      selectionModel().selectLast()
      selectionModel().selectedItem.onChange(
        (_, _, newValue) => pad.singleSelected.value = newValue == "Single"
      )
    }
    val terminationResolutionChoiceBox = new ChoiceBox(pad.terminationResolutionLabels) {
      value <==> pad.selectedLvdsCurrent
    }

    new VBox {
      padding = Insets(8)
      style = "-fx-border-color: darkgrey; -fx-border-radius: 10;"
      spacing = 10
      content = List(
        new Label(pad.label),
        cmosLvdsChoiceBox,
        new HBox {
          spacing = 10
          content = List(
            createCmosControls(pad, singleDifferentialChoiceBox),
            createLvdsControls(pad, terminationResolutionChoiceBox)
          )
        },
        new HBox {
          spacing = 10
          content = List(
            new Button("Commit") {
              onAction = () => pad.Commit()
              disable <== !pad.changed
            },
            new Button("Default") {
              onAction = () => {
                cmosLvdsChoiceBox.selectionModel().selectFirst()
                singleDifferentialChoiceBox.selectionModel().selectLast()
                terminationResolutionChoiceBox.selectionModel().selectLast()
                pad.Reset()
              }
            }
          )
        }
      )
    }
  }

  private def createCmosControls(pad: Pad, choiceBox: ChoiceBox[String]): Node = {
    new VBox {
      spacing = 10
      disable <== !pad.cmosSelected
      content = List(
        choiceBox,
        new Slider {
          min = 0
          max = 3
          showTickMarks = true
          showTickLabels = true
          majorTickUnit = 1
          minorTickCount = 0
          blockIncrement = 1
          snapToTicks = true
          value <==> pad.power
        }
      )
    }
  }

  private def createLvdsControls(pad: Pad, choiceBox: ChoiceBox[String]): Node = {
    new VBox {
      spacing = 10
      disable <== pad.cmosSelected
      content = List(
        new CheckBox("Enable Termination") {
          selected <==> pad.enableTermination
        },
        choiceBox,
        new CheckBox("Power Down") {
          selected <==> pad.powerDown
        }
      )
    }
  }
}
