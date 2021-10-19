package com.example.testapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import vn.payme.sdk.PayME
import vn.payme.sdk.enums.*
import vn.payme.sdk.model.*
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random


val APP_TOKEN = "APP_TOKEN"
val PUBLIC_KEY = "PUBLIC_KEY"
val ON_LOG = "ON_LOG"
val SECRET_KEY = "SECRET_KEY"
val PRIVATE_KEY = "PRIVATE_KEY"
val APP_PHONE = "APP_PHONE"
val APP_USER_ID = "APP_USER_ID"

val APP_TOKEN_DEFAULT_SANDBOX ="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHBJZCI6MzAsImlhdCI6MTYyMTgyMzQ2N30.02jQIG7fqUckNzQnx0ya52ley4nWCHWt3w6tUrrRAtQ"
val PUBLIC_KEY_DEFAULT_SANDBOX = "-----BEGIN PUBLIC KEY-----\n" +
        "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAO4QQwo0WqZONzlJ5CMWDb6eSrO5q14r\n" +
        "D05Fc6JeC/ZfjdoO+9+G9RZrpa8eh8hIhdJ4siqHKcSiM/xlXIm6ddECAwEAAQ==\n" +
        "-----END PUBLIC KEY-----"
val SECRET_KEY_DEFAULT_SANDBOX = "d0b1240e28a109e052fa34354e9915f9"
val PRIVATE_KEY_DEFAULT_SANDBOX = "-----BEGIN RSA PRIVATE KEY-----\n" +
        "MIIBOgIBAAJBAMRQjlsYp5aR5IliyM/WsK6JtP79wdXCkyZ/PRV1ZcvyWx5/4A6f\n" +
        "9e4G+rGF8tSWjbYs1aRkyd/NY41QX+VBULECAwEAAQJAN5TDKUGKuVOnC8q/JjEX\n" +
        "puLwLr2zsoy7Usv1hGzPnHUK+GtCyROvG88K3EM7ouE2amk0BMJY1XZ8x1KkZnuw\n" +
        "vQIhAPkFALcE+dsV9G8gDGgTBr8PRmqpkinFzIHcev/wwMhjAiEAydFWDoeCwT2d\n" +
        "bbUt/fU/KSaGomp5slt+FZxd9A/tzNsCIQCGj9OBEqlJYCXD3teVbaKZn9F3VcZr\n" +
        "2DzYd6Hnp9sk7QIgfgE7b7rfwnML1bFnU8ZJdxHcwY8lCFzjbe7BIl7HpD0CICB5\n" +
        "KQhd7pUO2s5oPAvuzi30eI2NIISncH0xGxYAS+nu\n" +
        "-----END RSA PRIVATE KEY-----"







class MainActivity : AppCompatActivity() {
    companion object {



        var payme: PayME? = null
        lateinit var context: Context
        var env = Env.SANDBOX
        lateinit var paymePref: SharedPreferences
        var showLog: Boolean = false
    }


    fun convertInt(amount: String): Int {
        try {
            return Integer.parseInt(amount)

        } catch (e: Exception) {
            return 0

        }

    }

    fun openWallet() {
        payme?.openWallet(
            this.supportFragmentManager,
            onSuccess = { json: JSONObject? ->
            },
            onError = { jsonObject, code, message ->
                PayME.showError(message)
                println("code"+code+"message"+message)
                if (code == ERROR_CODE.EXPIRED) {
                    walletView.setVisibility(View.GONE)
                    payme?.logout()
                }

            })

    }


    lateinit var button: Button
    lateinit var buttonLogin: Button
    lateinit var buttonLogout: Button
    lateinit var buttonReload: ImageView
    lateinit var buttonDeposit: Button
    lateinit var buttonPayQR: Button
    lateinit var buttonScanQR: Button
    lateinit var buttonWithdraw: Button
    lateinit var buttonTransfer: Button
    lateinit var buttonScanQr: Button
    lateinit var buttonPayNotAccount: Button
    lateinit var buttonKYC: Button
    lateinit var buttonPay: Button
    lateinit var buttonOpenService: Button
    lateinit var textView: TextView
    lateinit var inputUserId: EditText
    lateinit var inputQRString: EditText
    lateinit var inputPhoneNumber: EditText
    lateinit var moneyDeposit: EditText
    lateinit var moneyPay: EditText
    lateinit var moneyWithdraw: EditText
    lateinit var moneyTransfer: EditText
    lateinit var walletView: LinearLayout
    lateinit var spinnerLanguage: Spinner
    lateinit var spinnerPayCode: Spinner
    lateinit var spinnerPayQRPayCode: Spinner
    lateinit var spinnerScanQRPayCode: Spinner
    lateinit var spinnerService: Spinner
    lateinit var loading: ProgressBar

    var ConnectToken: String =
        "qBpM18YIyB15rdpFFfJpzsUBXNkaQ9rnCAN3asLNCrmEgQoS9YlhEVL8iQWT+6hhLSMs/C6uBUXxqD1PN33yhtfisiynwC1TeGV8TuT5bcdsSdgR+il/apjp886i1HJ3"

    fun updateWalletInfo() {

        payme?.getWalletInfo(onSuccess = { jsonObject ->
            println("onSuccess=" + jsonObject.toString())
            val walletBalance = jsonObject.getJSONObject("Wallet")
            val balance = walletBalance.get("balance")
            val decimal = DecimalFormat("#,###")
            textView.text = "${decimal.format(balance)}đ"
        }, onError = { jsonObject, code, message ->
            PayME.showError(message)
            if (code == ERROR_CODE.ACCOUNT_NOT_ACTIVATED) {
                openWallet()
            }
        })


    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        context = this
        setContentView(R.layout.activity_main)
        paymePref = getSharedPreferences("PaymePref", MODE_PRIVATE)

        // Get value of keys
        showLog = paymePref.getBoolean(ON_LOG, false)!!

        val userId = paymePref.getString(APP_USER_ID, "1001")
        val phoneNumber = paymePref.getString(APP_PHONE, "0929000200")

        button = findViewById(R.id.button)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonLogout = findViewById(R.id.buttonLogout)
        buttonKYC = findViewById(R.id.buttonKYC)
        buttonScanQr = findViewById(R.id.buttonScanQr)
        buttonReload = findViewById(R.id.buttonReload)
        buttonDeposit = findViewById(R.id.buttonDeposit)
        buttonTransfer = findViewById(R.id.buttonTransfer)
        buttonPayNotAccount = findViewById(R.id.buttonPayNotAccount)
        loading = findViewById(R.id.loading)
        buttonWithdraw = findViewById(R.id.buttonWithdraw)
        buttonPay = findViewById(R.id.buttonPay)
        buttonScanQR = findViewById(R.id.buttonScanQr)
        buttonPayQR = findViewById(R.id.buttonPayQR)
        textView = findViewById(R.id.textBalance)
        inputUserId = findViewById(R.id.inputUserId)
        inputQRString = findViewById(R.id.inputPayQR)
        inputPhoneNumber = findViewById(R.id.inputPhoneNumber)
        moneyDeposit = findViewById(R.id.moneyDeposit)
        moneyPay = findViewById(R.id.moneyPay)
        moneyWithdraw = findViewById(R.id.moneyWithdraw)
        moneyTransfer = findViewById(R.id.moneyTransfer)
        walletView = findViewById(R.id.walletView)
        spinnerPayCode = findViewById(R.id.payCodeSpiner)
        spinnerPayQRPayCode = findViewById(R.id.payQrPayCodeSpinner)
        spinnerScanQRPayCode= findViewById(R.id.scanQrPayCodeSpinner)
        spinnerLanguage = findViewById(R.id.languageSpinner)
        spinnerService = findViewById(R.id.serviceSpinner)
        buttonOpenService = findViewById(R.id.buttonOpenService)
        inputUserId.setText(userId)
        inputPhoneNumber.setText(phoneNumber)
        inputUserId.addTextChangedListener {
            if (walletView.visibility == View.VISIBLE) {
                walletView.visibility = View.GONE
            }
        }
        inputPhoneNumber.addTextChangedListener {
            if (walletView.visibility == View.VISIBLE) {
                walletView.visibility = View.GONE
            }
        }
        var configColor = arrayOf<String>("#6756d6", "#6756d6")


        buttonReload.setOnClickListener {
            if (ConnectToken.length > 0) {
                updateWalletInfo()
            }

        }
        buttonPayQR.setOnClickListener {
            payme?.payQRCode(supportFragmentManager,inputQRString.text.toString(),spinnerPayQRPayCode.selectedItem.toString(),null,true,onSuccess = {

            },onError = {jsonObject, i, s ->
                PayME.showError(s)
            })
        }
        buttonScanQr.setOnClickListener {
            payme?.scanQR(this.supportFragmentManager,spinnerScanQRPayCode.selectedItem.toString(),null,onSuccess = {

            },onError = {jsonObject, i, s ->  })
        }
        buttonOpenService.setOnClickListener {
            payme?.openService(supportFragmentManager,Service(spinnerService.selectedItem.toString(),""),onSuccess = {},onError = {jsonObject, i, s ->
                PayME.showError(s)
            })
        }

        buttonKYC.setOnClickListener {
            payme?.openKYC(this.supportFragmentManager, onSuccess = {
                println("mo kyc thanh cong")

            }, onError = { jsonObject, i, s ->
                println("code"+i+"message"+s)

                PayME.showError(s)
            })
        }
        var list = arrayListOf<String>()
        list.add(Env.DEV.toString())
        list.add(Env.PRODUCTION.toString())
        list.add(Env.SANDBOX.toString())
        spinnerLanguage.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                if(payme!=null){
                    payme?.setLanguage(context,if(spinnerLanguage.selectedItem.toString() == LANGUAGES.VN.toString()) LANGUAGES.VN else LANGUAGES.EN)
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        })

        buttonLogin.setOnClickListener {


            if (inputPhoneNumber.text.toString().length >= 10 && inputUserId.text.toString().length > 0 && (inputPhoneNumber.text.toString().length == 10 || inputPhoneNumber.text.toString().length == 0) && loading.visibility != View.VISIBLE) {
                val params: MutableMap<String, Any> = mutableMapOf()
                val tz = TimeZone.getTimeZone("UTC")
                val df: DateFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'") // Quoted "Z" to indicate UTC, no timezone offset

                df.setTimeZone(tz)
                val nowAsISO: String = df.format(Date())

                val dataExample =
                    "{\"userId\":\"${inputUserId.text.toString()}\",\"timestamp\":\"${nowAsISO}\",\"phone\":\"${inputPhoneNumber.text.toString()}\"}"

                val connectToken = CryptoAES.encrypt(
                    dataExample,
                   SECRET_KEY_DEFAULT_SANDBOX
                )
                ConnectToken = connectToken
                loading.visibility = View.VISIBLE
                println("env"+env.toString())
                payme =
                    PayME(
                        this,
                        APP_TOKEN_DEFAULT_SANDBOX,
                      PUBLIC_KEY_DEFAULT_SANDBOX,
                        ConnectToken,
                      PRIVATE_KEY_DEFAULT_SANDBOX,
                        configColor,
                        if(spinnerLanguage.selectedItem.toString() == LANGUAGES.VN.toString()) LANGUAGES.VN else LANGUAGES.EN,
                        env,
                        showLog
                    )
                payme?.login(onSuccess = { accountStatus ->
                    println("accountStatus" + accountStatus)
                    if (accountStatus == AccountStatus.NOT_ACTIVATED) {
                        //Tài khoản chưa kich hoạt
                    }
                    if (accountStatus == AccountStatus.NOT_KYC) {
                        //Tài khoản chưa định danh
                    }
                    if (accountStatus == AccountStatus.KYC_APPROVED) {
                        //Tài khoản đã
                    }
                    payme?.getAccountInfo(onSuccess = { data ->
                        println("getAccountInfo" + data)


                    }, onError = { jsonObject, i, s ->

                    })
                    payme?.getSupportedServices(onSuccess = {arrayList ->
                        var list = arrayListOf<String>()

                        arrayList?.forEach { service ->
                            list.add(service.code)
                        }
                        val spinnerAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list)
                        spinnerService.adapter = spinnerAdapter

                    },onError = {jsonObject, i, s ->

                    })

                    loading.visibility = View.GONE
                    paymePref.edit().putString(APP_USER_ID, inputUserId.text.toString()).commit()
                    paymePref.edit().putString(APP_PHONE, inputPhoneNumber.text.toString())
                        .commit()
                    walletView.setVisibility(View.VISIBLE)
                    Toast.makeText(
                        context,
                        "Đăng ky ConnectToken thành công",
                        Toast.LENGTH_LONG
                    ).show()
                },
                    onError = { jsonObject, code, message ->
                        loading.visibility = View.GONE
                        PayME.showError(message)

                    })


            }


        }




        buttonLogout.setOnClickListener {
            if (payme != null) {
                payme?.logout()
                inputPhoneNumber.text = null
                inputUserId.text = null
                walletView.setVisibility(View.GONE)
            }

        }

        button.setOnClickListener {
            if (ConnectToken.length > 0) {
                payme?.openWallet(
                    this.supportFragmentManager,
                    onSuccess = { json: JSONObject? ->
                    },
                    onError = { jsonObject, code, message ->
                        PayME.showError(message)
                        println("code"+code+"message"+message)

                        if (code == ERROR_CODE.EXPIRED) {
                            walletView.setVisibility(View.GONE)
                            payme?.logout()
                        }
                    })
            }




        }

        buttonDeposit.setOnClickListener {


            val amount = convertInt(moneyDeposit.text.toString())
            payme?.deposit(
                this.supportFragmentManager,
                amount,
                true,
                onSuccess = { json: JSONObject? ->
                },
                onError = { jsonObject, code, message ->
                    PayME.showError(message)
                    println("code"+code+"message"+message)

                    if (code == ERROR_CODE.EXPIRED) {
                        walletView.setVisibility(View.GONE)
                    }
                    if (code == ERROR_CODE.ACCOUNT_NOT_KYC || code == ERROR_CODE.ACCOUNT_NOT_ACTIVATED) {
                        openWallet()
                    }
                })


        }
        buttonWithdraw.setOnClickListener {

            val amount = convertInt(moneyWithdraw.text.toString())

            payme?.withdraw(this.supportFragmentManager,amount, false,
                onSuccess = { json: JSONObject? ->
                },
                onError = { jsonObject, code, message ->
                    println("code"+code+"message"+message)

                    PayME.showError(message)
                    if (code == ERROR_CODE.EXPIRED) {
                        walletView.setVisibility(View.GONE)
                    }
                    if (code == ERROR_CODE.ACCOUNT_NOT_KYC || code == ERROR_CODE.ACCOUNT_NOT_ACTIVATED) {
                        openWallet()
                    }
                })
        }
        buttonTransfer.setOnClickListener {

            val amount = convertInt(moneyTransfer.text.toString())

            payme?.transfer(this.supportFragmentManager,amount, "chuyen tien cho ban nhe", true,
                onSuccess = { json: JSONObject? ->
                    println("onSuccesstransfer")
                },
                onError = { jsonObject, code, message ->
                    PayME.showError(message)
                    println("code"+code+"message"+message)

                    if (code == ERROR_CODE.EXPIRED) {
                        walletView.setVisibility(View.GONE)
                    }
                    if (code == ERROR_CODE.ACCOUNT_NOT_KYC || code == ERROR_CODE.ACCOUNT_NOT_ACTIVATED) {
                        openWallet()
                    }
                })
        }

        buttonPay.setOnClickListener {
            val nextValues = Random.nextInt(0, 100000)

            val amount = convertInt(moneyPay.text.toString())

            val storeId: Long =
                10581207
            val infoPayment =
                InfoPayment(
                    "PAY",
                    amount,
                    "Nội dung đơn hàng",
                    nextValues.toString(),
                    storeId,
                    "OpenEWallet",
                    ""
                )
            payme?.pay(this.supportFragmentManager, infoPayment, true,spinnerPayCode.selectedItem.toString(),"apptest://payment.vnpay.result",
                onSuccess = { json: JSONObject? ->
                    println("jsononSuccess"+json)
                },
                onError = { jsonObject, code, message ->

                    if (message != null && message.length > 0) {
                        PayME.showError(message)
                    }
                    if (code == ERROR_CODE.EXPIRED) {
                        walletView.setVisibility(View.GONE)
                    }
                    if (code == ERROR_CODE.ACCOUNT_NOT_KYC || code == ERROR_CODE.ACCOUNT_NOT_ACTIVATED) {
                        openWallet()
                    }
                }

            )





        }


    }



}
