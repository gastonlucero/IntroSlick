package datahack.slick.utils

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

trait Configuraciones {
  val logger = LoggerFactory.getLogger("Datahack")

  lazy val config = {
    logger.info("Cargando configuraciones de application.conf")
    ConfigFactory.load("application.conf") // o .load()
  }


  /** Constantes en donde definimos como buscar en las configuraciones */

  val NombreEsquema = "schema.nombre"
  val SchemaDispositivo = "schema.tablaDispositivo"
  val SchemaMensajes = "schema.tablaMensaje"

}
