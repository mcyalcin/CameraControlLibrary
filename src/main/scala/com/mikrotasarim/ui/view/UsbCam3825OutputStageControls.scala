package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.UsbCam3825TestUtilityModel
import com.mikrotasarim.ui.model.UsbCam3825TestUtilityModel.Pad

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
                  createPadControls(UsbCam3825TestUtilityModel.OutputStage.pads("out_ser<0>")),
                  createPadControls(UsbCam3825TestUtilityModel.OutputStage.pads("out_ser<1>")),
                  createPadControls(UsbCam3825TestUtilityModel.OutputStage.pads("out_ser<2>")),
                  createPadControls(UsbCam3825TestUtilityModel.OutputStage.pads("out_ser<3>"))
                )
              },
              new HBox {
                spacing = 10
                content = List(
                  createPadControls(UsbCam3825TestUtilityModel.OutputStage.pads("fval_pad")),
                  createPadControls(UsbCam3825TestUtilityModel.OutputStage.pads("data_frame_clk_mux")),
                  createPadControls(UsbCam3825TestUtilityModel.OutputStage.pads("out_data_clk")),
                  createPadControls(UsbCam3825TestUtilityModel.OutputStage.pads("dval_pad"))
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
                  UsbCam3825UiHelper.createBiasSliderGroup("LVDS OpAmp Bias Current", UsbCam3825TestUtilityModel.OutputStage.ibiasOp, 0, 511),
                  UsbCam3825UiHelper.createBiasSliderGroup("LVDS Driver Bias Current", UsbCam3825TestUtilityModel.OutputStage.ibiasDrv, 0, 511),
                  new HBox {
                    spacing = 10
                    content = List(
                      new ChoiceBox(UsbCam3825TestUtilityModel.OutputStage.testDataLabels) {
                        selectionModel().selectLast()
                        selectionModel().selectedItem.onChange(
                          (_, _, newValue) => UsbCam3825TestUtilityModel.OutputStage.testSelected.value = "Test" == newValue
                        )
                      },
                      new ChoiceBox(UsbCam3825TestUtilityModel.OutputStage.adcTestMemMdacLabels) {
                        disable <== !UsbCam3825TestUtilityModel.OutputStage.testSelected
                        selectionModel().selectLast()
                        selectionModel().selectedItem.onChange(
                          (_, _, newValue) => UsbCam3825TestUtilityModel.OutputStage.adcTestMemMdacSel.value = "Memory" == newValue
                        )
                      }
                    )
                  },
                  new HBox {
                    spacing = 20
                    content = List(
                      new ChoiceBox(UsbCam3825TestUtilityModel.OutputStage.adcTestSelTopLabels) {
                        disable <== !UsbCam3825TestUtilityModel.OutputStage.testSelected || UsbCam3825TestUtilityModel.OutputStage.adcTestMemMdacSel
                        selectionModel().selectLast()
                        selectionModel().selectedItem.onChange(
                          (_, _, newValue) => UsbCam3825TestUtilityModel.OutputStage.adcTestSelTop.value = UsbCam3825TestUtilityModel.OutputStage.adcTestSelTopLabels(0) == newValue
                        )
                      },
                      new HBox {
                        disable <== !UsbCam3825TestUtilityModel.OutputStage.testSelected || !UsbCam3825TestUtilityModel.OutputStage.adcTestMemMdacSel
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
                      new ChoiceBox(UsbCam3825TestUtilityModel.OutputStage.adcTestSelBotLabels) {
                        disable <== !UsbCam3825TestUtilityModel.OutputStage.testSelected || UsbCam3825TestUtilityModel.OutputStage.adcTestMemMdacSel
                        selectionModel().selectLast()
                        selectionModel().selectedItem.onChange(
                          (_, _, newValue) => UsbCam3825TestUtilityModel.OutputStage.adcTestSelBot.value = UsbCam3825TestUtilityModel.OutputStage.adcTestSelBotLabels(0) == newValue
                        )
                      },
                      new HBox {
                        disable <== !UsbCam3825TestUtilityModel.OutputStage.testSelected || !UsbCam3825TestUtilityModel.OutputStage.adcTestMemMdacSel
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
                      new ChoiceBox(UsbCam3825TestUtilityModel.OutputStage.msbLsbLabels) {
                        selectionModel().selectLast()
                        selectionModel().selectedItem.onChange(
                          (_, _, newValue) => UsbCam3825TestUtilityModel.OutputStage.msbSelected.value = "Least Significant Bit" == newValue
                        )
                      },
                      new Label {
                        text <== UsbCam3825TestUtilityModel.OutputStage.msbLsbHelpText
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
                          new ChoiceBox(UsbCam3825TestUtilityModel.OutputStage.inputLabels) {
                            value <==> UsbCam3825TestUtilityModel.OutputStage.dataFrameClock
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
                  UsbCam3825UiHelper.createDelaySliderGroup("Clock Out Delay", UsbCam3825TestUtilityModel.OutputStage.clkOutDlySel, 0, 63),
                  UsbCam3825UiHelper.createDelaySliderGroup("Frame Valid Delay", UsbCam3825TestUtilityModel.OutputStage.fvalDlySel, 0, 15),
                  UsbCam3825UiHelper.createDelaySliderGroup("Data Valid Delay", UsbCam3825TestUtilityModel.OutputStage.dvalDlySel, 0, 15),
                  UsbCam3825UiHelper.createDelaySliderGroup("Coarse Delay", UsbCam3825TestUtilityModel.OutputStage.coarseDlySel, 0, 7),
                  UsbCam3825UiHelper.createDelaySliderGroup("Clock In Delay", UsbCam3825TestUtilityModel.OutputStage.clkInDlySel, 0, 63)
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
        new ChoiceBox(UsbCam3825TestUtilityModel.OutputStage.inputLabels) {
          value <==> UsbCam3825TestUtilityModel.OutputStage.selectedOutputs(channel)
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
            new Button("Commit"),
            new Button("Default")
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
