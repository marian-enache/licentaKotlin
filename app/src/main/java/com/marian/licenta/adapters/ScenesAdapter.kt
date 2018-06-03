package com.marian.licenta.adapters

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.RelativeLayout
import com.marian.licenta.App
import com.marian.licenta.R
import com.marian.licenta.base.mvp.BaseMvpAdapter
import com.marian.licenta.room.models.Scene
import com.marian.licenta.ui.fragments.gallery.GalleryPresenter
import org.jetbrains.anko.runOnUiThread


/**
 * Created by Marian on 01.04.2018.
 */
class ScenesAdapter(presenter: GalleryPresenter) : BaseMvpAdapter<ScenesAdapter.ViewHolder, GalleryPresenter>(presenter) {


    lateinit var callback: Callback

    interface Callback {
        fun onClick()
    }

    override fun setHolderFunctions(holder: ViewHolder, position: Int) {
        callback?.let {
            holder.callback = callback
        }
    }

    override fun initViewHolder(view: View): ViewHolder {
        return ViewHolder(view)

    }

    override fun bindCellLayout(): Int {
        return R.layout.cell_scene
    }


    class ViewHolder : BaseMvpAdapter.BaseViewHolder {

        lateinit var callback: Callback

        var cvScene: CardView
        var rlScene: RelativeLayout
        var ivBackground: ImageView

        constructor(itemView: View) : super(itemView) {
            cvScene = itemView.findViewById(R.id.cvScene)
            rlScene = itemView.findViewById(R.id.rlScene)
            ivBackground = itemView.findViewById(R.id.ivBackground)
        }

        fun setFields(scene: Scene) {

            var bitmapOption: BitmapFactory.Options = BitmapFactory.Options()
            bitmapOption.inScaled = true
            bitmapOption.inSampleSize = 4
            bitmapOption.inDensity = ivBackground.width

            val backgroundBitmap = BitmapFactory.decodeFile(scene.backgroundImage, bitmapOption)
            ivBackground.setImageBitmap(backgroundBitmap)

            var sceneWidth: Int = 0
            var sceneHeight: Int = 0

            rlScene.removeAllViews()

            scene.layers?.let {
                Handler().postDelayed({
                    itemView.context.runOnUiThread {
                        for (layer in scene.layers!!) {
                            var ivLayer = ImageView(itemView.context)

                            var viewTreeObserver = ivLayer.viewTreeObserver
                            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        ivLayer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    } else {
                                        ivLayer.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                    }

                                    if(sceneWidth == 0 || sceneHeight ==0) {
                                        sceneWidth = ivBackground.measuredWidth
                                        sceneHeight = ivBackground.measuredHeight
                                    }

                                    var ivParams = RelativeLayout.LayoutParams((layer.width.toLong() * sceneWidth.toLong() / App.instance.surfaceViewWidth!!.toLong()).toInt() ,
                                            (layer.height.toLong()* sceneHeight.toLong() / App.instance.surfaceViewHeight!!.toLong()).toInt() )

                                    ivParams.leftMargin =(layer.marginLeft.toLong() * sceneWidth.toLong() / App.instance.surfaceViewWidth!!.toLong()).toInt()
                                    ivParams.topMargin = (layer.marginTop.toLong() * sceneHeight.toLong() / App.instance.surfaceViewHeight!!.toLong()).toInt()

                                    ivLayer.layoutParams = ivParams

                                    ivLayer.setImageDrawable(ContextCompat.getDrawable(itemView.context, layer.source!!.toInt()))
                                }
                            })

                            rlScene.addView(ivLayer)
                        }
                    }
                }, 500 )

            }


//            ivImage.setTag(id)
//            ivImage.setOnLongClickListener {
//
//                callback?.let {
//                    callback.onClick()
//                }
//            }

        }


    }


}