package com.marian.licenta.ui.activities.main

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.marian.licenta.R
import com.marian.licenta.base.mvp.BaseMvpActivity
import com.marian.licenta.ui.fragments.CameraFragment

class MainActivity : BaseMvpActivity<MainContract.Presenter>(), MainContract.View, View.OnClickListener {

    private lateinit var ivMenu : ImageView
    private lateinit var dlDrawer : DrawerLayout
    private lateinit var flContainer : FrameLayout

    private var cameraFragment: CameraFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @LayoutRes
    override internal fun bindLayout(): Int {
        return R.layout.activity_main
    }

    @IdRes
    override internal fun bindLoadingScreen(): Int {
        return R.id.rlLoading;
    }

    override internal fun initViews() {
        ivMenu = findViewById(R.id.ivMenu)
        ivMenu.setOnClickListener(this)
        dlDrawer = findViewById(R.id.dlDrawer)
        flContainer = findViewById(R.id.flContainer)

        //drawer.ul nu se deschidea peste surfaceView
        dlDrawer.addDrawerListener(object : DrawerLayout.DrawerListener {

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                //Called when a drawer's position changes.
                dlDrawer.bringChildToFront(drawerView);
                dlDrawer.requestLayout();
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })

        getMyFragmentManager().loadFragment(getFragment(CameraFragment.TAG), CameraFragment.TAG)
    }

    fun onImageDragStarted() {
        ivMenu.visibility = View.GONE
    }

    fun onImageDragFinished() {
        ivMenu.visibility = View.VISIBLE
    }

    private fun getFragment(tag: String): Fragment? {

        when (tag) {
            CameraFragment.TAG -> {
                if (cameraFragment == null) {
                    cameraFragment = CameraFragment.newInstance()
                }
                return cameraFragment
            }

        }
        return null
    }

    override internal fun bindPresenter(): MainContract.Presenter {
        return MainPresenter(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ivMenu -> openMenu()
        }
    }

    fun openMenu() {
        dlDrawer.openDrawer(GravityCompat.START)

    }
}