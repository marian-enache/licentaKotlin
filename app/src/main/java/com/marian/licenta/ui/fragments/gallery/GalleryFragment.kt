package com.marian.licenta.ui.fragments.gallery

import android.support.annotation.LayoutRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.marian.licenta.R
import com.marian.licenta.adapters.ScenesAdapter
import com.marian.licenta.base.mvp.BaseMvpFragment
import com.marian.licenta.ui.activities.main.MainActivity

class GalleryFragment : BaseMvpFragment<GalleryContract.Presenter>(), GalleryContract.View {
    companion object {

        open val TAG: String = GalleryFragment::class.java.simpleName
        open fun newInstance(): GalleryFragment = GalleryFragment()

    }
    private lateinit var rvScenes: RecyclerView

    private lateinit var scenesAdapter: ScenesAdapter
    @LayoutRes
    override internal fun bindLayout(): Int {
        return R.layout.fragment_gallery
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        activity?.let {
            if (!hidden && (activity as MainActivity).shouldRefreshGallery()) {
                getPresenter().adapterItemsListInit()
                (activity as MainActivity).setShouldRefreshGallery(false)
            }
        }
    }

    override internal fun initViews(view: View?) {
        rvScenes = view?.findViewById(R.id.rvScenes)!!

        rvScenes.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        scenesAdapter = ScenesAdapter(getPresenter() as GalleryPresenter)
        scenesAdapter.callback = scenesAdapterCallback
        rvScenes.adapter = scenesAdapter
    }

    internal var scenesAdapterCallback = object : ScenesAdapter.Callback {
        override fun onClick() {

        }
    }

    override internal fun bindPresenter(): GalleryContract.Presenter {
        return GalleryPresenter(this)
    }

    override fun notifyAdapter() {
        scenesAdapter.notifyDataSetChanged()
    }

}
