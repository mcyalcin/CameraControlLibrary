package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.ProbeTestController
import com.mikrotasarim.ui.model.ProbeTestController.TestCase

import scalafx.Includes._
import scalafx.beans.property.BooleanProperty
import scalafx.geometry.Insets
import scalafx.scene.{Node, Scene}
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, VBox}
import scalafx.stage.{FileChooser, Stage}

object MtAs1410x2ProbeTestStage extends Stage {

  width = 800
  height = 800
  title = "MTAS1410X2 Probe Test"
  scene = new Scene() {
    root = new ScrollPane {
      content = new HBox {
        padding = Insets(10)
        spacing = 50
        content = List(
          createIdColumn(),
          createTestColumn()//,
        )
      }
    }
  }

  private def createIdColumn(): Node = new VBox {
    spacing = 10
    content = List(
      createLabelControls(),
      new Button("Run All Tests") {
        onAction = handle {
          ProbeTestController.RunAllTests()
        }
      },
      new Button("Save Result and Proceed") {
        onAction = handle {
          ProbeTestController.SaveAndProceed()
        }
      },
      new ChoiceBox(ProbeTestController.comPortList) {
        value <==> ProbeTestController.selectedComPort
      },
      new HBox {
        spacing = 10
        content = List(
          new Label("Output"),
          new Button("On") {
            onAction = handle {
              ProbeTestController.outputOn()
            }
          },
          new Button("Off") {
            onAction = handle {
              ProbeTestController.outputOff()
            }
          }
        )
      },
      new HBox {
        spacing = 10
        content = List(
          new Label("Mode"),
          new Button("Local") {
            onAction = handle {
              ProbeTestController.setLocal()
            }
          },
          new Button("Remote") {
            onAction = handle {
              ProbeTestController.setRemote()
            }
          }
        )
      },
      createAdcConversionControl(),
      createSweepReferenceControl(),
      createCommentControl()
    )
  }

  private def createAdcConversionControl(): Node = new HBox {
    spacing = 10
    content = List(
      new Label("Conversion freq"),
      new ChoiceBox(ProbeTestController.adcConfigOptions) {
        value <==> ProbeTestController.selectedAdcConfig
      }
    )
  }

  private def createCommentControl(): Node = {
    new VBox {
      spacing = 10
      content = List(
        new Label("Comment:"),
        new TextField {
          text <==> ProbeTestController.comment
        }
      )
    }

  }

  private def createSweepReferenceControl(): Node = {
    new Button("Select Sweep Reference File") {
      onAction = handle {
        val fileChooser = new FileChooser() {
          title = "Pick a sweep reference file"
        }

        val filePath = fileChooser.showOpenDialog(MtAs1410x2ProbeTestStage)

        if (filePath != null) {
          ProbeTestController.sweepReferenceFilePath.value = filePath.getAbsolutePath
        }
      }
    }
  }

  private def createTestColumn(): Node = new VBox {
    spacing = 10
    content = for (testCase <- ProbeTestController.testCases) yield createTestControl(testCase)
  }

  private def createTestControl(testCase: TestCase): Node = new HBox {
    spacing = 10
    content = List(
      new Label(testCase.label) {
        prefWidth = 280
      },
      passFailControl(testCase.pass, testCase.fail),
      new Button("Run") {
        onAction = handle {
          testCase.Run()
        }
      }
    )
  }

  private def passFailControl(pass: BooleanProperty, fail: BooleanProperty): Node = {
    val tog = new ToggleGroup()

    new HBox {
      spacing = 10
      prefWidth = 120
      content = List(
        new RadioButton("Pass") {
          toggleGroup = tog
          selected <==> pass
        },
        new RadioButton("Fail") {
          toggleGroup = tog
          selected <==> fail
        }
      )
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
      new Label("Output path") {
        prefWidth = 85
      },
      new TextField {
        prefWidth = 150
        text <==> ProbeTestController.outputPath
      }
    )
  }

  private def createWaferControl = new HBox {
    spacing = 10
    content = List(
      new Label("Wafer Id") {
        prefWidth = 85
      },
      new TextField {
        prefWidth = 150
        text <==> ProbeTestController.waferId
      }
    )
  }

  private def createDieControl = new HBox {
    spacing = 10
    content = List(
      new Label("Die #") {
        prefWidth = 85
      },
      new TextField {
        prefWidth = 150
        text.onChange({
          text.value = text.value.replaceAll("[^0-9]", "")
        })
        text <==> ProbeTestController.dieNumber
      }
    )
  }
}
