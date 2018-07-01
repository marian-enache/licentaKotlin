package com.marian.licenta.ui.activities.scene

import com.marian.licenta.base.mvp.BaseMvpPresenter
import com.marian.licenta.room.AppDatabase
import com.marian.licenta.room.DbWorkerThread
import com.marian.licenta.room.models.Scene


class ScenePresenter(view: SceneContract.View) : BaseMvpPresenter<SceneContract.View, SceneModel>(view), SceneContract.Presenter {

    private var dbWorkerThread: DbWorkerThread? = null

    private var appDatabase: AppDatabase? = null


    override fun bindModel(): SceneModel {
        return SceneModel()
    }

    override fun onBind() {
        appDatabase = AppDatabase.getInstance(getView().getContext())

        dbWorkerThread = DbWorkerThread("dbWorkerThread")
        dbWorkerThread?.start()
    }

    override fun updateScene(scene: Scene) {
        val task = Runnable {
            appDatabase?.sceneDao()?.update(scene)
        }
        dbWorkerThread?.postTask(task)
    }

    override fun onDetachView() {
        dbWorkerThread?.quit()
        AppDatabase.destroyInstance()
    }

}
