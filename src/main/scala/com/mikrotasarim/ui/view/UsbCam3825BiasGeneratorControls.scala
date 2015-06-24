package com.mikrotasarim.ui.view

import javafx.util.converter.IntegerStringConverter

import com.mikrotasarim.ui.model.DeviceInterfaceModel.biasGenerator

import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.control._
import scalafx.scene.layout.{VBox, HBox}
import scalafx.util.converter.DoubleStringConverter

object UsbCam3825BiasGeneratorControls {
  def createBiasGeneratorTab: Node = {
    new ScrollPane {
      content = new HBox {
        padding = Insets(10)
        spacing = 20
        content = List(
          new VBox {
            spacing = 20
            content = List(
              new CheckBox("Activate") {
                selected <==> biasGenerator.BiasGeneratorActivator.switch
              },
              new HBox {
                spacing = 10
                content = List(
                  new CheckBox("Power Down Top") {
                    selected <==> biasGenerator.BiasGeneratorPowerSettings.powerDownTop
                  },
                  new CheckBox("Power Down Bottom") {
                    selected <==> biasGenerator.BiasGeneratorPowerSettings.powerDownBot
                  }
                )
              },
              new VBox {
                spacing = 5
                content = (createPowerSlider +: List(new Label("Voltage DACs"))) ++ createBiasGeneratorVoltageDacControls ++ createBiasGeneratorCurrentDacControls
              }
            )
          }
        )
      }
    }
  }

  private def createBiasGeneratorCurrentDacControls: List[Node] = UsbCam3825UiHelper.createDacControls(biasGenerator.biasGeneratorCurrentDacs, "%3.0f", "uA", 1)

  private def createBiasGeneratorVoltageDacControls: List[Node] = UsbCam3825UiHelper.createDacControls(biasGenerator.biasGeneratorVoltageDacs, "%1.3f", "V", 0.001)

  private def createPowerSlider: Node = new HBox {
    spacing = 10
    content = List(
      new Label {
        text = "Power"
        prefWidth = 100
      },
      new Slider {
        prefWidth = 200
        min = 0
        max = 8
        majorTickUnit = 1
        minorTickCount = 0
        value <==> biasGenerator.power
        showTickLabels = true
        showTickMarks = true
        snapToTicks = true
      },
      new Button("Reset") {
        onAction = () => biasGenerator.ResetPower()
        tooltip = "Reset to " + 4
      },
      new CheckBox("Low Power") {
        inner =>
        inner.selected <==> biasGenerator.lowPower
      },
      new CheckBox("Power Down") {
        inner =>
        inner.selected <==> biasGenerator.powerDown
      },
      new Button("Commit") {
        onAction = () => {
          biasGenerator.CommitPower()
        }
        disable <== !biasGenerator.powerChanged
      }
    )
  }

}
