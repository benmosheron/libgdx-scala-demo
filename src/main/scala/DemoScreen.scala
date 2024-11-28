package com.benmosheron
import com.badlogic.gdx.{Gdx, InputMultiplexer, Screen}
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.{FitViewport, Viewport}
import com.benmosheron.input.ScrollInputProcessor

class DemoScreen(finish: () => Unit) extends Screen {

  private val scrollInputProcessor = new ScrollInputProcessor()
  Gdx.input.setInputProcessor(new InputMultiplexer(scrollInputProcessor))

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

    // Zoom the camera
    val scroll = scrollInputProcessor.readScrollAndReset
    camera.zoom = if (scroll == 0) { camera.zoom }
    else if (scroll == 1) { camera.zoom * 1.1f }
    else { camera.zoom / 1.1f }
    camera.update()
    shapeRenderer.setProjectionMatrix(camera.combined)

    Gdx.gl.glClearColor(0.01f, 0.1f, 0.3f, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    Gdx.gl.glLineWidth(2)

    xy.set(viewport.unproject(new Vector2(Gdx.input.getX.toFloat, Gdx.input.getY.toFloat)))
    xy.set(
      math.max(math.min(xy.x, viewport.getWorldWidth - 1), 0),
      math.max(math.min(xy.y, viewport.getWorldHeight - 1), 0)
    )

    updateSquares(delta, xy)

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
}
