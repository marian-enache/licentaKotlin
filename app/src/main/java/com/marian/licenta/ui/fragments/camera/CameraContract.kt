package com.marian.licenta.ui.fragments.camera

import com.marian.licenta.base.mvp.BaseMvpContract
import com.marian.licenta.room.models.Layer

interface CameraContract {

    interface View : BaseMvpContract.MvpView {
        fun afterPictureTaken()

    }

    interface Presenter : BaseMvpContract.MvpPresenter {
        fun storeScene(name: String, layersCount: Int, layersList: MutableList<Layer>)
        fun initImageSavingThread()
        fun setImageData(imageData: ByteArray)
    }
}