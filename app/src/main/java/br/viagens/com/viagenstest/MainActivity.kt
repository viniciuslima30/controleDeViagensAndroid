package br.viagens.com.viagenstest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val repositorio = remember(context) {
        ViagemRepositorio(context.applicationContext)
    }

    val viagens = remember {
        mutableStateListOf<Viagem>()
    }

    NavHost(
        navController = navController,
        startDestination = "cadastro"
    ) {
        composable("cadastro") {
            TelaCadastro(
                onSalvar = { viagem ->
                    val id = repositorio.inserir(viagem)

                    if (id != -1L) {
                        viagens.add(viagem.copy(id = id))
                        true
                    } else {
                        false
                    }
                },
                onVerHistorico = {
                    viagens.clear()
                    viagens.addAll(repositorio.listar())
                    navController.navigate("historico")
                }
            )
        }

        composable("historico") {
            // Atualiza os dados do banco ao abrir o histórico.
            TelaHistorico(
                viagens = viagens,
                resumo = repositorio.calcularResumo(),
                onVoltar = {
                    navController.popBackStack()
                },
                onAtualizar = {
                    viagens.clear()
                    viagens.addAll(repositorio.listar())
                }
            )
        }
    }
}

private fun numero(texto: String): Double? {
    return texto.trim()
        .replace(",", ".")
        .toDoubleOrNull()
}

private fun moeda(valor: Double): String {
    return String.format(Locale("pt", "BR"), "R$ %.2f", valor)
}

private fun decimal(valor: Double): String {
    return String.format(Locale("pt", "BR"), "%.2f", valor)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCadastro(
    onSalvar: (Viagem) -> Boolean,
    onVerHistorico: () -> Unit
) {
    var data by remember { mutableStateOf("") }
    var kmInicial by remember { mutableStateOf("") }
    var kmFinal by remember { mutableStateOf("") }
    var litros by remember { mutableStateOf("") }
    var combustivel by remember { mutableStateOf("") }
    var pedagios by remember { mutableStateOf("0") }
    var valorLitro by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Controle de Viagens") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = data,
                onValueChange = { data = it },
                label = { Text("Data da viagem (dd/MM/aaaa)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = kmInicial,
                onValueChange = { kmInicial = it },
                label = { Text("Quilometragem inicial") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = kmFinal,
                onValueChange = { kmFinal = it },
                label = { Text("Quilometragem final") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = litros,
                onValueChange = { litros = it },
                label = { Text("Litros abastecidos") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = combustivel,
                onValueChange = { combustivel = it },
                label = { Text("Combustível (gasolina, diesel etc.)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = valorLitro,
                onValueChange = { valorLitro = it },
                label = { Text("Preço do combustível por litro (R$)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = pedagios,
                onValueChange = { pedagios = it },
                label = { Text("Total de pedágios (R$)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val kmI = numero(kmInicial)
                    val kmF = numero(kmFinal)
                    val qtdLitros = numero(litros)
                    val preco = numero(valorLitro)
                    val pedagio = numero(pedagios)

                    if (
                        data.isBlank() ||
                        combustivel.isBlank() ||
                        kmI == null ||
                        kmF == null ||
                        qtdLitros == null ||
                        preco == null ||
                        pedagio == null
                    ) {
                        mensagem = "Preencha todos os campos corretamente."
                    } else if (
                        kmI < 0 ||
                        kmF <= kmI ||
                        qtdLitros <= 0 ||
                        preco < 0 ||
                        pedagio < 0
                    ) {
                        mensagem = "Verifique os quilômetros e os valores."
                    } else {
                        val viagem = Viagem(
                            data = data.trim(),
                            kmInicial = kmI,
                            kmFinal = kmF,
                            litros = qtdLitros,
                            combustivel = combustivel.trim(),
                            pedagios = pedagio,
                            valorLitro = preco
                        )

                        if (onSalvar(viagem)) {
                            mensagem = "Viagem salva no banco SQLite!"
                            data = ""
                            kmInicial = ""
                            kmFinal = ""
                            litros = ""
                            combustivel = ""
                            pedagios = "0"
                            valorLitro = ""
                        } else {
                            mensagem = "Não foi possível salvar a viagem."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cadastrar viagem")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onVerHistorico,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Consultar histórico")
            }

            if (mensagem.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(mensagem)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaHistorico(
    viagens: List<Viagem>,
    resumo: ResumoViagens,
    onVoltar: () -> Unit,
    onAtualizar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histórico de Viagens") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Resumo das viagens",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total de quilômetros: ${decimal(resumo.totalKm)} km")
                    Text("Gastos com combustível: ${moeda(resumo.totalCombustivel)}")
                    Text("Gastos com pedágios: ${moeda(resumo.totalPedagios)}")
                    Text(
                        "Gasto total: ${moeda(resumo.totalGeral)}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onVoltar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Voltar")
                }

                Button(
                    onClick = onAtualizar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Atualizar")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (viagens.isEmpty()) {
                Text("Nenhuma viagem cadastrada.")
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = viagens,
                        key = { it.id }
                    ) { viagem ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    "Data: ${viagem.data}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text("Combustível: ${viagem.combustivel}")
                                Text(
                                    "Quilometragem: " +
                                            "${decimal(viagem.kmInicial)} a " +
                                            "${decimal(viagem.kmFinal)} km"
                                )
                                Text(
                                    "Distância percorrida: " +
                                            "${decimal(viagem.distancia)} km"
                                )
                                Text("Litros: ${decimal(viagem.litros)}")
                                Text(
                                    "Consumo médio: " +
                                            "${decimal(viagem.consumoMedio)} km/L"
                                )
                                Text(
                                    "Combustível: ${moeda(viagem.gastoCombustivel)}"
                                )
                                Text("Pedágios: ${moeda(viagem.pedagios)}")
                                Text(
                                    "Total da viagem: ${moeda(viagem.gastoTotal)}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
