

class Alamat() {
    lateinit var Jalan: String
    lateinit var Kota: String
    lateinit var Provinsi: String
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var OnMap: Boolean = false

    constructor(
        Jalan: String,
        Kota: String,
        Provinsi: String,
        latitude: Double,
        longitude: Double
    ) : this() {
        this.Jalan = Jalan
        this.Kota = Kota
        this.Provinsi = Provinsi
        this.latitude = latitude
        this.longitude = longitude
    }

    constructor(latitude: Double, longitude: Double) : this() {
        this.latitude = latitude
        this.longitude = longitude
    }

    // 🔥 Tambahkan constructor ini
    constructor(latitude: Double, longitude: Double, OnMap: Boolean) : this() {
        this.latitude = latitude
        this.longitude = longitude
        this.OnMap = OnMap
    }
}
