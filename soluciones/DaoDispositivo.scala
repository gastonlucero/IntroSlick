package datahack.slick.dao

import datahack.slick.modelo.Dispositivo
import datahack.slick.utils.Configuraciones
import datahack.slick.utils.DBUtils._
import slick.dbio.Effect
import slick.jdbc.PostgresProfile.api._
import slick.sql.FixedSqlStreamingAction

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

/**
  * Al extender del trait, tenemos referencia tanto al logger, como a la variable config, que nos permite leer todas
  * las configuraciones que hemos definido
  */
class DaoDispositivo extends Configuraciones {

  def crearTabla(): Unit = {
    logger.info(s"Creando esquema para la tabla ${config.getString(SchemaDispositivo)}")
    Try {
      val action = tablaDispositivo.schema.create
      Await.result(db.run(action), 10 seconds) //En este paso si usamos await, porque necesitamos el esquema
    } match {
      case Success(s) => logger.info(s"Esquema creado para la tabla ${tablaDispositivo.schema.createStatements.mkString}")
      case Failure(f) => logger.error("El esquema ya existe")
    }
  }

  def insertar(dispositivo: Dispositivo): Future[Dispositivo] = {
    logger.info("Insertando dispositivo")
    val nuevo: Future[Dispositivo] = db.run(tablaDispositivo returning tablaDispositivo += dispositivo)
    nuevo
  }

  /**
    * Insertar con transacciones
    */
  def insertarTx(dispositivo: Dispositivo) = {
    logger.info("Insertando dispositivo con transacciones")
    val accionTx = (tablaDispositivo returning tablaDispositivo += dispositivo).transactionally
    val conRollback = accionTx.asTry.flatMap {
      case Success(s) => DBIO.successful(s)
      case Failure(e) => DBIO.failed(e)
    }
    val nuevoDispositivo: Future[Dispositivo] = db.run(conRollback)
    nuevoDispositivo
  }

  def insertarLista(dispositivos: Seq[Dispositivo]) = {
    val nuevo = db.run(tablaDispositivo ++= dispositivos)
    nuevo
  }

  def actualizar(dispositivo: Dispositivo, nuevoModelo: String): Future[Int] = {
    val query = for {
      viejoDispositivo <- tablaDispositivo if viejoDispositivo.id === dispositivo.id
    } yield viejoDispositivo.modelo
    val accion = query.update(nuevoModelo)
    db.run(accion)
  }

  def actualizar(dispositivo: Dispositivo, nuevoModelo: String, nuevaMarca: String) = {
    db.run(tablaDispositivo.filter(_.id === dispositivo.id)
      .map(dispositivo => (dispositivo.modelo, dispositivo.marca))
      .update((nuevoModelo, nuevaMarca)))
  }

  def upsert(dispositivoActualizado: Dispositivo) = {
    val cantidadActualizada: Future[Int] = db.run(tablaDispositivo.insertOrUpdate(dispositivoActualizado))
  }

  def eliminar(imei: String) = {
    val eliminado: Future[Int] = db.run(tablaDispositivo.filter(_.imei === imei).delete)
    eliminado
  }


  def obtenerTodos(): Future[Seq[Dispositivo]] = {
    logger.info(tablaDispositivo.result.statements.mkString)
    db.run(tablaDispositivo.result)
  }

  def obtenerTodosOrdenados(): Future[Seq[Dispositivo]] = {
    db.run(tablaDispositivo.sortBy(_.imei.desc).take(10).result)
  }

  def obtenerPorMarca(marca: String): Future[Seq[Dispositivo]] = {
    val query: Query[TablaDispositivo, Dispositivo, Seq] = tablaDispositivo.filter(_.marca === marca)
    val accion: FixedSqlStreamingAction[Seq[Dispositivo], Dispositivo, Effect.Read] = query.result
    val resultado: Future[Seq[Dispositivo]] = db.run(accion)
    val acciones: DBIOAction[Unit, NoStream, Effect.Read] = DBIO.seq(accion, accion)
    resultado
    //La forma lazy
    //db.run(tablaDispositivo.filter(_.marca === marca).result)
  }

  //Filtro y Map
  def obtenerSoloImeiPorMarca(marca: String): Future[Seq[String]] = {
    val soloImeis: Future[Seq[String]] = db.run(tablaDispositivo.filter(_.marca === marca).map(_.imei).result)
    soloImeis
  }

  //Filtro y Map
  def obtenerPrimerImeiPorMarca(marca: String): Future[String] = {
    val soloImeis: Future[String] = db.run(tablaDispositivo.filter(_.marca === marca).map(_.imei).result.head)
    soloImeis
  }

  //Map y Filtro
  def obtenerSoloImeisTerminadosEn(terminadoEn: String): Future[Seq[String]] = {
    val soloImeis: Future[Seq[String]] = db.run(tablaDispositivo.map(_.imei)
      .filter {
        imei: Rep[String] => imei.endsWith(terminadoEn)
      }.result)
    soloImeis
  }

  def validarUltimaPosicion(posicion: String) = {
    db.run(tablaDispositivo.filter(_.ultimaPosicion === Option(posicion)).result)
  }


  def obtenerMensajesPorDispositivoMonadic(imei: String) = {
    val queryMensajes = for {
      dispositivos <- tablaDispositivo.filter(_.imei === imei)
      mensajes <- tablaMensaje if (dispositivos.imei === mensajes.dispositivoImei)
    } yield mensajes.texto
    db.run(queryMensajes.result)
  }

  def obtenerMensajesPorDispositivoAplicativo(imei: String) = {
    val queryMensajes = for {
      (dispositivos, mensajes) <- tablaDispositivo.filter(_.imei === imei) join tablaMensaje on (_.imei === _.dispositivoImei)
    } yield mensajes.texto
    db.run(queryMensajes.result)
  }


}
