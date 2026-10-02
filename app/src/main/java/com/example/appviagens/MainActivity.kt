package com.example.appviagens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf // IMPORTAÇÃO ADICIONADA
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}

data class RelatorioViagem(val data: String, val consumoMedio: String, val custoTotal: String)

@Composable
fun App(modifier: Modifier = Modifier) {
    var dataViagem by remember { mutableStateOf("") }
    var kmInicial by remember { mutableStateOf("") }
    var kmFinal by remember { mutableStateOf("") }
    var litros by remember { mutableStateOf("") }
    var combustivel by remember { mutableStateOf("") }
    var pedagio by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }

    var resultado by remember { mutableStateOf<RelatorioViagem?>(null) }
    // Corrigido: Agora o mutableStateListOf está importado corretamente
    val listaDeResultados = remember { mutableStateListOf<RelatorioViagem>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Bem vindo ao Sistema de Viagens Android!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = dataViagem,
            onValueChange = { dataViagem = it },
            label = { Text("Digite a data") },
            placeholder = { Text("15/10/2026") }
        )

        OutlinedTextField(
            value = kmInicial,
            onValueChange = { kmInicial = it },
            label = { Text("Digite a kilometragem inicial") },
            placeholder = { Text("15") }
        )

        OutlinedTextField(
            value = kmFinal,
            onValueChange = { kmFinal = it },
            label = { Text("Digite a kilometragem final") },
            placeholder = { Text("60") }
        )

        OutlinedTextField(
            value = litros,
            onValueChange = { litros = it },
            label = { Text("Digite os litros") },
            placeholder = { Text("15") }
        )

        OutlinedTextField(
            value = combustivel,
            onValueChange = { combustivel = it },
            label = { Text("Digite o valor do combustivel por L") },
            placeholder = { Text("6.55") }
        )

        OutlinedTextField(
            value = pedagio,
            onValueChange = { pedagio = it },
            label = { Text("Digite o valor do pedagio") },
            placeholder = { Text("33.88") }
        )

        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Digite o valor que gastou na viagem") },
            placeholder = { Text("2567.99") }
        )

        Button(
            onClick = {
                // Geramos o relatório chamando a função de cálculo
                val novoRelatorio = criarViagem(
                    data = dataViagem,
                    kmIni = kmInicial,
                    kmFin = kmFinal,
                    litrosGastos = litros,
                    precoCombustivel = combustivel,
                    valorPedagio = pedagio,
                    valorTotal = valor
                )

                resultado = novoRelatorio
                listaDeResultados.add(novoRelatorio) // Corrigido: .add() insere na lista do Compose

                // Limpa os campos
                dataViagem = ""
                kmInicial = ""
                kmFinal = ""
                litros = ""
                combustivel = ""
                pedagio = ""
                valor = ""
            }
        ) {
            Text("Cadastrar viagem")
        }

        // Informações da última viagem cadastrada
        resultado?.let { (data, consumo, custo) ->
            Text("📅 Data da viagem: $data")
            Text("⛽ Consumo calculado: $consumo")
            Text("💵 Custo total viagem: $custo")
        }

        // OPCIONAL: Se quiser mostrar o histórico de todas as viagens salvas na lista
        if (listaDeResultados.isNotEmpty()) {
            Text(
                text = "Histórico de Viagens (${listaDeResultados.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            listaDeResultados.forEach { viagem ->
                Text("• ${viagem.data} | Consumo: ${viagem.consumoMedio} | Total: R$ ${viagem.custoTotal}")
            }
        }
    }
}

// Corrigido: Modificado para ser uma função pura de cálculo (sem mexer na lista diretamente)
fun criarViagem(
    data: String,
    kmIni: String,
    kmFin: String,
    litrosGastos: String,
    precoCombustivel: String,
    valorPedagio: String,
    valorTotal: String
): RelatorioViagem {
    val kmI = kmIni.toDoubleOrNull() ?: 0.0
    val kmF = kmFin.toDoubleOrNull() ?: 0.0
    val precoComb = precoCombustivel.toDoubleOrNull() ?: 0.0
    val total = valorTotal.toDoubleOrNull() ?: 0.0
    val pedagio = valorPedagio.toDoubleOrNull() ?: 0.0 // Corrigido para evitar crash se estiver vazio

    val distancia = kmF - kmI
    val consumoMedio = distancia * precoComb
    val totalViagem = total + consumoMedio + pedagio

    return RelatorioViagem(
        data = data,
        consumoMedio = String.format("%.2f", consumoMedio),
        custoTotal = String.format("%.2f", totalViagem)
    )
}

@Preview(showBackground = true, name = "Visualização Padrão")
@Composable
fun AppPreview() {
    App()
}
