package com.example.messagingappwithcompose

import android.graphics.Color.rgb
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.VectorConverter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.messagingappwithcompose.Model.MessageContent
import com.example.messagingappwithcompose.ui.theme.MessagingAppWithComposeTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.security.Timestamp
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MessagingAppWithComposeTheme {
                androidx.compose.material.Surface {
                    MyNavigation()
                }
            }
        }
    }
}
@Composable
fun MyNavigation(){
    val navController = rememberNavController()
    val auth = Firebase.auth
    val user = auth.currentUser
    if (user!=null) {
        NavHost(navController = navController, startDestination = "friendslist") {
            composable("profile") { LoginScreen(navController = navController) }
            composable("friendslist") { MessageScreen() }
        }
    }else{
        NavHost(navController = navController, startDestination = "profile") {
            composable("profile") { LoginScreen(navController = navController) }
            composable("friendslist") { MessageScreen() }
        }
    }

}

@Composable
fun LoginScreen(navController: NavHostController){
    val auth = Firebase.auth
    val user = auth.currentUser
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
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color(rgb(32, 32, 32)))) {
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp).background(Color(rgb(51,51,51)))) {
               Image(bitmap = ImageBitmap.imageResource(id = R.drawable.scarlet2), contentDescription =null,
                modifier = Modifier
                    .size(100.dp).padding(8.dp)
                    .clip(CircleShape)
                    )
                Text(text = "Scarlet Jhonasson", fontSize = 35.sp, textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(5.dp), color = Color.White)
            }
            val db = Firebase.firestore
            val auth = Firebase.auth
            val user = auth.currentUser
            val messageList = remember{ mutableStateListOf<MessageContent>()}
            val docRef = db.collection("Messages").addSnapshotListener { value, error ->
                if (error!=null){
                    error.printStackTrace()
                }else{
                    if (value!=null){
                        if (!value.isEmpty){
                            val documents = value.documents
                            for (document in documents){
                                val sender = document.get("sender")
                                val message = document.get("message")
                                val timeStamp = document.get("date") as com.google.firebase.Timestamp
                                val messageContent = MessageContent(sender.toString(),message.toString(),timeStamp)
                                messageList.add(messageContent)
                            }
                        }
                    }
                }
            }
            MessageList(messageList = messageList,auth)
        }
            Row(modifier = Modifier.fillMaxWidth(),verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End) {
                var text = remember { mutableStateOf("")}
                TextField(value = text.value, onValueChange = {
                    text.value=it
                }, modifier = Modifier.padding(5.dp).clip(RoundedCornerShape(40.dp))
                    .background(Color(rgb(51, 51, 51))).weight(85f))
                Button(onClick = {
                    val auth = Firebase.auth
                    val user = auth.currentUser
                    user?.let {
                        var message = MessageContent(user.email.toString(),text.value, com.google.firebase.Timestamp.now())
                        val db = Firebase.firestore
                        val uuid = UUID.randomUUID()
                        val hashMap = hashMapOf(
                            "sender" to message.sender,
                            "message" to message.message,
                            "date" to message.timeStamp
                        )
                        db.collection("Messages").document(uuid.toString()).set(hashMap)
                            .addOnSuccessListener {
                                println("message yollandı")
                                text.value=""
                            }.addOnFailureListener {
                                it.printStackTrace()
                            }
                    }

                }, modifier = Modifier.height(65.dp).weight(15f).clip(RoundedCornerShape(20.dp)).padding(0.dp,0.dp,0.dp,5.dp)) {
                    Image(imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_send_24), contentDescription =null,
                    modifier = Modifier)
                } 
            }
    }
}


@Composable
fun MessageList(messageList:List<MessageContent>,auth:FirebaseAuth){
    LazyColumn (){
        items(messageList){crypto->
            MessageRow(messageContent = crypto,auth)
            println("messagelist içi")
        }
    }
}
@Composable
fun MessageRow(messageContent:MessageContent,auth:FirebaseAuth){
    val user = auth.currentUser
    if (user!!.email.equals(messageContent.sender)) {
        Box(modifier = Modifier
            .padding(80.dp, 5.dp, 5.dp, 5.dp)
            .clip(shape = RoundedCornerShape(20.dp))
            .background(Color(rgb(173, 216, 230)))
        ) {
            Row(modifier = Modifier
                .padding(5.dp)) {
                Image(bitmap = ImageBitmap.imageResource(id = R.drawable.kerem), contentDescription =null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterVertically))
                Text(text = messageContent.message, fontSize = 20.sp, color = Color.White,
                    modifier = Modifier
                        .padding(8.dp, 5.dp, 8.dp, 5.dp)
                        .fillMaxWidth(),textAlign = TextAlign.Start)
            }
        }

    }else{
        Box(modifier = Modifier
            .padding(5.dp, 5.dp, 80.dp, 5.dp)
            .clip(shape = RoundedCornerShape(20.dp))
            .background(Color(rgb(112, 128, 144)))
            ) {
            Row(modifier = Modifier
                .padding(5.dp)
            ) {
                Row() {
                    Image(bitmap = ImageBitmap.imageResource(id = R.drawable.scarlet2), contentDescription =null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterVertically))
                    Column(modifier = Modifier
                        .padding(8.dp, 5.dp, 80.dp, 5.dp)) {
                        Text(text = messageContent.sender,
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp, 5.dp, 8.dp, 5.dp))
                        Text(text = messageContent.message, fontSize = 20.sp, color = Color.White,
                            modifier = Modifier
                                .padding(8.dp, 5.dp, 8.dp, 5.dp)
                        )
                    }
                }
            }
        }



    }


    println("message row içi")
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
        MyNavigation()
    }
}