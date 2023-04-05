package com.example.travelapp.ui.CurrencyExchange

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.travelapp.R
import com.example.travelapp.databinding.FragmentCurrencyBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.net.URL

class CurrencyFragment : Fragment(R.layout.fragment_currency) {

    private lateinit var binding: FragmentCurrencyBinding
    private val client = OkHttpClient()

    // This property is only valid between onCreateView and
    // onDestroyView.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCurrencyBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


//        binding.convertToEditTxt.setAdapter(arrayAdapter)

        binding = FragmentCurrencyBinding.inflate(layoutInflater)
        val root: View = binding.root

        val currencyArray = resources.getStringArray(R.array.currency)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, currencyArray)
        binding.convertFromEditTxt.setAdapter(arrayAdapter)
        binding.convertToEditTxt.setAdapter(arrayAdapter)

        binding.convertBtn.setOnClickListener {
            fetchUrl()
        }

        return root
    }

    private fun fetchUrl() {
        val value = binding.amtEditTxt.text.toString()
        val from = binding.convertFromEditTxt.text.toString()
        val to = binding.convertToEditTxt.text.toString()



        if (to.isNotEmpty() && from.isNotEmpty()) {
            val url = URL("https://open.er-api.com/v6/latest/$from")

            val request = okhttp3.Request.Builder().url(url).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val jsonObject =
                        JSONTokener(response.body()?.string()).nextValue() as JSONObject

                    val rates = jsonObject.getJSONObject("rates") as JSONObject
                    activity?.runOnUiThread(Runnable {
                        kotlin.run {
                            Log.d("TO", "$to")
                            println(rates)

                            println(rates.getString(to))
                            var currencyRate = rates.getString(to).toDouble()

                            println(value.toDouble())

                            currencyRate = convert(currencyRate, value.toDouble())
                            binding.convertToText.text = getString(R.string.convert_acronym, to)


                            binding.convertToValue.text = getString(R.string.currency, currencyRate.toString())
                        }

                    })

                }

            })
        } else {
            if (to.isNullOrEmpty()) {
                Toast.makeText(context, "Right dropdown can not be empty", Toast.LENGTH_SHORT)
                    .show()
            } else if (from.isNullOrEmpty()) {
                Toast.makeText(context, "Left dropdown can not be empty", Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(context, "Nothing was entered", Toast.LENGTH_SHORT).show()
        }


    }

    private fun convert(to: Double, value: Double): Double {
        return value * to
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}