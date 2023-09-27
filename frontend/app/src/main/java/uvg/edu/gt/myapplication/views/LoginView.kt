package uvg.edu.gt.myapplication.views

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import uvg.edu.gt.myapplication.Screen
import uvg.edu.gt.myapplication.model.LoginResponse
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(navController: NavController) {
    // Returns a scope that's cancelled when F is removed from composition
    val coroutineScope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    Column (
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize()
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Login",
                fontSize = (32.sp),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(text = "username") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "password") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            val context = LocalContext.current
            Button(
                onClick = {
                    coroutineScope.launch {
                        val (statusCode, loginResponse) = sendLoginCredentials(username, password)
                        if(statusCode == 200){
                            navController.navigate(Screen.HomeView.route + "?OauthToken=${loginResponse.token}")
                        }
                        else{
                            Toast.makeText(context,
                                "${loginResponse.message}.\n Try again.${statusCode}",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(text = "Go!")
            }
            Text(text = text)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Don't have and account?")
            Button(onClick = { navController.navigate(Screen.SignUpView.route) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                Text(text = "Sign Up", color = Color.Blue, textDecoration = TextDecoration.Underline)
            }
        }
    }
}

// Function to send login credentials to the server
suspend fun sendLoginCredentials(username: String, password: String): Pair<Int, LoginResponse> {
    return withContext(Dispatchers.IO) {

        val client = OkHttpClient()

        val json = JSONObject()
        json.put("username", username)
        json.put("password", password)
        val url = "http://192.168.0.6:3000/login"

        val mediaType = "application/json; charset=UTF-8".toMediaType()
        val requestBody = json.toString().toRequestBody(mediaType)

        // Create a POST request
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        // Execute the request and get the response
        try{        val response = client.newCall(request).execute()
            val statusCode = response.code

            // Check if the request was successful
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                // Parse the JSON response using kotlinx.serialization
                val loginResponse = Json.decodeFromString<LoginResponse>(responseBody)
                Pair(statusCode, loginResponse)
            } else {
                // Handle the error here or return a default response
                Pair(statusCode, LoginResponse("Login failed", ""))
            }
        } catch (e: Exception){
            Pair(500, LoginResponse("Cant connect to server", ""))
        }

    }
}