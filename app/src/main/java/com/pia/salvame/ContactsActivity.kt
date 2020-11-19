package com.pia.salvame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.AdapterView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_contacts.*

private val db = FirebaseFirestore.getInstance()

class ContactsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        val bundle = intent.extras
        val email = bundle?.getString("email")

        getContacts(email?: "")

addContactButton.setOnClickListener {
    showAddContacts(email?:"")
}

        goToAccountButton.setOnClickListener {
            showHome(email?:"")
            this.finish()
        }

        goToHomeButton.setOnClickListener {
            showPrincipal(email?:"")
            this.finish()
        }


    }

    private fun getContacts(email: String) {
        var lista = mutableListOf<Contacto>()
        var contacto = Contacto("", "",false)
        db.collection("users").document(email).collection("contacts").get().addOnSuccessListener { resultado ->
            for (docs in resultado) {

                db.collection("users").document(docs.id).get().addOnSuccessListener{
                    val name = it.getString("name") + " " + it.getString("lastName")
                    db.collection("users").document(docs.id).collection("security").document("state").get().addOnSuccessListener {
                        val estado =it.getBoolean("emergency")
                        contacto = Contacto(docs.id, name, estado?:false)
                        lista.add(contacto)
                        val adapter = ContactosAdapter(this, lista)
                        contactListView.adapter = adapter
                    }

                }

            }
        }
        contactListView.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
showMaps(lista.get(position).email)
        Log.e("Contacto: ", contacto.email)})

        countDownTimer(email)

    }

    private fun showAddContacts (email:String) {
        val addContactsIntent:Intent= Intent(this, AddContactActivity::class.java).apply{
            putExtra("email", email)
        }
        startActivity(addContactsIntent)
    }

    private fun showHome (email:String) {
        val homeIntent: Intent = Intent(this, HomeActivity::class.java).apply{
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }

    private fun showPrincipal (email: String) {
        val principalIntent: Intent = Intent(this, PrincipalActivity::class.java).apply{
            putExtra("email", email)
        }
        startActivity(principalIntent)
    }

    private fun showMaps (contacto:String) {
        val mapsIntent: Intent = Intent(this, MapsActivity::class.java).apply{
            putExtra("email", contacto)
        }
        startActivity(mapsIntent)
    }


    private fun countDownTimer(email:String){
        val timer = object : CountDownTimer(5000, 5000) {
            override fun onTick(millisUntilFinished: Long) {


            }

            override fun onFinish() {
                Log.e("TIMER", "MENSAJE DEL TIMER")

                getContacts(email)
            }
        }.start()
    }

}