package com.aroundly.aroundly.uiLibrary.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aroundly.aroundly.uiLibrary.admin.AdminDashboardScreen
import com.aroundly.aroundly.uiLibrary.auth.LoginScreen
import com.aroundly.aroundly.uiLibrary.auth.SignupScreen
import com.aroundly.aroundly.uiLibrary.auth.WelcomeScreen
import com.aroundly.aroundly.uiLibrary.common.EquipmentShopDetailsScreen
import com.aroundly.aroundly.uiLibrary.common.HotelDetailsScreen
import com.aroundly.aroundly.uiLibrary.common.PlaceToVisitDetailsScreen
import com.aroundly.aroundly.uiLibrary.common.RestaurantDetailsScreen
import com.aroundly.aroundly.uiLibrary.dashboard.DashboardScreen
import com.aroundly.aroundly.uiLibrary.manager.ManagerDashboardScreen
import com.aroundly.aroundly.uiLibrary.manager.ManagerSignupScreen
import com.aroundly.aroundly.uiLibrary.map.MapScreen
import com.aroundly.aroundly.uiLibrary.viewModel.MenuViewModel
import com.aroundly.aroundly.uiLibrary.viewModel.RestaurantDetailsViewModel
import com.aroundly.aroundly.uiLibrary.viewModel.HotelDetailsViewModel
import com.aroundly.aroundly.uiLibrary.viewModel.PlaceDetailsViewModel
import com.aroundly.aroundly.uiLibrary.viewModel.EquipmentDetailsViewModel
import com.aroundly.aroundly.uiLibrary.viewModel.ReviewViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome") {

        composable("welcome") {
            WelcomeScreen(
                onExplorerClick = {
                    navController.navigate("signup")
                },
                onBusinessOwnerClick = {
                    navController.navigate("manager_signup")
                },
                onSignInClick = {
                    navController.navigate("login") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("role_router") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onAdminLoginSuccess = {
                    navController.navigate("admin_dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToSignup = {
                    navController.navigate("signup")
                },
                onNavigateToManagerSignup = {
                    navController.navigate("manager_signup")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("signup") {
            SignupScreen(
                onSignupSuccess = {
                    navController.navigate("role_router") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("manager_signup") {
            ManagerSignupScreen(
                onSignupSuccess = {
                    navController.navigate("role_router") {
                        popUpTo("manager_signup") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("role_router") {
            RoleRouterScreen(
                onNavigateToUserDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("role_router") { inclusive = true }
                    }
                },
                onNavigateToManagerDashboard = {
                    navController.navigate("manager_dashboard") {
                        popUpTo("role_router") { inclusive = true }
                    }
                },
                onNavigateToAdminDashboard = {
                    navController.navigate("admin_dashboard") {
                        popUpTo("role_router") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("role_router") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                },
                onRestaurantClick = { r ->
                    navController.navigate("restaurant_details/${r.id}")
                },
                onHotelClick = { h ->
                    navController.navigate("hotel_details/${h.id}")
                },
                onPlaceClick = { p ->
                    navController.navigate("place_details/${p.id}")
                },
                onEquipmentClick = { e ->
                    navController.navigate("equipment_details/${e.id}")
                },
                onMapClick = {
                    navController.navigate("map")
                }
            )
        }

        composable("map") {
            MapScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            "restaurant_details/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            val viewModel: RestaurantDetailsViewModel = hiltViewModel()
            val reviewViewModel: ReviewViewModel = hiltViewModel()
            val menuViewModel: MenuViewModel = hiltViewModel()
            val restaurant by viewModel.restaurant.collectAsStateWithLifecycle()
            restaurant?.let { r ->
                RestaurantDetailsScreen(
                    restaurant = r,
                    reviewViewModel = reviewViewModel,
                    menuViewModel = menuViewModel,
                    onBack = { navController.popBackStack() }
                )
            } ?: run {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        composable(
            "hotel_details/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            val viewModel: HotelDetailsViewModel = hiltViewModel()
            val reviewViewModel: ReviewViewModel = hiltViewModel()
            val menuViewModel: MenuViewModel = hiltViewModel()
            val hotel by viewModel.hotel.collectAsStateWithLifecycle()
            hotel?.let { h ->
                HotelDetailsScreen(
                    hotel = h,
                    reviewViewModel = reviewViewModel,
                    menuViewModel = menuViewModel,
                    onBack = { navController.popBackStack() }
                )
            } ?: run {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        composable(
            "place_details/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            val viewModel: PlaceDetailsViewModel = hiltViewModel()
            val reviewViewModel: ReviewViewModel = hiltViewModel()
            val menuViewModel: MenuViewModel = hiltViewModel()
            val place by viewModel.place.collectAsStateWithLifecycle()
            place?.let { p ->
                PlaceToVisitDetailsScreen(
                    place = p,
                    reviewViewModel = reviewViewModel,
                    menuViewModel = menuViewModel,
                    onBack = { navController.popBackStack() }
                )
            } ?: run {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        composable(
            "equipment_details/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            val viewModel: EquipmentDetailsViewModel = hiltViewModel()
            val reviewViewModel: ReviewViewModel = hiltViewModel()
            val menuViewModel: MenuViewModel = hiltViewModel()
            val equipment by viewModel.equipment.collectAsStateWithLifecycle()
            equipment?.let { e ->
                EquipmentShopDetailsScreen(
                    shop = e,
                    reviewViewModel = reviewViewModel,
                    menuViewModel = menuViewModel,
                    onBack = { navController.popBackStack() }
                )
            } ?: run {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        composable("manager_dashboard") {
            ManagerDashboardScreen(
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable("admin_dashboard") {
            AdminDashboardScreen(
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
            )
        }
    }
}
