package io.legado.app.ui.book.read.page.delegate

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Region
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.MotionEvent
import android.view.View
import io.legado.app.ui.book.read.page.ReadView
import io.legado.app.ui.book.read.page.entities.PageDirection
import io.legado.app.utils.screenshot
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.min

@Suppress("DEPRECATION")
class SimulationPageDelegate(readView: ReadView) : HorizontalPageDelegate(readView) {

    companion object {
    }

    private var mTouchX = 0.01f
    private var mTouchY = 0.01f

    private var mCornerX = 1
    private var mCornerY = 1
    private val mPath0: Path = Path()
    private val mPath1: Path = Path()

    private val mBezierStart1 = PointF()
    private val mBezierControl1 = PointF()
    private val mBezierVertex1 = PointF()
    private val mBezierEnd1 = PointF()

    private val mBezierStart2 = PointF()
    private val mBezierControl2 = PointF()
    private val mBezierVertex2 = PointF()
    private val mBezierEnd2 = PointF()

    private var mMiddleX = 0f
    private var mMiddleY = 0f
    private var mDegrees = 0f
    private var mTouchToCornerDis = 0f
    private var mColorMatrixFilter = ColorMatrixColorFilter(
        ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    )
    private val mMatrix: Matrix = Matrix()
    private val mMatrixArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f)

    private var mIsRtOrLb = false
    private var mMaxLength = hypot(viewWidth.toDouble(), viewHeight.toDouble()).toFloat()

    private val mBackShadowDrawableRL = GradientDrawable(
        GradientDrawable.Orientation.RIGHT_LEFT,
        intArrayOf(0xff111111.toInt(), 0x111111)
    ).apply { gradientType = GradientDrawable.LINEAR_GRADIENT }

    private val mBackShadowDrawableLR = GradientDrawable(
        GradientDrawable.Orientation.LEFT_RIGHT,
        intArrayOf(0xff111111.toInt(), 0x111111)
    ).apply { gradientType = GradientDrawable.LINEAR_GRADIENT }

    private val mPaint: Paint = Paint().apply { style = Paint.Style.FILL }

    private var curBitmap: Bitmap? = null
    private var prevBitmap: Bitmap? = null
    private var nextBitmap: Bitmap? = null
    private var canvas: Canvas = Canvas()

    override fun setBitmap() {
        when (mDirection) {
            PageDirection.PREV -> {
                prevBitmap = prevPage.screenshot(prevBitmap, canvas)
                curBitmap = curPage.screenshot(curBitmap, canvas)
            }

            PageDirection.NEXT -> {
                nextBitmap = nextPage.screenshot(nextBitmap, canvas)
                curBitmap = curPage.screenshot(curBitmap, canvas)
            }

            else -> Unit
        }
    }

    override fun setViewSize(width: Int, height: Int) {
        super.setViewSize(width, height)
        mMaxLength = hypot(viewWidth.toDouble(), viewHeight.toDouble()).toFloat()
    }

    override fun onTouch(event: MotionEvent) {
        super.onTouch(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                calcCornerXY(event.x, event.y)
            }

            MotionEvent.ACTION_MOVE -> {
                if ((startY > viewHeight / 3f && startY < viewHeight * 2f / 3f)
                    || mDirection == PageDirection.PREV
                ) {
                    readView.touchY = viewHeight.toFloat()
                }

                if (startY > viewHeight / 3f && startY < viewHeight / 2f
                    && mDirection == PageDirection.NEXT
                ) {
                    readView.touchY = 1f
                }
            }
        }
    }

    override fun setDirection(direction: PageDirection) {
        super.setDirection(direction)
        when (direction) {
            PageDirection.PREV ->
                if (startX > viewWidth / 2) {
                    calcCornerXY(startX, viewHeight.toFloat())
                } else {
                    calcCornerXY(viewWidth - startX, viewHeight.toFloat())
                }

            PageDirection.NEXT ->
                if (viewWidth / 2 > startX) {
                    calcCornerXY(viewWidth - startX, startY)
                }

            else -> Unit
        }
    }

    override fun onAnimStart(animationSpeed: Int) {
        var dx: Int
        var dy: Int
        if (isCancel) {
            dx = if (mCornerX > 0 && mDirection == PageDirection.NEXT) {
                (viewWidth - touchX).toInt()
            } else {
                -touchX.toInt()
            }
            if (mDirection != PageDirection.NEXT) {
                dx = -(viewWidth + touchX.toInt())
            }
            dy = if (mCornerY > 0) {
                (viewHeight - touchY).toInt()
            } else {
                -touchY.toInt()
            }
        } else {
            dx = if (mCornerX > 0 && mDirection == PageDirection.NEXT) {
                -(viewWidth + touchX.toInt())
            } else {
                (viewWidth - touchX + viewWidth).toInt()
            }
            dy = if (mCornerY > 0) {
                (viewHeight - touchY).toInt()
            } else {
                (1 - touchY).toInt()
            }
        }
        scroller.startScroll(
            touchX.toInt(), touchY.toInt(),
            dx, dy,
            animationSpeed
        )
        isRunning = true
        isStarted = true
        readView.invalidate()
    }

    override fun onAnimStop() {
        readView.setLayerType(View.LAYER_TYPE_NONE, null)
        if (!isCancel) {
            readView.fillPage(mDirection)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (!isRunning) return
        canvas.save()
        canvas.clipRect(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        when (mDirection) {
            PageDirection.NEXT -> {
                calcPoints()
                drawCurrentPageArea(canvas, curBitmap)
                drawNextPageAreaAndShadow(canvas, nextBitmap)
                drawCurrentBackArea(canvas, curBitmap)
            }

            PageDirection.PREV -> {
                calcPoints()
                drawCurrentPageArea(canvas, prevBitmap)
                drawNextPageAreaAndShadow(canvas, curBitmap)
                drawCurrentBackArea(canvas, prevBitmap)
            }

            else -> Unit
        }
        canvas.restore()
    }

    private fun drawCurrentBackArea(
        canvas: Canvas,
        bitmap: Bitmap?
    ) {
        bitmap ?: return
        mPath1.reset()
        mPath1.moveTo(mBezierVertex2.x, mBezierVertex2.y)
        mPath1.lineTo(mBezierVertex1.x, mBezierVertex1.y)
        mPath1.lineTo(mBezierEnd1.x, mBezierEnd1.y)
        mPath1.lineTo(mTouchX, mTouchY)
        mPath1.lineTo(mBezierEnd2.x, mBezierEnd2.y)
        mPath1.close()

        canvas.save()
        try {
            canvas.clipPath(mPath0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                canvas.clipPath(mPath1)
            } else {
                canvas.clipPath(mPath1, Region.Op.INTERSECT)
            }

            mPaint.colorFilter = mColorMatrixFilter
            val dis = hypot(
                mCornerX - mBezierControl1.x.toDouble(),
                mBezierControl2.y - mCornerY.toDouble()
            ).toFloat()
            val f8 = (mCornerX - mBezierControl1.x) / dis
            val f9 = (mBezierControl2.y - mCornerY) / dis
            mMatrixArray[0] = 1 - 2 * f9 * f9
            mMatrixArray[1] = 2 * f8 * f9
            mMatrixArray[3] = mMatrixArray[1]
            mMatrixArray[4] = 1 - 2 * f8 * f8
            mMatrix.reset()
            mMatrix.setValues(mMatrixArray)
            mMatrix.preTranslate(-mBezierControl1.x, -mBezierControl1.y)
            mMatrix.postTranslate(mBezierControl1.x, mBezierControl1.y)
            canvas.drawBitmap(bitmap, mMatrix, mPaint)
            mPaint.colorFilter = null
        } finally {
            canvas.restore()
        }
    }

    private fun drawNextPageAreaAndShadow(
        canvas: Canvas,
        bitmap: Bitmap?
    ) {
        bitmap ?: return
        mPath1.reset()
        mPath1.moveTo(mBezierStart1.x, mBezierStart1.y)
        mPath1.lineTo(mBezierVertex1.x, mBezierVertex1.y)
        mPath1.lineTo(mBezierVertex2.x, mBezierVertex2.y)
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y)
        mPath1.lineTo(mCornerX.toFloat(), mCornerY.toFloat())
        mPath1.close()
        mDegrees = Math.toDegrees(
            atan2(
                (mBezierControl1.x - mCornerX).toDouble(),
                mBezierControl2.y - mCornerY.toDouble()
            )
        ).toFloat()

        val leftx: Int
        val rightx: Int
        val mBackShadowDrawable: GradientDrawable
        if (mIsRtOrLb) {
            leftx = mBezierStart1.x.toInt()
            rightx = (mBezierStart1.x + mTouchToCornerDis / 4).toInt()
            mBackShadowDrawable = mBackShadowDrawableLR
        } else {
            leftx = (mBezierStart1.x - mTouchToCornerDis / 4).toInt()
            rightx = mBezierStart1.x.toInt()
            mBackShadowDrawable = mBackShadowDrawableRL
        }

        canvas.save()
        try {
            canvas.clipPath(mPath0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                canvas.clipPath(mPath1)
            } else {
                canvas.clipPath(mPath1, Region.Op.INTERSECT)
            }
        } catch (_: Exception) {
        }

        canvas.drawBitmap(bitmap, 0f, 0f, null)
        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y)
        mBackShadowDrawable.setBounds(
            leftx, mBezierStart1.y.toInt(),
            rightx, (mMaxLength + mBezierStart1.y).toInt()
        )
        mBackShadowDrawable.draw(canvas)
        canvas.restore()
    }

    private fun drawCurrentPageArea(
        canvas: Canvas,
        bitmap: Bitmap?
    ) {
        bitmap ?: return
        mPath0.reset()
        mPath0.moveTo(mBezierStart1.x, mBezierStart1.y)
        mPath0.quadTo(mBezierControl1.x, mBezierControl1.y, mBezierEnd1.x, mBezierEnd1.y)
        mPath0.lineTo(mTouchX, mTouchY)
        mPath0.lineTo(mBezierEnd2.x, mBezierEnd2.y)
        mPath0.quadTo(mBezierControl2.x, mBezierControl2.y, mBezierStart2.x, mBezierStart2.y)
        mPath0.lineTo(mCornerX.toFloat(), mCornerY.toFloat())
        mPath0.close()

        canvas.save()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                canvas.clipOutPath(mPath0)
            } else {
                canvas.clipPath(mPath0, Region.Op.XOR)
            }
            canvas.drawBitmap(bitmap, 0f, 0f, null)
        } finally {
            canvas.restore()
        }
    }

    private fun calcCornerXY(x: Float, y: Float) {
        mCornerX = if (x <= viewWidth / 2f) 0 else viewWidth
        mCornerY = if (y <= viewHeight / 2f) 0 else viewHeight
        mIsRtOrLb = (mCornerX == 0 && mCornerY == viewHeight)
                || (mCornerX == viewWidth && mCornerY == 0)
    }

    private fun calcPoints() {
        mTouchX = touchX
        mTouchY = touchY

        mMiddleX = (mTouchX + mCornerX) / 2
        mMiddleY = (mTouchY + mCornerY) / 2
        val cornerXf = mCornerX.toFloat()
        val cornerYf = mCornerY.toFloat()
        val cornerXMinusMiddleX = cornerXf - mMiddleX
        val cornerYMinusMiddleY = cornerYf - mMiddleY

        mBezierControl1.x =
            mMiddleX - cornerYMinusMiddleY * cornerYMinusMiddleY / cornerXMinusMiddleX
        mBezierControl1.y = cornerYf
        mBezierControl2.x = cornerXf

        if (cornerYMinusMiddleY == 0f) {
            mBezierControl2.y = mMiddleY - cornerXMinusMiddleX * cornerXMinusMiddleX / 0.1f
        } else {
            mBezierControl2.y =
                mMiddleY - cornerXMinusMiddleX * cornerXMinusMiddleX / cornerYMinusMiddleY
        }
        mBezierStart1.x = mBezierControl1.x - (cornerXf - mBezierControl1.x) / 2
        mBezierStart1.y = cornerYf

        if (mTouchX > 0 && mTouchX < viewWidth) {
            if (mBezierStart1.x < 0 || mBezierStart1.x > viewWidth) {
                if (mBezierStart1.x < 0)
                    mBezierStart1.x = viewWidth - mBezierStart1.x

                val f1 = abs(mCornerX - mTouchX)
                val f2 = viewWidth * f1 / mBezierStart1.x
                mTouchX = abs(mCornerX - f2)

                val f3 = abs(mCornerX - mTouchX) * abs(mCornerY - mTouchY) / f1
                mTouchY = abs(mCornerY - f3)

                mMiddleX = (mTouchX + mCornerX) / 2
                mMiddleY = (mTouchY + mCornerY) / 2
                val recalcCornerXMinusMiddleX = cornerXf - mMiddleX
                val recalcCornerYMinusMiddleY = cornerYf - mMiddleY

                mBezierControl1.x =
                    mMiddleX - recalcCornerYMinusMiddleY * recalcCornerYMinusMiddleY / recalcCornerXMinusMiddleX
                mBezierControl1.y = cornerYf

                mBezierControl2.x = cornerXf
                if (recalcCornerYMinusMiddleY == 0f) {
                    mBezierControl2.y =
                        mMiddleY - recalcCornerXMinusMiddleX * recalcCornerXMinusMiddleX / 0.1f
                } else {
                    mBezierControl2.y =
                        mMiddleY - recalcCornerXMinusMiddleX * recalcCornerXMinusMiddleX / recalcCornerYMinusMiddleY
                }

                mBezierStart1.x = mBezierControl1.x - (cornerXf - mBezierControl1.x) / 2
            }
        }
        mBezierStart2.x = cornerXf
        mBezierStart2.y = mBezierControl2.y - (cornerYf - mBezierControl2.y) / 2

        mTouchToCornerDis = hypot(
            (mTouchX - mCornerX).toDouble(),
            (mTouchY - mCornerY).toDouble()
        ).toFloat()

        getCross(mTouchX, mTouchY, mBezierControl1, mBezierStart1, mBezierStart2, mBezierEnd1)
        getCross(mTouchX, mTouchY, mBezierControl2, mBezierStart1, mBezierStart2, mBezierEnd2)

        mBezierVertex1.x = (mBezierStart1.x + 2 * mBezierControl1.x + mBezierEnd1.x) / 4
        mBezierVertex1.y = (2 * mBezierControl1.y + mBezierStart1.y + mBezierEnd1.y) / 4
        mBezierVertex2.x = (mBezierStart2.x + 2 * mBezierControl2.x + mBezierEnd2.x) / 4
        mBezierVertex2.y = (2 * mBezierControl2.y + mBezierStart2.y + mBezierEnd2.y) / 4
    }

    private fun getCross(
        p1x: Float, p1y: Float,
        p2: PointF, p3: PointF, p4: PointF,
        out: PointF
    ) {
        val a1 = (p2.y - p1y) / (p2.x - p1x)
        val b1 = (p1x * p2.y - p2.x * p1y) / (p1x - p2.x)
        val a2 = (p4.y - p3.y) / (p4.x - p3.x)
        val b2 = (p3.x * p4.y - p4.x * p3.y) / (p3.x - p4.x)
        out.x = (b2 - b1) / (a1 - a2)
        out.y = a1 * out.x + b1
    }
}
