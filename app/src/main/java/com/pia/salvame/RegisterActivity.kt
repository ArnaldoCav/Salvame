package com.pia.salvame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_auth.emailEditText
import kotlinx.android.synthetic.main.activity_auth.paswordEditText
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setup()
    }

    private fun setup () {
        title="Registrate"
        signUpButton.setOnClickListener {
            if (emailEditTextRegister.text.isNotEmpty() && paswordEditTextRegister.text.isNotEmpty() && nameEditText.text.isNotEmpty() && lastEditText.text.isNotEmpty()) {

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailEditTextRegister.text.toString(), paswordEditTextRegister.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        showHome(it.result?.user?.email?: "")
                        insertUserData(emailEditTextRegister.text.toString(), ProviderType.BASIC.toString(),nameEditText.text.toString(), lastEditText.text.toString())
                    }
                    else
                    {
                        showAlert()
                    }
                }
            }
            else
            {
                showAlertReg()
            }

        }

        GoSignInButton.setOnClickListener{
            showAuth()
        }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Error de registro, intentalo de nuevo.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showAlertReg(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Recuerda completar los datos e intenta de nuevo.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome (email:String) {
        val homeIntent: Intent = Intent(this, PrincipalActivity::class.java).apply{
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }

    private fun showAuth () {
        val authIntent: Intent = Intent(this, AuthActivity::class.java)
        startActivity(authIntent)
    }

    private fun insertUserData(email:String, provider:String, name:String,lastName:String){
db.collection("users").document(email).set(
        hashMapOf("provider" to provider,
        "name" to name,
        "lastName" to lastName)
)
    }


}
