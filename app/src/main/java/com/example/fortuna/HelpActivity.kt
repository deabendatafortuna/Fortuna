package com.example.fortuna

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fortuna.databinding.ActivityHelpBinding // Importa il binding per il layout di aiuto

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Usa View Binding per inflare il layout, come fai in MainActivity
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Qui puoi aggiungere la logica specifica per la tua schermata di aiuto,
        // ad esempio, impostare un testo o un listener per un pulsante "Indietro".
        // Esempio: binding.backButton.setOnClickListener { finish() }
    }
}
