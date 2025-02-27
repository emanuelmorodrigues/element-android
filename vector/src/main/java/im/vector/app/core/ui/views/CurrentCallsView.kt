/*
 * Copyright 2020-2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package im.vector.app.core.ui.views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import im.vector.app.R
import im.vector.app.databinding.ViewCurrentCallsBinding
import im.vector.app.features.call.webrtc.WebRtcCall
import im.vector.app.features.themes.ThemeUtils
import im.vector.lib.strings.CommonPlurals
import im.vector.lib.strings.CommonStrings
import org.matrix.android.sdk.api.session.call.CallState

class CurrentCallsView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    interface Callback {
        fun onTapToReturnToCall()
    }

    val views: ViewCurrentCallsBinding
    var callback: Callback? = null

    init {
        inflate(context, R.layout.view_current_calls, this)
        views = ViewCurrentCallsBinding.bind(this)
        setBackgroundColor(ThemeUtils.getColor(context, com.google.android.material.R.attr.colorPrimary))
        val outValue = TypedValue().also {
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, it, true)
        }
        foreground = AppCompatResources.getDrawable(context, outValue.resourceId)
        setOnClickListener { callback?.onTapToReturnToCall() }
    }

    fun render(calls: List<WebRtcCall>, formattedDuration: String) {
        val tapToReturnFormat = if (calls.size == 1) {
            val firstCall = calls.first()
            when (firstCall.mxCall.state) {
                is CallState.Idle,
                is CallState.CreateOffer,
                is CallState.LocalRinging,
                is CallState.Dialing -> {
                    resources.getString(CommonStrings.call_ringing)
                }
                is CallState.Answering -> {
                    resources.getString(CommonStrings.call_connecting)
                }
                else -> {
                    resources.getString(CommonStrings.call_one_active, formattedDuration)
                }
            }
        } else {
            resources.getQuantityString(CommonPlurals.call_active_status, calls.size, calls.size)
        }
        views.currentCallsInfo.text = resources.getString(CommonStrings.call_tap_to_return, tapToReturnFormat)
    }
}
