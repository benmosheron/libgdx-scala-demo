package com.benmosheron
import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}

object Main {

  @main
  def main: Unit =
    new Launcher().runUntilFinished { finish =>
      val config = new LwjglApplicationConfiguration()
      config.height = 500
      config.width = 1000
      val _ = new LwjglApplication(new DemoGame(finish), config)
    }
}
