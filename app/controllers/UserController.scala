package controllers

import play.api._
import play.api.mvc._
import repos.UserRepo
import scala.concurrent.Future
import models.CustomError
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiResponses
import com.wordnik.swagger.annotations.ApiResponse
import models.User
import play.api.libs.json.JsError
import com.wordnik.swagger.annotations.ApiImplicitParams
import com.wordnik.swagger.annotations.ApiImplicitParam

@Api(value = "/user", description = "Operations about users")
object UserController extends Controller {

  @ApiOperation(value = "Get list of users.",
        notes = "",
        nickname = "list", 
        httpMethod = "GET",
        response = classOf[User]
        )
  @ApiResponses(Array(
    		new ApiResponse(code = 400, message = "Something went wrong"),
    		new ApiResponse(code = 200, message = "List of users")
    ))
  def list = CrossOriginAction.async {
    UserRepo.list.map(result => result match {
	  case Left(error) => BadRequest("")
	  case Right(resp) => Ok(Json.toJson(resp))
	})
  }
  

  @ApiOperation(value = "Create a new user.",
        notes = "",
        nickname = "create", 
        httpMethod = "POST",
        response = classOf[Boolean]
        )
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "body", 
        value = "Created user object", 
        required = true, 
        dataType = "User", 
        paramType = "body")))
  @ApiResponses(Array(
    		new ApiResponse(code = 400, message = "Something went wrong"),
    		new ApiResponse(code = 200, message = "")
    ))
  def create = CrossOriginAction.async(parse.json) { request =>
    request.body.validate[User].map{ 
      case u => UserRepo.create(u).map(result => result match {
									  case Left(error) => BadRequest("")
									  case Right(resp) => Ok
									})
    }.recoverTotal{
      e => Future(BadRequest("Detected error:"+ JsError.toFlatJson(e)))
    }
  }
  
  @ApiOperation(value = "Search all users given search criteria.",
        notes = "",
        nickname = "search", 
        httpMethod = "GET",
        response = classOf[User]
        )
      @ApiImplicitParams(Array(
    new ApiImplicitParam(name="name", 
        value="attribute name",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(name="value", 
        value="attribute value", 
        required = true, 
        dataType = "string", 
        paramType = "query")))
  @ApiResponses(Array(
    		new ApiResponse(code = 400, message = "Something went wrong"),
    		new ApiResponse(code = 200, message = "List of users")
    ))
  def search(name: String, value: String) = CrossOriginAction.async { request =>
  	UserRepo.search(name, value).map(result => result match {
									  case Left(error) => BadRequest("")
									  case Right(resp) => Ok(Json.toJson(resp))
									})
  }
}

object CrossOriginAction extends CrossOriginAction {}

class CrossOriginAction extends ActionBuilder[Request] {

    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[SimpleResult]) = {
    	block(request).map(res => res.withHeaders(
        ("Access-Control-Allow-Origin", "*"),
        ("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT"),
        ("Access-Control-Allow-Headers", "Content-Type, api_key, Authorization, Access-Control-Allow-Origin")))
    }
}