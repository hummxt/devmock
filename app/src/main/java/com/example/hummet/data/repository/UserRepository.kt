package com.example.hummet.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class UserData(
    val uid: String = "",
    val name: String = "",
    val role: String = "",
    val goal: String = "",
    val experienceLevel: String = "Beginner",
    val learningPath: String = "Mobile Development",
    val readyMeter: Int = 0,
    val profilePhotoUrl: String = ""
)

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    suspend fun saveUserProfile(name: String, role: String, goal: String, profilePhotoUrl: String? = null) {
        val uid = auth.currentUser?.uid ?: return
        val updates = mutableMapOf(
            "name" to name,
            "role" to role,
            "goal" to goal
        )
        profilePhotoUrl?.let { updates["profilePhotoUrl"] = it }
        
        usersCollection.document(uid).update(updates as Map<String, Any>).addOnFailureListener {
            usersCollection.document(uid).set(
                updates + mapOf("uid" to uid, "readyMeter" to 0),
                com.google.firebase.firestore.SetOptions.merge()
            )
        }.await()
    }

    suspend fun updateOnboardingData(name: String, level: String, path: String, goal: String) {
        val uid = auth.currentUser?.uid ?: return
        val updates = mapOf(
            "name" to name,
            "experienceLevel" to level,
            "learningPath" to path,
            "goal" to goal
        )
        usersCollection.document(uid).set(updates + mapOf("uid" to uid, "readyMeter" to 0), com.google.firebase.firestore.SetOptions.merge()).await()
    }

    suspend fun getUserProfile(): UserData? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val snapshot = usersCollection.document(uid).get().await()
            snapshot.toObject(UserData::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
