package com.example.pokedex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedex.data.model.Pokemon

class PokemonAdapter(
    private val pokemons: MutableList<Pokemon> = mutableListOf(),
    private val onItemClick: (Pokemon) -> Unit
) : RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = pokemons[position]
        holder.bind(pokemon)
    }

    override fun getItemCount(): Int = pokemons.size

    fun updateData(newPokemons: List<Pokemon>) {
        pokemons.clear()
        pokemons.addAll(newPokemons)
        notifyDataSetChanged()
    }

    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tvPokemonName)

        fun bind(pokemon: Pokemon) {
            nameTextView.text = pokemon.name
            itemView.setOnClickListener { onItemClick(pokemon) }
        }
    }
}