package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.mvc.Flash._
import play.api.mvc.Flash
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

case class LoginDetails(email:String,password:String)

case class SignupDetails(email:String,password:String,confirmPassword:String)

case class ForgotPasswordDetails(email:String)

case class ChangePasswordDetails(newPassword:String,confirmPassword:String)

case class ChangeEmailDetails(newEmail:String)


class Authentication extends Controller{

  val user=List(LoginDetails("Rahul@knoldus.com","1234"),LoginDetails("Sonu@knoldus.com","7890"))

  val loginForm:Form[LoginDetails]=Form(
    mapping(
      "email"-> email,
      "password" -> text
    )(LoginDetails.apply)(LoginDetails.unapply)
  )

  val signupForm:Form[SignupDetails]=Form(
    mapping(
      "email" -> email,
      "password" ->nonEmptyText,
      "confirmPassword"->nonEmptyText
      )(SignupDetails.apply)(SignupDetails.unapply)

  )

  val forgotPasswordForm:Form[ForgotPasswordDetails]=Form(
    mapping(
      "email" -> email
    )(ForgotPasswordDetails.apply)(ForgotPasswordDetails.unapply)
  )

  val changePasswordForm:Form[ChangePasswordDetails]=Form(
    mapping(
      "newPassword" -> nonEmptyText,
      "confirmPassword" -> nonEmptyText
    )(ChangePasswordDetails.apply)(ChangePasswordDetails.unapply)
  )

  val changeEmailForm:Form[ChangeEmailDetails]=Form(
    mapping(
      "newEmail" -> email
    )(ChangeEmailDetails.apply)(ChangeEmailDetails.unapply)
  )

  def loginController = Action { implicit request =>

    Ok(views.html.login(loginForm))
  }

  def submit=Action{

    implicit request =>

      loginForm.bindFromRequest().fold(
        formWithErrors => {
          Redirect("/login").flashing(
            "error" -> "Either email or password is incorrect")
        },
        userData => {
          if (user.contains(userData)) {
            //request.session.get("email").map{user => Ok(user)}.getOrElse{Unauthorized("oops")}
            Redirect("/details").withSession("email" -> userData.email,"password"->userData.password)
          }
          else if (userData.email == "") {

              Redirect("/login").flashing(
                "error" -> " email incorrect or empty")

          }
          else if(userData.password==""){
              Redirect("/login").flashing(
                "error" -> " password is incorrect")
          }
          else{
            Redirect("/login").flashing(
              "error" -> " Please check your credentials and enter again")
          }
        })
  }



  def signUpController = Action { implicit request =>
    Ok(views.html.signupPage(signupForm))
  }


def signupSubmitController=Action{
  implicit request =>

    signupForm.bindFromRequest().fold(
      formWithErrors => {
        Redirect("/signupPage").flashing(
          "error" -> "Either email or password is incorrect")
      },
      userData => {
        if(user.exists(_.email.contains(userData.email)))
        {
          Redirect("/login").flashing(
            "error" -> "User already exist. Please login first or click forgot password.")
        }
        else if(userData.password==userData.confirmPassword){
          user::List(LoginDetails(userData.email,userData.password))
          Redirect("/details").withSession("email"->userData.email,"password"->userData.password)
        }
        else{
          Redirect("/signupPage").flashing(
            "error"->"mismatched password"
          )
        }
      }
    )
}

  def forgotPasswordController=Action{
    Ok(views.html.forgotPasswordPage(forgotPasswordForm))
  }

  def forgotPasswordSubmit = Action { implicit request =>
    forgotPasswordForm.bindFromRequest().fold(
      formWithErrors => {
        Redirect("/forgotPasswordPage")
      },
      userData => {
        if(user.exists(_.email.contains(userData.email))){
        Ok(views.html.changePassword(changePasswordForm))
        }
       else
        {
         Redirect("/forgotPassword")
        }
      }

    )
  }


  def detailsController=Action{ implicit request =>
      if (request.session.get("email").isEmpty && request.session.get("password").isEmpty){
        Redirect("/login").flashing(
          "error" -> "Please Login First")
      }
      else{
        Ok(views.html.details(loginForm))
      }
  }

  def changePasswordController =Action{ implicit request =>
    if (request.session.get("email").isEmpty && request.session.get("password").isEmpty){
      Redirect("/login").flashing(
        "error" -> "Please Login First")
    }
    else {
      Ok(views.html.changePassword(changePasswordForm))
    }
  }

  def changePasswordSubmitController = Action { implicit request =>
    changePasswordForm.bindFromRequest().fold(
      formWithErrors => {
        Redirect(routes.Authentication.changePasswordController()).flashing("error" ->"The Passwords cannot be empty")      },
      userData =>
        {
          if(userData.newPassword==userData.confirmPassword){
            Redirect("/details").flashing("success" -> "Password successfully changed")
          }
          else{
            Redirect(routes.Authentication.changePasswordController()).flashing("error" ->"The Passwords do not match")
          }
        }
    )
  }

  def changeEmailController= Action { implicit request =>
    if (request.session.get("email").isEmpty){
      Redirect("/login").flashing(
        "error" -> "Please Login First")
    }
    else {
      Ok(views.html.email(changeEmailForm))
    }
  }

  def changeEmailSubmitController= Action { implicit request =>
    changeEmailForm.bindFromRequest().fold(
      formWithErrors => {
        Redirect("/changeEmail").flashing("error"->"Your email id is incorrect")
      },
      userData => {
        Redirect("/details").flashing("success"->"Email Successfully changed")
      }
    )
  }

  def logoutController = Action { implicit request =>
    Redirect("/login").withNewSession
  }
}
