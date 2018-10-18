package datahack.slick

import datahack.slick.controladores.Controlador
import org.slf4j.LoggerFactory

object Main extends App {

  val logger = LoggerFactory.getLogger("Datahack")

  logger.info("BootCamp Slick (presionar cualquier tecla para salir)")

  logger.info("Iniciando clase Crntrolador")
  val controlador = new Controlador
  controlador.validarEsquemaTablas()
  controlador.testDispositivos()
  controlador.testMensajes()

  scala.io.StdIn.readLine()
  System.exit(0)
}
