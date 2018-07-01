package com.marian.licenta.ui.fragments.camera

import com.marian.licenta.base.mvp.BaseMvpModel;


class CameraModel : BaseMvpModel() {

    private  var imagesList : ArrayList<String> = ArrayList()

    fun getImagesList() : ArrayList<String> {
        return imagesList
    }

    fun setImagesList(imagesList : ArrayList<String>) {
        this.imagesList = imagesList
    }

} 