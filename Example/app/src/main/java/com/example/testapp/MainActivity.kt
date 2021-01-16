package com.example.testapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import org.json.JSONObject
import vn.payme.sdk.PayME
import vn.payme.sdk.model.Action
import vn.payme.sdk.model.Env
import java.security.PublicKey
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

val APP_TOKEN = "APP_TOKEN"
val SECRET_KEY = "SECRET_KEY"
val PUBLIC_KEY = "PUBLIC_KEY"
val ON_LOG = "ON_LOG"

class MainActivity : AppCompatActivity() {
    lateinit var AppToken: String
    lateinit var PrivateKey: String
    lateinit var PublicKey: String
    lateinit var payme: PayME
    lateinit var context: Context

   fun convertInt(amount: String): Int {
        try {
            return Integer.parseInt(amount)

        } catch (e: Exception) {
            return 0

        }

    }


    lateinit var button: Button
    lateinit var buttonLogin: Button
    lateinit var buttonLogout: Button
    lateinit var buttonReload: ImageView
    lateinit var buttonDeposit: Button
    lateinit var buttonWithdraw: Button
    lateinit var buttonPay: Button
    lateinit var textView: TextView
    lateinit var inputUserId: EditText
    lateinit var inputPhoneNumber: EditText
    lateinit var moneyDeposit: EditText
    lateinit var moneyPay: EditText
    lateinit var moneyWithdraw: EditText
    lateinit var walletView: LinearLayout
    lateinit var buttonSetting: ImageView
    lateinit var spinnerEnvironment: Spinner

    var ConnectToken: String = "qBpM18YIyB15rdpFFfJpzsUBXNkaQ9rnCAN3asLNCrmEgQoS9YlhEVL8iQWT+6hhLSMs/C6uBUXxqD1PN33yhtfisiynwC1TeGV8TuT5bcdsSdgR+il/apjp886i1HJ3"

    fun updateWalletInfo() {

        payme.getWalletInfo(onSuccess = { jsonObject ->
            println("onSuccess=" + jsonObject.toString())
            val walletBalance = jsonObject.getJSONObject("walletBalance")
            val balance = walletBalance.get("balance")
            val decimal = DecimalFormat("#,###")
            textView.text = "${decimal.format(balance)}đ"
        }, onError = { jsonObject, code, message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()

            println("onError=" + message)
        })

    }

    fun getEnviromentSelected(value: String): Env {
        if(value === "DEV"){
            return Env.TEST
        }else if(value === "Production"){
            return Env.PRODUCTION
        }
        return Env.SANDBOX
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        context = this
        setContentView(R.layout.activity_main)

        val paymePref = getSharedPreferences("PaymePref", MODE_PRIVATE)
        AppToken = paymePref.getString(APP_TOKEN, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHBJZCI6MX0.wNtHVZ-olKe7OAkgLigkTSsLVQKv_YL9fHKzX9mn9II")!!
        PrivateKey = paymePref.getString(SECRET_KEY, "-----BEGIN PRIVATE KEY-----\n" +
                "    MIIBPAIBAAJBAKWcehEELB4GdQ4cTLLQroLqnD3AhdKiwIhTJpAi1XnbfOSrW/Eb\n" +
                "    w6h1485GOAvuG/OwB+ScsfPJBoNJeNFU6J0CAwEAAQJBAJSfTrSCqAzyAo59Ox+m\n" +
                "    Q1ZdsYWBhxc2084DwTHM8QN/TZiyF4fbVYtjvyhG8ydJ37CiG7d9FY1smvNG3iDC\n" +
                "    dwECIQDygv2UOuR1ifLTDo4YxOs2cK3+dAUy6s54mSuGwUeo4QIhAK7SiYDyGwGo\n" +
                "    CwqjOdgOsQkJTGoUkDs8MST0MtmPAAs9AiEAjLT1/nBhJ9V/X3f9eF+g/bhJK+8T\n" +
                "    KSTV4WE1wP0Z3+ECIA9E3DWi77DpWG2JbBfu0I+VfFMXkLFbxH8RxQ8zajGRAiEA\n" +
                "    8Ly1xJ7UW3up25h9aa9SILBpGqWtJlNQgfVKBoabzsU=\n" +
                "    -----END PRIVATE KEY-----")!!
        PublicKey = paymePref.getString(PUBLIC_KEY, "-----BEGIN PUBLIC KEY-----\n" +
                "   MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKWcehEELB4GdQ4cTLLQroLqnD3AhdKi\n" +
                "   wIhTJpAi1XnbfOSrW/Ebw6h1485GOAvuG/OwB+ScsfPJBoNJeNFU6J0CAwEAAQ==\n" +
                "   -----END PUBLIC KEY-----")!!

//        Log.d("Test - AppToken",AppToken)
//        Log.d("Test - PrivateKey",PrivateKey)
//        Log.d("Test - PublicKey",PublicKey)

        button = findViewById(R.id.button)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonLogout = findViewById(R.id.buttonLogout)
        buttonSetting = findViewById(R.id.buttonSetting)
        buttonReload = findViewById(R.id.buttonReload)
        buttonDeposit = findViewById(R.id.buttonDeposit)
        buttonWithdraw = findViewById(R.id.buttonWithdraw)
        buttonPay = findViewById(R.id.buttonPay)
        textView = findViewById(R.id.textBalance)
        inputUserId = findViewById(R.id.inputUserId)
        inputPhoneNumber = findViewById(R.id.inputPhoneNumber)
        moneyDeposit = findViewById(R.id.moneyDeposit)
        moneyPay = findViewById(R.id.moneyPay)
        moneyWithdraw = findViewById(R.id.moneyWithdraw)
        walletView = findViewById(R.id.walletView)
        spinnerEnvironment = findViewById(R.id.enviromentSpiner)

        var configColor = arrayOf<String>("#75255b", "#9d455f")
        this.payme =
                PayME(this, AppToken, PublicKey, ConnectToken, PrivateKey, configColor, Env.SANDBOX)

        buttonReload.setOnClickListener {
            if (ConnectToken.length > 0) {
                updateWalletInfo()
            }

        }

        buttonLogin.setOnClickListener {
            if (inputUserId.text.toString().length > 0 && inputPhoneNumber.text.toString().length >= 10) {
                val params: MutableMap<String, Any> = mutableMapOf()
                val tz = TimeZone.getTimeZone("UTC")
                val df: DateFormat =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'") // Quoted "Z" to indicate UTC, no timezone offset

                df.setTimeZone(tz)
                val nowAsISO: String = df.format(Date())

                val dataExample =
                    "{\"userId\":\"${inputUserId.text.toString()}\",\"timestamp\":\"${nowAsISO}\",\"phone\":\"${inputPhoneNumber.text.toString()}\"}"

                val connectToken = CryptoAES.encrypt(dataExample, "3zA9HDejj1GnyVK0")
                Log.d("connectToken",connectToken)
                ConnectToken = connectToken
                Toast.makeText(
                    context,
                    "Đăng ký Connect Token thành công",
                    Toast.LENGTH_LONG
                ).show()

                this.payme =
                    PayME(
                        this,
                        AppToken,
                        PublicKey,
                        ConnectToken,
                        PrivateKey,
                        configColor, getEnviromentSelected(spinnerEnvironment.selectedItem.toString())
                    )

                walletView.setVisibility(View.VISIBLE)

            }
        }

        buttonLogout.setOnClickListener {
            inputPhoneNumber.text = null
            inputUserId.text = null
            walletView.setVisibility(View.GONE)
        }

        button.setOnClickListener {
            if (ConnectToken.length > 0) {
                payme.openWallet(
                    Action.OPEN, null, null, null,
                    onSuccess = { json: JSONObject ->
                        println("onSuccess2222" + json.toString())
                    },
                    onError = { message: String ->
                        println("onError" + message)
                    })
            }


        }
        buttonDeposit.setOnClickListener {
            if (ConnectToken.length > 0) {

                val amount = convertInt(moneyDeposit.text.toString())
                payme.openWallet(Action.DEPOSIT, amount, null, null,
                    onSuccess = { json: JSONObject ->
                        println("onSuccess2222" + json.toString())
                    },
                    onError = { message: String ->
                        println("onError" + message)
                    })
            }

        }
        buttonWithdraw.setOnClickListener {
            if (ConnectToken.length > 0) {

                val amount = convertInt(moneyWithdraw.text.toString())

                payme.openWallet(Action.WITHDRAW, amount, null, null,
                    onSuccess = { json: JSONObject ->
                        println("onSuccess2222" + json.toString())
                    },
                    onError = { message: String ->
                        println("onError" + message)
                    })
            }
        }

        buttonPay.setOnClickListener {
            if (ConnectToken.length > 0) {
                val amount = convertInt(moneyPay.text.toString())
                payme.pay(this.supportFragmentManager, amount, "Merchant ghi chú đơn hàng", "", "",
                    onSuccess = { json: JSONObject ->
                        println("onSuccess2222" + json.toString())
                    },
                    onError = { message: String ->
                        println("onError" + message)
                    },
                    onClose = {
                        println("CLOSE")
                    }
                )

            }
        }

        buttonSetting.setOnClickListener {
            val intent = Intent(this, SettingAcitivity::class.java)
            startActivity(intent)
        }
    }
}