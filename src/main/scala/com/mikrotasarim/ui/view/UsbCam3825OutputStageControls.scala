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
          new HBox {
            content = List(
              new Label("ADC Test Memory Top") {
                prefWidth = 150
              },
              new TextField {
                text.onChange(if (text.value.length > 4) text.value = text.value.substring(0,4))
                prefColumnCount = 4
              }
            )
          }, // adc test memory top
          new HBox {
            content = List(
              new Label("ADC Test Memory Bottom") {
                prefWidth = 150
              },
              new TextField {
                text.onChange(if (text.value.length > 4) text.value = text.value.substring(0,4))
                prefColumnCount = 4
              }
            )
          }, // adc test memory bot
          new HBox {
            spacing = 10
            content = List(
              new Label("ibias op") {
                prefWidth = 50
              },
              new Slider {
                min = 0
                max = 511
                blockIncrement = 1
                value <==> UsbCam3825TestUtilityModel.OutputStage.ibiasOp
              },
              new Label {
                text <== UsbCam3825TestUtilityModel.OutputStage.ibiasOp.asString
              }
            )
          },
          new HBox {
            spacing = 10
            content = List(
              new Label("ibias drv") {
                prefWidth = 50
              },
              new Slider {
                min = 0
                max = 511
                blockIncrement = 1
                value <==> UsbCam3825TestUtilityModel.OutputStage.ibiasDrv
              },
              new Label {
                text <== UsbCam3825TestUtilityModel.OutputStage.ibiasDrv.asString
              }
            )
          },
          new HBox {
            spacing = 20
            content = List(
              new ChoiceBox(UsbCam3825TestUtilityModel.OutputStage.testDataLabels) {
                selectionModel().selectLast()
                selectionModel().selectedItem.onChange(
                  (_, _, newValue) => UsbCam3825TestUtilityModel.OutputStage.testSelected.value = "Test" == newValue
                )
              },
              new VBox {
                content = List(
                  new HBox {
                    spacing = 20
                    content = List(
                      new CheckBox("adc test mem mdac sel") {
                        disable <== !UsbCam3825TestUtilityModel.OutputStage.testSelected
                        selected <==> UsbCam3825TestUtilityModel.OutputStage.adcTestMemMdacSel
                      },
                      new CheckBox("adc test sel top") {
                        disable <== !UsbCam3825TestUtilityModel.OutputStage.testSelected
                        selected <==> UsbCam3825TestUtilityModel.OutputStage.adcTestSelTop
                      },
                      new CheckBox("adc test sel bot") {
                        disable <== !UsbCam3825TestUtilityModel.OutputStage.testSelected
                        selected <==> UsbCam3825TestUtilityModel.OutputStage.adcTestSelBot
                      }
                    )
                  }
                )
              }
            )
          },
          new HBox { // multiplexer
            spacing = 10
            content = List(
              new ChoiceBox,
              new ChoiceBox,
              new ChoiceBox,
              new ChoiceBox,
              new ChoiceBox
            )
          },
          new VBox { // delay sliders
            spacing = 10
            content = List(
              new HBox {
                spacing = 10
                content = List(
                  new Label,
                  new Slider,
                  new Label
                )
              },
              new HBox {
                spacing = 10
                content = List(
                  new Label,
                  new Slider,
                  new Label
                )
              },
              new HBox {
                spacing = 10
                content = List(
                  new Label,
                  new Slider,
                  new Label
                )
              },
              new HBox {
                spacing = 10
                content = List(
                  new Label,
                  new Slider,
                  new Label
                )
              },
              new HBox {
                spacing = 10
                content = List(
                  new Label,
                  new Slider,
                  new Label
                )
              }
            )
          }
        )
      }
    }
  }

  def createPadControls(pad: Pad): Node = {
    new VBox {
      spacing = 10
      content = List(
        new Label(pad.label),
        new ChoiceBox(pad.cmosLvdsLabels) {
          selectionModel().selectLast()
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
