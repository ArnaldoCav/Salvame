package com.pia.salvame

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AlertDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    lateinit var sharedPreferences:SharedPreferences

    //var isRemembered=false

    override fun onCreate(savedInstanceState: Bundle?) {
        //Splash
        setTheme(R.style.AppTheme)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        //Event de analytics
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de Firebase completa")
        analytics.logEvent("InitScreen", bundle)

        sharedPreferences=getSharedPreferences("prefs", Context.MODE_PRIVATE)



val usuario=sharedPreferences.getString("email","")
            val clave =sharedPreferences.getString("password","")

            if(usuario !=null && clave!= null)
            {
                if(usuario.isNotEmpty() && clave.isNotEmpty())
                {
iniciarSesion(usuario,clave)
                }
            }


        setup()



    }

    private fun setup() {
        title = "Autenticación"

        loginButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && paswordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailEditText.text.toString(), paswordEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "")
                        val editor:SharedPreferences.Editor=sharedPreferences.edit()
                        editor.putString("email",emailEditText.text.toString())
                        editor.putString("password",paswordEditText.text.toString())
                        editor.apply()
                        this.finish()
                    } else {
                        showAlert()
                    }
                }
            } else {
                showAlertAuth()
            }
        }

        GoSignUpButton.setOnClickListener {
            showRegister()
        }
    }

    private fun iniciarSesion(email: String, password:String){
        if (email.isNotEmpty() && password.isNotEmpty()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    showHome(it.result?.user?.email ?: "")
                    this.finish()
                } else {
                    showAlert()
                }
            }
        } else {
            showAlertAuth()
        }
    }



    private fun showAlertAuth() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Recuerda completar los datos e intenta de nuevo.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Error de autenticación, intentalo de nuevo.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String) {
        val homeIntent: Intent = Intent(this, PrincipalActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }

    private fun showRegister() {
        val registerIntent: Intent = Intent(this, RegisterActivity::class.java)
        startActivity(registerIntent)
    }
    }

