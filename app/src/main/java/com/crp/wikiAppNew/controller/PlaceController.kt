package cr.ac.una.controlfinanciero.controller

import com.crp.wikiAppNew.datamodel.PlaceEntity


class PlaceController {

    var places: ArrayList<PlaceEntity> = arrayListOf()


    fun insertPlace(place: PlaceEntity) {
        places.add(place)
    }

    fun getPlace(): List<PlaceEntity> {
        return places
    }
//
//    fun validarDecimales(amount: String): Boolean {
//
//        if (amount.endsWith(".0")) {
//            return true
//        }
//
//        val amountOfDecimals: Int
//
//        try {
//            amountOfDecimals = amount.substring(amount.indexOf('.')).length - 1
//        } catch (e: Exception) {
//            return true
//        }
//
//        return amountOfDecimals <= 2
//    }
//
//    fun showPlaces() {
//        for (place in places) {
//            Log.d(
//                "Movimiento",
//                "Monto: ${movimiento.monto} Tipo: ${movimiento.tipo} Fecha: ${movimiento.fecha}"
//            )
//        }
//    }
}