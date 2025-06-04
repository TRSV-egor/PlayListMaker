package com.practicum.playlistmaker.settings.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.ui.model.EmailData
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.util.App
import com.practicum.playlistmaker.util.yandexSansFamily

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel) {

    val context = LocalContext.current
    val checked by settingsViewModel.observeDarkTheme().observeAsState()

    Scaffold(
        backgroundColor = colorResource(id = R.color.theme_white_black)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(true) {},
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(vertical = 21.dp)
                        .padding(start = 16.dp),
                    text = stringResource(id = R.string.settings_button1),
                    color = colorResource(id = R.color.toolbar),
                    style = TextStyle(
                        color = colorResource(id = R.color.toolbar),
                        fontSize = 16.sp,
                        fontFamily = yandexSansFamily,
                        fontWeight = FontWeight.Normal,
                    )
                )
                Switch(
                    modifier = Modifier.padding(top = 10.dp, end = 6.dp, bottom = 11.dp),

                    checked = checked!!,
                    onCheckedChange = {
                        (context.applicationContext as App).switchTheme(it)
                        settingsViewModel.toggleTheme(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorResource(id = R.color.blue),
                        checkedTrackColor = colorResource(id = R.color.blue_light),
                        uncheckedThumbColor = colorResource(id = R.color.gray),
                        uncheckedTrackColor = colorResource(id = R.color.gray_edit_text),
                    )
                )
            }

            MenuElement(
                name = stringResource(id = R.string.settings_button2),
                icon = painterResource(id = R.drawable.settings_share),
                action = {
                    settingsViewModel.shareApp(
                        context.getString(R.string.course_url)
                    )
                }
            )

            MenuElement(
                name = stringResource(id = R.string.settings_button3),
                icon = painterResource(id = R.drawable.settings_support),
                action = {
                    settingsViewModel.getHelp(
                        EmailData(
                            email = context.getString(R.string.my_email),
                            subject = context.getString(R.string.email_subject),
                            text = context.getString(R.string.email_text),
                        )
                    )
                }
            )

            MenuElement(
                name = stringResource(id = R.string.settings_button4),
                icon = painterResource(id = R.drawable.settings_track_arrow_forward),
                action = {
                    settingsViewModel.userAgreement(
                        context.getString(R.string.practicum_offer)
                    )
                }
            )

        }
    }
}

@Composable
fun MenuElement(name: String, icon: Painter, action: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(true) {
                action()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 21.dp)
                .padding(start = 16.dp),
            text = name,
            color = colorResource(id = R.color.toolbar),
            style = TextStyle(
                color = colorResource(id = R.color.toolbar),
                fontSize = 16.sp,
                fontFamily = yandexSansFamily,
                fontWeight = FontWeight.Normal,
            ),
        )
        Icon(
            modifier = Modifier.padding(top = 19.dp, end = 12.dp, bottom = 18.dp),
            painter = icon,
            contentDescription = null,
            tint = colorResource(id = R.color.toolbar),
        )

    }
}