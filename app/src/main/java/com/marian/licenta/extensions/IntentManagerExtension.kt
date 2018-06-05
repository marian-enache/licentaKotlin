package com.marian.licenta.extensions

import android.content.Context
import com.marian.licenta.room.models.Scene
import com.marian.licenta.room.pojos.SceneView
import com.marian.licenta.ui.activities.main.MainActivity
import com.marian.licenta.ui.activities.scene.SceneActivity
import com.marian.licenta.utils.Constants
import org.jetbrains.anko.intentFor

/**
 * Created by Marian on 23.03.2018.
 */

fun Context.gotToMainActivity() {
    startActivity(intentFor<MainActivity>())
}

fun Context.goToSceneActivity(scene: Scene) {
    startActivity(intentFor<SceneActivity>().putExtra(Constants.INTENT_EXTRA_SCENE, scene))

}