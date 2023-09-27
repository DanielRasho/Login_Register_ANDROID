package uvg.edu.gt.myapplication.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import uvg.edu.gt.myapplication.Screen

@Composable
fun HomeView(navController: NavController, oauthToken : String){
    Column( modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Your current token is : $oauthToken")
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Check Token Remaining time")
        }
        Spacer(modifier = Modifier.height(15.dp))
    }
}