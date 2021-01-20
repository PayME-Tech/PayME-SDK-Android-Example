PayME SDK là bộ thư viện để các app có thể tương tác với PayME Platform. PayME SDK bao gồm các chức năng chính như sau:

- Hệ thống đăng nhập, eKYC thông qua tài khoản ví PayME
- Hỗ trợ app lấy thông tin số dư ví PayME
- Chức năng nạp rút từ ví PayME.

**Một số thuật ngữ**

|     | Name    | Giải thích                                                                                               |
| --- | ------- | -------------------------------------------------------------------------------------------------------- |
| 1   | app     | Là app mobile iOS/Android hoặc web sẽ tích hợp SDK vào để thực hiện chức năng thanh toán ví PayME.       |
| 2   | SDK     | Là bộ công cụ hỗ trợ tích hợp ví PayME vào hệ thống app.                                                 |
| 3   | backend | Là hệ thống tích hợp hỗ trợ cho app, server hoặc api hỗ trợ                                              |
| 4   | AES     | Hàm mã hóa dữ liệu AES256 PKCS5. [Tham khảo](https://en.wikipedia.org/wiki/Advanced_Encryption_Standard) |
| 5   | RSA     | Thuật toán mã hóa dữ liệu RSA.                                                                           |
| 6   | IPN     | Instant Payment Notification , dùng để thông báo giữa hệ thống backend của app và backend của PayME      |

**Bước 1 :**

Tích hợp thư viện hỗ trợ. Dung lượng 472.76 KB

**File build.gradle Project**

```kotlin
allprojects {
  repositories {
    google()
    jcenter()
    maven {
      url  "https://dl.bintray.com/payme/maven"
    }
   maven {
      url "https://plugins.gradle.org/m2/"
    }
  maven {
      url "https://jitpack.io"
  }
 }
}
```

- **File build.gradle Module**

```java
dependencies {
...
/ thư viện chính
  implementation 'vn.payme.sdk:payme-sdk:0.2.3'
  // thư viện kèm theo
  ...
  implementation 'com.android.volley:volley:1.1.1'
  implementation 'org.greenrobot:eventbus:3.0.0'
  implementation 'com.airbnb.android:lottie:3.0.3'
  implementation 'com.google.android.material:material:1.2.1'
  implementation 'com.google.zxing:core:3.3.3'
  implementation ('com.journeyapps:zxing-android-embedded:3.6.0') { transitive = false }

  implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.0'
  implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9'
  implementation 'com.madgag.spongycastle:prov:1.58.0.0'
  implementation 'com.squareup.picasso:picasso:2.71828'

  implementation 'com.github.GrenderG:Toasty:1.5.0'
  implementation 'com.otaliastudios:cameraview:2.6.4'
  implementation "androidx.fragment:fragment:1.2.5"

  def fragment_version = "1.2.5"

  // Java language implementation
  // Kotlin
  implementation "androidx.fragment:fragment-ktx:$fragment_version"
  implementation "androidx.cardview:cardview:1.0.0"
  implementation "com.theartofdev.edmodo:android-image-cropper:2.8.+"
  implementation 'androidx.constraintlayout:constraintlayout:2.0.4'
  implementation 'androidx.appcompat:appcompat:1.2.0'

...
}
```

- **Android Manifests**

  ```xml
  <uses-sdk tools:overrideLibrary="com.google.zxing.client.android" />

  <uses-permission android:name="android.permission.CAMERA" />
  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.CAMERA" />
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.WRITE_INTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.READ_INTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
  <application android:requestLegacyExternalStorage="true"
  ```

# Cách sử dụng SDK:

Hệ thống PayME sẽ cung cấp cho app tích hợp các thông tin sau:

- **PublicKey** : Dùng để mã hóa dữ liệu, app tích hợp cần truyền cho SDK để mã hóa.
- **AppToken** : AppId cấp riêng định danh cho mỗi app, cần truyền cho SDK để mã hóa
- **SecretKey** : Dùng đã mã hóa và xác thực dữ liệu ở hệ thống backend cho app tích hợp.

Bên App sẽ cung cấp cho hệ thống PayME các thông tin sau:

- **AppPublicKey** : Sẽ gửi qua hệ thống backend của PayME dùng để mã hóa.
- **AppPrivateKey**: Sẽ truyền vào PayME SDK để thực hiện việc giải mã.

Chuẩn mã hóa: RSA-512bit.

### Khởi tạo PayME SDK:

Trước khi sử dụng PayME SDK cần gọi phương thức khởi tạo một lần duy nhất để khởi tạo SDK.

```
import vn.payme.sdk.model.Env
val configColor = arrayOf<String>("#75255b","#9d455f"}
val payme: PayME = PayME(context, AppToken, PublicKey,ConnectToken, AppPrivateKey, configColor, Env.SANDBOX, false)
// sau khi khởi tạo có thể gọi payme.login() tại đây
```

### login()

Có 2 trường hợp

- Dùng để login lần đầu tiên ngay sau khi khởi tạo PayME.
- Dùng khi accessToken hết hạn, khi gọi hàm của SDK mà trả về mã lỗi ERROR_CODE.EXPIRED, lúc này app cần gọi login lại để lấy accessToken dùng cho các chức năng khác.

Sau khi gọi login() thành công rồi thì mới gọi các chức năng khác của SDK ( openWallet, pay ... )

```kotlin
public fun login(
  onSuccess:(JSONObject)->Unit,
  onError: (JSONObject?, Int?, String) -> Unit
}
```

Ví dụ:

```kotlin
public fun loginExample(){
  payme.loggin(onSuccess = { jsonObject ->
                    walletView.setVisibility(View.VISIBLE)
                },
                    onError = { jsonObject, code, message ->
                        PayME.showError(message)
                    })
}
```

configColor : là tham số màu để có thể thay đổi màu sắc giao dịch ví PayME, kiểu dữ liệu là chuỗi với định dạng #rrggbb. Nếu như truyền 2 màu thì giao diện PayME sẽ gradient theo 2 màu truyền vào.

[![img](https://github.com/PayME-Tech/PayME-SDK-Android-Example/raw/main/fe478f50-e3de-4c58-bd6d-9f77d46ce230.png?raw=true)](https://github.com/PayME-Tech/PayME-SDK-Android-Example/blob/main/fe478f50-e3de-4c58-bd6d-9f77d46ce230.png?raw=true)

Cách tạo **connectToken**:

connectToken cần để truyền gọi api từ tới PayME và sẽ được tạo từ hệ thống backend của app tích hợp. Cấu trúc như sau:

```kotlin
connectToken = AES256("{ timestamp: 34343242342, userId : "ABC", phone : "0909998877" }" + secretKey )
```

| **Tham số**    | **Bắt buộc** | **Giải thích**                                                                                                                                      |
| -------------- | ------------ | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| **timestamp**  | Yes          | Thời gian tạo ra connectToken theo định dạng iSO 8601 , Dùng để xác định thời gian timeout cùa connectToken. Ví dụ 2021-01-20T06:53:07.621Z         |
| **\*userId\*** | Yes          | là giá trị cố định duy nhất tương ứng với mỗi tài khoản khách hàng ở dịch vụ, thường giá trị này do server hệ thống được tích hợp cấp cho PayME SDK |
| **\*phone\***  | No           | Số điện thoại của hệ thống tích hợp, nếu hệ thống không dùng số điện thoại thì có thể không cần truyền lên hoặc truyền null                         |

Trong đó **\*AES\*** là hàm mã hóa theo thuật toán AES. Tùy vào ngôn ngữ ở server mà bên hệ thống dùng thư viện tương ứng. Xem thêm tại đây https://en.wikipedia.org/wiki/Advanced_Encryption_Standard

### Các c**hức năng của PayME SDK**

###

### getAccountInfo()

App có thể dùng thược tính này sau khi khởi tạo SDK để biết được trạng thái liên kết tới ví PayME.

```kotlin
public fun getAccountInfo() : AccountInfo
```

Ví dụ:

```kotlin
val accountInfo =   payme.getAccountInfo()
if(accountInfo.accountActiveSuccess){
  //Tài khoản đã được kích hoạt
  //Có thể kiểm tra số dư tài khoản
}
if(accountInfo.accountKycSuccess){
    //Tài khoản đã được định danh
    //Có thể Thanh toán,nạp,rút
}
```

**openWallet() - Mở UI chức năng PayME tổng hợp**

```kotlin
public fun openWallet( action: Action, amount: Int?, description : String?, extraData : String?, onSuccess: (JSONObject)->Unit, onError:(JSONObject?, Int?, String) -> Unit )

enum class Action {
  DEPOSIT, PAY, OPEN,
}
```

Hàm này được gọi khi từ app tích hợp khi muốn gọi 1 chức năng PayME bằng cách truyền vào tham số Action như trên.

| **Tham số** | **Bắt buộc** | **Giải thích**                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| ----------- | ------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| contex      | Yes          | context để PayME SDK dựa vào đó tự mở giao diện của PayME lên.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| action      | Yes          | Open : Dùng để mở giao diện ví PayME WebView và không thực hiện hành động nào đặc biệt. Deposit : Dùng để mở giao diện ví PayME và thực hiện chức năng nạp tiền PayME sẽ xử lý và có thông báo thành công thất bại trên UI của PayME. Ngoài ra sẽ trả về cho app tích hợp kết quả nếu muốn tự hiển thị và xử lý trên app. Withdraw: Dùng để mở giao diện ví PayME và thực hiện chức năng rút tiền PayME sẽ xử lý và có thông báo thành công thất bại trên UI của PayME. Ngoài ra sẽ trả về cho app tích hợp kết quả nếu muốn tự hiển thị và xử lý trên app. |
| amount      | No           | Dùng trong trường hợp action là Deposit/Withdraw thì truyền vào số tiền                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| description | No           | Truyền mô tả của giao dịch nếu có                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| extraData   | No           | Khi thực hiện Deposit hoặc Withdraw thì app tích hợp cần truyền thêm các dữ liệu khác nếu muốn để hệ thông backend PayME có thể IBN lại hệ thống backend app tích hợp đối chiều. Ví dụ : transactionID của giao dịch hay bất kỳ dữ liệu nào cần thiết đối với hệ thống app tích hợp.                                                                                                                                                                                                                                                                        |
| onSuccess   | Yes          | Dùng để bắt callback khi thực hiện giao dịch thành công từ PayME SDK                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| onError     | Yes          | Dùng để bắt callback khi có lỗi xảy ra trong quá trình gọi PayME SDK                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |

**Ví dụ :**

```kotlin
payme.openWallet(this, Action.OPEN, null, null, null,
	onSuccess = { json: JSONObject ->   },
	onError = { jsonObject, code, message ->
			PayME.showError(message)
                        //Lỗi khi hết hạn đăng nhập
                        if (code == ERROR_CODE.EXPIRED) {
                            walletView.setVisibility(View.GONE)
                            payme.logout()
			    // hoặc có thể gọi payme.login() để tự động đăng nhập lại vì khi gọi login là đã có gọi payme.logout() trước.
			    // Sau đó có thể gọi lại openWallet() để mở lại UI ví PayME.
			}
		   }
		 )
}
```

### deposit() - Nạp tiền

```kotlin
public fun deposit(
  amount : Int,
  description : String?,
  extraData : String,
  onSuccess: (JSONObject) -> Unit,
  onError:(JSONObject?, Int?, String) -> Unit)
```

Ví dụ :

```kotlin
payme.deposit(amount, null, "",
                onSuccess = { json: JSONObject ->
                },
                onError = { jsonObject, code, message ->

		    ME.showError(message)
                    if (code == ERROR_CODE.EXPIRED) {
                        walletView.setVisibility(View.GONE)
                        payme.logout()
                    }
                    //ERROR_CODE.ACCOUNT_NOT_KYC : tài khoản chưa định danh
                    // ERROR_CODE.ACCOUNT_NOT_ACTIVETES : chưa kích hoạt
                    //cần gọi lại hàm openWallet

                    if (code == ERROR_CODE.ACCOUNT_NOT_KYC || code == ERROR_CODE.ACCOUNT_NOT_ACTIVETES) {
                        openWallet()
                    }
		}
	     )
```

Hàm này có ý nghĩa giống như khi gọi openWallet với action **Action.Deposit.**

### withdraw() - Rút tiền

```kotlin
public fun withdraw(amount: Int, description: String?, extraData: String,
                    onSuccess: (JSONObject) -> Unit, onError: (JSONObject?, Int?, String) -> Unit)
```

Ví dụ:

```kotlin
payme.withdraw(amount, null, "",
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

Hàm này có ý nghĩa giống như gọi openWallet với action là **Action.Withdraw**.

### pay() - Thanh toán

Hàm này được dùng khi app cần thanh toán 1 khoản tiền từ ví PayME đã được kích hoạt.

```kotlin
public fun pay(
            fragmentManager: FragmentManager,
            infoPayment: InfoPayment,
            onSuccess: ((JSONObject?) -> Unit)?,
            onError: ((JSONObject?, Int?, String) -> Unit)?,
        )
```

Ví dụ:

```kotlin
val amount = convertInt(moneyPay.text.toString())
val nextValues = List(10) { Random.nextInt(0, 100000) }
val infoPayment = InfoPayment("PAY", amount, "Nội dung đơn hàng", nextValues.toString(), 4, "OpenEWallet")

- action: loại giao dịch ( 'PAYMENT' => thanh toán)
- amount: số tiền thanh toán
- note: Mô tả giao dịch từ phía đối tác
- orderId: mã giao dịch của đối tác, cần duy nhất trên mỗi giao dịch
- storeId: ID của store phía công thanh toán thực hiên giao dịch thanh toán
- type: OpenEWallet

payme.pay(this.supportFragmentManager, infoPayment,
          onSuccess = { json: JSONObject -> /* Thành công, thông báo kết quả */},
          onError = { jsonObject, code, message ->
                    PayME.showError(message)
                    if (code == ERROR_CODE.EXPIRED) {
                        walletView.setVisibility(View.GONE)
                        payme.logout()
			// Có thể thông báo lỗi cho user sau đó login lại để lấy lại token.
                    }
                    if (code == ERROR_CODE.ACCOUNT_NOT_KYC || code == ERROR_CODE.ACCOUNT_NOT_ACTIVETES) {
                        openWallet()
                    }
                }
            )
```

| Tham số    | **Bắt buộc** | **Giải thích**                                                                                                                                                                                                                  |
| ---------- | ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| amount     | Yes          | Số tiền cần thanh toán bên app truyền qua cho SDK                                                                                                                                                                               |
| descriptio | No           | Mô tả nếu có                                                                                                                                                                                                                    |
| extraData  | Yes          | Khi thực hiện thanh toans thì app cần truyền thêm các dữ liệu khác nếu muốn để hệ thông backend PayME có thể IBN lại hệ thống backend tích hợp đối chiều. Ví dụ : transactionID của giao dịch hay bất kỳ dữ liệu nào cần thiết. |

Trong trường hợp app tích hợp cần lấy số dư để tự hiển thị lên UI trên app thì có thể dùng hàm, hàm này không hiển thị UI của PayME SDK

### getWalletInfo() - **Lấy các thông tin của ví**

```kotlin
public fun geWalletInfo(onSuccess: (JSONObject) -> Unit,onError:(JSONObject?, Int?, String) -> Unit)
```

- Trong trường hợp lỗi thì hàm sẽ trả về message mỗi tại hàm onError , khi đó app có thể hiển thị balance là 0.
- Trong trường hợp thành công SDK trả về thông tin như sau:

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

**\*balance\*** : App tích hợp có thể sử dụng giá trị trong key balance để hiển thị, các field khác hiện tại chưa dùng.

**\*detail.cash :\*** Tiền có thể dùng

**\*detail.lockCash:\*** tiền bị lock

<details class="details-reset details-overlay details-overlay-dark" id="jumpto-line-details-dialog" style="box-sizing: border-box; display: block;"><summary data-hotkey="l" aria-label="Jump to line" role="button" style="box-sizing: border-box; display: list-item; cursor: pointer; list-style: none;"></summary></details>
