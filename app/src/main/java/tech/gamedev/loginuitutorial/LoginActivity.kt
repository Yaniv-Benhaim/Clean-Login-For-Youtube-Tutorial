package tech.gamedev.loginuitutorial

import android.content.Intent
import android.graphics.Color
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import tech.gamedev.loginuitutorial.data.Constants.AUTH_REQUEST_CODE
import tech.gamedev.loginuitutorial.data.Constants.WEB_CLIENT_ID
import tech.gamedev.loginuitutorial.data.SharedPreference


class LoginActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        //SIGN OUT USER BECAUSE FIREBASE AUTOMATICLY SAVES LAST LOGGED IN USER
        signOut()


        //LOGIN WITH EMAIL AND PASSWORD
        btnLogin.setOnClickListener {
            loginUserWithEmailAndPassWord()
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
        }

        //REGISTER WITH EMAIL AND PASSWORD
        btnRegister.setOnClickListener {
            registerUser()
        }

        //LOGIN WITH GOOGLE
        btnGoogleLogin.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

            val signInClient = GoogleSignIn.getClient(this, gso)
            signInClient.signInIntent.also {  signInIntent ->
                startActivityForResult(signInIntent, AUTH_REQUEST_CODE)
            }
            animateBtn(it)
        }



    }

    //REGISTER WITH EMAIL AND PASSWORD
    private fun registerUser() {
        //GET EMAIL AND PASSWORD IN STRING VALUES
        val email = etUserName.text.toString()
        val password = etPassWord.text.toString()
        //CHECK IF STRINGS ARE NOT EMPTY
        if(email.isNotEmpty() && password.isNotEmpty()) {
            //ENTER A BACKGROUND THREAD
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    //CREATE NEW USER WITH EMAIL AND PASSWORD AND WAIT TILL FINISHED WITH .awat()
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        //UPDATE UI TO DISPLAY USER
                        checkedLoggedInState()
                    }
                } catch (e: Exception) {
                    //DISPLAY ERROR IN UI
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    //LOGIN WITH EMAIL AND PASSWORD
    private fun loginUserWithEmailAndPassWord() {
        val email = etUserName.text.toString()
        val password = etPassWord.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        checkedLoggedInState()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun checkedLoggedInState() {
        if(auth.currentUser == null) {
            tvLoggedIn.text = "NOT LOGGED IN"
            tvLoggedIn.setBackgroundColor(Color.RED)
        } else {
            tvLoggedIn.text = "Welcome ${auth.currentUser!!.email}"
            tvLoggedIn.setBackgroundColor(Color.GREEN)
        }
    }







    //SIGN OUT USER
    private fun signOut() {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                auth.signOut()
                checkedLoggedInState()
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }


    //ANIMATE BUTTONS
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == AUTH_REQUEST_CODE) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
                account?.let {
                    googleAuthForFirebase(it)
                }
            } catch (e: Exception) {
                Log.d("GOOGLE", e.message.toString())
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }


        }
    }

    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Logged in successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /*private fun setupSharedPreferences() {
        //SET SHARED PREFERENCE OBJECT
        val sharedPreference = SharedPreference(this)
        //CHECK FOR SAVED USERS
        if(sharedPreference.getValueString("USER_NAME") != null && sharedPreference.getValueString("PASS_WORD") != null){
            etUserName.setText(sharedPreference.getValueString("USER_NAME"))
            etPassWord.setText(sharedPreference.getValueString("PASS_WORD"))
            cbRememberMe.isChecked = true
        }
        //END SET SHARED PREFERENCE OBJECT

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

    }*/

    override fun onStart() {
        super.onStart()
        checkedLoggedInState()
    }
}