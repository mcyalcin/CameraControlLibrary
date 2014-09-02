package com.mikrotasarim.camera.command

class CompositeCommand(subCommands: List[Command]) extends Command {
  def Execute(): Unit = {
    for (command <- subCommands) {
      command.Execute()
    }
  }
}
