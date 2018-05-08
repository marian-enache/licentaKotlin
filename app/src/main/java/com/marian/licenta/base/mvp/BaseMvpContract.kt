package com.marian.licenta.base.mvp

/**
 * Created by Marian on 20.03.2018.
 */
interface BaseMvpContract {

    interface MvpView {
        fun showProgress()
        fun hideProgress()
    }

    interface MvpPresenter {
        fun onBind()
        fun onDetachView()


    }

    interface MvpAdapterView {

    }

    interface MvpAdapterPresenter<HOLDER : BaseMvpAdapter.BaseViewHolder> {
        fun onBindAdapterItems(position : Int, holder : HOLDER)
        fun getItemCount() : Int
    }
}