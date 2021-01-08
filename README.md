PayME SDK là bộ thư viện để các app có thể tương tác với PayME Platform. PayME SDK bao gồm các chức năng chính như sau:

- Hệ thống đăng nhập, eKYC thông qua tài khoản ví PayME
- Hỗ trợ app lấy thông tin số dư ví PayME
- Chức năng nạp rút từ ví PayME.

**Một số thuật ngữ**

|     | Name    | Giải thích                                                                                               |
| :-- | :------ | -------------------------------------------------------------------------------------------------------- |
| 1   | app     | Là app mobile iOS/Android hoặc web sẽ tích hợp SDK vào để thực hiện chức năng thanh toán ví PayME.       |
| 2   | SDK     | Là bộ công cụ hỗ trợ tích hợp ví PayME vào hệ thống app.                                                 |
| 3   | backend | Là hệ thống tích hợp hỗ trợ cho app, server hoặc api hỗ trợ                                              |
| 4   | AES     | Hàm mã hóa dữ liệu AES256 PKCS5. [Tham khảo](https://en.wikipedia.org/wiki/Advanced_Encryption_Standard) |
| 5   | RSA     | Thuật toán mã hóa dữ liệu RSA.                                                                           |
| 6   | IPN     | Instant Payment Notification , dùng để thông báo giữa hệ thống backend của app và backend của PayME      |

**Bước 1 :**

Tích hợp thư viện hỗ trợ. Dung lượng 472.76 KB

**File build.gradle Project**

```gradle
allprojects {
  repositories {
    google()
    jcenter()
    maven {
      url  "https://dl.bintray.com/payme/maven"
    }
 }
}
```

- **File build.gradle Module**

```gradle
dependencies {
  ...
// thư viện chính
implementation 'vn.payme.sdk:payme-sdk:0.1.7'
// thư viện kèm theo
implementation 'com.android.volley:volley:1.1.1'
implementation 'org.greenrobot:eventbus:3.0.0'
implementation 'com.chaos.view:pinview:1.4.4'
implementation 'com.airbnb.android:lottie:3.0.3'
implementation 'com.google.android.material:material:1.2.1'
implementation 'com.google.zxing:core:3.3.3'
implementation ('com.journeyapps:zxing-android-embedded:3.6.0') { transitive = false }
implementation 'org.apache.httpcomponents:httpmime:4.5.6'
implementation 'com.camerakit:camerakit:1.0.0-beta3.10'
implementation 'com.camerakit:jpegkit:0.1.0'
implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.0'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.0.0'

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

```kotlin
import vn.payme.sdk.model.Env
val configColor = arrayOf<String>("#75255b","#9d455f"}
val payme: PayME = PayME(context, AppToken, PublicKey,ConnectToken, AppPrivateKey, configColor, Env.SANDBOX)
```

configColor : là tham số màu để có thể thay đổi màu sắc giao dịch ví PayME, kiểu dữ liệu là chuỗi với định dạng #rrggbb. Nếu như truyền 2 màu thì giao diện PayME sẽ gradient theo 2 màu truyền vào.

![img](https://github.com/PayME-Tech/PayME-SDK-Android-Example/blob/main/fe478f50-e3de-4c58-bd6d-9f77d46ce230.png?raw=true)

Cách tạo **connectToken**:

connectToken cần để truyền gọi api từ tới PayME và sẽ được tạo từ hệ thống backend của app tích hợp. Cấu trúc như sau:

```kotlin
connectToken = AES256("{ timestamp: 34343242342, userId : "ABC", phone : "0909998877" }" + secretKey )
```

| **Tham số**   | **Bắt buộc** | **Giải thích**                                                                                                                                           |
| :------------ | :----------- | :------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **timestamp** | Yes          | Thời gian tạo ra connectToken theo định danh Unix time, Dùng để xác định thời gian timeout cùa connectToken. xem https://en.wikipedia.org/wiki/Unix_time |
| **_userId_**  | Yes          | là giá trị cố định duy nhất tương ứng với mỗi tài khoản khách hàng ở dịch vụ, thường giá trị này do server hệ thống được tích hợp cấp cho PayME SDK      |
| **_phone_**   | No           | Số điện thoại của hệ thống tích hợp, nếu hệ thống không dùng số điện thoại thì có thể không cần truyền lên hoặc truyền null                              |

Trong đó **_AES_** là hàm mã hóa theo thuật toán AES. Tùy vào ngôn ngữ ở server mà bên hệ thống dùng thư viện tương ứng. Xem thêm tại đây https://en.wikipedia.org/wiki/Advanced_Encryption_Standard

### Các c**hức năng của PayME SDK**

### isConnected()

App có thể dùng thược tính này sau khi khởi tạo SDK để biết được trạng thái liên kết tới ví PayME.

```kotlin
public fun getAccountInfo( 
  onSuccess:(JSONObject)->Unit,
  onError: (String) -> Unit) 
}
```
Ví dụ:
```kotlin
        payme.getAccountInfo(onSuccess = { jsonObject ->
            val OpenEWallet = jsonObject.getJSONObject("OpenEWallet")
            val Init = OpenEWallet.getJSONObject("Init")

            val isExistInMainWallet = Init.optBoolean("isExistInMainWallet")
//            Cần phải Register hay không, hay chỉ Login của người dùng ( false -> gọi register, true -> gọi login)

            val succeeded = Init.optBoolean("succeeded")
//            Kết quả (có tồn tại account hay chưa )

            val kyc = Init.optJSONObject("kyc")
            if (kyc != null) {
                val state = kyc.optString("kyc")
//            APPROVED
//            Đã duyệt
//            REJECTED
//            Đã từ chối
//            PENDING
//            Chờ duyệt
//            CANCELED
//            Đã huỷ
//            BANNED
//            Bị ban do sai nhìu lần
            }
        }, onError = { jsonObject, code, mesage ->

        })
```

**openWallet() - Mở UI chức năng PayME tổng hợp**

```kotlin
public fun openWallet( action: Action, amount: Int?, description : String?, extraData : String?, onSuccess: (JSONObject)->Unit, onError: (String) -> Unit )

enum class Action {
  DEPOSIT, PAY, OPEN,
}
```

Hàm này được gọi khi từ app tích hợp khi muốn gọi 1 chức năng PayME bằng cách truyền vào tham số Action như trên.

| **Tham số** | **Bắt buộc** | **Giải thích**                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| :---------- | :----------: | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| contex      |     Yes      | context để PayME SDK dựa vào đó tự mở giao diện của PayME lên.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| action      |     Yes      | Open : Dùng để mở giao diện ví PayME WebView và không thực hiện hành động nào đặc biệt. Deposit : Dùng để mở giao diện ví PayME và thực hiện chức năng nạp tiền PayME sẽ xử lý và có thông báo thành công thất bại trên UI của PayME. Ngoài ra sẽ trả về cho app tích hợp kết quả nếu muốn tự hiển thị và xử lý trên app. Withdraw: Dùng để mở giao diện ví PayME và thực hiện chức năng rút tiền PayME sẽ xử lý và có thông báo thành công thất bại trên UI của PayME. Ngoài ra sẽ trả về cho app tích hợp kết quả nếu muốn tự hiển thị và xử lý trên app. |
| amount      |      No      | Dùng trong trường hợp action là Deposit/Withdraw thì truyền vào số tiền                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| description |      No      | Truyền mô tả của giao dịch nếu có                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| extraData   |      No      | Khi thực hiện Deposit hoặc Withdraw thì app tích hợp cần truyền thêm các dữ liệu khác nếu muốn để hệ thông backend PayME có thể IBN lại hệ thống backend app tích hợp đối chiều. Ví dụ : transactionID của giao dịch hay bất kỳ dữ liệu nào cần thiết đối với hệ thống app tích hợp.                                                                                                                                                                                                                                                                        |
| onSuccess   |     Yes      | Dùng để bắt callback khi thực hiện giao dịch thành công từ PayME SDK                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| onError     |     Yes      | Dùng để bắt callback khi có lỗi xảy ra trong quá trình gọi PayME SDK                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |

**Ví dụ :**

```kotlin
package com.example.applicationkotlindemo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import org.json.JSONObject
import vn.payme.sdk.PayME
import vn.payme.sdk.model.Action
import vn.payme.sdk.model.UserInfo
class MainActivity : AppCompatActivity() {
  val AppToken: String = "AppToken"
  val PublicKey: String = "PublicKey"
  val ConnectToken: String = "ConnectToken"
  val PrivateKey: String = "PrivateKey"
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val button: Button = findViewById(R.id.button)
    button.setOnClickListener {
      val payme: PayME = PayME(AppToken, PublicKey, ConnectToken, PrivateKey)
      payme.openWallet(this, Action.OPEN, null, null, null,
                       onSuccess = { json: JSONObject ->  ... },
                       onError = { message: String -> ...})
    }
  }
}
```

### deposit() - Nạp tiền

```kotlin
public fun deposit(
  amount : Int,
  description : String?,
  extraData : String,
  onSuccess: (JSONObject) -> Unit,
  onError: (String) -> Unit )
```

Hàm này có ý nghĩa giống như khi gọi openWallet với action **Action.Deposit.**

### withdraw() - Rút tiền

```kotlin
public fun withdraw(amount: Int, description: String?, extraData: String,
                    onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit)
```

Hàm này có ý nghĩa giống như gọi openWallet với action là **Action.Withdraw**.

### pay() - Thanh toán

Hàm này được dùng khi app cần thanh toán 1 khoản tiền từ ví PayME đã được kích hoạt.

```kotlin
public fun pay(amount: Int, description: String?, extraData: String,
               onSuccess: (JSONObject) -> Unit,onError: (String) -> Unit )
```

| Tham số    | **Bắt buộc** | **Giải thích**                                                                                                                                                                                                                  |
| :--------- | :----------: | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| amount     |     Yes      | Số tiền cần thanh toán bên app truyền qua cho SDK                                                                                                                                                                               |
| descriptio |      No      | Mô tả nếu có                                                                                                                                                                                                                    |
| extraData  |     Yes      | Khi thực hiện thanh toans thì app cần truyền thêm các dữ liệu khác nếu muốn để hệ thông backend PayME có thể IBN lại hệ thống backend tích hợp đối chiều. Ví dụ : transactionID của giao dịch hay bất kỳ dữ liệu nào cần thiết. |

Trong trường hợp app tích hợp cần lấy số dư để tự hiển thị lên UI trên app thì có thể dùng hàm, hàm này không hiển thị UI của PayME SDK

### getWalletInfo() - **Lấy các thông tin của ví**

```kotlin
public fun geWalletInfo(onSuccess: (JSONObject) -> Unit,onError: (String) -> Unit)
```

- Trong trường hợp lỗi thì hàm sẽ trả về message mỗi tại hàm onError , khi đó app có thể hiển thị balance là 0.

- Trong trường hợp thành công SDK trả về thông tin như sau:

```json
{
  "walletBalance": {
    "balance": 111,
    "detail": {
      "cash": 1,
      "lockCash": 2
    }
  }
}
```

**_balance_** : App tích hợp có thể sử dụng giá trị trong key balance để hiển thị, các field khác hiện tại chưa dùng.

**_detail.cash :_** Tiền có thể dùng

**_detail.lockCash:_** tiền bị lock
