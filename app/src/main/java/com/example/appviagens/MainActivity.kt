package br.viagens.com.viagenstest

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// Extensão para criar o DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "configuracoes")

// Chave para salvar o motorista
object PreferenceKeys {
    val NOME_MOTORISTA = stringPreferencesKey("nome_motorista")
}

data class RelatorioViagem(
    val data: String,
    val consumoMedio: String,
    val custoTotal: String
)

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
    val relatorios = remember { mutableStateListOf<RelatorioViagem>() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Recuperando o nome do motorista do DataStore
    val nomeMotoristaFlow: Flow<String> = context.dataStore.data
        .map { preferences: Preferences ->
            preferences[PreferenceKeys.NOME_MOTORISTA] ?: ""
        }
    val nomeMotoristaSalvo by nomeMotoristaFlow.collectAsState(initial = "")

    NavHost(
        navController = navController,
        startDestination = "cadastro"
    ) {
        composable("cadastro") {
            TelaCadastro(
                nomeMotoristaSalvo = nomeMotoristaSalvo,
                onSalvarMotorista = { novoNome ->
                    coroutineScope.launch {
                        context.dataStore.edit { preferences ->
                            preferences[PreferenceKeys.NOME_MOTORISTA] = novoNome
                        }
                    }
                },
                onCriarViagem = { viagem ->
                    relatorios.add(viagem)
                },
                onVerHistorico = {
                    navController.navigate("historico")
                }
            )
        }

        composable("historico") {
            TelaHistorico(
                relatorios = relatorios,
                onVoltar = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCadastro(
    nomeMotoristaSalvo: String,
    onSalvarMotorista: (String) -> Unit,
    onCriarViagem: (RelatorioViagem) -> Unit,
    onVerHistorico: () -> Unit
) {
    // Estado livre para digitação (não trava o app ao digitar)
    var nomeMotorista by remember { mutableStateOf("") }
    var dataViagem by remember { mutableStateOf("") }
    var kmInicial by remember { mutableStateOf("") }
    var kmFinal by remember { mutableStateOf("") }
    var litros by remember { mutableStateOf("") }
    var combustivel by remember { mutableStateOf("") }
    var pedagio by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }

    // Carrega o valor salvo do DataStore apenas na primeira vez que abrir a tela
    LaunchedEffect(nomeMotoristaSalvo) {
        if (nomeMotorista.isEmpty() && nomeMotoristaSalvo.isNotEmpty()) {
            nomeMotorista = nomeMotoristaSalvo
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Controle de Viagens")
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {

            // Campo do Nome do Motorista (atualiza apenas a tela enquanto digita)
            OutlinedTextField(
                value = nomeMotorista,
                onValueChange = { nomeMotorista = it },
                label = {
                    Text("Nome do Motorista")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = dataViagem,
                onValueChange = { dataViagem = it },
                label = { Text("Data da viagem") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = kmInicial,
                onValueChange = { kmInicial = it },
                label = { Text("KM inicial") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = kmFinal,
                onValueChange = { kmFinal = it },
                label = { Text("KM final") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = litros,
                onValueChange = { litros = it },
                label = { Text("Litros") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = combustivel,
                onValueChange = { combustivel = it },
                label = { Text("Combustível") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pedagio,
                onValueChange = { pedagio = it },
                label = { Text("Pedágio") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = valor,
                onValueChange = { valor = it },
                label = { Text("Valor do combustível") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Salva o motorista no DataStore no momento que clica em criar viagem
                    onSalvarMotorista(nomeMotorista)

                    val viagem = criarViagem(
                        data = dataViagem,
                        kmInicial = kmInicial,
                        kmFinal = kmFinal,
                        litros = litros,
                        combustivel = combustivel,
                        pedagio = pedagio,
                        valor = valor
                    )

                    if (viagem != null) {
                        onCriarViagem(viagem)
                        dataViagem = ""
                        kmInicial = ""
                        kmFinal = ""
                        litros = ""
                        combustivel = ""
                        pedagio = ""
                        valor = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Criar viagem")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onVerHistorico()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver histórico")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaHistorico(
    relatorios: List<RelatorioViagem>,
    onVoltar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Histórico de Viagens")
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Button(
                onClick = {
                    onVoltar()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Voltar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (relatorios.isEmpty()) {
                Text(text = "Nenhuma viagem cadastrada.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(relatorios) { viagem ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Data: ${viagem.data}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Consumo médio: ${viagem.consumoMedio}")
                                Text(text = "Custo total: ${viagem.custoTotal}")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun criarViagem(
    data: String,
    kmInicial: String,
    kmFinal: String,
    litros: String,
    combustivel: String,
    pedagio: String,
    valor: String
): RelatorioViagem? {
    return try {
        val kmI = kmInicial.toDouble()
        val kmF = kmFinal.toDouble()
        val litrosUsados = litros.toDouble()
        val precoCombustivel = valor.toDouble()
        val valorPedagio = pedagio.toDouble()

        val distancia = kmF - kmI

        if (distancia <= 0 || litrosUsados <= 0) {
            return null
        }

        val consumoMedio = distancia / litrosUsados
        val custoCombustivel = litrosUsados * precoCombustivel
        val totalViagem = custoCombustivel + valorPedagio

        RelatorioViagem(
            data = data,
            consumoMedio = String.format("%.2f km/L", consumoMedio),
            custoTotal = String.format("R$ %.2f", totalViagem)
        )
    } catch (e: NumberFormatException) {
        null
    }
}
