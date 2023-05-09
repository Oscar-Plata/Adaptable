package com.example.adaptable

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class AppViewModel: ViewModel() {
    private val _estado = MutableStateFlow(AppEstado())
    val estado : StateFlow<AppEstado> = _estado

    init{
        inicializar()
    }
    //Funcion para asignar valores de inicio al viewmodel
    private fun inicializar(){
        //Lista de pokemons predefinidos de manera local
        val listaAux= listOf<Pokemon>(
            Pokemon("Pikachu",25,"Electrico","",25),
            Pokemon("Vulpix",37,"Fuego","",12),
            Pokemon("Exeggutor",103,"Planta","Psiquico",30),
            Pokemon("Eevee",133,"Normal","",28),
            Pokemon("Mr Mime",122,"Psiquico","Hada",10),
            Pokemon("Charmander",4,"Fuego","",7),
            Pokemon("Squirtle",7,"Agua","",8),
            Pokemon("Snorlax",143,"Normal","",35)
        )
        _estado.value = AppEstado(lista = listaAux, actual =listaAux[0], actualPos = 0 )
    }
    //Funcion para seleccionar un pokemon de la lista (llamado desde fragmeto PantallaPokemones)
    fun seleccionarPokemon(pkm:Pokemon){
        _estado.update {
            it.copy(
                actual = pkm,
                actualPos = it.lista.indexOf(pkm),
                paginaHome = false
            )
        }
    }
    //Funcion para restablecer el pokemon seleccionado, llamado desde (DetallesPokemon)
    fun regresar(){
        _estado.update {
            it.copy(
                actualPos = 0,
                actual = it.lista[0],
                paginaHome = true
            )
        }
    }
}
    //Clase para modelar y crear objetos tipo Pokemon
    data class AppEstado(
        val paginaHome: Boolean = true,
        val lista: List<Pokemon> = listOf(),
        val actual: Pokemon = Pokemon("",0,"","",0),
        val actualPos: Int = 0
    )