package com.marian.licenta.base.mvp

/**
 * Created by Marian on 20.03.2018.
 */
open abstract class BaseMvpPresenter<VIEW : BaseMvpContract.MvpView, MODEL : BaseMvpModel>()
    : BaseMvpContract.MvpPresenter {

    private lateinit var view : VIEW
    private lateinit var model : MODEL

    constructor(view: VIEW) : this() {
        this.view = view
        this.model = bindModel()
    }

    internal abstract fun bindModel(): MODEL

    internal fun getView() : VIEW {
        return view
    }

    internal fun getModel() : MODEL {
        return model
    }

    internal fun hideProgress() {
        getView().hideProgress()
    }

    internal fun showProgress() {
        getView().showProgress()
    }

}