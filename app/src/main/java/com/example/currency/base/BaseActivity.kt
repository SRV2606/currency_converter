package com.example.currency.base

import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import javax.inject.Singleton


@Singleton
abstract class BaseActivity<B : ViewDataBinding>(@LayoutRes private val layoutId: Int) :
    AppCompatActivity() {

    private lateinit var _binding: B

    protected val binding: B
        get() = _binding

    protected val activity by lazy { this }


    abstract fun readArguments(extras: Intent)
    abstract fun setupUi()
    abstract fun observeData()
    abstract fun setListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView<B>(this, layoutId)
        _binding.lifecycleOwner = this
        readArguments(intent)
        observeData()
        setListener()
        setupUi()
    }

}