package com.example.adaptable

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adaptable.ui.theme.AdaptableTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdaptableTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    //OBTENER EL TAMAñO de la Ventana
                    val tamVentana =  calculateWindowSizeClass(activity = this)
                    AppAdaptable(tamVentana.widthSizeClass)
                }
            }
        }
    }
}

@Composable
fun AppAdaptable(ventana: WindowWidthSizeClass,modifier: Modifier=Modifier) {
    val viewModel: AppViewModel = viewModel()
    val estadoVM = viewModel.estado.collectAsState().value
    val modoLayout:Int

    when(ventana){
        //Celulares en modo vertical
        WindowWidthSizeClass.Compact -> {
            modoLayout=0;
            LayoutVertical(estadoVM,modoLayout,onPokemonClick={pkm:Pokemon -> viewModel.seleccionarPokemon(pkm)}, onRegresar = {viewModel.regresar()})
        }
        //Tabletas en modo vertical
        WindowWidthSizeClass.Medium -> {
            modoLayout=1;
            LayoutVertical(estadoVM,modoLayout,onPokemonClick={pkm:Pokemon -> viewModel.seleccionarPokemon(pkm)}, onRegresar = {viewModel.regresar()})
        }
        //Tabletas, Escritorios y Celulares* en modo Horizontal
        WindowWidthSizeClass.Expanded ->{
            modoLayout=2;
            LayoutHorizontal(estadoVM,modoLayout,onPokemonClick={pkm:Pokemon -> viewModel.seleccionarPokemon(pkm)})
        }
        else ->{
            modoLayout=0;
            LayoutVertical(estadoVM,modoLayout,onPokemonClick={pkm:Pokemon -> viewModel.seleccionarPokemon(pkm)}, onRegresar = {viewModel.regresar()})
        }
    }
}

//Layout de la aplicacion cuando esta en modo vertical
@Composable
fun LayoutVertical(estado:AppEstado,modo:Int,onPokemonClick:(Pokemon)-> Unit,onRegresar: () -> Unit,modifier: Modifier=Modifier){
    //Se muestra en la columna principal uno u otro "fragmento" segun la interacion del usuario en la app
    Column(modifier = modifier.fillMaxSize()) {
        if (estado.paginaHome) {
            //Muestra la lista de los Pokemones
            PantallaPokemones(estado = estado,onPokemonClick = onPokemonClick,modifier = Modifier.weight(1f)
            )
        } else {
            //Muestra la descripcion del pokemon seleccionado
            DescripcionPokemon(estado=estado, onRegresar = onRegresar, modifier = Modifier.weight(1f), modo = modo)
        }
    }
}

//Layout de la aplicacion cuando esta en modo horizontal
@Composable
fun LayoutHorizontal(estado:AppEstado,modo:Int,onPokemonClick:(Pokemon)-> Unit,modifier: Modifier=Modifier){
    //Se muestra en toda la pantalla ambos fragmentos
    Row(modifier = modifier.fillMaxSize()){
        PantallaPokemones(estado=estado,onPokemonClick=onPokemonClick,modifier=Modifier.weight(1f))
        DescripcionPokemon(estado = estado,modo=modo,onRegresar = {},modifier = Modifier.weight(1f))
    }
}

//ELEMENTO "FRAGMENTO" QUE MUESTRA LA LISTA DE POKEMONS
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PantallaPokemones(modifier: Modifier = Modifier, estado: AppEstado,onPokemonClick: (Pokemon) -> Unit){
    val ctx=LocalContext.current
    val lista = estado.lista
    Column(modifier = modifier.padding(horizontal = 0.dp)) {
        //BARRA SUPERIOR
        Text(text = "POKEDEX", fontSize = 30.sp, color = Color.Yellow, modifier = Modifier
            .fillMaxWidth()
            .background(Color.Blue)
            .padding(10.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(5.dp))

        //"RECYCLER VIEW" LISTA DE POKEMONS
        LazyColumn(modifier = modifier.padding(horizontal = 0.dp)) {
            items(lista) { pkm ->
                Card(modifier = Modifier.padding(vertical = 3.dp, horizontal = 10.dp), onClick = {onPokemonClick(pkm) },backgroundColor = Color.Red) {
                    Row(modifier = modifier
                        .fillMaxWidth()
                        .padding(10.dp)) {
                        Text(text = pkm.id.toString(),fontSize = 20.sp,color = Color.Yellow, modifier = Modifier.width(60.dp),textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(text = pkm.nombre,fontSize = 25.sp,color = Color.White)
                        Spacer(modifier = Modifier.weight(1f))
                        Image(painter = painterResource(id = R.drawable.pokeball), contentDescription = "Pokeball",modifier=Modifier.width(50.dp))
                    }
                }
            }
        }
    }
}

//ELEMENTO "FRAGMENTO" QUE MUESTRA LA DESCRIPCION DE UN POKEMON
@Composable
fun DescripcionPokemon(modifier: Modifier=Modifier,estado: AppEstado,onRegresar: () -> Unit,modo:Int){
    BackHandler {
        onRegresar()
    }
    val pkm=estado.actual
    Column(modifier = modifier.padding(horizontal = 0.dp)) {
        //BARRA SUPERIOR
        Row(modifier = Modifier.background(Color.Blue)){
            //Muestra el boton segun el layout correspondiente al tamaño de pantalla actual.
            if(modo!=2){
                Button(onClick = {onRegresar()}, modifier = Modifier
                    .padding(5.dp)
                    .clip(CircleShape),colors = ButtonDefaults.buttonColors(backgroundColor =Color.Red)) {
                    Text(text = "<",fontSize = 25.sp)
                }
            }
            Text(text = pkm.nombre, fontSize = 30.sp, color = Color.Yellow, modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(modifier = Modifier.height(5.dp))
        //INFORMACION DEL POKEMON
        LazyColumn(modifier = modifier.padding(horizontal = 0.dp)) {
            item{ Image(painter = painterResource(id = R.drawable.pokeball), contentDescription = "pokeball", modifier = Modifier.padding(15.dp).fillMaxWidth())}
            item {
                Card(modifier = Modifier
                    .padding(vertical = 3.dp, horizontal = 10.dp)
                    .fillMaxWidth(), backgroundColor = Color.Red) {
                    Column{
                        Text(text = "No.\t\t${pkm.id}", fontSize = 25.sp)
                        Text(text = "Lvl:\t\t${pkm.nivel}",fontSize = 25.sp)
                        Text(text = "Tipo:\n\t\t${pkm.tipo1}\t\t${pkm.tipo2}",fontSize = 25.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun SmallPreview() {
    AdaptableTheme {
        AppAdaptable(WindowWidthSizeClass.Compact)
    }
}

@Preview(showBackground = true, widthDp = 900)
@Composable
fun LargePreview() {
    AdaptableTheme {
        AppAdaptable(WindowWidthSizeClass.Expanded)
    }
}