package br.edu.utfpr.appcontatos.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.edu.utfpr.appcontatos.ui.contact.form.ContactFormScreen
import br.edu.utfpr.appcontatos.ui.contact.list.ContactsListScreen

@Composable
fun AppContactsNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "list"
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = "list") {
            ContactsListScreen(
                onAddPressed = {
                    navController.navigate("form")
                },
                onContactPressed = { contact ->
                    navController.navigate("form?id=${contact.id}")
                }
            )
        }
        composable(
            route = "form?id={id}",
            arguments = listOf(
                navArgument(name = "id") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            ContactFormScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
                onUpdated = {
                    navController.navigate("list") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}