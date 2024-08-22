package com.example.rma_projekat_1.navigation

import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rma_projekat_1.album.gallery.breedAlbum
import com.example.rma_projekat_1.album.grid.breedGallery
import com.example.rma_projekat_1.catDetails.breedDetails
import com.example.rma_projekat_1.cats.list.breeds
import com.example.rma_projekat_1.leaderboard.listLeaderboard.leaderboard
import com.example.rma_projekat_1.quiz.quiz
import com.example.rma_projekat_1.userDetails.details.profile
import com.example.rma_projekat_1.userDetails.edit.profileEdit
import com.example.rma_projekat_1.users.login.logIn

@Composable
fun CatNavigation(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "login",
        enterTransition = { slideInHorizontally { it } },
        exitTransition = { scaleOut(targetScale = 0.8f)  },
        popExitTransition = { slideOutHorizontally { it } },
        popEnterTransition = { scaleIn(initialScale = 0.8f) }
    ) {

        logIn(
            route = "login",
            onUserClick = {
                navController.navigate(route = "breeds")
            }
        )

        breeds(
            route = "breeds",
            onUserClick = {
                if (it.equals("quiz"))
                    navController.navigate(route = it)
                else if (it.equals("leaderboard"))
                    navController.navigate(route = it)
                else if (it.equals("profile"))
                    navController.navigate(route = it)
                else
                    navController.navigate(route = "breeds/$it")
            }
        )

        breedDetails(
            route = "breeds/{breed_id}",
            onUserClick = {
                navController.navigate(route = "images/$it")
            },
            arguments = listOf(
                navArgument(name = "breed_id"){
                    nullable = false
                    type = NavType.StringType
                }
            ),
            onClose = {
                navController.navigateUp()
            }
        )

        breedGallery(
            route = "images/{breed_id}",
            onUserClick = {breedId, photoId ->
                navController.navigate(route = "albums/$breedId/$photoId")
            },
            arguments = listOf(
                navArgument(name = "breed_id"){
                    nullable = false
                    type = NavType.StringType
                }
            ),
            onClose = {
                navController.navigateUp()
            }
        )

        breedAlbum(
            route = "albums/{breed_id}/{photo_id}",
            arguments = listOf(
                navArgument(name = "breed_id") {
                    nullable = false
                    type = NavType.StringType
                },
                navArgument(name = "photo_id") {
                    nullable = false
                    type = NavType.StringType
                }
            ),
            onClose = {
                navController.navigateUp()
            }
        )

        quiz(
            route = "quiz",
            onUserClick = {
                if (it.equals("breeds"))
                    navController.navigate(route = it)
                else if (it.equals("leaderboard"))
                    navController.navigate(route = it)
                else if (it.equals("profile"))
                    navController.navigate(route = it)
            }
        )

        profile(
            route = "profile",
            onUserClick = {
                if (it.equals("breeds"))
                    navController.navigate(route = it)
                else if (it.equals("leaderboard"))
                    navController.navigate(route = it)
                else if (it.equals("quiz"))
                    navController.navigate(route = it)
                else
                    navController.navigate("profileEdit")
            }
        )

        profileEdit(
            route = "profileEdit",
            onUserClick = {
                if (it.equals("breeds"))
                    navController.navigate(route = it)
                else if (it.equals("leaderboard"))
                    navController.navigate(route = it)
                else if (it.equals("quiz"))
                    navController.navigate(route = it)
                else if (it.equals("profile"))
                    navController.navigate(route = it)
            },
            onClose = {
                navController.navigateUp()
            }
        )

        leaderboard(
            route = "leaderboard",
            onUserClick = {
                if (it.equals("breeds"))
                    navController.navigate(route = it)
                else if (it.equals("quiz"))
                    navController.navigate(route = it)
                else if (it.equals("profile"))
                    navController.navigate(route = it)
            }
        )

    }
}

inline val SavedStateHandle.breedId: String
    get() = checkNotNull(get("breed_id")) {"breed_id is mendatory"}

inline val SavedStateHandle.photoId: String
    get() = checkNotNull(get("photo_id")) {"photo_id is mendatory"}