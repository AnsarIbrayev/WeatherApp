package com.example.weatherapp.favorites

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FavoritesRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance(
        "https://nmd-assignment8-weatherapp-default-rtdb.europe-west1.firebasedatabase.app"
    )

    private fun ref(uid: String): DatabaseReference =
        db.getReference("users").child(uid).child("favorites")

    suspend fun ensureAnonUid(): String {
        auth.currentUser?.let { return it.uid }
        auth.signInAnonymously().awaitResult()
        return auth.currentUser?.uid ?: error("Anonymous auth failed")
    }

    fun observe(uid: String): Flow<List<FavoriteItem>> = callbackFlow {
        val r = ref(uid)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(FavoriteItem::class.java) }
                    .sortedByDescending { it.createdAt }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        r.addValueEventListener(listener)
        awaitClose { r.removeEventListener(listener) }
    }

    fun add(uid: String, title: String, note: String) {
        val r = ref(uid)
        val key = r.push().key ?: return
        val item = FavoriteItem(
            id = key,
            title = title.trim(),
            note = note.trim(),
            createdAt = System.currentTimeMillis(),
            createdBy = uid
        )
        r.child(key).setValue(item)
    }

    fun update(uid: String, id: String, title: String, note: String) {
        val updates = mapOf<String, Any>(
            "title" to title.trim(),
            "note" to note.trim()
        )
        ref(uid).child(id).updateChildren(updates)
    }

    fun delete(uid: String, id: String) {
        ref(uid).child(id).removeValue()
    }
}
