package com.halalrishtey.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.halalrishtey.R
import com.halalrishtey.models.ProfileCardData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.profile_card.view.*

class CardDataRVAdapter(private var items: List<ProfileCardData>) :
    RecyclerView.Adapter<CardDataRVAdapter.CardDataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardDataViewHolder {
        val inflatedView =
            LayoutInflater.from(parent.context).inflate(R.layout.profile_card, parent, false)
        return CardDataViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: CardDataViewHolder, position: Int) {
        val card = items[position]
        holder.bindCard(card)
    }

    override fun getItemCount() = items.size

    class CardDataViewHolder constructor(
        v: View
    ) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var cardData: ProfileCardData? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            Toast.makeText(
                p0?.context,
                "${p0?.cardTitleTextView?.text}'s profile was clicked",
                Toast.LENGTH_SHORT
            ).show()
        }


        companion object {
            private val KEY = "CARD"
        }

        fun bindCard(card: ProfileCardData) {
            this.cardData = card

            if (card.isUserShortlisted) {
                view.showInterestBtn.setIconResource(R.drawable.ic_favorite)
            } else {
                view.showInterestBtn.setIconResource(R.drawable.ic_favorite_border)
            }

            if (card.data.photoUrl.length > 5) {
                Picasso.get().load(card.data.photoUrl)
                    .into(view.cardImageView)

                Picasso.get().load(card.data.photoUrl)
                    .into(view.cardAvatarImageView)
            }

            view.cardTitleTextView.text = card.data.displayName
            view.cardSubtitleTextView.text = "${card.data.age} - ${card.data.height}"

            view.showInterestBtn.setOnClickListener(card.showBtnInterestListener)
            view.sendMessageBtn.setOnClickListener(card.messageBtnListener)
        }
    }
}
