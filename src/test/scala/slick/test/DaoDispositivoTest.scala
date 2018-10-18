package slick.test

import java.util.UUID

import datahack.slick.controladores.Controlador
import datahack.slick.dao.DaoDispositivo
import datahack.slick.modelo.Dispositivo
import datahack.slick.utils.DBUtils
import datahack.slick.utils.DBUtils.esquema
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.duration._

class DaoDispositivoTest extends FlatSpec with Matchers with BeforeAndAfterAll with ScalaFutures {

  lazy val controladorTest = new Controlador
  lazy val daoImpl = new DaoDispositivo()

  //Antes de comenzar los test, iniciamos las tablas e insertamos datos de test
  override protected def beforeAll(): Unit = {
    controladorTest.validarEsquemaTablas()
    controladorTest.testDispositivos()
  }

  "Un dispositivo" should "tiene un id unico" in{
    val dispositivo = Dispositivo(id = UUID.randomUUID().toString, imei = "Test", marca = "Raspberry", modelo = "Tempertura")
    dispositivo.id shouldNot be eq UUID.randomUUID().toString
  }

  "Guardar un dispositivo" should "insertar un registro en la bd" in {
    val dispositivo = Dispositivo(id = UUID.randomUUID().toString, imei = "Test", marca = "Raspberry", modelo = "Tempertura")
    whenReady(daoImpl.insertar(dispositivo), timeout(10 seconds)) { resultado =>
      resultado.imei should be eq "Test"
    }
  }

  "Obtener dispositivos" should "leer datos de la bd" in {
    whenReady(daoImpl.obtenerTodos(), timeout(10 seconds)) { resultado =>
      resultado.size should be > 0
    }
  }

  //Cuando terminan todos los test, borramos el esquema de prueba generado
  override protected def afterAll(): Unit = {

    val dropSchemaSql = s"drop schema $esquema cascade;"
    whenReady(DBUtils.db.run(sqlu"#$dropSchemaSql"), timeout(10 seconds)) { resultado =>
      resultado shouldEqual 0
    }

  }

}
