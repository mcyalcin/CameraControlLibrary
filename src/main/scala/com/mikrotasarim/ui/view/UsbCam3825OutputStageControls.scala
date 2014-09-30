package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.OutputStage._

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
        spacing = 20
        content = List(
          new VBox {
            spacing = 10
            content = List(
              new HBox {
                spacing = 10
                content = List(
                  createPadControls(pads(0)),
                  createPadControls(pads(1)),
                  createPadControls(pads(2)),
                  createPadControls(pads(3))
                )
              },
              new HBox {
                spacing = 10
                content = List(
                  createPadControls(pads(4)),
                  createPadControls(pads(5)),
                  createPadControls(pads(6)),
                  createPadControls(pads(7))
                )
              }
            )
          },
          new VBox {
            spacing = 30
            content = List(
              new VBox {
                spacing = 10
                content = List(
                  UsbCam3825UiHelper.createBiasSliderGroup("LVDS OpAmp Bias Current", opAmpBias, 0, 63),
                  UsbCam3825UiHelper.createBiasSliderGroup("LVDS Driver Bias Current", driverBias, 0, 63),
                  new HBox {
                    spacing = 10
                    content = List(
                      new ChoiceBox(testDataLabels) {
                        selectionModel().selectLast()
                        selectionModel().selectedItem.onChange(
                          (_, _, newValue) => testSelected.value = "Test" == newValue
                        )
                      },
                      new ChoiceBox(adcTestMemMdacLabels) {
                        disable <== !testSelected
                        selectionModel().selectLast()
                        selectionModel().selectedItem.onChange(
                          (_, _, newValue) => adcTestMemMdacSel.value = "Memory" == newValue
                        )
                      }
                    )
                  },
                  new HBox {
                    spacing = 20
                    content = List(
                      new ChoiceBox(adcTestSelTopLabels) {
                        disable <== !testSelected || adcTestMemMdacSel
                        selectionModel().selectLast()
                        selectionModel().selectedItem.onChange(
                          (_, _, newValue) => adcTestSelTop.value = adcTestSelTopLabels(0) == newValue
                        )
                      },
                      new HBox {
                        disable <== !testSelected || !adcTestMemMdacSel
                        spacing = 10
                        content = createTestMemoryControl("Top", adcTestMemoryTop)
                      }
                    )
                  },
                  new HBox {
                    spacing = 20
                    content = List(
                      new ChoiceBox(adcTestSelBotLabels) {
                        disable <== !testSelected || adcTestMemMdacSel
                        selectionModel().selectLast()
                        selectionModel().selectedItem.onChange(
                          (_, _, newValue) => adcTestSelBot.value = adcTestSelBotLabels(0) == newValue
                        )
                      },
                      new HBox {
                        disable <== !testSelected || !adcTestMemMdacSel
                        spacing = 10
                        content = createTestMemoryControl("Bottom", adcTestMemoryBot)
                      }
                    )
                  },
                  new HBox {
                    spacing = 10
                    content = List(
                      new ChoiceBox(msbLsbLabels) {
                        selectionModel().selectLast()
                        selectionModel().selectedItem.onChange(
                          (_, _, newValue) => msbSelected.value = "Least Significant Bit" == newValue
                        )
                      },
                      new Label {
                        text <== msbLsbHelpText
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
                          new ChoiceBox(inputLabels) {
                            value <==> dataFrameClock
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
                spacing = 10
                content = List(
                  UsbCam3825UiHelper.createDelaySliderGroup("Clock Out Delay", delayWord.clkOutDlySel, 0, 63, delayWord.CommitCod, delayWord.ResetCod, delayWord.clkOutDlyChanged),
                  UsbCam3825UiHelper.createDelaySliderGroup("Frame Valid Delay", delayWord.fvalDlySel, 0, 15, delayWord.CommitFval, delayWord.ResetFval, delayWord.fvalDlyChanged),
                  UsbCam3825UiHelper.createDelaySliderGroup("Data Valid Delay", delayWord.dvalDlySel, 0, 15, delayWord.CommitDval, delayWord.ResetDval, delayWord.dvalDlyChanged),
                  UsbCam3825UiHelper.createDelaySliderGroup("Coarse Delay", moreDelayWord.coarseDlySel, 0, 7, moreDelayWord.CommitCoarseDelay, moreDelayWord.ResetCoarseDelay, moreDelayWord.coarseDlyChanged),
                  UsbCam3825UiHelper.createDelaySliderGroup("Clock In Delay", moreDelayWord.clkInDlySel, 0, 63, moreDelayWord.CommitClkInDelay, moreDelayWord.ResetClkInDelay, moreDelayWord.clkInDlyChanged)
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
        new ChoiceBox(inputLabels) {
          value <==> selectedOutputs(channel)
        }
      )
    }
  }

  private def createPadControls(pad: Pad): Node = {
    val cmosLvdsChoiceBox = new ChoiceBox(pad.cmosLvdsLabels) {
      selectionModel().selectFirst()
      selectionModel().selectedItem.onChange(
        (_, _, newValue) => pad.cmosSelected.value = newValue == "CMOS"
      )
    }
    val singleDifferentialChoiceBox = new ChoiceBox(pad.singleDifferentialLabels) {
      selectionModel().selectLast()
      selectionModel().selectedItem.onChange(
        (_, _, newValue) => pad.singleSelected.value = newValue == "Single"
      )
    }
    val terminationResolutionChoiceBox = new ChoiceBox(pad.terminationResolutionLabels) {
      selectionModel().selectLast()
      selectionModel().selectedItem.onChange(
        (_, _, newValue) => pad.lowTerminationResolution.value = newValue == "3.5 mA"
      )
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
