package com.benmosheron
import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}


// This kills scalafmt
@main
def main: Unit =
  new Launcher().runUntilFinished { finish =>
    val config = new LwjglApplicationConfiguration()
    config.height = 1080
    config.width = 1920
    val _ = new LwjglApplication(new DemoGame(finish), config)
  }