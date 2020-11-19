package com.pia.salvame

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType{
    BASIC
}
class HomeActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Setup
        val bundle = intent.extras
       val email= bundle?.getString("email")

        cargarDatos(email?:"")
        setup(email ?:"")
    }

    private fun setup(email:String){

        title="Cuenta"
        emailTextView.text =email

logOutButton.setOnClickListener{
    FirebaseAuth.getInstance().signOut()
    eliminarSesion()
    showAuth()
}

        updateButton.setOnClickListener {
            updatetUserData(email?:"",name_EditText.text.toString(),lastNameEditText.text.toString())
        }

        goToHomeButton.setOnClickListener {
            showPrincipal(email)
            this.finish()
        }

        goToContactsButton.setOnClickListener {
            showContacts(email)
            this.finish()
        }

        goToAccountButton.isClickable=false
        goToContactsButton.isClickable=true
        goToHomeButton.isClickable=true





    }

    private fun eliminarSesion(){
        sharedPreferences=getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor=sharedPreferences.edit()
        editor.remove("email")
        editor.remove("password")
        editor.apply()
    }


    private fun cargarDatos (email:String){
        db.collection("users").document(email).get().addOnSuccessListener {
            name_EditText.setText(it.get("name")as String?)
            lastNameEditText.setText(it.get("lastName")as String?)
            providerTextView.setText(it.get("provider") as String?)
        }
    }

    private fun updatetUserData(email:String, name:String,lastName:String){
        db.collection("users").document(email).set(
            hashMapOf("provider" to "BASIC",
                "name" to name,
                "lastName" to lastName)
        )
    }

    private fun showContacts (email:String) {
        val contactsIntent: Intent = Intent(this, ContactsActivity::class.java).apply{
            putExtra("email", email)
        }
        startActivity(contactsIntent)
    }

    private fun showPrincipal (email: String) {
        val principalIntent: Intent = Intent(this, PrincipalActivity::class.java).apply{
putExtra("email", email)
        }
        startActivity(principalIntent)
    }

    private fun showAuth () {
        val authIntent: Intent = Intent(this, AuthActivity::class.java)
        startActivity(authIntent)
    }

}