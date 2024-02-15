package com.example.zoho.repository

import com.example.zoho.models.Post
import com.example.zoho.retrofit.ApiService
import com.example.zoho.room.PostsDataBase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ApiRepository @Inject constructor(private val apiService: ApiService,
                                        private val postsDataBase: PostsDataBase) {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val post: StateFlow<List<Post>>
        get() = _posts
    suspend fun getPosts() {
        val offLineResponse = postsDataBase.postsDao().getAllPosts()

        if (offLineResponse.isNotEmpty()) {
            _posts.emit(offLineResponse)
        } else {
            val response = apiService.getPosts()
            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                postsDataBase.postsDao().addPosts(response.body()!!)
                _posts.emit(response.body()!!)
            }
        }
    }
}