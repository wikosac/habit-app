package com.dicoding.habitapp.ui.list

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Test
import com.dicoding.habitapp.R

// Write UI test to validate when user tap Add Habit (+), the AddHabitActivity displayed
class HabitActivityTest {
    @Test
    fun testAddHabit() {
        ActivityScenario.launch(HabitListActivity::class.java)
        onView(
            withId(R.id.fab))
            .perform(click()
        )
        onView(
            withId(R.id.activity_add_habit))
            .check(matches(isDisplayed())
        )
    }
}