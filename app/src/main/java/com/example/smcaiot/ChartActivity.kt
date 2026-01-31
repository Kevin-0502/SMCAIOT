package com.example.smcaiot

import android.Manifest
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smaciot.sensoresDataa.Estanques
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.EntryXComparator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale

class ChartActivity : AppCompatActivity() {

    private val oxigenEntries = ArrayList<Entry>()
    private val phEntries = ArrayList<Entry>()
    private val temperatureEntries = ArrayList<Entry>()

    private lateinit var nombreTextView: TextView
    private lateinit var ubicacionTextView: TextView
    private lateinit var chose_opc: TextView

    private lateinit var lineChartOxigen: LineChart
    private lateinit var lineChartPH: LineChart
    private lateinit var lineChartTemperature: LineChart

    private lateinit var fechaSelectorButton: Button

    private lateinit var tableLayoutOxigen: TableLayout
    private lateinit var tableLayoutPH: TableLayout
    private lateinit var tableLayoutTemperature: TableLayout
    private lateinit var tableLayoutResumen: TableLayout

    private var fechaInicio: Date? = null
    private var fechaFin: Date? = null

    private lateinit var estanque: Estanques
    private var opc: Int = 0

    private val NOTIFICATION_CHANNEL_ID = "alerta_rangos_channel"
    private val NOTIFICATION_ID = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chart)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Views binding
        nombreTextView = findViewById(R.id.nombreTextView)
        ubicacionTextView = findViewById(R.id.ubicacionTextView)
        chose_opc = findViewById(R.id.opc)

        lineChartOxigen = findViewById(R.id.lineChartOxigen)
        lineChartPH = findViewById(R.id.lineChartPH)
        lineChartTemperature = findViewById(R.id.lineChartTemperature)

        fechaSelectorButton = findViewById(R.id.fechaSelectorButton)

        tableLayoutOxigen = findViewById(R.id.tableLayoutOxigen)
        tableLayoutPH = findViewById(R.id.tableLayoutPH)
        tableLayoutTemperature = findViewById(R.id.tableLayoutTemperature)
        tableLayoutResumen = findViewById(R.id.tableLayoutResumen)

        estanque = intent.getSerializableExtra("ESTANQUE") as? Estanques ?: return
        opc = intent.getIntExtra("OPCION", 0)

        nombreTextView.text = estanque.nombre
        ubicacionTextView.text = "Ubicación: " + estanque.ubicacion

        configurarOpcionYFechas(opc)
        configurarSelectorDeFecha()

        actualizarGrafica()
    }

    private fun configurarOpcionYFechas(opc: Int) {
        val calendarInicio = Calendar.getInstance()
        val calendarFin = Calendar.getInstance()

        when (opc) {
            1 -> {
                chose_opc.text = "Grafica diaria"
                calendarInicio.set(Calendar.HOUR_OF_DAY, 0)
                calendarInicio.set(Calendar.MINUTE, 0)
                calendarInicio.set(Calendar.SECOND, 0)
                calendarInicio.set(Calendar.MILLISECOND, 0)

                calendarFin.set(Calendar.HOUR_OF_DAY, 23)
                calendarFin.set(Calendar.MINUTE, 59)
                calendarFin.set(Calendar.SECOND, 59)
                calendarFin.set(Calendar.MILLISECOND, 999)

                tableLayoutOxigen.visibility = View.GONE
                tableLayoutPH.visibility = View.GONE
                tableLayoutTemperature.visibility = View.GONE

                lineChartOxigen.visibility = View.VISIBLE
                lineChartPH.visibility = View.VISIBLE
                lineChartTemperature.visibility = View.VISIBLE
            }
            2 -> {
                chose_opc.text = "Grafica mensual"
                calendarInicio.set(Calendar.DAY_OF_MONTH, 1)
                calendarInicio.set(Calendar.HOUR_OF_DAY, 0)
                calendarInicio.set(Calendar.MINUTE, 0)
                calendarInicio.set(Calendar.SECOND, 0)
                calendarInicio.set(Calendar.MILLISECOND, 0)

                calendarFin.set(Calendar.DAY_OF_MONTH, calendarFin.getActualMaximum(Calendar.DAY_OF_MONTH))
                calendarFin.set(Calendar.HOUR_OF_DAY, 23)
                calendarFin.set(Calendar.MINUTE, 59)
                calendarFin.set(Calendar.SECOND, 59)
                calendarFin.set(Calendar.MILLISECOND, 999)

                tableLayoutOxigen.visibility = View.GONE
                tableLayoutPH.visibility = View.GONE
                tableLayoutTemperature.visibility = View.GONE

                lineChartOxigen.visibility = View.VISIBLE
                lineChartPH.visibility = View.VISIBLE
                lineChartTemperature.visibility = View.VISIBLE
            }
            3 -> {
                chose_opc.text = "Reporte diario"
                calendarInicio.set(Calendar.HOUR_OF_DAY, 0)
                calendarInicio.set(Calendar.MINUTE, 0)
                calendarInicio.set(Calendar.SECOND, 0)
                calendarInicio.set(Calendar.MILLISECOND, 0)

                calendarFin.set(Calendar.HOUR_OF_DAY, 23)
                calendarFin.set(Calendar.MINUTE, 59)
                calendarFin.set(Calendar.SECOND, 59)
                calendarFin.set(Calendar.MILLISECOND, 999)

                lineChartOxigen.visibility = View.GONE
                lineChartPH.visibility = View.GONE
                lineChartTemperature.visibility = View.GONE

                tableLayoutOxigen.visibility = View.VISIBLE
                tableLayoutPH.visibility = View.VISIBLE
                tableLayoutTemperature.visibility = View.VISIBLE
            }
            4 -> {
                chose_opc.text = "Reporte mensual"
                calendarInicio.set(Calendar.DAY_OF_MONTH, 1)
                calendarInicio.set(Calendar.HOUR_OF_DAY, 0)
                calendarInicio.set(Calendar.MINUTE, 0)
                calendarInicio.set(Calendar.SECOND, 0)
                calendarInicio.set(Calendar.MILLISECOND, 0)

                calendarFin.set(Calendar.DAY_OF_MONTH, calendarFin.getActualMaximum(Calendar.DAY_OF_MONTH))
                calendarFin.set(Calendar.HOUR_OF_DAY, 23)
                calendarFin.set(Calendar.MINUTE, 59)
                calendarFin.set(Calendar.SECOND, 59)
                calendarFin.set(Calendar.MILLISECOND, 999)

                lineChartOxigen.visibility = View.GONE
                lineChartPH.visibility = View.GONE
                lineChartTemperature.visibility = View.GONE

                tableLayoutOxigen.visibility = View.VISIBLE
                tableLayoutPH.visibility = View.VISIBLE
                tableLayoutTemperature.visibility = View.VISIBLE
            }
            else -> {
                chose_opc.text = "Opción desconocida"
                calendarInicio.time = Date(0)
                calendarFin.time = Date(0)

                lineChartOxigen.visibility = View.GONE
                lineChartPH.visibility = View.GONE
                lineChartTemperature.visibility = View.GONE

                tableLayoutOxigen.visibility = View.GONE
                tableLayoutPH.visibility = View.GONE
                tableLayoutTemperature.visibility = View.GONE
            }
        }
        fechaInicio = calendarInicio.time
        fechaFin = calendarFin.time

        actualizarTextoBoton()
    }

    private fun configurarSelectorDeFecha() {
        fechaSelectorButton.setOnClickListener {
            val esDiario = opc == 1 || opc == 3
            val esMensual = opc == 2 || opc == 4

            if (esDiario) {
                mostrarDatePickerDia(fechaInicio) { fechaSeleccionada ->
                    val calIni = Calendar.getInstance().apply {
                        time = fechaSeleccionada
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val calFin = Calendar.getInstance().apply {
                        time = fechaSeleccionada
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        set(Calendar.MILLISECOND, 999)
                    }
                    fechaInicio = calIni.time
                    fechaFin = calFin.time
                    actualizarTextoBoton()
                    actualizarGrafica()
                }
            } else if (esMensual) {
                mostrarDatePickerMesAnyo(fechaInicio) { fechaInicioMes, fechaFinMes ->
                    fechaInicio = fechaInicioMes
                    fechaFin = fechaFinMes
                    actualizarTextoBoton()
                    actualizarGrafica()
                }
            }
        }
    }

    private fun actualizarTextoBoton() {
        val sdfDia = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val sdfMes = SimpleDateFormat("yyyy/MM", Locale.getDefault())
        val esDiario = opc == 1 || opc == 3

        fechaSelectorButton.text = if (esDiario) {
            fechaInicio?.let { sdfDia.format(it) } ?: "Seleccionar día"
        } else {
            fechaInicio?.let { sdfMes.format(it) } ?: "Seleccionar mes"
        }
    }

    private fun mostrarDatePickerDia(fechaInicial: Date?, onFechaSeleccionada: (Date) -> Unit) {
        val calendar = Calendar.getInstance().apply { if (fechaInicial != null) time = fechaInicial }
        val dpd = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            onFechaSeleccionada(cal.time)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        dpd.show()
    }

    private fun mostrarDatePickerMesAnyo(fechaInicial: Date?, onRangoSeleccionado: (Date, Date) -> Unit) {
        val calendar = Calendar.getInstance().apply { if (fechaInicial != null) time = fechaInicial }
        val dpd = DatePickerDialog(this, { _, year, month, _ ->
            val calInicio = Calendar.getInstance().apply {
                set(year, month, 1, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val calFin = Calendar.getInstance().apply {
                set(year, month, getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
                set(Calendar.MILLISECOND, 999)
            }
            onRangoSeleccionado(calInicio.time, calFin.time)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        try {
            val datePickerField = dpd.datePicker::class.java.getDeclaredField("mDaySpinner")
            datePickerField.isAccessible = true
            (datePickerField.get(dpd.datePicker) as? View)?.visibility = View.GONE
        } catch (_: Exception) {
            // Ignorar si no se puede ocultar
        }

        dpd.show()
    }

    private fun actualizarGrafica() {
        if (fechaInicio == null || fechaFin == null) {
            Log.w("ChartActivity", "Fechas no definidas")
            return
        }

        oxigenEntries.clear()
        phEntries.clear()
        temperatureEntries.clear()

        obtenerDatos(estanque.lecturas as? Map<String, Any> ?: return, fechaInicio!!, fechaFin!!)

        Collections.sort(oxigenEntries, EntryXComparator())
        Collections.sort(phEntries, EntryXComparator())
        Collections.sort(temperatureEntries, EntryXComparator())

        val dataSetOxygen = LineDataSet(oxigenEntries, "Oxígeno").apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
            valueTextSize = 16f
            setDrawCircles(true)
            circleRadius = 7f
            circleHoleRadius = 3.5f
            setCircleColor(Color.BLUE)
        }
        val dataSetPH = LineDataSet(phEntries, "PH").apply {
            color = Color.BLACK
            valueTextColor = Color.BLACK
            valueTextSize = 16f
            setDrawCircles(true)
            circleRadius = 7f
            circleHoleRadius = 3.5f
            setCircleColor(Color.BLACK)
        }
        val dataSetTemperature = LineDataSet(temperatureEntries, "Temperatura").apply {
            color = Color.RED
            valueTextColor = Color.BLACK
            valueTextSize = 16f
            setDrawCircles(true)
            circleRadius = 7f
            circleHoleRadius = 3.5f
            setCircleColor(Color.RED)
        }

        lineChartOxigen.data = LineData(dataSetOxygen)
        lineChartOxigen.invalidate()

        lineChartPH.data = LineData(dataSetPH)
        lineChartPH.invalidate()

        lineChartTemperature.data = LineData(dataSetTemperature)
        lineChartTemperature.invalidate()

        val xAxisFormatter = object : ValueFormatter() {
            private val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return sdf.format(Date(value.toLong()))
            }
        }

        lineChartOxigen.xAxis.apply {
            valueFormatter = xAxisFormatter
            granularity = 1f
            position = XAxis.XAxisPosition.BOTTOM
        }
        lineChartPH.xAxis.apply {
            valueFormatter = xAxisFormatter
            granularity = 1f
            position = XAxis.XAxisPosition.BOTTOM
        }
        lineChartTemperature.xAxis.apply {
            valueFormatter = xAxisFormatter
            granularity = 1f
            position = XAxis.XAxisPosition.BOTTOM
        }

        // Actualizar tablas debajo de cada gráfico
        actualizarTablaIndividual(oxigenEntries, tableLayoutOxigen, "Oxígeno")
        actualizarTablaIndividual(phEntries, tableLayoutPH, "PH")
        actualizarTablaIndividual(temperatureEntries, tableLayoutTemperature, "Temperatura")

        // Actualizar tabla resumen con estadísticas
        actualizarTablaResumen()

        // Verificar rangos y mostrar notificación si hay valores fuera de rango
        verificarRangosYMostrarNotificacion()
    }

    fun obtenerDatos(datos: Map<String, Any>, fechaHoraInicio: Date, fechaHoraFin: Date) {
        val datosMap = datos["datos"] as? Map<String, Map<String, Any>> ?: return
        val dateFormat = SimpleDateFormat("yyyy/M/dd HH:mm:ss", Locale.getDefault())

        for ((_, value) in datosMap) {
            val fecha = value["Fecha"] as? String ?: continue
            val hora = value["Hora"] as? String ?: continue
            val oxigeno = value["Oxigeno"] as? Double ?: continue
            val ph = value["ph"] as? Double ?: continue
            val temperatura = value["temperatura"] as? Double ?: continue

            try {
                val fechaHoraStr = "$fecha $hora"
                val fechaHora = dateFormat.parse(fechaHoraStr) ?: continue

                if (fechaHora >= fechaHoraInicio && fechaHora <= fechaHoraFin) {
                    oxigenEntries.add(Entry(fechaHora.time.toFloat(), oxigeno.toFloat()))
                    phEntries.add(Entry(fechaHora.time.toFloat(), ph.toFloat()))
                    temperatureEntries.add(Entry(fechaHora.time.toFloat(), temperatura.toFloat()))
                }
            } catch (e: Exception) {
                Log.e("ChartActivity", "Error parseando fecha y hora: ${e.message}")
            }
        }
    }

    private fun crearCelda(texto: String, bg_color: Int, txt_color: Int): TextView {
        val tv = TextView(this)
        tv.text = texto
        tv.setPadding(8, 8, 8, 8)
        tv.setTextColor(txt_color)
        tv.textSize = 16f

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(bg_color)
            setStroke(1, Color.BLACK) // borde negro de 1px
        }
        tv.background = drawable

        return tv
    }

    private fun actualizarTablaIndividual(entries: List<Entry>, table: TableLayout, titulo: String) {
        table.removeAllViews()

        val headerRow = TableRow(this)
        arrayOf("Fecha", "Hora", titulo).forEach {
            headerRow.addView(crearCelda(it, Color.BLUE, Color.WHITE))
        }
        table.addView(headerRow)

        val sdfFecha = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val sdfHora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        for (entry in entries) {
            val fecha = Date(entry.x.toLong())
            val row = TableRow(this)

            row.addView(crearCelda(sdfFecha.format(fecha), Color.WHITE, Color.BLACK))
            row.addView(crearCelda(sdfHora.format(fecha), Color.WHITE, Color.BLACK))
            row.addView(crearCelda(String.format(Locale.getDefault(), "%.2f", entry.y), Color.WHITE, Color.BLACK))

            table.addView(row)
        }
    }

    private fun actualizarTablaResumen() {
        tableLayoutResumen.removeAllViews()

        val headerRow = TableRow(this)
        arrayOf("Medida", "Promedio", "Máximo", "Mínimo").forEach {
            headerRow.addView(crearCelda(it, Color.BLACK, Color.WHITE))
        }
        tableLayoutResumen.addView(headerRow)

        fun calcStats(data: List<Entry>): Triple<Double, Double, Double> {
            if (data.isEmpty()) return Triple(Double.NaN, Double.NaN, Double.NaN)
            val values = data.map { it.y.toDouble() }
            return Triple(values.average(), values.maxOrNull() ?: Double.NaN, values.minOrNull() ?: Double.NaN)
        }

        val oxiStats = calcStats(oxigenEntries)
        val phStats = calcStats(phEntries)
        val tempStats = calcStats(temperatureEntries)

        fun addRow(nombre: String, stats: Triple<Double, Double, Double>) {
            val row = TableRow(this)

            row.addView(crearCelda(nombre, Color.BLACK, Color.WHITE))
            row.addView(crearCelda(if (stats.first.isNaN()) "-" else String.format(Locale.getDefault(), "%.2f", stats.first), Color.WHITE, Color.BLACK))
            row.addView(crearCelda(if (stats.second.isNaN()) "-" else String.format(Locale.getDefault(), "%.2f", stats.second), Color.WHITE, Color.BLACK))
            row.addView(crearCelda(if (stats.third.isNaN()) "-" else String.format(Locale.getDefault(), "%.2f", stats.third), Color.WHITE, Color.BLACK))

            tableLayoutResumen.addView(row)
        }

        addRow("Oxígeno", oxiStats)
        addRow("PH", phStats)
        addRow("Temperatura", tempStats)
    }

    private fun verificarRangosYMostrarNotificacion() {
        var fueraTemperatura = false
        var fueraPH = false
        var fueraOxigeno = false

        for (entry in temperatureEntries) {
            if (entry.y < 25f || entry.y > 30f) {
                fueraTemperatura = true
                break
            }
        }
        for (entry in phEntries) {
            if (entry.y < 7f || entry.y > 8f) {
                fueraPH = true
                break
            }
        }
        for (entry in oxigenEntries) {
            if (entry.y < 4f || entry.y > 6f) {
                fueraOxigeno = true
                break
            }
        }

        if (fueraTemperatura || fueraPH || fueraOxigeno) {
            val camposFuera = mutableListOf<String>()
            if (fueraTemperatura) camposFuera.add("Temperatura")
            if (fueraPH) camposFuera.add("pH")
            if (fueraOxigeno) camposFuera.add("Oxígeno")

            val ahora = Calendar.getInstance().time
            val sdfFechaHora = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
            val fechaHoraStr = sdfFechaHora.format(ahora)

            val mensaje = "Estanque: ${estanque.nombre}\n" +
                    "Fecha y hora de alerta: $fechaHoraStr\n" +
                    "Valores fuera de rango en: ${camposFuera.joinToString(", ")}"

            mostrarNotificacion(mensaje)
        }
    }

    private fun mostrarNotificacion(mensaje: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Alertas de rangos",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notificaciones cuando los valores están fuera del rango normal"
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("Alerta de Rangos")
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build())
    }
}
