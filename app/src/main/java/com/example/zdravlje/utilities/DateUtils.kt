package com.example.zdravlje.utilities

import android.app.DatePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import android.util.Log
import android.widget.TextView
import com.nites.ezdravljeapp.R
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val dateFormatWithoutHours = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


    fun showDatePickerDialog(
        context: Context, textView: TextView,
        onDateSelected: (String) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val languageCode = sharedPreferences.getString("selected_language", "sr_Latn") ?: "sr_Latn"

        setLocale(context, languageCode)

        val datePickerDialog = DatePickerDialog(
            context,
            R.style.DialogTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                textView.text = selectedDate
                onDateSelected(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun setLocale(context: Context, languageCode: String) {
        val locale = when (languageCode) {
            "sr-rRS" -> Locale.Builder().setLanguage("sr").setScript("Latn").setRegion("RS")
                .build() // Latinica
            "sr" -> Locale.Builder().setLanguage("sr").setScript("Cyrl").setRegion("RS")
                .build()     // Ćirilica
            "en" -> Locale("en")                                                                     // Engleski
            else -> Locale.Builder().setLanguage("sr").setScript("Latn").setRegion("RS").build()
        }

        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }

    fun getCurrentDateTimeFromMidnight(): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val date = calendar.time
        return dateFormat.format(date)
    }

    fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val date = calendar.time
        return dateFormat.format(date)
    }

    fun getSixMonthsLaterDateTime(): String {
        val calendar = Calendar.getInstance()

        calendar.add(Calendar.MONTH, 6)
        val date = calendar.time
        return dateFormat.format(date)
    }

    fun getSixMonthsBeforeDateTime(): String {
        val calendar = Calendar.getInstance()

        calendar.add(Calendar.MONTH, -6)
        val date = calendar.time
        return dateFormat.format(date)
    }

    fun compareWithCurrentDate(date: String): Boolean {
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        val formattedCurrentDate = dateFormatWithoutHours.format(currentDate)
        val isSame = formattedCurrentDate == date
        return isSame
    }

    fun checkIsFirstNextEvent(dateToCompare: String): Boolean {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.time
        val currentTimeString = dateFormat.format(currentTime)
        return try {
            val isFirstEventAfterCurrentTime =
                dateFormat.parse(dateToCompare)?.after(dateFormat.parse(currentTimeString))
            isFirstEventAfterCurrentTime ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun getHoursFromDate(dateString: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        return try {
            val date = dateFormat.parse(dateString)
            val formattedTime = timeFormat.format(date)
            formattedTime
        } catch (e: Exception) {
            Log.e("TimeDebug", "Failed to parse date: $dateString", e)
            ""
        }
    }


    fun getDateWithWeekName(datum: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val datumObjekat = sdf.parse(datum) ?: return ""

        val format = SimpleDateFormat("EEEE, dd.MM.yyyy.", Locale.getDefault())
        var formatiranDatum = format.format(datumObjekat)

        // Prvo slovo dana u veliko
        formatiranDatum = formatiranDatum.replaceFirstChar { it.uppercase() }

        if (Locale.getDefault().language != Locale.ENGLISH.language) {
            // Konverzija na latinicu
            formatiranDatum = formatiranDatum
                .replace("Mon", "Ponedeljak")
                .replace("Tue", "Utorak")
                .replace("Wed", "Sreda")
                .replace("Thu", "Cetvrtak")
                .replace("Fri", "Petak")
                .replace("Sat", "Subota")
                .replace("Sun", "Nedelja")
        }

        return formatiranDatum
    }

    fun getFormattedDate(datum: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val datumObjekat = sdf.parse(datum) ?: return ""

        val format = SimpleDateFormat("dd.MM.yyyy", Locale("sr", "RS"))
        return format.format(datumObjekat)
    }

    fun getFormattedDateLineUp(datum: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val datumObjekat = sdf.parse(datum) ?: return ""

        val format = SimpleDateFormat("dd/MM/yyyy", Locale("sr", "RS"))
        return format.format(datumObjekat)
    }

    fun convertyyyyMMddHorLineToddMyyyyVerLine(datum: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val datumObjekat = sdf.parse(datum) ?: return ""

        val format = SimpleDateFormat("dd/M/yyyy", Locale("sr", "RS"))
        return format.format(datumObjekat)
    }

    fun formatTimestampToHoursAndMinutes(timestamp: Int): String {
        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(timestamp.toLong()),
            ZoneId.systemDefault()
        )

        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return dateTime.format(formatter)
    }

    fun formatTimestampToDate(timestamp: Int): String {
        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(timestamp.toLong()),
            ZoneId.systemDefault()
        )

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return dateTime.format(formatter)
    }

    fun convertMonthCyrillicToLatin(monthCyrillic: String): String {
        val monthsCyrillicToLatin = mapOf(
            "јануар" to "Januar",
            "фебруар" to "Februar",
            "март" to "Mart",
            "април" to "April",
            "мај" to "Maj",
            "јун" to "Jun",
            "јул" to "Jul",
            "август" to "Avgust",
            "септембар" to "Septembar",
            "октобар" to "Oktobar",
            "новембар" to "Novembar",
            "децембар" to "Decembar"
        )

        return monthsCyrillicToLatin[monthCyrillic] ?: "Непознат месец"
    }

    fun formatTimestampToYYYYMMddHHmmMiddleLine(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun formatTimestampToDotddMMYYYYHHmm(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun formatTimestampToDotddMMYYYY(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }


    fun convertYYYYMMDDHHssToDotDDMMYYYY(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        val outputFormat = SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault())
        return outputFormat.format(date)
    }

    fun convertYYYYMMDDHHssToHorLineDDMMYYYY(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        val outputFormat = SimpleDateFormat("dd/MM/yyyy.", Locale.getDefault())
        return outputFormat.format(date)
    }

    fun convertYYYYMMDDHHssToDotTDDMMYYYY(input: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val date = parser.parse(input)
            formatter.format(date!!)
        } catch (e: Exception) {
            "" // ili vrati input ako želiš: input
        }
    }

    fun convertYYYYDDMMHHssToDotDDMMYYYY(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-dd-MM HH:mm", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return outputFormat.format(date)
    }


    fun convertYYYYDDMMHHssToDotDDMMYYYYHHss(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-dd-MM HH:mm", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return outputFormat.format(date)
    }

    fun convertDotddMMyyyToHorLineyyyyMMdd(inputDate: String): String {
        val inputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return outputFormat.format(date)
    }

    fun convertDotddMMyyyToVerLineddMyyyy(inputDate: String): String {
        val inputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        val outputFormat = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())
        return outputFormat.format(date)
    }

    fun getCurrentTimeyyyyMMddHHmm(): String {
        val currentDate = Date()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(currentDate)
    }

    fun getCurrentTimeHorLineddMyyyy(): String {
        val currentDate = Date()
        val sdf = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())
        return sdf.format(currentDate)
    }

    fun convertTimestampToDateWithMiddleLine(unixTimestamp: Long): String {
        val date = Date(unixTimestamp * 1000)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(date)
    }

    fun convertTimestampToDateDotddMMyyyy(timestamp: Long): String {
        if (timestamp == 0L) return "N/A"
        val date = Date(timestamp * 1000)
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    fun convertlineddMMyyyyToDotddMMyyyy(dateString: String): String {
        val inputFormatter = DateTimeFormatterBuilder()
            .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, java.time.format.SignStyle.NOT_NEGATIVE)
            .appendLiteral('/')
            .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, java.time.format.SignStyle.NOT_NEGATIVE)
            .appendLiteral('/')
            .appendValue(ChronoField.YEAR, 4)
            .toFormatter(Locale.getDefault())

        val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())

        val date = LocalDate.parse(dateString, inputFormatter)
        return date.format(outputFormatter)
    }

    fun convertlineddMMyyyyToHorLineddMMyyyy(dateString: String): String {
        val inputFormatter = DateTimeFormatterBuilder()
            .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, java.time.format.SignStyle.NOT_NEGATIVE)
            .appendLiteral('/')
            .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, java.time.format.SignStyle.NOT_NEGATIVE)
            .appendLiteral('/')
            .appendValue(ChronoField.YEAR, 4)
            .toFormatter(Locale.getDefault())

        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

        val date = LocalDate.parse(dateString, inputFormatter)
        return date.format(outputFormatter)
    }

    fun convertVerLineddMyyyyToHorLineyyyyMMdd(inputDate: String): String {
        val inputFormatter = DateTimeFormatterBuilder()
            .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
            .appendLiteral('/')
            .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NOT_NEGATIVE)
            .appendLiteral('/')
            .appendValue(ChronoField.YEAR, 4)
            .toFormatter(Locale.getDefault())

        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

        val date = LocalDate.parse(inputDate, inputFormatter)
        return date.format(outputFormatter)
    }


    fun convertDotddMyyyyToHorLineddMMyyyyHHmm(inputDate: String): String {
        val inputFormatter = DateTimeFormatterBuilder()
            .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
            .appendLiteral('.')
            .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NOT_NEGATIVE)
            .appendLiteral('.')
            .appendValue(ChronoField.YEAR, 4)
            .toFormatter(Locale.getDefault())

        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.getDefault())

        val date = LocalDate.parse(inputDate, inputFormatter)
        return date.format(outputFormatter)
    }

    fun convertDotddMyyyyToHorLineddMMyyyy(inputDate: String): String {
        val inputFormatter = DateTimeFormatterBuilder()
            .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
            .appendLiteral('.')
            .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NOT_NEGATIVE)
            .appendLiteral('.')
            .appendValue(ChronoField.YEAR, 4)
            .toFormatter(Locale.getDefault())

        val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-YYYY", Locale.getDefault())

        val date = LocalDate.parse(inputDate, inputFormatter)
        return date.format(outputFormatter)
    }

    fun formatDate_HorizLineddMyyyy_DotddMMyyyy(dateString: String): String {
        val inputFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return outputFormat.format(date!!)
    }

    fun formatDate_VerLineyyyyMMdd_DotddMMyyyy(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return outputFormat.format(date!!)
    }

    fun stringToDate(dateString: String, format: String): Date? {
        return try {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun dateToString(date: Date, format: String): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(date)
    }

    fun formatDate(input: String): String? {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.getDefault())
            val date = inputFormat.parse(input)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}