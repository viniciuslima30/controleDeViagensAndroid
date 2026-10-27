
package br.viagens.com.viagenstest

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BancoHelper(context: Context) : SQLiteOpenHelper(
    context,
    "controle_viagens.db",
    null,
    1
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE viagens (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                data TEXT NOT NULL,
                km_inicial REAL NOT NULL,
                km_final REAL NOT NULL,
                litros REAL NOT NULL,
                combustivel TEXT NOT NULL,
                pedagios REAL NOT NULL,
                valor_litro REAL NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Neste exemplo, a versão inicial do banco é 1.
        // Ao alterar a estrutura no futuro, implemente a migração.
    }
}
