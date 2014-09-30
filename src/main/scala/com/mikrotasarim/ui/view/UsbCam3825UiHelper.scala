package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.DacControlModel
import com.mikrotasarim.ui.model.OutputStage.BiasCurrent

import scalafx.Includes._
import scalafx.beans.property.{BooleanProperty, IntegerProperty}
import scalafx.scene.Node
import scalafx.scene.control._
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color
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

  def createDelaySliderGroup(label: String, model: IntegerProperty, mini: Double, maxi: Double, commitMethod: () => Unit, resetMethod: () => Unit, changed: BooleanProperty): Node = {
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
        new Button("Commit") {
          disable <== !changed
          onAction = commitMethod
        },
        new Button("Default") {
          onAction = resetMethod
        }
      )
    }
  }

  def createBiasSliderGroup(label: String, model: BiasCurrent, mini: Double, maxi: Double): Node = {
    val choiceBox = new ChoiceBox(model.resLabels) {
      selectionModel().select(1)
      selectionModel().selectedItem.onChange(
        model.resolution.value = selectionModel().selectedItem.value
      )
    }
    new HBox {
      spacing = 10
      content = List(
        new Label(label) {
          prefWidth = 150
        },
        choiceBox,
        new Slider {
          min = mini
          max = maxi
          value <==> model.sliderValue
          snapToTicks = true
          blockIncrement = 1
          majorTickUnit = 1
        },
        new Label {
          text <== model.displayValue.asString("%3.2f") + " uA"
          prefWidth = 60
          text.onChange((_,_,_) =>
            if (model.displayValue.value > model.maxValue) {
              textFill = Color.RED
              tooltip = "Max safe current is 163 uA"
            } else {
              textFill = Color.BLACK
              tooltip = null
            }
          )
        },
        new Button("Commit") {
          disable <== !model.changed
          onAction = () => model.Commit()
        },
        new Button("Default") {
          onAction = () => {
            choiceBox.selectionModel().select(1)
            model.Reset()
          }
        }
      )
    }
  }
}
