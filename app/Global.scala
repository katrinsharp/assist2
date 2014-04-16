import play.api._
import java.io.File
import com.typesafe.config.ConfigFactory
import play.api.mvc.RequestHeader
import scala.concurrent.Future
import play.api.mvc.Results._
import play.api.libs.json._

object Global extends GlobalSettings {
    
    override def onError(request: RequestHeader, ex: Throwable) = {
    	Future.successful(InternalServerError(Json.obj("error"->"application error", "details" -> ex.getMessage())))
    			
    }
    
    override def onHandlerNotFound(request: RequestHeader) = {
    	Future.successful(NotFound(Json.obj("error"->"route not found", "route" -> request.path)))
    }
}
