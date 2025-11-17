package com.example.fortuna


import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.text
import com.example.fortuna.databinding.ActivityHelpBinding // Importa il binding per il layout di aiuto

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inizializza il View Binding
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Imposta il listener per il click sul pulsante di allarme
        binding.btnAlarm.setOnClickListener {
            // Recupera il numero di telefono dall'EditText
            val phoneNumber = binding.etPhone.text.toString().trim()

            // Controlla che il numero non sia vuoto
            if (phoneNumber.isNotEmpty()) {
                sendWhatsAppMessage(phoneNumber, "Messaggio di aiuto automatico.") // Puoi personalizzare il messaggio
            } else {
                // Mostra un avviso se il campo del telefono è vuoto
                Toast.makeText(this, "Per favore, inserisci un numero di telefono", Toast.LENGTH_SHORT).show()
                // Puoi anche segnalare l'errore direttamente sul campo di testo
                binding.tilPhone.error = "Campo obbligatorio"
            }
        }

        // Opzionale: rimuovi il messaggio di errore quando l'utente inizia a scrivere
        binding.etPhone.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tilPhone.error = null
            }
        }
    }

    /**
     * Invia un messaggio WhatsApp a un numero di telefono specifico.
     * @param phoneNumber Il numero di telefono del destinatario (senza prefisso internazionale).
     * @param message Il testo del messaggio da inviare.
     */
    private fun sendWhatsAppMessage(phoneNumber: String, message: String) {
        // Formatta il numero: rimuovi spazi, trattini e aggiungi il prefisso italiano +39
        // ATTENZIONE: Questo presuppone un numero italiano. Per una soluzione globale,
        // la gestione del prefisso dovrebbe essere più avanzata.
        val formattedNumber = "+39" + phoneNumber.replace(Regex("[\\s-]"), "")

        // Crea l'URI per l'intent di WhatsApp
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$formattedNumber&text=${Uri.encode(message)}")

        // Crea l'intent per aprire WhatsApp
        val intent = Intent(Intent.ACTION_VIEW, uri)

        // Verifica se WhatsApp è installato sul dispositivo
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Se WhatsApp non è installato, avvisa l'utente
            Toast.makeText(this, "WhatsApp non è installato.", Toast.LENGTH_SHORT).show()
            // Potresti suggerire di installarlo aprendo il Play Store
            // val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.whatsapp"))
            // startActivity(playStoreIntent)
        }
    }
}
