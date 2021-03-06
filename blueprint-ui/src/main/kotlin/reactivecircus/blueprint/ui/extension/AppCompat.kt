package reactivecircus.blueprint.ui.extension

import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import java.util.concurrent.Executor

/**
 * Sets the precomputed text future on the [AppCompatTextView].
 *
 * @param charSequence the text to be displayed.
 *  Precomputed text future is not set if null is passed in.
 * @param executor the executor to be used for processing the text layout.
 *  Default single threaded pool will be used if null is passed in.
 */
public fun AppCompatTextView.setPrecomputedTextFuture(
    charSequence: CharSequence?,
    executor: Executor? = null,
) {
    if (charSequence == null) return
    setTextFuture(
        PrecomputedTextCompat.getTextFuture(
            charSequence,
            TextViewCompat.getTextMetricsParams(this),
            executor
        )
    )
}
