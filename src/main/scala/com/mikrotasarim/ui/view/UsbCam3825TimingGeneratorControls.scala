package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.UsbCam3825TestUtilityModel
import com.mikrotasarim.ui.model.UsbCam3825TestUtilityModel.PhaseSignal

import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, VBox}

object UsbCam3825TimingGeneratorControls {
  def createTimingGeneratorTab: Node = {
    new ScrollPane {
      content = new VBox {
        padding = Insets(10)
        spacing = 10
        content =
          (createTimingGeneratorMainControls +:
            createTimingGeneratorVcdlControls +:
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
        new CheckBox("Enable") {
          selected <==> UsbCam3825TestUtilityModel.TimingGeneratorMainControls.enable
        },
        new ChoiceBox(UsbCam3825TestUtilityModel.TimingGeneratorMainControls.pwOutLabels) {
          selectionModel().select(1)
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => UsbCam3825TestUtilityModel.TimingGeneratorMainControls.selectedPwOut.value = newValue
          )
        },
        new ChoiceBox(UsbCam3825TestUtilityModel.TimingGeneratorMainControls.pwRefLabels) {
          selectionModel().select(2)
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => UsbCam3825TestUtilityModel.TimingGeneratorMainControls.selectedPwOut.value = newValue
          )
        }
      )
    }
  }

  private def createTimingGeneratorVcdlControls: Node = {
    new VBox {
      spacing = 10
      content = List(new HBox {
        spacing = 10
        content = List(
          new CheckBox("pd cp") {
            selected <==> UsbCam3825TestUtilityModel.TimingGeneratorVcdlControls.pdCp
          },
          new CheckBox("pd vcdl") {
            selected <==> UsbCam3825TestUtilityModel.TimingGeneratorVcdlControls.pdVcdl
          }
        )
      },
        new HBox {
          spacing = 10
          content = List(
            new HBox {
              spacing = 10
              content = List(
                new Label("sel vpbias vcdl"),
                new Slider {
                  min = 0
                  max = 127
                  value <==> UsbCam3825TestUtilityModel.TimingGeneratorVcdlControls.vpBiasVcdl
                },
                new Label {
                  prefWidth = 20
                  text <== UsbCam3825TestUtilityModel.TimingGeneratorVcdlControls.vpBiasVcdl.asString
                }
              )
            },
            new HBox {
              spacing = 10
              content = List(
                new Label("sel vnbias vcdl"),
                new Slider {
                  min = 0
                  max = 127
                  value <==> UsbCam3825TestUtilityModel.TimingGeneratorVcdlControls.vnBiasVcdl
                },
                new Label {
                  prefWidth = 20
                  text <== UsbCam3825TestUtilityModel.TimingGeneratorVcdlControls.vnBiasVcdl.asString
                }
              )
            }
          )
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
            new CheckBox("Lock relative") {
              selected <==> UsbCam3825TestUtilityModel.lockPhaseSignals
            },
            new Button("Reset") {
              onAction = () => UsbCam3825TestUtilityModel.ResetPhaseSignals()
            },
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

  private def createTimingGeneratorCurrentDacControls: List[Node] = UsbCam3825UiHelper.createDacControls(UsbCam3825TestUtilityModel.timingGeneratorCurrentDacs, "%3.0f", "uA", 1)

  private def createTimingGeneratorVoltageDacControls: List[Node] = UsbCam3825UiHelper.createDacControls(UsbCam3825TestUtilityModel.timingGeneratorVoltageDacs, "%1.3f", "V", 0.001)

}
