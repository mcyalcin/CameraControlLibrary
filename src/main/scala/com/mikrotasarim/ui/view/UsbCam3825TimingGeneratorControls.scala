package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.view.UsbCam3825UiHelper._
import com.mikrotasarim.ui.model.TimingGenerator._

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
        new ChoiceBox(TimingGeneratorDigitalTestControls.testOutput0Labels) {
          selectionModel().selectFirst()
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => TimingGeneratorDigitalTestControls.selectedTestOutput0.value = newValue
          )
        },
        new ChoiceBox(TimingGeneratorDigitalTestControls.testOutput1Labels) {
          selectionModel().selectFirst()
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => TimingGeneratorDigitalTestControls.selectedTestOutput1.value = newValue
          )
        },
        new ChoiceBox(TimingGeneratorDigitalTestControls.testOutput2Labels) {
          selectionModel().selectFirst()
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => TimingGeneratorDigitalTestControls.selectedTestOutput2.value = newValue
          )
        },
        new ChoiceBox(TimingGeneratorDigitalTestControls.testOutput3Labels) {
          selectionModel().selectFirst()
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => TimingGeneratorDigitalTestControls.selectedTestOutput3.value = newValue
          )
        },
        new ChoiceBox(TimingGeneratorDigitalTestControls.testOutput4Labels) {
          selectionModel().selectFirst()
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => TimingGeneratorDigitalTestControls.selectedTestOutput4.value = newValue
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
          selected <==> TimingGeneratorAnalogTestControls.selLfCap
        },
        createLabeledSnappingSliderGroup("sel vctrl", TimingGeneratorAnalogTestControls.selVctrl, 0, 7, 1),
        new CheckBox("en vctrl ext") {
          selected <==> TimingGeneratorAnalogTestControls.enVctrlExt
        },
        new ChoiceBox(TimingGeneratorAnalogTestControls.analogCurrentTestLabels) {
          selectionModel().selectFirst()
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => TimingGeneratorAnalogTestControls.selectedCurrentTest.value = newValue
          )
        },
        new ChoiceBox(TimingGeneratorAnalogTestControls.analogVoltageTestLabels) {
          selectionModel().selectFirst()
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => TimingGeneratorAnalogTestControls.selectedVoltageTest.value = newValue
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
          selected <==> TimingGeneratorMainControls.enable
        },
        new ChoiceBox(TimingGeneratorMainControls.pwOutLabels) {
          selectionModel().select(1)
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => TimingGeneratorMainControls.selectedPwOut.value = newValue
          )
        },
        new ChoiceBox(TimingGeneratorMainControls.pwRefLabels) {
          selectionModel().select(2)
          selectionModel().selectedItem.onChange(
            (_, _, newValue) => TimingGeneratorMainControls.selectedPwRef.value = newValue
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
            selected <==> TimingGeneratorVcdlControls.pdCp
          },
          new CheckBox("pd vcdl") {
            selected <==> TimingGeneratorVcdlControls.pdVcdl
          }
        )
      },
        new HBox {
          spacing = 10
          content = List(
            createLabeledSnappingSliderGroup("sel vpbias vcdl", TimingGeneratorVcdlControls.vpBiasVcdl, 0, 127, 1),
            createLabeledSnappingSliderGroup("sel vnbias vcdl", TimingGeneratorVcdlControls.vnBiasVcdl, 0, 127, 1),
            createLabeledSnappingSliderGroup("sel ibias vcdl", VcdlBiasCurrent.iBiasVcdl, 0, 255, 1)
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
              selected <==> lockPhaseSignals
            },
            new Button("Reset") {
              onAction = () => ResetPhaseSignals()
            },
            new Button("Commit") {
              disable <== !phaseSignalsChanged
              onAction = () => CommitPhaseSignals()
            }
          )
        }
      )
    }
  }

  private def createPhaseSignalSliders: Node = {
    new VBox {
      spacing = 10
      content = (for (phaseSignal <- phaseSignals) yield createPhaseSignalSliderPair(phaseSignal)).toList
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

  private def createTimingGeneratorCurrentDacControls: List[Node] = UsbCam3825UiHelper.createDacControls(timingGeneratorCurrentDacs, "%3.0f", "uA", 1)

  private def createTimingGeneratorVoltageDacControls: List[Node] = UsbCam3825UiHelper.createDacControls(timingGeneratorVoltageDacs, "%1.3f", "V", 0.001)

}
