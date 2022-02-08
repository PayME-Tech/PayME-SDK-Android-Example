PayME Android SDK is a set of libraries to easily integrate PayME E-Wallet into your application. PayME Android SDK includes the following main functions:

- Login system, eKYC via PayME wallet account
- Deposit, withdrawal and payment functions are provided by PayME.
- PayME's available services.

**Some terms**

| | Name | Explanation |
| ---- | ------- | --------------------------------------------------- ---------- |
| 1 | MC app | As the merchant's app, it will integrate the SDK to perform the PayME wallet payment function. |
| 2 | SDK | Is a toolkit to support the integration of PayME wallet into the app system. |
| 3 | backend | An integrated system that supports an app, server or api that supports |
| 4 | AES | AES256 CBC PKCS5 data encryption function. [Reference](https://en.wikipedia.org/wiki/Advanced_Encryption_Standard) |
| 5 | RSA | RSA data encryption algorithm. |
| 6 | IPN | Instant Payment Notification , used to notify between the app's backend and PayME's backend |

**PayME Android Sample App**

You can refer to [Android Sample App](https://github.com/PayME-Tech/PayME-SDK-Android-Example) to know how to integrate SDK into your application .

**Integration steps:**

***Step 1 :***

**Update file build.gradle project **

```kotlin
allprojects {
repositories {
google()
jcenter()
maven {
url "https:// plugins.gradle.org/m2/"
}
maven {
url "https://jitpack.io"
}
}
}
```

- **Update file build.gradle module **

```java
android {
...
packagingOptions {
exclude 'META-INF/DEPENDENCIES'
exclude 'META- INF/LICENSE'
exclude 'META-INF/LICENSE.txt'
exclude 'META-INF/license.txt'
exclude 'META-INF/NOTICE'
exclude 'META-INF/NOTICE.txt'
exclude 'META-INF/notice. txt'
exclude 'META-INF/ASL2.0'
}
...
}
dependencies {
...
implementation 'com.github.PayME-Tech:PayME-SDK-Android:0.9.37'
...
}
```

- **Update Android Manifest file **
-
Grant access to contacts when using phone top-up and money transfer
```xml
...

...
```

# How to use the SDK:

The PayME system will provide the integrated app with the following information:

- **PublicKey** : Used to encrypt data, the built-in app needs to pass it to the SDK for encryption.
- **AppToken** : AppToken provides a unique identifier for each app MC, which needs to be passed to the SDK for encryption
- **SecretKey** : Used to encrypt and authenticate data in the backend system for the integrated app.

The App side will provide the PayME system with the following information:

- **AppPublicKey** : It will be sent through PayME's backend system for encryption.
- **AppPrivateKey**: Will pass in PayME SDK to perform decryption.

Encryption standard: RSA-512bit.

### Initialize PayME SDK:

Before using PayME SDK need to call the initialization method only once to initialize the SDK.

```kotlin
import vn.payme.sdk.model.Env
val configColor = arrayOf("#75255b","#9d455f"}
val payme: PayME = PayME(context, AppToken, PublicKey,ConnectToken, AppPrivateKey, configColor, LANGUAGES. VI, Env.SANDBOX, false)
// after initialization can call payme.login() here
```

### login()

There are 2 cases

- Used to login for the first time right after initializing PayME .-
  Used when the accessToken expires, when calling the function of the SDK that returns the error code ERROR_CODE.EXPIRED, now the MC app needs to call login again to get the accessToken then manipulate the callback to the same functions depending on the flow of the MC app .

After calling login() successfully, then call other functions of the SDK ( openWallet, pay ... )

```kotlin
public fun login(
onSuccess:(AccountStatus)->Unit,
onError: (JSONObject?, Int ) , String?) -> Unit
}
```

Example:

```kotlin
public fun openKYC(
fragmentManager: FragmentManager,
        onSuccess: (JSONObject?) -> Unit,
        onError: (JSONObject?, Int, String?) -> Unit
```
Ví dụ :

```kotlin
 payme?.openHistory(supportFragmentManager,onSuccess = {

            },onError = {jsonObject, i, s ->
                PayME.showError(s)
            })
```

### openKYC() - Open account identifier modal
This function is called when from the built-in app when you want to open the account identifier modal (requires account to be anonymous)

```kotlin
public fun openKYC(
onSuccess: (JSONObject?) -> Unit,
onError:(JSONObject?, Int?, String) -> Unit)
```
Example:

```kotlin
payme.openKYC(
onSuccess = { json: JSONObject ->
},
onError = { jsonObject, code, message ->
}
)
```



### deposit() - Recharge

```kotlin
public fun deposit(
amount : Int,
closeDepositResult: Boolean,
onSuccess: (JSONObject) -> Unit,
onError:(JSONObject ?, Int, String?) -> Unit)
```
closeDepositResult : closes sdk screen when there is successful or failed deposit result

Example :

```kotlin
payme.deposit(
amount,
false,
onSuccess = { json: JSONObject ->
},
onError = { jsonObject , code, message ->

ME.showError(message)
if (code == ERROR_CODE.EXPIRED) {
walletView.setVisibility(View.GONE)
payme.logout()
}
//ERROR_CODE.ACCOUNT_NOT_KYC : unknown account
// ERROR_CODE .ACCOUNT_NOT_ACTIVETES : not activated
//need to call openWallet function again

if (code == ERROR_CODE.ACCOUNT_NOT_KYC || code == ERROR_CODE.ACCOUNT_NOT_ACTIVETES) {
openWallet()
}
}
)
```

This function has the same meaning as when calling openWallet with action **Action.Deposit.**

### withdraw() - Withdrawal

```kotlin
public fun withdraw(
amount: Int,
closeWithdrawResult: Boolean,
onSuccess : (JSONObject) -> Unit,
onError: (JSONObject?, Int, String?) -> Unit)
```
closeWithdrawResult : closes sdk screen when successful or failed withdrawal result


Example:

``` kotlin
payme.withdraw(
amount,
false,
onSuccess = { json: JSONObject ->
},
onError = { jsonObject, code, message ->
PayME.showError(message)
if (code == ERROR_CODE.EXPIRED) {
walletView.setVisibility(View.GONE)
payme.logout()
}

if (code == ERROR_CODE.ACCOUNT_NOT_KYC || code == ERROR_CODE.ACCOUNT_NOT_ACTIVETES) {
openWallet()
}
})
```

### transfer() - Transfer money

```kotlin
public fun transfer(
amount: Int,
description: String,
closeTransferResult: Boolean,
onSuccess: (JSONObject?) -> Unit,
onError: (JSONObject?, Int, String?) -> Unit
)
``
amount: Amount to transfer

description : Notes when transferring money

closeTransferResult : close the sdk screen when there is a successful or failed transfer result


Example:

```kotlin
payme.transfer( 
		amount,
		"chuyen tien cho ban nhe",
		false,
                onSuccess = { json: JSONObject ->
                },
                onError = { jsonObject, code, message ->
                    PayME.showError(message)
                    if (code == ERROR_CODE.EXPIRED) {
                        walletView.setVisibility(View.GONE)
                        payme.logout()
                    }

                    if (code == ERROR_CODE.ACCOUNT_NOT_KYC || code == ERROR_CODE.ACCOUNT_NOT_ACTIVETES) {
                        openWallet()
                    }
                })
```

### getSupportedServices()

This app can be used after initializing the SDK for a list of services that PayME is providing

```kotlin
  public fun getSupportedServices(onSuccess: (ArrayList<Service>?) -> Unit,onError: (JSONObject?, Int?, String) -> Unit)  {
      
    }

```
### setLanguage()
Switch language of sdk
```kotlin
fun setLanguage(context: Context,language: LANGUAGES){
}
```

### openService()

This function is called when from the built-in app when you want to call a service that PayME also provides by passing the Service parameter as above

```kotlin
public fun openService(
fragmentManager: FragmentManager,
service: Service,
onSuccess: (JSONObject?) -> Unit,
onError: (JSONObject?, Int, String?) -> Unit
)

```

Example:

```kotlin
val service : Service = payme?.getListService()?.get(2)!!
payme?.openService(
this.supportFragmentManager,
service,
onSuccess = { json: JSONObject? -> },
onError = { jsonObject, code, message ->
PayME.showError(message)
if (code == ERROR_CODE.EXPIRED) {
walletView. setVisibility(View.GONE)
payme?.logout()
}
})
```




### pay() - Payment

This function is used when the app needs to pay an amount from the activated PayME wallet.

```kotlin
public fun pay(
fragmentManager: FragmentManager,
infoPayment: InfoPayment,
isShowResultUI: Boolean,
payCode: String,
onSuccess: ((JSONObject?) -> Unit)?,
onError: ((JSONObject?, Int, String?) - > Unit)?,
)


class InfoPayment {
var action : String? = null
var amount : Int? = null
var note : String? = null
var orderId : String? = null
var storeId : Long ?= null
var type : String? = null
var referExtraData : String? = null
var userName : String ?= null

}

- action: transaction type ( 'PAYMENT' => payment)
- amount: payment amount
- note: Description of counterparty transaction
- orderId: transaction code of counterparty, needs to be unique on each transaction
- storeId: ID of the paying public store that made the payment transaction
- type: OpenEWallet
- referExtraData: Extra Information (extraData) is an asset defined as a string , contains additional information of a transaction that the counterparty wants to receive when completing a transaction with PAYME.
If Merchant does not need IPN to add his custom data, he can skip
- userName: login account name on mc page

Note: Only userName or storeId, if using userName, let storeId = null and vice versa


-When paying with PayME wallet, it requires an activated account, identifier and balance in the wallet must be greater than the payment amount
account information obtained through the getAccountInfo() function
balance information obtained through the getWalletInfo() function



```

Example:

```kotlin
val amount = convertInt(moneyPay.text.toString())
val nextValues = List(10) { Random.nextInt(0, 100000) }
val infoPayment = InfoPayment(
                    "PAY",
                    amount,
                    "Nội dung đơn hàng",
                    nextValues.toString(),
                    storeId,
                    "OpenEWallet",
                    "",
		    null
                )
payme?.pay( this.supportFragmentManager,
	    infoPayment,
	    true,
	    PAY_CODE.PAYME,
	    null,
            onSuccess = { json: JSONObject? -> },
            onError = { jsonObject, code, message ->
                        if (message != null && message.length > 0) {
                            PayME.showError(message)
                        }
                        if (code == ERROR_CODE.EXPIRED) {
                            walletView.setVisibility(View.GONE)
                            payme?.logout()
                        }
                        if (code == ERROR_CODE.ACCOUNT_NOT_KYC || code == ERROR_CODE.ACCOUNT_NOT_ACTIVETES) {
                            openWallet()
                        }
                    }
            )


```

List of PAY_CODE

| **Parameters** | **Required** | **Explanation** |
| -------------- | ------------ | --------------------------------------------------- ---------- |
| PAYME| Yes | PayME wallet payment |
| ATM | Yes | Domestic ATM card payment|
| CREDIT | Yes | Credit Card |
| MANUAL_BANK | Yes | Bank transfer payment |
| MANUAL_BANK | Yes | Bank transfer payment |

### scanQR() - Open QR code scanning for payment

```kotlin
fun scanQR(
fragmentManager: FragmentManager,
payCode: String,
onSuccess: (JSONObject?) -> Unit,
onError: (JSONObject?, Int, String ?) -> Unit
) : Unit
```
qr format:
```kotlin
val qrString = "{$type}|${storeId}|${action}|${amount}|${note}|${orderId}|${userName}"
```

Example:
` ``kotlin
val qrString = "OPENEWALLET|54938607|PAYMENT|20000|Chuyentien|2445562323|taikhoan"
```

- action: transaction type ( 'PAYMENT' => payment)
- amount: payment amount
- note: Model transaction description from the counterparty side
- orderId: the partner's transaction code, which needs to be unique on each transaction
- storeId: ID of the payment public store that made the payment transaction
- type: OPENEWALLET
- payCode : in the payCode table above

### payQRCode() - payment QR code
```kotlin
fun payQRCode(
fragmentManager: FragmentManager,
qr: String,
payCode: String,
isShowResultUI:Boolean,
onSuccess: (JSONObject?) -> Unit,
onError:(JSONObject?, Int, String?) -> Unit)
```

- qr: QR Code for payment ( Specify QR format like scanQR() )
- isShowResultUI: Do you want to display transaction results or not
- payCode : in the above payCode table


### getWalletInfo() - **Get wallet information**

```kotlin
public fun geWalletInfo(onSuccess: (JSONObject) -> Unit,onError:(JSONObject?, Int?, String) -> Unit)
```

- In case of error, the function will return a message every time at the onError function, then the app can display the balance as 0.
- In the successful case, the SDK returns the following information:

```kotlin
{
  "Wallet": {
    "balance": 111,
    "detail": {
      "cash": 1,
      "lockCash": 2
    }
  }
}
```

### close() - Close UI

This function is used for the integrated app to close the SDK's UI during payment or openWallet

```kotlin
 fun close(){
 }
```
**\*balance\*** : The built-in app can use the value in the balance key to display, other fields are currently unused.

**\*detail.cash :\*** Money can be used

**\*detail.lockCash:\*** money is locked