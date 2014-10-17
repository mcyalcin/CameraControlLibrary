package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.PhaseSignal
import com.mikrotasarim.ui.view.UsbCam3825UiHelper._
import com.mikrotasarim.ui.model.DeviceInterfaceModel.timingGenerator

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
            createTimingGeneratorAnalogTestControls +:
            createTimingGeneratorDigitalTestControls +:
            new Label("Voltage DACs") +:
            createTimingGeneratorVoltageDacControls) ++
            (new Label("Current DACs") +:
              createTimingGeneratorCurrentDacControls) :+
            new Label("Phase Signals") :+
            createPhaseSignalControls
      }
    }
  }

  private def createTimingGeneratorDigitalTestControls: Node = {
    new HBox {
      spacing = 10
      content = List(
        new ChoiceBox(timingGenerator.TimingGeneratorDigitalTestControls.testOutput0Labels) {
          selectionModel().selectFirst()
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => timingGenerator.TimingGeneratorDigitalTestControls.selectedTestOutput0.value = newValue
          )
        },
        new ChoiceBox(timingGenerator.TimingGeneratorDigitalTestControls.testOutput1Labels) {
          selectionModel().selectFirst()
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => timingGenerator.TimingGeneratorDigitalTestControls.selectedTestOutput1.value = newValue
          )
        },
        new ChoiceBox(timingGenerator.TimingGeneratorDigitalTestControls.testOutput2Labels) {
          selectionModel().selectFirst()
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => timingGenerator.TimingGeneratorDigitalTestControls.selectedTestOutput2.value = newValue
          )
        },
        new ChoiceBox(timingGenerator.TimingGeneratorDigitalTestControls.testOutput3Labels) {
          selectionModel().selectFirst()
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => timingGenerator.TimingGeneratorDigitalTestControls.selectedTestOutput3.value = newValue
          )
        },
        new ChoiceBox(timingGenerator.TimingGeneratorDigitalTestControls.testOutput4Labels) {
          selectionModel().selectFirst()
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => timingGenerator.TimingGeneratorDigitalTestControls.selectedTestOutput4.value = newValue
          )
        }
      )
    }
  }

  private def createTimingGeneratorAnalogTestControls: Node = {
    new HBox {
      spacing = 10
      content = List(
        new CheckBox("sel lf cap") {
          selected <==> timingGenerator.TimingGeneratorAnalogTestControls.selLfCap
        },
        createLabeledSnappingSliderGroup("sel vctrl", timingGenerator.TimingGeneratorAnalogTestControls.selVctrl, 0, 7, 1),
        new CheckBox("en vctrl ext") {
          selected <==> timingGenerator.TimingGeneratorAnalogTestControls.enVctrlExt
        },
        new ChoiceBox(timingGenerator.TimingGeneratorAnalogTestControls.analogCurrentTestLabels) {
          selectionModel().selectFirst()
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => timingGenerator.TimingGeneratorAnalogTestControls.selectedCurrentTest.value = newValue
          )
        },
        new ChoiceBox(timingGenerator.TimingGeneratorAnalogTestControls.analogVoltageTestLabels) {
          selectionModel().selectFirst()
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => timingGenerator.TimingGeneratorAnalogTestControls.selectedVoltageTest.value = newValue
          )
        }
      )
    }
  }

  private def createTimingGeneratorMainControls: Node = {
    new HBox {
      spacing = 10
      content = List(
        new CheckBox("Enable") {
          selected <==> timingGenerator.TimingGeneratorMainControls.enable
        },
        new ChoiceBox(timingGenerator.TimingGeneratorMainControls.pwOutLabels) {
          selectionModel().select(1)
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => timingGenerator.TimingGeneratorMainControls.selectedPwOut.value = newValue
          )
        },
        new ChoiceBox(timingGenerator.TimingGeneratorMainControls.pwRefLabels) {
          selectionModel().select(2)
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => timingGenerator.TimingGeneratorMainControls.selectedPwRef.value = newValue
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
            selected <==> timingGenerator.TimingGeneratorVcdlControls.pdCp
          },
          new CheckBox("pd vcdl") {
            selected <==> timingGenerator.TimingGeneratorVcdlControls.pdVcdl
          }
        )
      },
        new HBox {
          spacing = 10
          content = List(
            createLabeledSnappingSliderGroup("sel vpbias vcdl", timingGenerator.TimingGeneratorVcdlControls.vpBiasVcdl, 0, 127, 1),
            createLabeledSnappingSliderGroup("sel vnbias vcdl", timingGenerator.TimingGeneratorVcdlControls.vnBiasVcdl, 0, 127, 1),
            createLabeledSnappingSliderGroup("sel ibias vcdl", timingGenerator.VcdlBiasCurrent.iBiasVcdl, 0, 255, 1)
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
              selected <==> timingGenerator.lockPhaseSignals
            },
            new Button("Reset") {
              onAction = () => timingGenerator.ResetPhaseSignals()
            },
            new Button("Commit") {
              disable <== !timingGenerator.phaseSignalsChanged
              onAction = () => timingGenerator.CommitPhaseSignals()
            }
          )
        }
      )
    }
  }

  private def createPhaseSignalSliders: Node = {
    new VBox {
      spacing = 10
      content = (for (phaseSignal <- timingGenerator.phaseSignals) yield createPhaseSignalSliderPair(phaseSignal)).toList
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

  private def createTimingGeneratorCurrentDacControls: List[Node] = UsbCam3825UiHelper.createDacControls(timingGenerator.timingGeneratorCurrentDacs, "%3.0f", "uA", 1)

  private def createTimingGeneratorVoltageDacControls: List[Node] = UsbCam3825UiHelper.createDacControls(timingGenerator.timingGeneratorVoltageDacs, "%1.3f", "V", 0.001)

}
