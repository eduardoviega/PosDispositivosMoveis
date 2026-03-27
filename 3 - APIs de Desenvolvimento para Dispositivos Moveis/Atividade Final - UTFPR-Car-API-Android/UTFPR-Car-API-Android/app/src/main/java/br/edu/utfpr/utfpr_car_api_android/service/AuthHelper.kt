package br.edu.utfpr.utfpr_car_api_android.service

import android.content.Context
import android.widget.Toast
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import br.edu.utfpr.utfpr_car_api_android.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object AuthHelper {
    private const val WEB_CLIENT_ID = "WEB_CLIENT_ID"

    private val auth: FirebaseAuth by lazy { Firebase.auth }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    suspend fun signInWithGoogle(context: Context): FirebaseUser? {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID).build()

        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

        val result = credentialManager.getCredential(
            context = context, request = request
        )

        return handleCredential(result.credential)
    }

    private suspend fun handleCredential(credential: Credential): FirebaseUser? {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val firebaseCredential =
                GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

            val authResult = auth.signInWithCredential(firebaseCredential).await()
            return authResult.user
        }
        return null
    }

    fun signOut(context: Context, onComplete: () -> Unit) {
        val credentialManager = CredentialManager.create(context)
        auth.signOut()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (_: Exception) {
                Toast.makeText(
                    context,
                    context.getString(R.string.erro_ao_limpar_credenciais),
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                onComplete()
            }
        }
    }
}
