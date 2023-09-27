package uvg.edu.gt.myapplication.views

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import uvg.edu.gt.myapplication.Screen
import uvg.edu.gt.myapplication.model.HomeResponse

@Composable
fun HomeView(navController: NavController, oauthToken : String){
    val coroutineScope = rememberCoroutineScope()
    var timeRemaining by remember { mutableStateOf(1) }

    Column( modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Your current token is : $oauthToken")
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {
            coroutineScope.launch {
                timeRemaining = checkTokenRemainingTime(oauthToken)
                if(timeRemaining < 0 )
                    navController.navigate(Screen.LoginView.route){
                        popUpTo(Screen.LoginView.route) {
                            inclusive = true
                        }
                    }
            }
        }) {
            Text(text = "Check Token Remaining time")
        }
        Spacer(modifier = Modifier.height(15.dp))
        Text(text = "Your session will end in:\n $timeRemaining",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center)
    }
}

suspend fun checkTokenRemainingTime(oauthToken: String): Int {
    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val url = "http://192.168.0.6:3000/home"

        // Create an HTTP request with the OAuth token in the header
        val request = Request.Builder()
            .url(url)
            .addHeader("authorization", oauthToken)
            .build()

        // Execute the request and get the response
        val response = client.newCall(request).execute()

        // Check if the request was successful
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            val serverResponse = Json.decodeFromString<HomeResponse>(responseBody)
            serverResponse.timeRemaining // Extract the "message" property
        } else {
            -1
        }
    }
}
