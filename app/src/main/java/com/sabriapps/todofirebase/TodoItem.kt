package com.sabriapps.todofirebase

data class TodoItem(
    val text: String,
    val done: Boolean
) {


    constructor() : this("", false) {

    }


}