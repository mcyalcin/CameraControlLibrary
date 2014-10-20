package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.MtAs1410x2MemoryMap._
import com.mikrotasarim.ui.view.MtAs1410x2MemoryMapStage._

import scalafx.Includes._
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.{Button, TextField, Label, ScrollPane}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.stage.Stage

object MtAs1410x2ProbeTestStage extends Stage {

  width = 400
  height = 600
  title = "MTAS1410X2 Memory Map"
  scene = new Scene() {
    root = new ScrollPane {
      content = new VBox {

      }
    }
  }

  private def createLabelControls(): Node = {
    new VBox {
      spacing = 10
      content = List(
        createPathControl,
        createWaferControl,
        createDieControl
      )
    }
  }

  private def createPathControl = new HBox {
    spacing = 10
    content = List(
      new Label("Output path"),
      new TextField {

      }
    )
  }


  private def createWaferControl = new HBox {
    spacing = 10
    content = List(
      new Label("Wafer #"),
      new TextField {

      }
    )
  }

  private def createDieControl = new HBox {
    spacing = 10
    content = List(
      new Label("Die #"),
      new TextField {

      },
      new Button("Next")
    )
  }
}
