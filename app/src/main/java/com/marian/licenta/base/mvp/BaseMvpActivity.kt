package com.marian.licenta.base.mvp

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RelativeLayout
import com.marian.licenta.managers.MyFragmentManager

/**
 * Created by Marian on 20.03.2018.
 */
abstract class BaseMvpActivity<PRESENTER : BaseMvpContract.MvpPresenter> : AppCompatActivity(), BaseMvpContract.MvpView {

    private lateinit var presenter : PRESENTER

    private lateinit var rlLoading : RelativeLayout

    private lateinit var myFragmentManager: MyFragmentManager


    override  fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindLayout())
        myFragmentManager = MyFragmentManager(supportFragmentManager)

        rlLoading = findViewById(bindLoadingScreen())

        bindPresenter()?.let {
            presenter = bindPresenter()
            presenter.onBind()
        }

        initViews()
    }

    internal abstract fun initViews()

    @LayoutRes
    internal abstract fun bindLayout():  Int

    @IdRes
    internal abstract fun bindLoadingScreen() : Int

    internal abstract fun bindPresenter() : PRESENTER

    protected fun getPresenter() : PRESENTER {
        return presenter
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDetachView()
    }

    override fun showProgress() {
        rlLoading?.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        rlLoading?.visibility = View.GONE
    }

    public fun getMyFragmentManager() : MyFragmentManager {
        return myFragmentManager
    }
}