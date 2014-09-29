package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.DacControlModel

import scalafx.Includes._
import scalafx.beans.property.IntegerProperty
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

  def createDelaySliderGroup(label: String, model: IntegerProperty, mini: Double, maxi: Double): Node = {
    new HBox {
      spacing = 10
      content = List(
        new Label(label) {
          prefWidth = 100
        },
        new Slider {
          min = mini
          max = maxi
          value <==> model
          snapToTicks = true
          blockIncrement = 1
          majorTickUnit = 1
        },
        new Label {
          text <== model.asString
          prefWidth = 20
        },
        new Button("Commit"),
        new Button("Default")
      )
    }
  }

  def createBiasSliderGroup(label: String, model: IntegerProperty, mini: Double, maxi: Double): Node = {
    new HBox {
      spacing = 10
      content = List(
        new Label(label) {
          prefWidth = 150
        },
        new CheckBox,
        new CheckBox,
        new Slider {
          min = mini
          max = maxi
          value <==> model
          snapToTicks = true
          blockIncrement = 1
          majorTickUnit = 1
        },
        new Label {
          text <== model.asString
          prefWidth = 20
        },
        new Button("Commit"),
        new Button("Default")
      )
    }
  }
}
