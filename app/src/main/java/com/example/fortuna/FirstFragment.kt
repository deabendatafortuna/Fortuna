package com.example.fortuna

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import com.example.fortuna.databinding.FragmentFirstBinding
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CallGemini()
        playMp3()

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    private fun
            playMp3() {
        mediaPlayer = MediaPlayer.create(this.context, R.raw.over_the_horizon)
        mediaPlayer?.start()
    }

    private fun CallGemini() {
        Log.d("FirstFragment", "CallGemini called")
        val textViewFirst = binding.root.findViewById<TextView>(R.id.textview_first)
        val apiKey = "YOUR_API_KEY"

        try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-pro",
                apiKey = apiKey
            )
            textViewFirst.text = "2"
            val chat = generativeModel.startChat()
            val prompt = "Che cosa è la retinite pigmentosa?"

            MainScope().launch {
                try {
                    Log.d("FirstFragment", "Coroutine started")
                    val response = chat.sendMessage(prompt)
                    Log.d("FirstFragment", "Response received: ${response.text}")

                    textViewFirst.text = response.text
                } catch (e: Exception) {
                    textViewFirst.text = "Invalid API Key"
                    Log.e("FirstFragment", "Error during API call", e)
                }
            }
        } catch (e: Exception) {
            Log.e("FirstFragment", "Error during Generative Model creation", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
