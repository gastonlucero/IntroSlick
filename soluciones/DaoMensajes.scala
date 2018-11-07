package datahack.slick.dao

import datahack.slick.modelo.Mensaje
import datahack.slick.utils.Configuraciones
import datahack.slick.utils.DBUtils._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

/**
  * Dao para los mensajes de los dispositvos
  * La clase extiende al trait Configuraciones para tener acceso a las configuraciones y al logger
  */
class DaoMensajes extends Configuraciones {

  def crearTabla(): Unit = {
    logger.info(s"Creando esquema para la tabla ${config.getString(SchemaMensajes)}")
    Try {
      val action = tablaMensaje.schema.create
      Await.result(db.run(action), 10 seconds) //Usamos await, porque necesitamos el esquema creado
    } match {
      case Success(s) => logger.info(s"Esquema creado para la tabla ${tablaMensaje.schema.createStatements.mkString}")
      case Failure(f) => logger.error("El esquema ya existe")
    }
  }


  def insertar(mensaje: Mensaje): Future[Mensaje] = {
    db.run(tablaMensaje returning tablaMensaje += mensaje)
  }

  def eliminarPorDispositivo(imei: String) = {
    db.run(tablaMensaje.filter(_.dispositivoImei === imei).delete)
  }

  def obtenerTodos() = {
    db.run(tablaMensaje.result)
  }
}
