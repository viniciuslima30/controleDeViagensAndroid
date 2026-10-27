
package br.viagens.com.viagenstest

import android.content.ContentValues
import android.content.Context

data class Viagem(
    val id: Long = 0,
    val data: String,
    val kmInicial: Double,
    val kmFinal: Double,
    val litros: Double,
    val combustivel: String,
    val pedagios: Double,
    val valorLitro: Double
) {
    val distancia: Double
        get() = kmFinal - kmInicial

    val gastoCombustivel: Double
        get() = litros * valorLitro

    val gastoTotal: Double
        get() = gastoCombustivel + pedagios

    val consumoMedio: Double
        get() = if (litros > 0) distancia / litros else 0.0
}

data class ResumoViagens(
    val totalKm: Double,
    val totalCombustivel: Double,
    val totalPedagios: Double,
    val totalGeral: Double
)

class ViagemRepositorio(context: Context) {

    private val bancoHelper = BancoHelper(context)

    fun inserir(viagem: Viagem): Long {
        val db = bancoHelper.writableDatabase

        val valores = ContentValues().apply {
            put("data", viagem.data)
            put("km_inicial", viagem.kmInicial)
            put("km_final", viagem.kmFinal)
            put("litros", viagem.litros)
            put("combustivel", viagem.combustivel)
            put("pedagios", viagem.pedagios)
            put("valor_litro", viagem.valorLitro)
        }

        return db.insert("viagens", null, valores)
    }

    fun listar(): List<Viagem> {
        val viagens = mutableListOf<Viagem>()
        val db = bancoHelper.readableDatabase

        val cursor = db.query(
            "viagens",
            null,
            null,
            null,
            null,
            null,
            "data DESC, id DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                viagens.add(
                    Viagem(
                        id = it.getLong(it.getColumnIndexOrThrow("id")),
                        data = it.getString(
                            it.getColumnIndexOrThrow("data")
                        ),
                        kmInicial = it.getDouble(
                            it.getColumnIndexOrThrow("km_inicial")
                        ),
                        kmFinal = it.getDouble(
                            it.getColumnIndexOrThrow("km_final")
                        ),
                        litros = it.getDouble(
                            it.getColumnIndexOrThrow("litros")
                        ),
                        combustivel = it.getString(
                            it.getColumnIndexOrThrow("combustivel")
                        ),
                        pedagios = it.getDouble(
                            it.getColumnIndexOrThrow("pedagios")
                        ),
                        valorLitro = it.getDouble(
                            it.getColumnIndexOrThrow("valor_litro")
                        )
                    )
                )
            }
        }

        return viagens
    }

    fun calcularResumo(): ResumoViagens {
        val viagens = listar()

        return ResumoViagens(
            totalKm = viagens.sumOf { it.distancia },
            totalCombustivel = viagens.sumOf { it.gastoCombustivel },
            totalPedagios = viagens.sumOf { it.pedagios },
            totalGeral = viagens.sumOf { it.gastoTotal }
        )
    }

    fun fechar() {
        bancoHelper.close()
    }
}
