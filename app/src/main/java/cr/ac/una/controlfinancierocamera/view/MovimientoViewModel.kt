package cr.ac.una.controlfinancierocamera.view

import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cr.ac.una.controlfinancierocamera.db.AppDatabase
import cr.ac.una.controlfinancierocamera.entity.Movimiento
import cr.ac.una.jsoncrud.dao.MovimientoDAO

class MovimientoViewModel:  ViewModel(){
    private var _movimientoView: MutableLiveData<List<Movimiento?>> = MutableLiveData()
    var movimientoView: LiveData<List<Movimiento?>> = _movimientoView

    fun updateDatos(movimientoDao: MovimientoDAO){
        var datos = movimientoDao.getAll()
        _movimientoView.postValue(datos)
    }
}