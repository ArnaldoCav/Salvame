package com.pia.salvame

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.protobuf.Any
import kotlinx.android.synthetic.main.activity_add_contact.*

private val db = FirebaseFirestore.getInstance()

class AddContactActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        val bundle = intent.extras
        val email = bundle?.getString("email")

        buttonAddContacto.setOnClickListener {
            agregarContacto(editTextContacto.text.toString()?:"",email?:"")
            //insertContact(email?:"",editTextContacto.text.toString())
        }

    }

    private fun agregarContacto(contacto:String, email:String){

        if(contacto != email)
        {
            (db.collection("users").document(email).collection("contacts").document(contacto).get().addOnSuccessListener {
if(it.exists() == false)
{
    db.collection("users").document(contacto).get().addOnSuccessListener {
        if (it.exists())
        {
            val nombre = it.getString("name") + " " + it.getString("lastName")
            showConfirmar(nombre, contacto, email)
        }
        else
        {
            showAlert()
        }

    }
}
                else
{
    showAlert2()
}
            })

            
        }
        else
        {
            showAlert1()
        }


    }

    private fun showAlert2(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Este usuario ya forma parte de tus contactos.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showAlert1(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("No puedes agregarte a ti mismo como contacto, intentalo de nuevo.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error al encontrar usuario")
        builder.setMessage("No existe ningun usuario con este correo, intentalo de nuevo.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showConfirmar (nombre:String, contacto:String, email: String){
        val confirmar =AlertDialog.Builder(this)
        confirmar.setTitle("Confirmacion")
        confirmar.setMessage("¿Seguro que quieres agregar a $nombre a tus contactos?")
        confirmar.setCancelable(false)
        confirmar.setPositiveButton("Confirmar", DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
insertContact(email,contacto)
            showContacts(email)
        })

        confirmar.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, which ->
            showContacts(email)
        })
        confirmar.create()
        confirmar.show()
    }

    private fun insertContact(email:String, contacto:String){
        db.collection("users").document(email).collection("contacts").document(contacto).set(
                hashMapOf("contacto" to contacto)
        )
    }

    private fun showContacts (email:String) {
        val contactsIntent: Intent = Intent(this, ContactsActivity::class.java).apply{
            putExtra("email", email)
        }
        startActivity(contactsIntent)
    }

}