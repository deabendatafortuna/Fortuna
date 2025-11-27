package com.example.fortuna

import android.Manifest // Importa Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts // Importa per la gestione permessi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import androidx.core.content.ContextCompat // Importa ContextCompat
//import androidx.privacysandbox.tools.core.generator.build
import com.example.fortuna.databinding.ActivityHelpBinding
import com.google.android.gms.location.* // Importa le classi di localizzazione

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding

    // Client per i servizi di localizzazione di Google
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    // Callback per ricevere gli aggiornamenti della posizione
    private lateinit var locationCallback: LocationCallback

    // Launcher per la richiesta dei permessi
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permesso concesso, avviamo la richiesta di posizione
                Toast.makeText(this, "Permesso di localizzazione concesso", Toast.LENGTH_SHORT).show()
                startLocationUpdates()
            } else {
                // Permesso negato, informiamo l'utente
                binding.tvLocationStatus.text = "Permesso di localizzazione negato. Impossibile ottenere le coordinate."
                Toast.makeText(this, "Permesso negato, la funzione GPS non sarà attiva", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inizializza il client di localizzazione
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Imposta il listener per il pulsante di allarme (codice esistente)
        binding.btnAlarm.setOnClickListener {
            // ... il tuo codice per WhatsApp va qui ...
        }

        // Definisci cosa fare quando arriva una nuova posizione
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    // Abbiamo una posizione! Aggiorniamo la UI.
                    binding.tvLatitude.text = "Latitudine: ${location.latitude}"
                    binding.tvLongitude.text = "Longitudine: ${location.longitude}"
                    binding.tvLocationStatus.text = "Posizione aggiornata!"
                }
            }
        }

        // Avvia il processo per ottenere la posizione
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Hai già il permesso. Avvia la richiesta di posizione.
                startLocationUpdates()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // L'utente ha negato il permesso in precedenza.
                // Qui potresti mostrare un dialogo che spiega perché ti serve il permesso.
                binding.tvLocationStatus.text = "È richiesto il permesso di localizzazione per questa funzione."
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> {
                // Richiedi il permesso per la prima volta.
                binding.tvLocationStatus.text = "Richiesta permesso di localizzazione..."
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    // Metodo per avviare la ricezione degli aggiornamenti sulla posizione
    private fun startLocationUpdates() {
        // Controlliamo di nuovo il permesso (necessario per la sicurezza del compilatore)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return // Non dovremmo mai arrivare qui, ma è una sicurezza
        }

        binding.tvLocationStatus.text = "In attesa del segnale GPS..."

        // Configura la richiesta di aggiornamenti
        val locationRequest = LocationRequest.Builder(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 10000) // Aggiorna ogni 10 secondi
            .setMinUpdateIntervalMillis(5000) // Non prima di 5 secondi
            .build()

        // Avvia la richiesta
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    override fun onPause() {
        super.onPause()
        // È una buona pratica interrompere gli aggiornamenti quando l'activity non è visibile per risparmiare batteria
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        // Se l'utente torna all'app, riavvia gli aggiornamenti (se i permessi sono concessi)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        }
    }

    // ... Il resto del tuo codice, inclusa la funzione sendWhatsAppMessage ...
}
