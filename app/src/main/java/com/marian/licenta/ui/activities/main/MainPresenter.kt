package com.marian.licenta.ui.activities.main

import com.marian.licenta.base.mvp.BaseMvpPresenter


class MainPresenter(view: MainContract.View) : BaseMvpPresenter<MainContract.View, MainModel>(view), MainContract.Presenter {

    override fun bindModel(): MainModel {
        return MainModel()
    }

    override fun onBind() {
        // TODO
    }

    override fun onDetachView() {
        // TODO
    }

}