package com.marian.licenta.ui.activities.scene

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import com.marian.licenta.R
import com.marian.licenta.base.mvp.BaseMvpActivity;

class SceneActivity : BaseMvpActivity<SceneContract.Presenter>(), SceneContract.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @LayoutRes
    override internal fun bindLayout(): Int = R.layout.activity_scene

    @IdRes
    override internal fun bindLoadingScreen(): Int = R.id.rlLoading

    override internal fun initViews() {
        // TODO
    }

    override internal fun bindPresenter(): SceneContract.Presenter {
        return ScenePresenter(this)
    }

}