package com.marian.licenta.ui.fragments

import com.marian.licenta.R
import com.marian.licenta.adapters.images.ImagesAdapter
import com.marian.licenta.base.mvp.BaseMvpContract
import com.marian.licenta.base.mvp.BaseMvpPresenter


class CameraPresenter(view: CameraContract.View) : BaseMvpPresenter<CameraContract.View, CameraModel>(view), CameraContract.Presenter, BaseMvpContract.MvpAdapterPresenter<ImagesAdapter.ViewHolder> {


    override fun onBindAdapterItems(position: Int, holder: ImagesAdapter.ViewHolder) {
        var image : Int = getModel().getImagesList().get(position)

        holder.setFields(image)
    }

    override fun getItemCount(): Int {
        return getModel().getImagesList().size
    }

    override fun bindModel(): CameraModel {
        return CameraModel()
    }

    override fun onBind() {
        adapterItemsListInit()
    }

    private fun adapterItemsListInit() {
        var images : ArrayList<Int> = ArrayList()
        images.add(R.drawable.cat)
        images.add(R.drawable.dog)
        images.add(R.drawable.girl)
        images.add(R.drawable.hot_dog)
        images.add(R.drawable.stand)
        images.add(R.drawable.husky)

        getModel().getImagesList().addAll(images)
    }

    override fun onDetachView() {
        // TODO
    }

}