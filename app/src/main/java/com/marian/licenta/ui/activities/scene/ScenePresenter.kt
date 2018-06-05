package com.marian.licenta.ui.activities.scene

import com.marian.licenta.base.mvp.BaseMvpPresenter;


class ScenePresenter(view: SceneContract.View) : BaseMvpPresenter<SceneContract.View, SceneModel>(view), SceneContract.Presenter {

    override fun bindModel(): SceneModel {
        return SceneModel()
    }

    override fun onBind() {
        // TODO
    }

    override fun onDetachView() {
        // TODO
    }

}