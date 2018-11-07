package datahack.slick.utils

import datahack.slick.modelo.{Dispositivo, Mensaje}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

/**
  * Singleton que tiene a conexion a la bd,la definición de las tablas y la creación inicial del esquema
  */
object DBUtils extends Configuraciones {

  //Conexión a la bd
  lazy val db = Database.forConfig("datahack")

  val esquema = config.getString(NombreEsquema)
  val nombreTablaDispositivo = config.getString(SchemaDispositivo)
  val nombreTablaMensaje = config.getString(SchemaMensajes)

  def createEsquema(): Unit = {
    val createSchemaSql = s"create schema if not exists $esquema;"
    Try(Await.result(db.run(sqlu"#$createSchemaSql"), 10 seconds)) match {
      case Success(_) =>
        logger.info(s"Esquema $esquema creado")
      case Failure(e) =>
        throw e
    }
  }


  class TablaMensajes(tag: Tag) extends Table[Mensaje](tag, Some(esquema), nombreTablaMensaje) {

    //Automaticamente nos crea un indice sobre este campo y una sequencia autoincremental
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def texto = column[String]("texto")

    def dispositivoImei = column[String]("id_dispositivo")

    def * = (id ?, texto, dispositivoImei) <> (Mensaje.tupled, Mensaje.unapply)
  }

  val tablaMensaje = TableQuery[TablaMensajes]


  class TablaDispositivo(tag: Tag) extends Table[Dispositivo](tag, Some(esquema), nombreTablaDispositivo) {
    def id = column[String]("id")

    def imei = column[String]("imei")

    def marca = column[String]("marca")

    def modelo = column[String]("modelo")

    def ultimaPosicion = column[Option[String]]("ultima_posicion")

    def * = (id, imei, marca, modelo, ultimaPosicion) <> (Dispositivo.tupled, Dispositivo.unapply)

    def pk = primaryKey("dispositivo_pk", id) //Automaticamente nos crea un indice sobre este campo
  }

  //  def id = column[String]("id", O.PrimaryKey)


  val tablaDispositivo = TableQuery[TablaDispositivo]

}
