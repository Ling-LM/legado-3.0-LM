package io.legado.app.ui.book.read.page.delegate

import android.view.animation.Interpolator
import kotlin.math.sin

class EaseOutSineInterpolator : Interpolator {
    override fun getInterpolation(input: Float): Float {
        return sin((input * Math.PI) / 2).toFloat()
    }
}
