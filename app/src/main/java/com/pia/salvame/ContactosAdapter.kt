package com.pia.salvame

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_principal.*
import kotlinx.android.synthetic.main.item_contact.view.*

class ContactosAdapter (private val mContext: Context, private val listaContactos:List<Contacto>):
    ArrayAdapter<Contacto>(mContext,0, listaContactos) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mContext).inflate(R.layout.item_contact, parent, false)

        val contacto = listaContactos[position]

        layout.name.text = contacto.name
        layout.email.text = contacto.email
        if(contacto.emergency)
        {layout.imageView.setVisibility(View.VISIBLE)}
        else
        {layout.imageView.setVisibility(View.GONE)}

        return layout
    }
}