package com.benmosheron
import com.badlogic.gdx.{Gdx, Input, InputMultiplexer, Screen}
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.{Vector2, Vector3}
import com.badlogic.gdx.utils.viewport.{FitViewport, Viewport}
import com.benmosheron.input.ScrollInputProcessor

class DemoScreen(finish: () => Unit) extends Screen {

  private val scrollInputProcessor = new ScrollInputProcessor()
  Gdx.input.setInputProcessor(new InputMultiplexer(scrollInputProcessor))

  // This is quite annoying, ESC to show cursor
  Gdx.input.setCursorCatched(true)

  private val worldWidth = 2000
  private val worldHeight = 1000

  private val camera = new OrthographicCamera(Gdx.graphics.getWidth, Gdx.graphics.getHeight)
  private val viewport: Viewport = new FitViewport(worldWidth, worldHeight, camera)
  private val shapeRenderer = new ShapeRenderer

  camera.position.set(
    Gdx.graphics.getWidth / 2,
    Gdx.graphics.getHeight / 2,
    0
  )

  private val nx = 640
  private val ny = 320
  private val squares = Array.fill(nx * ny * 3)(0f)
  private val s = 3.125f

  private def setSquare(x: Int, y: Int, i: Int, f: Float): Unit = squares.update((x * ny * 3) + (y * 3) + i, f)
  private def getSquare(x: Int, y: Int, i: Int) = squares((x * ny * 3) + (y * 3) + i)

  private var total = 0f
  private def updateSquares(delta: Float, xy: Vector2): Unit = {
    total = total + delta

    val dr = math.abs(((total % 10f) / 10f) - 0.5f)
    val dg = math.abs((((3.3f + total) % 10f) / 10f) - 0.5f)
    val db = math.abs((((6.6f + total) % 10f) / 10f) - 0.5f)

    (0 until nx).map(x =>
      (0 until ny).map(y => {
        val r = getSquare(x, y, 0)
        val g = getSquare(x, y, 1)
        val b = getSquare(x, y, 2)

        val r0 = if (x + 1 < nx) 0.99f * getSquare(x + 1, y, 0) else 0f
        val g0 = if (x + 1 < nx) 0.99f * getSquare(x + 1, y, 1) else 0f
        val b0 = if (x + 1 < nx) 0.99f * getSquare(x + 1, y, 2) else 0f

        val r1 = if (x - 1 >= 0) 0.99f * getSquare(x - 1, y, 0) else 0f
        val g1 = if (x - 1 >= 0) 0.99f * getSquare(x - 1, y, 1) else 0f
        val b1 = if (x - 1 >= 0) 0.99f * getSquare(x - 1, y, 2) else 0f

        val r2 = if (y + 1 < ny) 0.99f * getSquare(x, y + 1, 0) else 0f
        val g2 = if (y + 1 < ny) 0.99f * getSquare(x, y + 1, 1) else 0f
        val b2 = if (y + 1 < ny) 0.99f * getSquare(x, y + 1, 2) else 0f

        val r3 = if (y - 1 >= 0) 0.99f * getSquare(x, y - 1, 0) else 0f
        val g3 = if (y - 1 >= 0) 0.99f * getSquare(x, y - 1, 1) else 0f
        val b3 = if (y - 1 >= 0) 0.99f * getSquare(x, y - 1, 2) else 0f

        setSquare(x, y, 0, math.max(math.max(math.max(math.max(r, r0), r1), r2), r3) * 0.99f)
        setSquare(x, y, 1, math.max(math.max(math.max(math.max(g, g0), g1), g2), g3) * 0.99f)
        setSquare(x, y, 2, math.max(math.max(math.max(math.max(b, b0), b1), b2), b3) * 0.99f)
      })
    )

    val xi = (xy.x / s).toInt
    val yi = (xy.y / s).toInt
    setSquare(xi, yi, 0, dr)
    setSquare(xi, yi, 1, dg)
    setSquare(xi, yi, 2, db)
  }

  private val xy = Vector2(0, 0)
  def render(delta: Float): Unit = {

    // Read the mouse position
    xy.set(viewport.unproject(new Vector2(Gdx.input.getX.toFloat, Gdx.input.getY.toFloat)))
    xy.set(
      math.max(math.min(xy.x, viewport.getWorldWidth - 1), 0),
      math.max(math.min(xy.y, viewport.getWorldHeight - 1), 0)
    )

    // Read mouse scroll
    val scroll = scrollInputProcessor.readScrollAndReset

    // Zoom the camera
    camera.zoom = if (scroll == 0) { camera.zoom }
    else if (scroll == 1) { camera.zoom * 1.1f }
    else { camera.zoom / 1.1f }

    // Allow mouse to escape the window on ESC
    if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
      Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched)
      val moveTo = viewport.project(xy)
      Gdx.input.setCursorPosition(moveTo.x.toInt, moveTo.y.toInt)
    }

    // Restrict the mouse to the window
    if(Gdx.input.getX < 0) Gdx.input.setCursorPosition(0, Gdx.input.getY)
    if(Gdx.input.getY < 0) Gdx.input.setCursorPosition(Gdx.input.getX, 0)
    if (Gdx.input.getX > Gdx.graphics.getWidth - 1) Gdx.input.setCursorPosition(Gdx.graphics.getWidth - 1, Gdx.input.getY)
    if (Gdx.input.getY > Gdx.graphics.getHeight - 1) Gdx.input.setCursorPosition(Gdx.input.getX, Gdx.graphics.getHeight - 1)

    // Detect the mouse at the window edge
    camera.position.add(MoveWindow.readVector(Gdx.input.getX, Gdx.input.getY, Gdx.graphics.getWidth - 1, Gdx.graphics.getHeight - 1, camera.zoom))

    // Update camera and renderers
    camera.update()
    shapeRenderer.setProjectionMatrix(camera.combined)

    // Run logic
    updateSquares(delta, xy)

    // Draw things
    Gdx.gl.glClearColor(0.01f, 0.1f, 0.3f, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    Gdx.gl.glLineWidth(2)
    
    shapeRenderer.begin(ShapeType.Filled)

    (0 until nx)
      .foreach(x => {
        (0 until ny)
          .foreach(y => {
            shapeRenderer.setColor(getSquare(x, y, 0), getSquare(x, y, 1), getSquare(x, y, 2), 1)
            shapeRenderer.rect(x * s, y * s, s, s)
          })
      })

    shapeRenderer.end()

  }

  def resize(width: Int, height: Int): Unit = {
    println("RESIZE")
    println(s"(${Gdx.graphics.getWidth}, ${Gdx.graphics.getHeight})")

    camera.update()
    viewport.update(width, height, true)
    shapeRenderer.setProjectionMatrix(camera.combined)
  }

  def show(): Unit = {}
  def hide(): Unit = {}
  def pause(): Unit = {}
  def resume(): Unit = {}
  def dispose(): Unit = finish()


  object MoveWindow {

    def readVector(mouseX: Int, mouseY: Int, width: Int, height: Int, scale: Float): Vector3 = {
      val v = if mouseX >= width then {
        if mouseY >= height then new Vector3(1,-1,0)
        else if mouseY <= 0 then new Vector3(1,1,0)
        else new Vector3(1,0,0)
      } else if mouseX <= 0 then {
        if mouseY >= height then new Vector3(-1,-1,0)
        else if mouseY <= 0 then new Vector3(-1,1,0)
        else new Vector3(-1,0,0)
      }
      else if mouseY >= height then new Vector3(0,-1,0)
      else if mouseY <= 0 then new Vector3(0,1,0)
      else new Vector3(0,0,0)

      v.scl(10f * scale)
    }

  }
}
