package com.mikrotasarim.ui.view

import com.mikrotasarim.ui.model.ProbeTestController

import scalafx.Includes._
import scalafx.beans.property.BooleanProperty
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.chart.{XYChart, LineChart, NumberAxis}
import scalafx.scene.{Node, Scene}
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, VBox}
import scalafx.stage.{FileChooser, Stage}

object MtAs1410x2ProbeTestStage extends Stage {

  width = 800
  height = 600
  title = "MTAS1410X2 Probe Test"
  scene = new Scene() {
    root = new ScrollPane {
      content = new HBox {
        padding = Insets(10)
        spacing = 50
        content = List(
          createIdColumn(),
          createTestColumn()//,
//          createDacSweepCharts()
        )
      }
    }
  }

//  private def createDacSweepCharts(): Node = new VBox {
//    spacing = 10
//    content = List(
//      new HBox {
//        spacing = 10
//        content = List(
//          createDacSweepChart(0),
//          createDacSweepChart(1)
//        )
//      },
//      new HBox {
//        spacing = 10
//        content = List(
//          createDacSweepChart(2),
//          createDacSweepChart(3)
//        )
//      }
//    )
//  }

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
    content = for (i <- 1 to 13) yield createTestControl(i)
  }

  private def createTestControl(i: Int): Node = new HBox {
    spacing = 10
    content = List(
      new Label(ProbeTestController.labels(i)) {
        prefWidth = 220
      },
      passFailControl(ProbeTestController.pass(i), ProbeTestController.fail(i)),
      new Button("Run") {
        onAction = handle {
          ProbeTestController.RunTest(i)
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

//  private def createDacSweepChart(i: Int): Node = {
//    val xAxis = new NumberAxis
//    xAxis.label = "Memory Value"
//    xAxis.forceZeroInRange = false
//    val yAxis = new NumberAxis
//
//    val lineChart = LineChart(xAxis, yAxis)
//    lineChart.title = "DAC Sweep Result " + i
//    lineChart.setPrefWidth(300)
//    lineChart.setPrefHeight(250)
//
//    val data =
//      ObservableBuffer(
//        (for (i <- 0x378 until 0xe24 by 10) yield (i, math.log10(i)))
//          map { case (x, y) => XYChart.Data[Number, Number](x, y).delegate})
//
//    ProbeTestController.chartData(i).onChange(
//      {
//        val data =
//          ObservableBuffer(
//            (for (j <- 0x378 until 0xe24 by 10) yield (j, ProbeTestController.chartData(i)(j)))
//              map { case (x, y) => XYChart.Data[Number, Number](x, y).delegate})
//      }
//    )
//
//    val series = XYChart.Series[Number, Number]("Value read", data)
//    lineChart.getData.add(series)
//    lineChart.createSymbols = false
//
//    lineChart
//  }

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
