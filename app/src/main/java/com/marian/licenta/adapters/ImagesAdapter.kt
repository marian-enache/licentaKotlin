package com.marian.licenta.adapters

import android.content.ClipData
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.marian.licenta.R
import com.marian.licenta.base.mvp.BaseMvpAdapter
import com.marian.licenta.ui.fragments.camera.CameraPresenter
import com.squareup.picasso.Picasso

/**
 * Created by Marian on 01.04.2018.
 */
class ImagesAdapter(presenter: CameraPresenter)
    : BaseMvpAdapter<ImagesAdapter.ViewHolder, CameraPresenter>(presenter) {


    lateinit var callback : Callback

    interface Callback {
        fun onDragStarted()
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
        return R.layout.cell_image
    }


    class ViewHolder : BaseMvpAdapter.BaseViewHolder {

        lateinit var callback : Callback

        var ivImage: ImageView

        constructor(itemView: View) : super(itemView) {
            ivImage = itemView.findViewById(R.id.ivImage)
        }

        fun setFields(imagePath: String) {

            Picasso.get()
                    .load(imagePath)
                    .into(ivImage)


            ivImage.setOnLongClickListener {

                callback?.let {
                    callback.onDragStarted()
                }

                val item = ClipData.Item(Uri.parse(imagePath))

                val data = ClipData.newPlainText("", "")
                data.addItem(item)
                val shadowBuilder = View.DragShadowBuilder(ivImage)
                ivImage.startDrag(data, shadowBuilder, ivImage, 0)
                true
            }

        }


    }




}