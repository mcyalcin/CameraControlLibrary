package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.MtAs1410x2MemoryMap._

import scalafx.Includes._
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.{Label, Button, TextField, ScrollPane}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.stage.Stage

object MtAs1410x2MemoryMapStage extends Stage {

  width = 400
  height = 600
  title = "MTAS1410X2 Memory Map"
  scene = new Scene() {
    root = new ScrollPane {
      content = new VBox {
        content = memoryModel.map(createMemoryLocationControl)
      }
    }
  }

  private def createMemoryLocationControl(model: MemLoc): Node = {
    new HBox {
      spacing = 10
      content = List(
        new Label("Addr: " + model.addr) {
          prefWidth = 75
        },
        new TextField {
          text <==> model.text
        },
        new Button("Commit") {
          onAction = handle { model.Commit() }
        }
      )
    }
  }
}
