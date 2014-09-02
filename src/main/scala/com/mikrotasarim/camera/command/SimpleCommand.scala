package com.mikrotasarim.camera.command

class SimpleCommand(opt: () => Unit) extends Command {
  override def Execute(): Unit = opt()
}
