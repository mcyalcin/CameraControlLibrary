package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.UsbCam3825TestUtilityModel
import com.mikrotasarim.ui.model.UsbCam3825TestUtilityModel.AdcChannelSettings

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
          createAdcChannel("Top", UsbCam3825TestUtilityModel.AdcChannelTopSettings),
          createAdcChannel("Bottom", UsbCam3825TestUtilityModel.AdcChannelBotSettings)
        )
      }
    }

  private def createAdcChannel(label: String, model: AdcChannelSettings): Node =
    new HBox {
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
        new HBox {
          spacing = 10
          content = List(
            new Label("sa margin") {
              prefWidth = 100
            },
            new Slider {
              min = 0
              max = 3
              blockIncrement = 1
              value <==> model.timingSliders.saMargin
            },
            new Label {
              prefWidth = 20
              text <== model.timingSliders.saMargin.asString
            }
          )
        },
        new HBox {
          spacing = 10
          content = List(
            new Label("non overlap delay") {
              prefWidth = 100
            },
            new Slider {
              min = 0
              max = 15
              blockIncrement = 1
              value <==> model.timingSliders.nod
            },
            new Label {
              prefWidth = 20
              text <== model.timingSliders.nod.asString
            }
          )
        },
        new HBox {
          spacing = 10
          content = List(
            new Label("delay") {
              prefWidth = 100
            },
            new Slider {
              min = 0
              max = 15
              blockIncrement = 1
              value <==> model.timingSliders.delay
            },
            new Label {
              prefWidth = 20
              text <== model.timingSliders.delay.asString
            }
          )
        },
        new HBox {
          spacing = 10
          content = List(
            new Label("bw predrv") {
              prefWidth = 100
            },
            new Slider {
              min = 0
              max = 3
              value <==> model.inputDriveControls.bwPredrv
            },
            new Label {
              text <== model.inputDriveControls.bwPredrv.asString
            }
          )
        },
        new CheckBox("short ref enable") {
          selected <==> model.inputDriveControls.shortRefEnable
        },
        new HBox {
          spacing = 10
          content = List(
            new Label("bw pga") {
              prefWidth = 100
            },
            new Slider {
              min = 0
              max = 3
              value <==> model.pgaSettings.bwPga
            },
            new Label {
              text <== model.pgaSettings.bwPga.asString
            }
          )
        },
        new HBox {
          spacing = 10
          content = List(
            new Label("PGA Gain"),
            new ChoiceBox(model.pgaSettings.gainLabels) {
              selectionModel().select(5)
              selectionModel().selectedItem.onChange(
                (_, _, newValue) => model.pgaSettings.selectedGain.value = newValue
              )
            }
          )
        },
        new CheckBox("enable mdac test") {
          selected <==> model.miscControls.enableMdacTest
        },
        new CheckBox("ext ref enable") {
          selected <==> model.miscControls.extRefEnable
        },
        new CheckBox("external ref dac drv") {
          selected <==> model.miscControls.externalRefDacDrv
        }
      )
    }

  private def createAdcChannelControls(model: AdcChannelSettings, index: Int): Node =
    new VBox {
      spacing = 10
      content = List(
        new Label("Channel " + index),
        new HBox {
          spacing = 10
          content = List(
            new CheckBox("sel adc clk") {
              selected <==> model.clockSettings.selAdcClk(index)
            },
            new CheckBox("sel pga clk") {
              selected <==> model.clockSettings.selPgaClk(index)
            },
            new CheckBox("en adc clk") {
              selected <==> model.clockSettings.enAdcClk(index)
            },
            new CheckBox("en pga clk") {
              selected <==> model.clockSettings.enPgaClk(index)
            },
            new CheckBox("sel clk mode"){
              selected <==> model.clockSettings.selClkMode(index)
            }
          )
        },
        new HBox {
          spacing = 10
          content = List(
            new CheckBox("pd Predriver n") {
              selected <==> model.inputDriveControls.pdn(index)
            },
            new CheckBox("pd Predriver p") {
              selected <==> model.inputDriveControls.pdp(index)
            },
            new CheckBox("pd pga") {
              selected <==> model.pgaSettings.pdPga(index)
            },
            new CheckBox("pd adc ref drv") {
              selected <==> model.miscControls.pdAdcRefDrv(index)
            },
            new CheckBox("pd pga ref drv") {
              selected <==> model.miscControls.pdPgaRefDrv(index)
            },
            new CheckBox("pd adc") {
              selected <==> model.miscControls.pdAdc(index)
            },
            new CheckBox("rstb dig cor") {
              selected <==> model.miscControls.rstbDigCor(index)
            }
          )
        },
        new HBox {
          spacing = 20
          content = List(
            new HBox {
              spacing = 10
              content = List(
                new Label("Positive"),
                new ChoiceBox(model.modePositive.channelModeLabels) {
                  selectionModel().select(3)
                  selectionModel().selectedItem.onChange((_, _, newValue) => model.modePositive.selectedMode(index).value = newValue)
                }
              )
            },
            new HBox {
              spacing = 10
              content = List(
                new Label("Negative"),
                new ChoiceBox(model.modeNegative.channelModeLabels) {
                  selectionModel().select(3)
                  selectionModel().selectedItem.onChange((_, _, newValue) => model.modeNegative.selectedMode(index).value = newValue)
                }
              )
            }
          )
        }
      )
    }
}