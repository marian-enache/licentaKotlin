package com.marian.licenta.ui.activities.scene

import com.marian.licenta.base.mvp.BaseMvpContract;
import com.marian.licenta.room.models.Scene

interface SceneContract {

    interface View : BaseMvpContract.MvpView {

    }

    interface Presenter : BaseMvpContract.MvpPresenter {
        fun updateScene(scene: Scene)

    }
}