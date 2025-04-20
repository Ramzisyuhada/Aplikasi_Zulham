import com.google.gson.annotations.SerializedName

data class CuacaResponse(
    @SerializedName("lokasi")
    val lokasi: Lokasi,

    @SerializedName("data")
    val data: List<CuacaDataWrapper>
)

data class CuacaDataWrapper(
    @SerializedName("lokasi")
    val lokasi: Lokasi,

    @SerializedName("cuaca")
    val cuaca: List<List<Cuaca>>
)

data class Lokasi(
    val adm1: String,
    val adm2: String,
    val adm3: String,
    val adm4: String,
    val provinsi: String,
    val kotkab: String,
    val kecamatan: String,
    val desa: String,
    val lon: Double,
    val lat: Double,
    val timezone: String
)

data class Cuaca(
    val datetime: String?,
    val t: Int?,
    val tcc: Int?,
    val tp: Double?,
    val weather: Int?,
    val weather_desc: String?,
    val weather_desc_en: String?,
    val wd_deg: Int?,
    val wd: String?,
    val wd_to: String?,
    val ws: Double?,
    val hu: Int?,
    val vs: Int?,
    val vs_text: String?,
    val time_index: String?,
    val analysis_date: String?,
    val image: String?,
    val utc_datetime: String?,
    val local_datetime: String?
)
