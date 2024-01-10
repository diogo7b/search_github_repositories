package com.example.github_repositories


import com.example.github_repositories.domain.Repos
import retrofit2.Call
import retrofit2.http.GET

interface RepositoriesApi {
    @GET("repos")
    fun getAllRepositories(): Call<List<Repos>>
}