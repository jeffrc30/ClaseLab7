package cr.ac.una.controlfinancierocamera

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import cr.ac.una.controlfinancierocamera.db.AppDatabase
import cr.ac.una.controlfinancierocamera.entity.Movimiento
import cr.ac.una.jsoncrud.dao.MovimientoDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class IngresarMovimientoFragment : Fragment() {


    lateinit var captureButton : Button
    lateinit var imageView : ImageView
    lateinit var elementoSeleccionado : String
    lateinit var monto: TextView
    private lateinit var datePicker: DatePicker
    lateinit var img: ImageView


    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dispatchTakePictureIntent()
        } else {
            // Permiso denegado, manejar la situación aquí si es necesario
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageView.setImageBitmap(imageBitmap)
        } else {
            // Manejar el caso en el que no se haya podido capturar la imagen
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lateinit var movimientoDao: MovimientoDAO
        movimientoDao = AppDatabase.getInstance(requireContext()).ubicacionDao()

        val botonNuevo = view.findViewById<Button>(R.id.saveMovimientoButtonEditar)

        monto = view.findViewById<TextView>(R.id.textMonto)
        datePicker = view.findViewById(R.id.datePicker)

      //  fecha = view.findViewById<TextView>(R.id.textFechaEditar)

        img = view.findViewById<ImageView>(R.id.imageView)

        botonNuevo.setOnClickListener {
            val confirmationDialog = AlertDialog.Builder(requireContext())
                .setTitle("Confirmación")
                .setMessage("¿Deseas ingresar este movimiento?")
                .setPositiveButton("Sí") { dialog, which ->
                    // Usuario ha confirmado, guarda el movimiento
                    var montoFinal = decimales(monto)
                    val day = datePicker.dayOfMonth
                    val month = datePicker.month + 1 // El mes se devuelve como un valor base 0, por lo que debes sumar 1
                    val year = datePicker.year

                    val fechaSeleccionada = "$day/$month/$year"

                    val movimiento = Movimiento(null, montoFinal, elementoSeleccionado, fechaSeleccionada)

                    lifecycleScope.launch {
                        withContext(Dispatchers.Default) {
                            movimientoDao.insert(movimiento)
                            fragmentManager?.popBackStack()
                        }
                    }
                }
                .setNegativeButton("No", null)
                .create()

            confirmationDialog.show()
        }

        val cancelButton = view.findViewById<Button>(R.id.cancelButtonEditar)
        cancelButton.setOnClickListener {
            // Regresa al fragmento anterior
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }

        val spinner: Spinner = view.findViewById(R.id.tipoMovimientoSpinnerEditar)

        ArrayAdapter.createFromResource(
            view.context,
            R.array.tiposMovimiento,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Obtiene el valor seleccionado del array de recursos
                val elementos = resources.getStringArray(R.array.tiposMovimiento)
                elementoSeleccionado = elementos[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Se llama cuando no hay ningún elemento seleccionado
            }
        }
        captureButton = view.findViewById(R.id.captureButtonEditar)
        imageView = view.findViewById(R.id.imageView)

        captureButton.setOnClickListener {
            if (checkCameraPermission()) {
                dispatchTakePictureIntent()
            } else {
                requestCameraPermission()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ingresar_movimiento, container, false)
    }
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                takePictureLauncher.launch(takePictureIntent)
            }
        }
    }

    private fun decimales(sMonto: TextView): Double{
        val montoStr = sMonto.text.toString()
        val montoDouble = montoStr.toDoubleOrNull() ?: 0.0
        return String.format("%.2f", montoDouble).toDouble()
    }
}