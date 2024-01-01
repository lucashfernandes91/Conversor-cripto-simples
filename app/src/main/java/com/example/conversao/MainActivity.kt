package com.example.conversao

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {

    private lateinit var result:TextView
    private lateinit var objPrice:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        result = findViewById<TextView>(R.id.txt_result)
        priceCripto()

        val btnConverter = findViewById<Button>(R.id.btn_converter)
        btnConverter.setOnClickListener {
            converter()
        }
    }

    private fun priceCripto(){
        Thread{
            try {
                val currencyOfList = listOf("BTC", "ETH", "XRP")

                for(currency in currencyOfList) {
                    val url = URL("https://api.poloniex.com/markets/${currency}_USDC/price")
                    val conn = url.openConnection() as HttpsURLConnection

                    val data = conn.inputStream.bufferedReader().readText()
                    val obj = JSONObject(data)

                    when(currency){
                        "BTC" -> objPrice = findViewById<TextView>(R.id.priceBTC)
                        "ETH" -> objPrice = findViewById<TextView>(R.id.priceETH)
                        "XRP" -> objPrice = findViewById<TextView>(R.id.priceXRP)
                        else  -> ""
                    }

                    runOnUiThread {
                        val res = obj.getDouble("price")
                        objPrice.text = "${"%.2f".format(res)}"
                        objPrice.visibility = View.VISIBLE
                    }
                }


            } finally {
                //conn.disconnect()
            }
        }.start()
    }

    private fun converter(){
        val selectedCurrency = findViewById<RadioGroup>(R.id.radio_group)
        val checked = selectedCurrency.checkedRadioButtonId
        val currency = when(checked){
            R.id.radio_btc -> "BTC"
            R.id.radio_eth -> "ETH"
            else           -> "XRP"
        }

        val editField = findViewById<TextView>(R.id.value_for_converter)
        val valueForConverter = editField.text.toString()

        if(valueForConverter.isEmpty()) return

        Thread {
            val url = URL("https://api.poloniex.com/markets/${currency}_USDC/price")
            val conn = url.openConnection() as HttpsURLConnection

            try {
                val data = conn.inputStream.bufferedReader().readText()

                val obj = JSONObject(data)

                runOnUiThread {
                    val priceBTC = obj.getDouble("price")

                    val valorConvertido = valueForConverter.toDouble() / priceBTC

                    //result.text = res.toString()
                    result.text = "${currency} ${"%.8f".format(valorConvertido)}"
                    result.visibility = View.VISIBLE
                }

            } finally {
                conn.disconnect()
            }
        }.start()
    }
}