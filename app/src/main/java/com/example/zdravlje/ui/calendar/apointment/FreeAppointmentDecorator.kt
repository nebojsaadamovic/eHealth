import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import com.example.zdravlje.ui.base.BaseActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.nites.ezdravljeapp.R

class FreeAppointmentDecorator(
    private val context: Context,
    private val availableDates: List<CalendarDay>
) : BaseActivity(), DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return availableDates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setColor(ContextCompat.getColor(context, R.color.base_blue_color))
        drawable.setSize(70, 70)
        view?.setBackgroundDrawable(drawable)
    }
}

//New class
class FirstAvailableDateDecorator(
    private val context: Context,
    private val firstAvailableDate: CalendarDay?
) : BaseActivity(), DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day == firstAvailableDate
    }

    override fun decorate(view: DayViewFacade?) {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setColor(ContextCompat.getColor(context, R.color.orange_FFD48029))
        view?.addSpan(ForegroundColorSpan(Color.WHITE))
        view?.addSpan(StyleSpan(Typeface.BOLD))
        drawable.setSize(70, 70)
        view?.setBackgroundDrawable(drawable)
    }
}


//New class
class SelectedDateDecorator(private val context: Context) : DayViewDecorator {
    private var selectedDate: CalendarDay? = null

    fun setSelectedDate(date: CalendarDay) {
        selectedDate = date
    }

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day == selectedDate
    }

    override fun decorate(view: DayViewFacade?) {
        if (selectedDate != null) {
            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.OVAL
            drawable.setColor(ContextCompat.getColor(context, R.color.red_FFE50915))
            drawable.setSize(70, 70)
            view?.setBackgroundDrawable(drawable)
        }
    }
}