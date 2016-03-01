import controllers.routes
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
//import play.test.WithApplication

@RunWith(classOf[JUnitRunner])
class AuthenticationSpec extends Specification{

  "Authentication" should {

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET,"/mypage")) must beSome.which(status(_)==NOT_FOUND)
    }

    "render the login page" in new WithApplication{
       val login= route(FakeRequest(GET,"/")).get
      status (login) must equalTo(OK)
    }

    "password is empty" in new WithApplication{
      val login= route(FakeRequest(POST,"/submit").withFormUrlEncodedBody("email"->"Sonu@knoldus.com","password"->"")).get
      status (login) must equalTo(303)
    }

    "email is empty" in new WithApplication{
      val login= route(FakeRequest(POST,"/submit").withFormUrlEncodedBody("email"->"","password"->"1234")).get
      status (login) must equalTo(303)
    }

    "Successfully Logged In" in new WithApplication{
      val login= route(FakeRequest(POST,"/submit").withFormUrlEncodedBody("email"->"Rahul@knoldus.com","password"->"1234")).get
      status (login) must equalTo(303)
    }

    "render the signUp page" in new WithApplication{
      val login= route(FakeRequest(GET,"/signupPage")).get
      status (login) must equalTo(OK)
    }

    "wrong email" in new WithApplication{
      val login= route(FakeRequest(POST,"/signupPage").withFormUrlEncodedBody("email"->"Rahul","password"->"1234")).get
      status (login) must equalTo(303)
    }

    "User already Exists" in new WithApplication{
      val login= route(FakeRequest(POST,"/signupPage").withFormUrlEncodedBody("email"->"Rahul@knoldus.com","password"->"1234")).get
      status (login) must equalTo(303)
    }

    "Successfully SignUp" in new WithApplication{
      val login= route(FakeRequest(POST,"/signupPage").withFormUrlEncodedBody("email"->"xyz@abc.com","password"->"9876","confirmPassword"->"9876")).get
      status (login) must equalTo(303)
    }

    "Password Mismatch" in new WithApplication{
      val login= route(FakeRequest(POST,"/signupPage").withFormUrlEncodedBody("email"->"xyz@abc.com","password"->"9876","confirmPassword"->"9856")).get
      status (login) must equalTo(303)
    }

    "render details page with logged in" in new WithApplication{
      val login= route(FakeRequest(GET,"/details").withSession("email"->"Rahul@knoldus.com","password"->"1234")).get
      status (login) must equalTo(OK)
    }

    "render details page without logged in" in new WithApplication{
      val login= route(FakeRequest(GET,"/details")).get
      status (login) must equalTo(303)
    }

    "render email controller without logged in" in new WithApplication{
      val login= route(FakeRequest(GET,"/changeEmail")).get
      status (login) must equalTo(303)
    }

    "render details page with logged in" in new WithApplication{
      val login= route(FakeRequest(GET,"/changeEmail").withSession("email"->"Rahul@knoldus.com","password"->"1234")).get
      status (login) must equalTo(OK)
    }

    "render change password successfully" in new WithApplication{
      val login= route(FakeRequest(GET,"/changePasswordForm").withSession("email"->"Rahul@knoldus.com","password"->"1234")).get
      status (login) must equalTo(OK)
    }

    "render change password  without logged in" in new WithApplication{
      val login= route(FakeRequest(GET,"/changePasswordForm")).get
      status (login) must equalTo(303)
    }

    "render email change submit  without logged in" in new WithApplication{
      val login= route(FakeRequest(GET,"/changeEmailSubmit")).get
      status (login) must equalTo(404)
    }

    "render password change submit  without logged in" in new WithApplication{
      val login= route(FakeRequest(GET,"/changePasswordSubmit")).get
      status (login) must equalTo(404)
    }

    "logout" in new WithApplication{
      val login= route(FakeRequest(GET,"/details").withSession("email"->"Rahul@knoldus.com","password"->"1234")).get
      val logout= route(FakeRequest(GET,"/logoutForm")).get
      status (logout) must equalTo(303)
    }
  }
}
