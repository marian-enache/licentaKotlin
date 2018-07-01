package com.marian.licenta.base.mvp

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Marian on 24.03.2018.
 */

abstract class BaseMvpFragment<PRESENTER : BaseMvpContract.MvpPresenter> : Fragment(), BaseMvpContract.MvpView {

    private lateinit var presenter: PRESENTER

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        var view : View = inflater!!.inflate(bindLayout(), container, false)


        bindPresenter()?.let {
            presenter = bindPresenter()
            presenter.onBind()
        }

        initViews(view)

        return view

    }

    internal abstract fun initViews(view : View?)

    @LayoutRes
    internal abstract fun bindLayout():  Int

    internal abstract fun bindPresenter() : PRESENTER

    protected fun getPresenter() : PRESENTER {
        return presenter
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }

    override fun showProgress() {
        activity?.let {
            if (activity is BaseMvpActivity<*>) run {
                var main: BaseMvpActivity<*> = activity as BaseMvpActivity<*>
                main.showProgress()
            }
        }
    }

    override fun hideProgress() {
        activity?.let {
            if (activity is BaseMvpActivity<*>) run {
                var main: BaseMvpActivity<*> = activity as BaseMvpActivity<*>
                main.hideProgress()
            }
        }
    }

    override fun getContext(): Context {
        return super.getContext()
    }
}