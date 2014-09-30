package com.mikrotasarim.ui.view

import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.control.{ChoiceBox, Button, Slider, Label}
import scalafx.scene.layout.{HBox, VBox}

import com.mikrotasarim.ui.model.DigitalController._

object UsbCam3825DigitalControllerControls {

  def createDigitalControllerTab = new HBox {
    padding = Insets(10)
    spacing = 20
    content = List(createOtherControls, createDriveControls)
  }

  private def createOtherControls = new VBox {
    spacing = 10
    content = List(createDigtestControls)
  }

  private def createDigtestControls = new HBox {
    spacing = 10
    content = List(createDigtestControl(0), createDigtestControl(1))
  }

  private def createDigtestControl(index: Int) = new VBox {
    spacing = 5
    content = List(
      new Label("digtest" + index),
      new ChoiceBox(ObservableBuffer(digTestOptions(index).keys.toList)) {
        selectionModel().selectFirst()
        selectionModel().selectedItem.onChange(
          (_,_,newValue) => digTestSelection(index).value = newValue
        )
      }
    )
  }

  private def createDriveControls = new VBox {
    spacing = 5
    content = Label("Drive Pad Strengths") +: digPadDrives.map(createDriveControl)
  }

  private def createDriveControl(drive: DigPadDrive): Node = {
    new HBox {
      spacing = 10
      content = List(
        new Label(drive.label) {
          prefWidth = 100
        },
        new Slider {
          min = 0
          max = 7
          snapToTicks = true
          showTickMarks = true
          majorTickUnit = 1
          minorTickCount = 0
          blockIncrement = 1
          value <==> drive.strength
        },
        new Label {
          text <== drive.strength.asString
          prefWidth = 20
        },
        new Button("Commit") {
          disable <== !drive.changed
          onAction = () => drive.Commit()
        },
        new Button("Default") {
          onAction = () => drive.Reset()
        }
      )
    }
  }
}
