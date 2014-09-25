package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.DacControlModel

import scalafx.Includes._
import scalafx.scene.Node
import scalafx.scene.control.{CheckBox, Button, Slider, Label}
import scalafx.scene.layout.HBox
import scalafx.util.converter.DoubleStringConverter

object UsbCam3825UiHelper {
  def createDacControls(dacList: Seq[DacControlModel], format: String, unitLabel: String, resolution: Double): List[Node] = {
    (for (dac <- dacList) yield new HBox {
      spacing = 10
      content = List(
        new Label {
          text = dac.name.value
          prefWidth = 100
        },
        new Slider {
          prefWidth = 200
          min = dac.valueRange._1
          max = dac.valueRange._2
          blockIncrement = resolution
          value <==> dac.value
          labelFormatter = new DoubleStringConverter
        },
        new Label {
          text <== (dac.value.asString(format) + " " + unitLabel)
          prefWidth = 50
        },
        new Button("Reset") {
          onAction = () => dac.Reset()
          tooltip = "Reset to " + dac.defaultValue + " " + unitLabel
        },
        new CheckBox("External") {
          inner =>
          inner.selected <==> dac.external
        },
        new CheckBox("Low Power") {
          inner =>
          inner.selected <==> dac.lowPower
        },
        new CheckBox("Power Down") {
          inner =>
          inner.selected <==> dac.powerDown
        },
        new Button("Commit") {
          onAction = () => {
            dac.Commit()
          }
          disable <== !dac.changed
        }
      )
    }).toList
  }
}
