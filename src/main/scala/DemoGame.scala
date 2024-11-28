package com.benmosheron

import com.badlogic.gdx._

class DemoGame(finish: () => Unit) extends Game {

  private var benScreen: Option[DemoScreen] = None

  override def create(): Unit = {
    benScreen = Some(new DemoScreen(finish))
    benScreen.foreach(setScreen)
  }

  override def resize(width: Int, height: Int): Unit =
    benScreen.foreach(_.resize(width, height))

  override def dispose(): Unit = benScreen.foreach(_.dispose())
}
