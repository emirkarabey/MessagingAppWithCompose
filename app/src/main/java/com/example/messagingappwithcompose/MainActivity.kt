package com.example.messagingappwithcompose

import android.content.ClipData
import android.os.Bundle
import android.text.Layout
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.messagingappwithcompose.ui.theme.MessagingAppWithComposeTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.intellij.lang.annotations.JdkConstants
import java.nio.file.Files.size
import kotlin.math.sign

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MessagingAppWithComposeTheme {
                androidx.compose.material.Surface {
                    MessageScreen()
                }
            }
        }
    }
}
@Composable
fun MyNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "profile") {
        composable("profile") { LoginScreen(navController = navController) }
        composable("friendslist") { MessageScreen() }
    }
}

@Composable
fun LoginScreen(navController: NavHostController){
    androidx.compose.material.Surface(Modifier.fillMaxSize()) {

        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var email = remember { mutableStateOf("")}
            var password = remember { mutableStateOf("")}
            Row(modifier = Modifier.border(width = 2.dp, color = Color.Black,)) {

                Image(imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_email_24),
                    contentDescription = null)
                TextField(value = email.value, onValueChange = {
                    email.value = it
                })
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Row() {

                Image(imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_lock_24),
                    contentDescription = null)
                TextField(value = password.value, onValueChange = {
                    password.value = it
                })
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Row() {
                Button(onClick = {
                    signIn(email.value,password.value, navController = navController)

                }) {
                    Text(text = "Sign In")
                }
                Spacer(modifier = Modifier.padding(30.dp))
                Button(onClick = {
                    signUp(email.value, password.value, navController = navController)
                }) {
                    Text(text = "Sign Up")
                }
            }
        }
    }
}

@Composable
fun MessageScreen(){
    androidx.compose.material.Surface(Modifier.fillMaxSize()){
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth()) {
               Image(imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_email_24), contentDescription =null,
                modifier = Modifier
                    .width(90.dp)
                    .height(90.dp)
                    .border(
                        width = 5.dp,
                        color = Color.Blue.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(32.dp)
                    ))
                Text(text = "Emir Karabey", fontSize = 35.sp, textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(5.dp))
            }
            LazyColumn(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                item {
                    Text(text = "First item")
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                var text = remember { mutableStateOf("")}
                TextField(value = text.value, onValueChange = {
                    text.value=it
                }, modifier = Modifier.padding(5.dp))
                Button(onClick = { /*TODO*/ }) {
                    Alignment.BottomEnd
                    Image(imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_send_24), contentDescription =null,
                    modifier = Modifier.height(50.dp).width(50.dp))
                }
            }
        }
    }
}
fun signUp(email:String,password:String,navController: NavHostController){
    var auth = Firebase.auth
    auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {

            //diğer sayfaya gönder.
            println("kayıt olma başarılı")
            navController.navigate("friendslist")
    }.addOnFailureListener {
        it.printStackTrace()
    }
}
fun signIn(email:String,password: String,navController: NavHostController){
    var auth = Firebase.auth
    auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
        //diğer sayfaya gönder
        println("giriş başarılı")
        navController.navigate("friendslist")
    }.addOnFailureListener {
        it.printStackTrace()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MessagingAppWithComposeTheme {
        MessageScreen()
    }
}