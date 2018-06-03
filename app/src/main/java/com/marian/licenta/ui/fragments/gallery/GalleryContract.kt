package com.marian.licenta.ui.fragments.gallery

import com.marian.licenta.base.mvp.BaseMvpContract;

interface GalleryContract {

    interface View : BaseMvpContract.MvpView {
        fun notifyAdapter()

    }

    interface Presenter : BaseMvpContract.MvpPresenter {
        fun adapterItemsListInit()
    }
}