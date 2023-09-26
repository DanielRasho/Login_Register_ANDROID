package uvg.edu.gt.myapplication

sealed class Screen (val route: String){
    object LoginView : Screen("LoginView")
    object SignUpView : Screen("SignUpView")
    object HomeView : Screen("HomeView")
}