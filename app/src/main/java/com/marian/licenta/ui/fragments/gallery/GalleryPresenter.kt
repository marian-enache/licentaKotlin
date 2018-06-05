package com.marian.licenta.ui.fragments.gallery

import com.marian.licenta.adapters.ScenesAdapter
import com.marian.licenta.base.mvp.BaseMvpContract
import com.marian.licenta.base.mvp.BaseMvpPresenter
import com.marian.licenta.room.AppDatabase
import com.marian.licenta.room.DbWorkerThread
import com.marian.licenta.room.models.Scene
import org.jetbrains.anko.runOnUiThread


class GalleryPresenter(view: GalleryContract.View) : BaseMvpPresenter<GalleryContract.View, GalleryModel>(view),
        GalleryContract.Presenter,
        BaseMvpContract.MvpAdapterPresenter<ScenesAdapter.ViewHolder>{


    private  var dbWorkerThread: DbWorkerThread? = null

    private var appDatabase: AppDatabase? = null

    override fun bindModel(): GalleryModel {
        return GalleryModel()
    }

    override fun onBind() {
        appDatabase = AppDatabase.getInstance(getView().getContext())

        dbWorkerThread = DbWorkerThread("dbWorkerThread")
        dbWorkerThread?.start()
        adapterItemsListInit()
    }

    override fun adapterItemsListInit() {
        var scenes : ArrayList<Scene> = ArrayList()

        val task = Runnable {
            var sceneViews = appDatabase?.sceneDao()?.getSceneViews() ?: ArrayList()
            for (sceneView in sceneViews) {
                var scene = sceneView.scene
                scene?.layers = sceneView.layers
                scene?.let {
                    if (!getModel().getscenesList().contains(scene)) {
                        scenes.add(scene)
                    }
                }
            }
            getModel().setScenesList(scenes)

            getView().getContext().runOnUiThread {
                getView().notifyAdapter()
            }

        }
        dbWorkerThread?.postTask(task)
    }

    override fun onBindAdapterItems(position: Int, holder: ScenesAdapter.ViewHolder) {
        var scene : Scene = getModel().getscenesList().get(position)

        holder.setFields(scene)    }

    override fun getItemCount(): Int {
        return getModel().getscenesList().size
    }

    override fun onDetachView() {
        dbWorkerThread?.quit()
        AppDatabase.destroyInstance()
    }

}