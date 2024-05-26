import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@kotlinx.parcelize.Parcelize
data class ServerLatestCurrencyData(
    @SerializedName("base")
    val base: String,
    @SerializedName("disclaimer")
    val disclaimer: String,
    @SerializedName("license")
    val license: String,
    @SerializedName("rates")
    val rates: Map<String, Double>,
    @SerializedName("timestamp")
    val timestamp: Int
) : Parcelable
