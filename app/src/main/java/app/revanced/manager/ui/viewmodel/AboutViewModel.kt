package app.revanced.manager.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.revanced.manager.domain.manager.KeystoreManager
import app.revanced.manager.network.api.MissingAssetException
import app.revanced.manager.network.api.ReVancedAPI
import app.revanced.manager.network.dto.ReVancedDonationLink
import app.revanced.manager.network.dto.ReVancedRelease
import app.revanced.manager.network.dto.ReVancedSocial
import app.revanced.manager.network.utils.getOrNull
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.brands.Discord
import compose.icons.fontawesomeicons.brands.Github
import compose.icons.fontawesomeicons.brands.Reddit
import compose.icons.fontawesomeicons.brands.Telegram
import compose.icons.fontawesomeicons.brands.Twitter
import compose.icons.fontawesomeicons.brands.Youtube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AboutViewModel(private val reVancedAPI: ReVancedAPI) : ViewModel() {
    val socials = mutableStateListOf<ReVancedSocial>()
    var contact: String? = null
    var donate: String? = null

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                reVancedAPI.getInfo("https://api.revanced.app").getOrNull()
            }?.let {
                socials.addAll(it.socials)
                contact = it.contact.email
                donate = it.donations.links.partition(ReVancedDonationLink::preferred).first.firstOrNull()?.url
            }
        }
    }

    companion object {
        private val _socialIcons = mapOf(
            "Discord" to FontAwesomeIcons.Brands.Discord,
            "GitHub" to FontAwesomeIcons.Brands.Github,
            "Reddit" to FontAwesomeIcons.Brands.Reddit,
            "Telegram" to FontAwesomeIcons.Brands.Telegram,
            "Twitter" to FontAwesomeIcons.Brands.Twitter,
            "X" to FontAwesomeIcons.Brands.Twitter,
            "YouTube" to FontAwesomeIcons.Brands.Youtube,
        )

        fun getSocialIcon(name: String) = _socialIcons[name] ?: Icons.Outlined.Language
    }
}