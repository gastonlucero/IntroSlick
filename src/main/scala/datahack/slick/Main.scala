package datahack.slick

import datahack.slick.controladores.Controlador
import org.slf4j.LoggerFactory

object Main extends App {


  val logger = LoggerFactory.getLogger("Datahack")

  logger.info("Datahack Slick (presionar cualquier tecla para salir)")

  val controlador = new Controlador
  controlador.validarEsquemaTablas()
  controlador.testDispositivos()
  controlador.testMensajes()

  scala.io.StdIn.readLine()
  System.exit(0)
}
