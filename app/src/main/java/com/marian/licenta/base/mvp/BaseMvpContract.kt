package com.marian.licenta.base.mvp

import android.content.Context

/**
 * Created by Marian on 20.03.2018.
 */
interface BaseMvpContract {

    interface MvpView {
        fun showProgress()
        fun hideProgress()
        fun getContext(): Context
    }

    interface MvpPresenter {
        fun onBind()
        fun onDetachView()
    }

    interface MvpAdapterView

    interface MvpAdapterPresenter<in HOLDER : BaseMvpAdapter.BaseViewHolder> {
        fun onBindAdapterItems(position : Int, holder : HOLDER)
        fun getItemCount() : Int
    }
}