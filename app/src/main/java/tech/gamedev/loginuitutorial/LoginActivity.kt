package tech.gamedev.loginuitutorial

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.activity_main.*
import tech.gamedev.loginuitutorial.data.Constants.AUTH_REQUEST_CODE
import tech.gamedev.loginuitutorial.data.SharedPreference


class LoginActivity : AppCompatActivity() {

    lateinit var mainAccount: GoogleSignInAccount




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //SET GOOGLE SIGN IN OPTIONS
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)






        //SET SHARED PREFERENCE OBJECT
        val sharedPreference = SharedPreference(this)
        //CHECK FOR SAVED USERS
        if(sharedPreference.getValueString("USER_NAME") != null && sharedPreference.getValueString("PASS_WORD") != null){
            etUserName.setText(sharedPreference.getValueString("USER_NAME"))
            etPassWord.setText(sharedPreference.getValueString("PASS_WORD"))
            cbRememberMe.isChecked = true
        }
        //END SET SHARED PREFERENCE OBJECT


        //REGULAR LOGIN START*****

        btnLogin.setOnClickListener {

            btnLogin.animate().apply {
                duration = 200
                scaleYBy(0.1f)
                scaleXBy(0.1f)
            }.withEndAction {
                btnLogin.animate().apply {
                    duration = 200
                    scaleYBy(-0.1f)
                    scaleXBy(-0.1f)
                }
            }.start()

            if(cbRememberMe.isChecked){
                if(etIsNotEmpty()){
                    val userName = etUserName.text.toString()
                    val passWord = etPassWord.text.toString()
                    sharedPreference.saveUsername("USER_NAME", userName)
                    sharedPreference.savePassword("PASS_WORD", passWord)
                    sharedPreference.saveRememberMe("IS_CHECKED", true)
                }else{
                    Toast.makeText(
                        this,
                        "Please fill in a username and password..",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else if(!cbRememberMe.isChecked &&
                    sharedPreference.getValueString("USER_NAME") != null &&
                    sharedPreference.getValueString("PASS_WORD") != null)
            {
                sharedPreference.deleteUserNameAndPassWord()
                sharedPreference.saveRememberMe("IS_CHECKED", false)
            }
        }
        //REGULAR LOGIN END*****


        //SOCIAL LOGIN BUTTONS START*****
        btnGoogleLogin.setOnClickListener {
            animateBtn(it)
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, AUTH_REQUEST_CODE)

        }

        btnFacebookLogin.setOnClickListener {
            animateBtn(it)
            signOut(mGoogleSignInClient)
        }

        btnTwitterLogin.setOnClickListener {
            animateBtn(it)

        }
        //SOCIAL LOGIN BUTTONS END*****


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("LOGIN", "entered onActivityResult")

        if (resultCode == AUTH_REQUEST_CODE){

            Log.d("LOGIN", "login started")

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                mainAccount = account!!

                handleSignInResult(account)

                Log.d("LOGIN", "firebaseAuthWithGoogle:")
            }catch (e: ApiException){
                Log.w("LOGIN", "Google sign in failed", e)
            }
        }
    }

    private fun handleSignInResult(account: GoogleSignInAccount?){
        try {
            Toast.makeText(this, "username: ${account!!.email}", Toast.LENGTH_SHORT).show()
        }catch (e: Exception){
            Log.w("LOGIN", "signInResult:failed code=$e");
        }
    }

    private fun signOut(mGoogleSignInClient: GoogleSignInClient) {


        mGoogleSignInClient.revokeAccess()
            .addOnCompleteListener(this) {
                Toast.makeText(this, "Sign out successful", Toast.LENGTH_SHORT).show()
            }
    }



    private fun etIsNotEmpty(): Boolean{

        return etUserName.text.isNotEmpty() && etPassWord.text.isNotEmpty()
    }

    private fun animateBtn(btn: View){

        when(btn){
            btnGoogleLogin -> {
                btnGoogleLogin.animate().apply {
                    duration = 400
                    rotationY(360f)
                }
            }

            btnFacebookLogin -> {
                btnFacebookLogin.animate().apply {
                    duration = 400
                    rotationY(360f)
                }
            }

            btnTwitterLogin -> {
                btnTwitterLogin.animate().apply {
                    duration = 400
                    rotationY(360f)
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()


    }
}