package com.example.messagingappwithcompose.Model

import com.google.firebase.Timestamp


class MessageContent(
    val sender:String,
    val message:String,
    val timeStamp:Timestamp
) {
}