package com.mikrotasarim.ui.view

import java.util.concurrent.Executor

import scala.util.Random
import scalafx.application.Platform
import scalafx.beans.property.BooleanProperty
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.stage.Stage
import scalafx.Includes._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

object JavaFXExecutionContext {
  implicit val javaFxExecutionContext: ExecutionContext =
    ExecutionContext.fromExecutor(new Executor {
      def execute(command: Runnable): Unit = Platform.runLater(command)
    })
}

object VideoFeedStage extends Stage {

  onShowing = handle {
    videoFeedOn.value = true
    DisplayNextFrame()
  }

  onHidden = handle {
    videoFeedOn.value = false
  }

  val canvas = new Canvas {
    width = 386
    height = 288
  }

  val bla = canvas.graphicsContext2D.getPixelWriter

  var frame = Future {
    VideoFeedSource.nextFrame
  }

  var currentFrame: Frame = new Frame

  val videoFeedOn = BooleanProperty(value = true)
  val frameChanged = BooleanProperty(value = false)

  frameChanged.onChange({
    println(System.currentTimeMillis())
    if (frameChanged.value && videoFeedOn.value) {
      frameChanged.value = false
      DisplayNextFrame()
    }
  })

  val ec = JavaFXExecutionContext.javaFxExecutionContext

  def DisplayNextFrame() {
    Future {
      VideoFeedSource.nextFrame
    }.map(result => {
      for (i <- 0 until 384)
        for (j <- 0 until 288)
          bla.setColor(i, j, Color.gray(result.bitmap(i)(j)))
      frameChanged.value = true
    })(ec)
  }

  title = "Video Feed"

  scene = new Scene() {
    root = new VBox() {
      content = canvas
    }
  }
}

object VideoFeedSource {

  def nextFrame: Frame = {
    Thread.sleep(100)
    new Frame
  }

  var image = new Frame
}

class Frame {
  val bitmap = Array.ofDim[Double](384, 288)

  for (i <- 0 until bitmap.length) {
    for (j <- 0 until bitmap(i).length) {
      bitmap(i)(j) = Math.abs(Random.nextDouble())
    }
  }
}
