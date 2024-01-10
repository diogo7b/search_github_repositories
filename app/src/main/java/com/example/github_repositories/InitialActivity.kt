package com.example.github_repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.github_repositories.adapters.ReposAdapter
import com.example.github_repositories.domain.Repos
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class InitialActivity : AppCompatActivity() {

    lateinit var etNameGithubUser: EditText
    lateinit var btnConfirmGithubUser: Button
    lateinit var btnClear: Button
    lateinit var rcListaRepositories: RecyclerView
    lateinit var reposApi: RepositoriesApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)

        setupView()
        setupListener()
        checkNicknameSaved()
    }

    fun checkNicknameSaved() {
        val nickname = getUserNickname()
        if (nickname == null || nickname == "") {
            emptyState()
        } else {
            if (checkConnection(this)) {
                setupRetrofit()
                getRepositories()
            } else {
                showErrorOnScreen()
            }
        }
    }

    fun setupView() {
        etNameGithubUser = findViewById(R.id.et_user_github)
        btnConfirmGithubUser = findViewById(R.id.btn_confirm)
        btnClear = findViewById(R.id.btn_clear)
        rcListaRepositories = findViewById(R.id.rv_list_repositories)
    }

    fun setupListener() {
        btnConfirmGithubUser.setOnClickListener {
            val userNickname = etNameGithubUser.text.toString()
            saveUserNickname(userNickname)
            rcListaRepositories.isVisible = true
        }
        btnClear.setOnClickListener {
            emptyState()
        }
    }

    fun setupRetrofit() {
        val nickname = getUserNickname()
        val builderRetrofit =
            Retrofit.Builder()
                .baseUrl("https://api.github.com/users/$nickname/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        reposApi = builderRetrofit.create(RepositoriesApi::class.java)
    }

    fun showErrorOnScreen() {
        Toast.makeText(this, "Erro ", Toast.LENGTH_LONG)
    }

    fun emptyState() {
        deleteUserNickname()
        rcListaRepositories.isVisible = false
        etNameGithubUser.text = null
    }

    fun checkConnection(context: Context?): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATE")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATE")
            return networkInfo.isConnected
        }
    }

    fun saveUserNickname(userNickname: String) {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPreferences.edit()) {
            putString(getString(R.string.github_nickname), userNickname)
            apply()
        }

    }

    fun getUserNickname(): String? {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        return sharedPreferences.getString(getString(R.string.github_nickname), null)
    }

    fun getRepositories() {
        reposApi.getAllRepositories().enqueue(object : Callback<List<Repos>> {
            override fun onResponse(call: Call<List<Repos>>, response: Response<List<Repos>>) {
                if (response.isSuccessful) {
                    rcListaRepositories.isVisible = true
                    Log.d("ResponseBody", response.body().toString())
                    response.body()?.let {
                        setupList(it)
                    }
                }
            }

            override fun onFailure(call: Call<List<Repos>>, t: Throwable) {
                Log.e("ErrorApi", "Falha ao comunicar")
            }
        })
    }

    fun setupList(respositoriesList: List<Repos>) {
        val lista = ReposAdapter(respositoriesList, this)
        Log.d("ListaBotao", lista.toString())
        rcListaRepositories.apply {
            adapter = lista
            isVisible = true
        }
    }

    fun deleteUserNickname() {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPreferences.edit()) {
            putString(getString(R.string.github_nickname), "")
            apply()
        }
    }

}




