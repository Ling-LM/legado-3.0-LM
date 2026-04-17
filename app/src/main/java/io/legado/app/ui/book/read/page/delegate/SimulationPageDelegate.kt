package io.legado.app.ui.book.read.page.delegate

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Region
import android.graphics.Shader
import android.os.Build
import android.view.MotionEvent
import io.legado.app.ui.book.read.page.ReadView
import io.legado.app.ui.book.read.page.entities.PageDirection
import io.legado.app.utils.screenshot
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin

@Suppress("DEPRECATION")
class SimulationPageDelegate(readView: ReadView) : HorizontalPageDelegate(readView) {

    companion object {
        private const val SHADOW_WIDTH = 25f
        private const val SQRT2 = 1.414f
        private const val SHADOW_DIAGONAL = SHADOW_WIDTH * SQRT2
    }

    private var mTouchX = 0.1f
    private var mTouchY = 0.1f

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

    private val mFolderShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val mBackShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val mFrontShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val mPaint: Paint = Paint().apply { style = Paint.Style.FILL }

    private var curBitmap: Bitmap? = null
    private var prevBitmap: Bitmap? = null
    private var nextBitmap: Bitmap? = null
    private var canvas: Canvas = Canvas()

    init {
        mFolderShadowPaint.shader = LinearGradient(
            0f, 0f, 1f, 0f,
            intArrayOf(0x333333, -0x4fcccccd),
            null,
            Shader.TileMode.CLAMP
        )
        mBackShadowPaint.shader = LinearGradient(
            0f, 0f, 1f, 0f,
            intArrayOf(-0xeeeeef, 0x111111),
            null,
            Shader.TileMode.CLAMP
        )
        mFrontShadowPaint.shader = LinearGradient(
            0f, 0f, 1f, 0f,
            intArrayOf(-0x7feeeeef, 0x111111),
            null,
            Shader.TileMode.CLAMP
        )
    }

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
                if ((startY > viewHeight / 3 && startY < viewHeight * 2 / 3)
                    || mDirection == PageDirection.PREV
                ) {
                    readView.touchY = viewHeight.toFloat()
                }

                if (startY > viewHeight / 3 && startY < viewHeight / 2
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
        var dx: Float
        val dy: Float
        if (isCancel) {
            dx = if (mCornerX > 0 && mDirection == PageDirection.NEXT) {
                (viewWidth - touchX)
            } else {
                -touchX
            }
            if (mDirection != PageDirection.NEXT) {
                dx = -(viewWidth + touchX)
            }
            dy = if (mCornerY > 0) {
                (viewHeight - touchY)
            } else {
                -touchY
            }
        } else {
            dx = if (mCornerX > 0 && mDirection == PageDirection.NEXT) {
                -(viewWidth + touchX)
            } else {
                viewWidth - touchX
            }
            dy = if (mCornerY > 0) {
                (viewHeight - touchY)
            } else {
                (1 - touchY)
            }
        }
        val distance = hypot(dx.toDouble(), dy.toDouble()).toFloat()
        val duration = (animationSpeed * distance / mMaxLength).coerceIn(200f, 400f).toLong()
        scroller.startScroll(
            touchX.toInt(), touchY.toInt(),
            dx.toInt(), dy.toInt(),
            duration.toInt()
        )
        isRunning = true
        isStarted = true
        readView.invalidate()
    }

    override fun onAnimStop() {
        if (!isCancel) {
            readView.fillPage(mDirection)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (!isRunning) return
        when (mDirection) {
            PageDirection.NEXT -> {
                calcPoints()
                drawCurrentPageArea(canvas, curBitmap)
                drawNextPageAreaAndShadow(canvas, nextBitmap)
                drawCurrentPageShadow(canvas)
                drawCurrentBackArea(canvas, curBitmap)
            }

            PageDirection.PREV -> {
                calcPoints()
                drawCurrentPageArea(canvas, prevBitmap)
                drawNextPageAreaAndShadow(canvas, curBitmap)
                drawCurrentPageShadow(canvas)
                drawCurrentBackArea(canvas, prevBitmap)
            }

            else -> return
        }
    }

    private fun drawCurrentBackArea(
        canvas: Canvas,
        bitmap: Bitmap?
    ) {
        bitmap ?: return
        val i = ((mBezierStart1.x + mBezierControl1.x) / 2).toInt()
        val f1 = abs(i - mBezierControl1.x)
        val i1 = ((mBezierStart2.y + mBezierControl2.y) / 2).toInt()
        val f2 = abs(i1 - mBezierControl2.y)
        val f3 = min(f1, f2)
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

            canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y)
            val left: Float
            val right: Float
            if (mIsRtOrLb) {
                left = mBezierStart1.x - 1
                right = mBezierStart1.x + f3 + 1
            } else {
                left = mBezierStart1.x - f3 - 1
                right = mBezierStart1.x + 1
            }
            val shaderWidth = right - left
            if (shaderWidth > 0) {
                val gradientX0: Float
                val gradientX1: Float
                if (mIsRtOrLb) {
                    gradientX0 = left
                    gradientX1 = right
                } else {
                    gradientX0 = right
                    gradientX1 = left
                }
                mFolderShadowPaint.shader = LinearGradient(
                    gradientX0, 0f, gradientX1, 0f,
                    intArrayOf(0x333333, -0x4fcccccd),
                    null,
                    Shader.TileMode.CLAMP
                )
                canvas.drawRect(
                    left, mBezierStart1.y,
                    right, mBezierStart1.y + mMaxLength,
                    mFolderShadowPaint
                )
            }
        } finally {
            canvas.restore()
        }
    }

    private fun drawCurrentPageShadow(canvas: Canvas) {
        val degree: Double = if (mIsRtOrLb) {
            Math.PI / 4 - atan2(mBezierControl1.y - mTouchY, mTouchX - mBezierControl1.x)
        } else {
            Math.PI / 4 - atan2(mTouchY - mBezierControl1.y, mTouchX - mBezierControl1.x)
        }
        val d1 = SHADOW_DIAGONAL * cos(degree)
        val d2 = SHADOW_DIAGONAL * sin(degree)
        val x = (mTouchX + d1).toFloat()
        val y: Float = if (mIsRtOrLb) {
            (mTouchY + d2).toFloat()
        } else {
            (mTouchY - d2).toFloat()
        }

        mPath1.reset()
        mPath1.moveTo(x, y)
        mPath1.lineTo(mTouchX, mTouchY)
        mPath1.lineTo(mBezierControl1.x, mBezierControl1.y)
        mPath1.lineTo(mBezierStart1.x, mBezierStart1.y)
        mPath1.close()

        canvas.save()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                canvas.clipOutPath(mPath0)
            } else {
                canvas.clipPath(mPath0, Region.Op.XOR)
            }
            canvas.clipPath(mPath1, Region.Op.INTERSECT)

            var leftX: Float
            var rightX: Float
            if (mIsRtOrLb) {
                leftX = mBezierControl1.x
                rightX = mBezierControl1.x + SHADOW_WIDTH
            } else {
                leftX = mBezierControl1.x - SHADOW_WIDTH
                rightX = mBezierControl1.x + 1
            }
            val rotateDegrees = Math.toDegrees(
                atan2(mTouchX - mBezierControl1.x, mBezierControl1.y - mTouchY).toDouble()
            ).toFloat()
            canvas.rotate(rotateDegrees, mBezierControl1.x, mBezierControl1.y)

            val shaderWidth = rightX - leftX
            if (shaderWidth > 0) {
                mFrontShadowPaint.shader = LinearGradient(
                    leftX, 0f, rightX, 0f,
                    intArrayOf(-0x7feeeeef, 0x111111),
                    null,
                    Shader.TileMode.CLAMP
                )
                canvas.drawRect(
                    leftX, mBezierControl1.y - mMaxLength,
                    rightX, mBezierControl1.y,
                    mFrontShadowPaint
                )
            }
        } finally {
            canvas.restore()
        }

        mPath1.reset()
        mPath1.moveTo(x, y)
        mPath1.lineTo(mTouchX, mTouchY)
        mPath1.lineTo(mBezierControl2.x, mBezierControl2.y)
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y)
        mPath1.close()

        canvas.save()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                canvas.clipOutPath(mPath0)
            } else {
                canvas.clipPath(mPath0, Region.Op.XOR)
            }
            canvas.clipPath(mPath1)

            var leftX: Float
            var rightX: Float
            if (mIsRtOrLb) {
                leftX = mBezierControl2.y
                rightX = mBezierControl2.y + SHADOW_WIDTH
            } else {
                leftX = mBezierControl2.y - SHADOW_WIDTH
                rightX = mBezierControl2.y + 1
            }
            val rotateDegrees = Math.toDegrees(
                atan2(mBezierControl2.y - mTouchY, mBezierControl2.x - mTouchX).toDouble()
            ).toFloat()
            canvas.rotate(rotateDegrees, mBezierControl2.x, mBezierControl2.y)
            val temp =
                if (mBezierControl2.y < 0) (mBezierControl2.y - viewHeight).toDouble()
                else mBezierControl2.y.toDouble()
            val hmg = hypot(mBezierControl2.x.toDouble(), temp)
            val leftBound: Float
            val rightBound: Float
            if (hmg > mMaxLength) {
                leftBound = mBezierControl2.x - SHADOW_WIDTH - hmg.toFloat()
                rightBound = mBezierControl2.x + mMaxLength - hmg.toFloat()
            } else {
                leftBound = mBezierControl2.x - mMaxLength
                rightBound = mBezierControl2.x
            }
            val shaderHeight = rightX - leftX
            if (shaderHeight > 0) {
                val gradientY0: Float
                val gradientY1: Float
                if (mIsRtOrLb) {
                    gradientY0 = leftX
                    gradientY1 = rightX
                } else {
                    gradientY0 = rightX
                    gradientY1 = leftX
                }
                mFrontShadowPaint.shader = LinearGradient(
                    0f, gradientY0, 0f, gradientY1,
                    intArrayOf(-0x7feeeeef, 0x111111),
                    null,
                    Shader.TileMode.CLAMP
                )
                canvas.drawRect(
                    leftBound, leftX,
                    rightBound, rightX,
                    mFrontShadowPaint
                )
            }
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

        canvas.save()
        try {
            canvas.clipPath(mPath0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                canvas.clipPath(mPath1)
            } else {
                canvas.clipPath(mPath1, Region.Op.INTERSECT)
            }
            canvas.drawBitmap(bitmap, 0f, 0f, null)

            canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y)
            val leftX: Float
            val rightX: Float
            if (mIsRtOrLb) {
                leftX = mBezierStart1.x
                rightX = mBezierStart1.x + mTouchToCornerDis / 4
            } else {
                leftX = mBezierStart1.x - mTouchToCornerDis / 4
                rightX = mBezierStart1.x
            }
            val shaderWidth = rightX - leftX
            if (shaderWidth > 0) {
                val gradientX0: Float
                val gradientX1: Float
                if (mIsRtOrLb) {
                    gradientX0 = leftX
                    gradientX1 = rightX
                } else {
                    gradientX0 = rightX
                    gradientX1 = leftX
                }
                mBackShadowPaint.shader = LinearGradient(
                    gradientX0, 0f, gradientX1, 0f,
                    intArrayOf(-0xeeeeef, 0x111111),
                    null,
                    Shader.TileMode.CLAMP
                )
                canvas.drawRect(
                    leftX, mBezierStart1.y,
                    rightX, mMaxLength + mBezierStart1.y,
                    mBackShadowPaint
                )
            }
        } finally {
            canvas.restore()
        }
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
        mCornerX = if (x <= viewWidth / 2) 0 else viewWidth
        mCornerY = if (y <= viewHeight / 2) 0 else viewHeight
        mIsRtOrLb = (mCornerX == 0 && mCornerY == viewHeight)
                || (mCornerY == 0 && mCornerX == viewWidth)
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
