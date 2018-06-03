package com.marian.licenta.ui.fragments.camera

import com.marian.licenta.base.mvp.BaseMvpModel;


class CameraModel : BaseMvpModel() {

    private  var imagesList : ArrayList<Int> = ArrayList()

    fun getImagesList() : ArrayList<Int> {
        return imagesList
    }

    fun setImagesList(imagesList : ArrayList<Int>) {
        this.imagesList = imagesList
    }

} 