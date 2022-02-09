PayME Android SDK is a set of libraries to easily integrate PayME E-Wallet into your application. PayME Android SDK includes the following main functions:

- Login system, eKYC via PayME wallet account
- Deposit, withdrawal and payment functions are provided by PayME.
- PayME's available services.

**Some terms**

| | Name | Explanation |
| ---- | ------- | ------------------------------------------------------------- |
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
- **AppToken** : AppToken is uniquely provided for each app MC to identifier, which needs to be passed to the SDK for encryption
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
public fun loginExample(){
  payme.login(		onSuccess = { accountStatus ->
                    if(accountStatus == AccountStatus.NOT_ACTIVATED){
                        //Account not activated
			// call fun openWallet() to activate account
                    }
                    if(accountStatus == AccountStatus.NOT_KYC){
                        //Account not identified
			// call fun openKYC() to identifier account

                    }
		      if (accountStatus == AccountStatus.KYC_REVIEW) {
                        //Account has sent identification information, pending approval
                    }
                    if (accountStatus == AccountStatus.KYC_REJECTED) {
                        //Identity request is denied
			// call fun openKYC() to identifier account

                    }
                    if(accountStatus == AccountStatus.KYC_APPROVED){
                        //Identified account
                      walletView.setVisibility(View.VISIBLE)
		    
               	    },
                    onError = { jsonObject, code, message ->
                        PayME.showError(message)
                    })
}
```

configColor : is the color parameter to change the color of PayME wallet transactions, data type format is #rrggbb. If 2 colors are transmitted, the color will be color gradient according to the 2 input colors.

[![img](https://github.com/PayME-Tech/PayME-SDK-Android-Example/raw/main/fe478f50-e3de-4c58-bd6d-9f77d46ce230.png?raw=true)](https://github.com/PayME-Tech/PayME-SDK-Android-Example/blob/main/fe478f50-e3de-4c58-bd6d-9f77d46ce230.png?raw=true)

How to create **connectToken**:

connectToken is needed to pass to call api to PayME and will be generated from the integrated app's backend. The structure is as follows:
AES algorithm, mode CBC,key size=256 bit, PKCS5Padding
```kotlin
connectToken = AES256("{ timestamp: "2021-01-20T06:53:07.621Z", userId : "ABC", phone : "0909998877" }" , secretKey )
```
Create connectToken including KYC information (For partners with their own KYC system)
```kotlin
connectToken = AES256("{ timestamp: "2021-01-20T06:53:07.621Z", userId : "ABC", phone : "0909998877",   kycInfo: {
        
            fullname :"Nguyễn Văn A" 
            gender: "MALE"
            birthday:"1995-01-20T06:53:07.621Z"
            address: "15 Nguyễn cơ thạch",
            identifyType:"CMND",
            identifyNumber: "142744332",
            issuedAt: "2013-01-20T06:53:07.621Z",
            placeOfIssue: "Hồ Chí Minh",
            video: "https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_480_1_5MG.mp4",
            face: "https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg",
            image: {
              front: "https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg",
              back: "https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg"
            }}
         }" , secretKey )
```












| **Parameters** | **Required** | **Explanation** |
| -------------- | ------------ | ------------------------------------------------------------ |
| **timestamp**  | Yes          | ConnectToken's creation time in the format iSO 8601 , used to determine the timeout time of connectToken. Example: 2021-01-20T06:53:07.621Z |
| **\*userId\*** | Yes          | is a unique fixed value corresponding to each customer account in the service, usually this value is provided by the integrated system server for the PayME SDK |
| **\*phone\***  | Yes           | Phone number of the system integrator |

**\*AES\*** is the encryption function according to the AES algorithm. Depending on the language on the server, the system side uses the corresponding library. See more here https://en.wikipedia.org/wiki/Advanced_Encryption_Standard

Parameter KycInfo

| **Parameters** | **Required** | **Explanation**                                              |
| -------------- | ------------ | ------------------------------------------------------------ |
| fullname | Yes          | Fullname |
| gender | Yes          |  Gender ( MALE/FEMALE) |
| address | Yes          |  Address |
| identifyType | Yes          |   Type of the identify document (ID card/CCCD) |
| identifyNumber | Yes          |   Number of identify documents  |
| issuedAt | Yes          |   Registration Date|
| placeOfIssue | Yes          |  Place of issue |
| video | No          |   url to video |
| face | No          |   url to face photo |
| front | No          |   url to the photo of the front of the identify document |
| back | No          |   url to the photo of the back of the identify document |


## Error codes of PayME SDK

| **Constant**   | **Error code** | **Explanation**                                               |
| :------------ | :----------- | :----------------------------------------------------------- |
| <code>EXPIRED</code> | <code>401</code>          | ***token*** is expired or another device is logged into the account. Recall function login () to continue the operation |
| <code>NETWORK</code>  | <code>-1</code>          | Connection error |
| <code>SYSTEM</code>   | <code>-2</code>           | System error |
| <code>LIMIT</code>   | <code>-3</code>           | Payment amount is over or less than transaction limit |
| <code>ACCOUNT_NOT_ACTIVATED</code>   | <code>-4</code>           | Account is not activated |
| <code>ACCOUNT_NOT_KYC</code>   | <code>-5</code>           | Account is not identified |
| <code>PAYMENT_ERROR</code>   | <code>-6</code>          | Payment failed |
| <code>PAYMENT_PENDING</code>   | <code>-11</code>          | Payment pending |
| <code>ERROR_KEY_ENCODE</code>   | <code>-7</code>           | Encryption/decryption error |
| <code>USER_CANCELLED</code>   | <code>-8</code>          | User canceled payment |
| <code>ACCOUNT_NOT_LOGIN</code>   | <code>-9</code>           | Account is not logged in |
| <code>BALANCE_ERROR</code>   | <code>-10</code>           | Error when paying with PayME wallet but the balance in the wallet is not enough |
| <code>ACCOUNT_ERROR</code>   | <code>-12</code>           | Account is locked or phone number is not transmitted |


## Functions of PayME SDK

### getAccountInfo()

App can use this function after initializing SDK to know the link status to PayME wallet.

```kotlin
public fun getAccountInfo(
        onSuccess: (JSONObject?) -> Unit,
        onError: (JSONObject?, Int, String?) -> Unit
        )
```

Example:

```kotlin
      payme?.getAccountInfo(
        onSuccess = { json: JSONObject? ->
        },
        onError = { jsonObject, code, message ->
                        PayME.showError(message)
                        if (code == ERROR_CODE.EXPIRED) {
                            walletView.setVisibility(View.GONE)
                            payme?.logout()
                        }
                    })
```

**openWallet() - Open the PayME wallet's functions UI**

```kotlin
public fun openWallet(onSuccess: (JSONObject)->Unit, onError:(JSONObject?, Int?, String) -> Unit )

```

This function is called when the intergrated app want to call a PayME function by passing the Action parameter as above.

| **Parameters** | **Required** | **Explanation**                                               |
| :---------- | ------------ | ------------------------------------------------------------ |
| onSuccess   | Yes          | Callback when transaction is succeeded from PayME SDK |
| onError     | Yes          | Callback when an error occurs while calling PayME SDK |

**Example :**

```kotlin
payme.openWallet(
	onSuccess = { json: JSONObject ->
              },
	onError = { jsonObject, code, message ->
			PayME.showError(message)
                        //Error when the access token is expired
                        if (code == ERROR_CODE.EXPIRED) {
                            walletView.setVisibility(View.GONE)
                            payme.logout()
			    // or can call payme.login() to automatically re-login because when calling login, payme.logout() has been called before.
			    // openWallet() can then be called again to reopen the PayME wallet UI.
			}
		   }
		 )
}
```

**openHistory() - Open the Account's Transaction History  **
Requires an activated and identified account to use this function
```kotlin
public fun openKYC(
fragmentManager: FragmentManager,
        onSuccess: (JSONObject?) -> Unit,
        onError: (JSONObject?, Int, String?) -> Unit
```
Example :

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
onError: ((JSONObject?, Int, String?) -> Unit)?,
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


-When paying with PayME wallet, it requires an activated identified account, and balance in the wallet must be greater than the payment amount
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
| -------------- | ------------ | ------------------------------------------------------------- |
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