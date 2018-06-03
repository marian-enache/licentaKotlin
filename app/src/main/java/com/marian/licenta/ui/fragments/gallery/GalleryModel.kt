package com.marian.licenta.ui.fragments.gallery

import com.marian.licenta.base.mvp.BaseMvpModel;
import com.marian.licenta.room.models.Scene


class GalleryModel : BaseMvpModel() {

    private var scenesList: ArrayList<Scene> = ArrayList()

    fun getscenesList() : ArrayList<Scene> {
        return scenesList
    }

    fun setScenesList(scenesList : ArrayList<Scene>) {
        this.scenesList = scenesList
    }




} 