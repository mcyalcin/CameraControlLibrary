package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.OutputStage._

import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, VBox}

object UsbCam3825OutputStageControls {
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
                  UsbCam3825UiHelper.createBiasSliderGroup("LVDS OpAmp Bias Current", ibiasOp, 0, 511),
                  UsbCam3825UiHelper.createBiasSliderGroup("LVDS Driver Bias Current", ibiasDrv, 0, 511),
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
                        content = List(
                          new Label("ADC Test Memory Top") {
                            prefWidth = 150
                          },
                          new TextField {
                            text.onChange(if (text.value.length > 4) text.value = text.value.substring(0, 4))
                            prefColumnCount = 4
                          },
                          new Label {
                            text = "0000 0000 0000 0000"
                          }
                        )
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
                        content = List(
                          new Label("ADC Test Memory Bottom") {
                            prefWidth = 150
                          },
                          new TextField {
                            text.onChange(if (text.value.length > 4) text.value = text.value.substring(0, 4))
                            prefColumnCount = 4
                          },
                          new Label {
                            text = "0000 0000 0000 0000"
                          }
                        )
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
                  UsbCam3825UiHelper.createDelaySliderGroup("Clock Out Delay", clkOutDlySel, 0, 63),
                  UsbCam3825UiHelper.createDelaySliderGroup("Frame Valid Delay", fvalDlySel, 0, 15),
                  UsbCam3825UiHelper.createDelaySliderGroup("Data Valid Delay", dvalDlySel, 0, 15),
                  UsbCam3825UiHelper.createDelaySliderGroup("Coarse Delay", coarseDlySel, 0, 7),
                  UsbCam3825UiHelper.createDelaySliderGroup("Clock In Delay", clkInDlySel, 0, 63)
                )
              }
            )
          }
        )
      }
    }
  }

  def createMuxControl(channel: Int): Node = {
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

  def createPadControls(pad: Pad): Node = {
    new VBox {
      padding = Insets(8)
      style = "-fx-border-color: darkgrey; -fx-border-radius: 10;"
      spacing = 10
      content = List(
        new Label(pad.label),
        new ChoiceBox(pad.cmosLvdsLabels) {
          selectionModel().selectFirst()
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => pad.cmosSelected.value = newValue == "CMOS"
          )
        },
        new HBox {
          spacing = 10
          content = List(
            createCmosControls(pad),
            createLvdsControls(pad)
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
              onAction = () => pad.Reset()
            }
          )
        }
      )
    }
  }

  def createCmosControls(pad: Pad): Node = {
    new VBox {
      spacing = 10
      disable <== !pad.cmosSelected
      content = List(
        new ChoiceBox(pad.singleDifferentialLabels) {
          selectionModel().selectLast()
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => pad.singleSelected.value = newValue == "Single"
          )
        },
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

  def createLvdsControls(pad: Pad): Node = {
    new VBox {
      spacing = 10
      disable <== pad.cmosSelected
      content = List(
        new CheckBox("Enable Termination") {
          selected <==> pad.enableTermination
        },
        new ChoiceBox(pad.terminationResolutionLabels) {
          selectionModel().selectLast()
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => pad.lowTerminationResolution.value = newValue == "3.5 mA"
          )
        },
        new CheckBox("Power Down") {
          selected <==> pad.powerDown
        }
      )
    }
  }
}
