package reactivecircus.blueprint.testing.assertion

import android.app.Activity
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.common.truth.Truth.assertThat
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import reactivecircus.blueprint.testing.RobotAssertions
import reactivecircus.blueprint.testing.currentActivity

/**
 * Check if the bottom navigation view associated with [bottomNavigationViewResId]
 * has [selectedItemResId] as the selected item.
 */
public fun RobotAssertions.bottomNavigationViewItemSelected(
    @IdRes bottomNavigationViewResId: Int,
    @IdRes selectedItemResId: Int
) {
    Espresso.onView(ViewMatchers.withId(bottomNavigationViewResId))
        .check(ViewAssertions.matches(hasSelectedNavigationItem(selectedItemResId)))
}

/**
 * Check if no activity is currently displayed
 */
public fun RobotAssertions.noActivityDisplayed() {
    ActivityNotDisplayedAssertion(currentActivity())
}

/**
 * Check if the activity of type [A] has been launched.
 */
public inline fun <reified A : Activity> RobotAssertions.activityLaunched() {
    Intents.intended(IntentMatchers.hasComponent(A::class.java.name))
}

/**
 * Check if the fragment of type [F] with [tag] is displayed.
 */
public inline fun <reified F : Fragment> RobotAssertions.fragmentDisplayed(tag: String) {
    val fragment = (currentActivity() as FragmentActivity)
        .supportFragmentManager.findFragmentByTag(tag)
    assertThat(fragment != null && fragment.isVisible && fragment is F)
        .isTrue()
}

/**
 * Check if the fragment of type [F]
 * with a navigation host associated with [navHostViewId] is displayed.
 */
public inline fun <reified F : Fragment> RobotAssertions.fragmentDisplayed(@IdRes navHostViewId: Int) {
    val fragment = (currentActivity() as? FragmentActivity)?.supportFragmentManager
        ?.findFragmentById(navHostViewId)?.childFragmentManager?.primaryNavigationFragment

    assertThat(fragment != null && fragment.isVisible && fragment is F)
        .isTrue()
}

/**
 * Returns a matcher that matches a [BottomNavigationView] with the item associated with [itemResId] selected.
 */
private fun hasSelectedNavigationItem(@IdRes itemResId: Int): Matcher<View> {
    return object : BoundedMatcher<View, BottomNavigationView>(BottomNavigationView::class.java) {
        private val checkedIds = hashSetOf<Int>()
        private var itemFound = false
        private var triedMatching = false

        override fun matchesSafely(bottomNavigationView: BottomNavigationView): Boolean {
            triedMatching = true
            with(bottomNavigationView.menu) {
                for (index in 0 until size()) {
                    val menuItem = getItem(index)
                    if (menuItem.isChecked) {
                        checkedIds.add(menuItem.itemId)
                    }
                    if (menuItem.itemId == itemResId) {
                        itemFound = true
                    }
                }
            }
            return checkedIds.contains(itemResId)
        }

        override fun describeTo(description: Description) {
            if (!triedMatching) {
                description.appendText("BottomNavigationView")
                return
            }

            description.appendText("BottomNavigationView to have a checked item with id=")
            description.appendValue(itemResId)
            if (itemFound) {
                description.appendText(", but selection was=")
                description.appendValue(checkedIds)
            } else {
                description.appendText(", but it doesn't have an item with such id")
            }
        }
    }
}

/**
 * A view assertion that checks if no activity is currently displayed.
 */
private class ActivityNotDisplayedAssertion(private val activity: Activity?) : ViewAssertion {

    override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) {
            throw noViewFoundException
        }

        activity?.run {
            MatcherAssert.assertThat(isFinishing, CoreMatchers.equalTo(true))
        }
    }
}
