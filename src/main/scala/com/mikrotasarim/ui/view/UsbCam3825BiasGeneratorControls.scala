package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.BiasGenerator._

import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.control.{Label, ChoiceBox, CheckBox, ScrollPane}
import scalafx.scene.layout.{VBox, HBox}

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
                selected <==> BiasGeneratorActivator.switch
              },
              new HBox {
                spacing = 10
                content = List(
                  new CheckBox("Power Down Top") {
                    selected <==> BiasGeneratorPowerSettings.powerDownTop
                  },
                  new CheckBox("Power Down Bottom") {
                    selected <==> BiasGeneratorPowerSettings.powerDownBot
                  }
                )
              },
              new HBox {
                spacing = 20
                content = List(
                  new ChoiceBox(BiasGeneratorTestSettings.voltageTestLabels) {
                    selectionModel().selectFirst()
                    selectionModel().selectedItem.onChange(
                      (_, _, newValue) => BiasGeneratorTestSettings.selectedVoltageTest.value = newValue
                    )
                  },
                  new ChoiceBox(BiasGeneratorTestSettings.currentTestLabels){
                    selectionModel().selectFirst()
                    selectionModel().selectedItem.onChange(
                      (_, _, newValue) => BiasGeneratorTestSettings.selectedCurrentTest.value = newValue
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
            spacing = 5
            content = List(new Label("Voltage DACs")) ++ createBiasGeneratorVoltageDacControls
          }
        )
      }
    }
  }

  private def createBiasGeneratorCurrentDacControls: List[Node] = UsbCam3825UiHelper.createDacControls(biasGeneratorCurrentDacs, "%3.0f", "uA", 1)

  private def createBiasGeneratorVoltageDacControls: List[Node] = UsbCam3825UiHelper.createDacControls(biasGeneratorVoltageDacs, "%1.3f", "V", 0.001)

}
