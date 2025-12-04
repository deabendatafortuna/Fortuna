package com.example.fortuna

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location // <-- IMPORTANTE: Aggiungi questo import
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import androidx.core.content.ContextCompat
import com.example.fortuna.databinding.ActivityHelpBinding
import com.google.android.gms.location.*
import java.text.DecimalFormat // <-- Import per formattare i numeri

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    // ▼▼▼ NUOVA PROPRIETÀ: Definiamo la posizione di destinazione ▼▼▼
    private val laFavoritaLocation = Location("La Favorita").apply {
        // Coordinate: 44°53'33.2"N 11°03'42.9"E
        // Convertite da DMS (gradi, minuti, secondi) a gradi decimali
        latitude = 44.892556
        longitude = 11.061917
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permesso di localizzazione concesso", Toast.LENGTH_SHORT).show()
                startLocationUpdates()
            } else {
                binding.tvLocationStatus.text = "Permesso di localizzazione negato."
                Toast.makeText(this, "Permesso negato, la funzione GPS non sarà attiva", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnAlarm.setOnClickListener {
            // ... codice per WhatsApp ...
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { userLocation ->
                    // Aggiorniamo la UI con le coordinate
                    binding.tvLatitude.text = "Latitudine: ${userLocation.latitude}"
                    binding.tvLongitude.text = "Longitudine: ${userLocation.longitude}"
                    binding.tvLocationStatus.text = "Posizione aggiornata!"

                    // ▼▼▼ NUOVA LOGICA: Calcolo e visualizzazione della distanza ▼▼▼
                    calculateAndShowDistance(userLocation)
                }
            }
        }

        checkLocationPermission()
    }

    // ▼▼▼ NUOVA FUNZIONE per calcolare e mostrare la distanza ▼▼▼
    private fun calculateAndShowDistance(currentUserLocation: Location) {
        // Calcola la distanza in metri tra la posizione dell'utente e "La Favorita"
        val distanceInMeters = currentUserLocation.distanceTo(laFavoritaLocation)

        // Converti la distanza in chilometri
        val distanceInKm = distanceInMeters / 1000.0

        // Formatta il numero per avere solo una cifra decimale (es. 5.2)
        val df = DecimalFormat("#.#")
        val formattedDistance = df.format(distanceInKm)

        // Aggiorna la TextView con il messaggio completo
        binding.tvDistance.text = "Sei a $formattedDistance Km dal parco \"La Favorita\""
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        }
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
}
