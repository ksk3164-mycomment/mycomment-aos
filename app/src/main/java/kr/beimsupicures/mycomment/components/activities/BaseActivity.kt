package kr.beimsupicures.mycomment.components.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadModel()
        loadViewModel()
        loadUI()
    }

    open fun loadModel() {

    }

    open fun loadViewModel() {

    }

    open fun loadUI() {

    }

    open fun reloadUI() {

    }

    open fun fetchModel() {

    }
}