package uvg.edu.gt.myapplication.views

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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import uvg.edu.gt.myapplication.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpView(navController: NavController){
    val coroutineScope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
                text = "Sign Up",
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
                onClick = { coroutineScope.launch {
                    var result = sendSignUpCredentials(username, password)
                    Toast.makeText(context,
                        result,
                        Toast.LENGTH_SHORT).show()
                } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(text = "Register!")
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Already have and account?")
            Button(onClick = { navController.navigate(Screen.LoginView.route){
                popUpTo(Screen.LoginView.route) {
                    inclusive = true
                }
            }},
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                Text(text = "Login", color = Color.Blue, textDecoration = TextDecoration.Underline)
            }
        }
    }
}

suspend fun sendSignUpCredentials(username: String, password: String): String {
    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()

        // Create a JSON object with the username and password
        val json = JSONObject()
        json.put("username", username)
        json.put("password", password)

        val url = "http://192.168.0.6:3000/signup" // Replace with your server URL

        val mediaType = "application/json; charset=UTF-8".toMediaType()
        val requestBody = json.toString().toRequestBody(mediaType)

        // Create a POST request
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        // Execute the request and get the response
        val response = client.newCall(request).execute()

        // Check if the request was successful
        if (response.isSuccessful) {
            response.body?.string() ?: "Empty response"
        } else {
            val code = response.code
            if (code == 409)
                "Username already exist"
            else
                "Something went wrong"
        }
    }
}