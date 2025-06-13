package com.kosiso.pupilmanager.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kosiso.pupilmanager.ui.screens.AddPupilScreen
import com.kosiso.pupilmanager.ui.screens.EditPupilScreen
import com.kosiso.pupilmanager.ui.screens.PupilsScreen
import com.kosiso.pupilmanager.ui.viewmodels.MainViewModel
import com.kosiso.sfinventory.utils.enum_classes.RootNav
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
            val rootNavController = rememberNavController()
            RootNavigation(rootNavController, mainViewModel)
        }
    }
}


@Composable
fun RootNavigation(rootNavController: NavHostController, mainViewModel: MainViewModel){
    NavHost(
        navController = rootNavController,
        startDestination = RootNav.PUPILS.route
    ) {

        composable(RootNav.PUPILS.route) {
            PupilsScreen(
                mainViewModel = mainViewModel,
                onNavigateToAddPupilScreen = {
                    rootNavController.navigate(RootNav.ADD_PUPIL.route)
                },
                onNavigateToPupilDetailsScreen = { pupil->
                    rootNavController.navigate("${RootNav.EDIT_PUPIL.route}/${pupil.pupilId}")
                }
            )
        }
        composable(RootNav.ADD_PUPIL.route) {
            AddPupilScreen(
                mainViewModel,
                onBackClick = { rootNavController.popBackStack() }
            )
        }
        composable(
            route = "${RootNav.EDIT_PUPIL.route}/{pupilId}",
            arguments = listOf(navArgument("pupilId") { type = NavType.IntType })
        ) {backStackEntry ->
            val pupilId = backStackEntry.arguments?.getInt("pupilId")
            Log.i("pupil id 2", "pupil id: $pupilId")
            EditPupilScreen(
                pupilId = pupilId!!,
                mainViewModel,
                onBackClick = { rootNavController.popBackStack() }
            )
        }
    }
}
