package com.benmosheron
package input
import com.badlogic.gdx.InputProcessor

class ScrollInputProcessor extends InputProcessor {

  private var scroll: Int = 0

  def readScrollAndReset = {
    val x = scroll
    scroll = 0
    x
  }

  override def keyDown(keycode: Int) = false
  override def keyUp(keycode: Int) = false
  override def keyTyped(character: Char) = false
  override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false
  override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false
  override def touchDragged(screenX: Int, screenY: Int, pointer: Int) = false
  override def mouseMoved(screenX: Int, screenY: Int) = false
  override def scrolled(amount: Int) = {
    scroll = amount
    true
  }
}
