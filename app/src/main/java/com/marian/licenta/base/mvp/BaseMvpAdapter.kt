package com.marian.licenta.base.mvp

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Marian on 26.03.2018.
 */
abstract class BaseMvpAdapter<HOLDER : BaseMvpAdapter.BaseViewHolder, PRESENTER : BaseMvpContract.MvpAdapterPresenter<HOLDER>>(private var  presenter : PRESENTER) : RecyclerView.Adapter<HOLDER>(), BaseMvpContract.MvpAdapterView{

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): HOLDER {
        this.context = parent!!.context


        var view = LayoutInflater
                .from(context)
                .inflate(bindCellLayout(), parent, false)

        return initViewHolder(view)
    }

    override fun onBindViewHolder(holder: HOLDER, position: Int) {
        presenter.onBindAdapterItems(position, holder)

        setHolderFunctions(holder, position)

    }

    override fun getItemCount(): Int {
        return presenter.getItemCount()
    }

    internal abstract fun setHolderFunctions(holder: HOLDER, position: Int)

    internal abstract fun initViewHolder(view: View): HOLDER

    @NonNull
    @LayoutRes
    internal abstract fun bindCellLayout(): Int

    open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    protected fun getPresenter() : PRESENTER {
        return presenter
    }

    protected fun getContext() : Context {
        return context
    }
}