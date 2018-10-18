package datahack.slick.controladores

import java.util.UUID

import datahack.slick.dao.{DaoDispositivo, DaoMensajes}
import datahack.slick.modelo.{Dispositivo, Mensaje}
import datahack.slick.utils.{Configuraciones, DBUtils}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class Controlador extends Configuraciones {

  lazy val daoImpl = new DaoDispositivo()
  lazy val daoMensajesImpl = new DaoMensajes()

  def validarEsquemaTablas() = {
    DBUtils.createEsquema()
    daoImpl.crearTabla()
    daoMensajesImpl.crearTabla()
  }

  def testDispositivos() = {
    val dispositivo = Dispositivo(id = UUID.randomUUID().toString, imei = "1", marca = "Raspberry", modelo = "Humedad")
    daoImpl.insertar(dispositivo) onComplete {
      case Success(s) => logger.info(s"Dispositivo insertado ${s.imei}")
      case Failure(e) => logger.error(s"Error al insertar dispositivo", e)
    }

    val dispositivos = Seq(
      Dispositivo(id = UUID.randomUUID().toString, imei = "2", marca = "Raspberry", modelo = "Tempertura"),
      Dispositivo(id = UUID.randomUUID().toString, imei = "3", marca = "Raspberry", modelo = "Posicion"),
      Dispositivo(id = UUID.randomUUID().toString, imei = "4", marca = "Arduino", modelo = "Infrarrojo"),
      Dispositivo(id = UUID.randomUUID().toString, imei = "5", marca = "Arduino", modelo = "Movimiento"),
      Dispositivo(id = UUID.randomUUID().toString, imei = "6", marca = "Arduino", modelo = "Humedad"),
    )
    daoImpl.insertarLista(dispositivos)
    daoImpl.obtenerTodos() onComplete {
      case Success(lista) => logger.info(s"Todos los Dispositivos = ${lista.mkString(",")}")
      case Failure(e) => logger.error(s"Error al obtener todos los dispositivos", e)
    }
    daoImpl.actualizar(dispositivo, "Temperatura") onComplete {
      case Success(s) => logger.info(s"Dispositivo actualizado")
      case Failure(e) => logger.error(s"Error al actaulizar dispositivo", e)
    }
    daoImpl.obtenerTodos() onComplete {
      case Success(lista) => logger.info(s"Todos los dispositivos Dispositivos = ${lista.mkString(",")}")
      case Failure(e) => logger.error(s"Error al obtener todos los dispositivos", e)
    }
    daoImpl.actualizar(dispositivo, "Infrarrojo", "Arduino") onComplete {
      case Success(s) => logger.info(s"Dispositivo actualizado")
      case Failure(e) => logger.error(s"Error al actaulizar dispositivo", e)
    }
    daoImpl.obtenerTodos() onComplete {
      case Success(lista) => logger.info(s"Todos los dispositivos Dispositivos = ${lista.mkString(",")}")
      case Failure(e) => logger.error(s"Error al obtener todos los dispositivos", e)
    }
    daoImpl.upsert(dispositivo.copy(ultimaPosicion = Some("-32.12312,-68.1231")))
    daoImpl.obtenerTodos() onComplete {
      case Success(lista) => logger.info(s"Todos los dispositivos Dispositivos = ${lista.mkString(",")}")
      case Failure(e) => logger.error(s"Error al obtener todos los dispositivos", e)
    }

  }

  def testMensajes() = {
    daoMensajesImpl.insertar(Mensaje(texto = "mensaje", dispositivoImei = "1"))

    daoMensajesImpl.obtenerTodos() onComplete {
      case Success(lista) => logger.info(s"Todos los Mensajes = ${lista.mkString(",")}")
      case Failure(e) => logger.error(s"Error al obtener todos los mensajes", e)
    }

    daoImpl.obtenerMensajesPorDispositivoMonadic("1") onComplete {
      case Success(lista) => logger.info(s"Monadic Query - Mensajes = ${lista.mkString(",")}")
      case Failure(e) => logger.error(s"Error al obtener todos los mensajes", e)
    }
    daoImpl.obtenerMensajesPorDispositivoAplicativo("1") onComplete {
      case Success(lista) => logger.info(s"Aplicative Query - Mensajes = ${lista.mkString(",")}")
      case Failure(e) => logger.error(s"Error al obtener todos los mensajes", e)
    }
  }
}
