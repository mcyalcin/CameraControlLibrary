package com.mikrotasarim.camera

import scalafx.beans.property.{BooleanProperty, StringProperty}

object Model {

  var bitfilePath: StringProperty = new StringProperty()

  var bitfileDeployed: BooleanProperty = new BooleanProperty() {value = false}
}