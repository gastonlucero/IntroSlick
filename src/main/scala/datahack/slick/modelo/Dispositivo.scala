package datahack.slick.modelo

case class Dispositivo(id: String, imei: String, marca: String, modelo: String, ultimaPosicion: Option[String] = None)
