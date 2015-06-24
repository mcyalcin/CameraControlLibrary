package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.{DeviceInterfaceModel, AdcChannelSettings}
import com.mikrotasarim.ui.model.DeviceInterfaceModel.adcChannel

import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, VBox}

object UsbCam3825AdcChannelControls {
  def createAdcChannelTab: Node =
    new ScrollPane {
      content = new VBox {
        padding = Insets(10)
        spacing = 30
        content = List(
          createAdcChannel("Top", adcChannel.AdcChannelTopSettings),
          createAdcChannel("Bottom", adcChannel.AdcChannelBotSettings)
        )
      }
    }

  private def createAdcChannel(label: String, model: AdcChannelSettings): Node =
    new HBox {
      padding = Insets(10)
      style = "-fx-border-color: darkgrey; -fx-border-radius: 10;"
      spacing = 10
      content = List(
        createAdcChannelCommonControls(label, model),
        new VBox {
          spacing = 10
          content = List(
            createAdcChannelControls(model, 0),
            createAdcChannelControls(model, 1)
          )
        }
      )
    }

  private def createAdcChannelCommonControls(label: String, model: AdcChannelSettings): Node =
    new VBox {
      spacing = 10
      content = List(
        new Label(label),
        new CheckBox("Shorted Input Noise Test") {
          selected <==> model.inputDriveControls.shortRefEnable
        },
        new HBox {
          spacing = 10
          content = List(
            new Label("PGA Gain"),
            new ChoiceBox(model.pgaSettings.gainLabels) {
              selectionModel().select(5)
              value <==> model.pgaSettings.selectedGain
            }
          )
        }
      )
    }

  private def createAdcChannelControls(model: AdcChannelSettings, index: Int): Node =
    new VBox {
      padding = Insets(10)
      style = "-fx-border-color: darkgrey; -fx-border-radius: 10;"
      spacing = 10
      content = List(
        new Label("Channel " + index),
        new HBox {
          spacing = 20
          content = Seq(
            new CheckBox("Power Down") {
              selected <==> model.powerDown(index)
            },
            new ChoiceBox(model.modeList) {
              value <==> model.selectedMode(index)
              disable <== model.powerDown(index)
            }
          )
        }
      )
    }
}
