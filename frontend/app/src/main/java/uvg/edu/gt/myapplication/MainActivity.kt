package uvg.edu.gt.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import uvg.edu.gt.myapplication.views.HomeView
import uvg.edu.gt.myapplication.views.LoginView
import uvg.edu.gt.myapplication.views.SignUpView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = Screen.LoginView.route){
                composable(route = Screen.LoginView.route){
                    LoginView(navController)
                }
                composable(route = Screen.SignUpView.route){
                    SignUpView(navController)
                }
                composable(
                    route = Screen.HomeView.route + "/{OauthToken}",
                    arguments = listOf(navArgument("OauthToken") { type = NavType.StringType })
                ){ backStackEntry ->
                    val oauthToken = backStackEntry.arguments?.getString("OauthToken")
                    requireNotNull(oauthToken) { Log.e("Error","Token must NOT be null")}
                    HomeView(navController, oauthToken)
                }
            }
        }
    }
}