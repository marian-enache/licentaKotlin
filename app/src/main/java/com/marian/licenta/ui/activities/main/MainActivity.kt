package com.marian.licenta.ui.activities.main

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.marian.licenta.R
import com.marian.licenta.base.mvp.BaseMvpActivity
import com.marian.licenta.ui.fragments.camera.CameraFragment
import com.marian.licenta.ui.fragments.gallery.GalleryFragment

class MainActivity : BaseMvpActivity<MainContract.Presenter>(), MainContract.View, View.OnClickListener {

    private lateinit var ivMenu: ImageView
    private lateinit var dlDrawer: DrawerLayout
    private lateinit var flContainer: FrameLayout

    private lateinit var rlBuild: RelativeLayout
    private lateinit var rlGallery: RelativeLayout

    private var cameraFragment: CameraFragment? = null
    private var galleryFragment: GalleryFragment? = null

    private var shouldRefreshGallery: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @LayoutRes
    override internal fun bindLayout(): Int {
        return R.layout.activity_main
    }

    @IdRes
    override internal fun bindLoadingScreen(): Int {
        return R.id.rlLoading
    }

    override internal fun initViews() {
        ivMenu = findViewById(R.id.ivMenu)
        ivMenu.setOnClickListener(this)
        dlDrawer = findViewById(R.id.dlDrawer)
        flContainer = findViewById(R.id.flContainer)

        rlBuild = findViewById(R.id.rlBuild)
        rlBuild.setOnClickListener(this)
        rlGallery = findViewById(R.id.rlGallery)
        rlGallery.setOnClickListener(this)

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

    fun hideMenuButton() {
        ivMenu.visibility = View.GONE
    }

    fun showMenuButton() {
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
            GalleryFragment.TAG -> {
                if (galleryFragment == null) {
                    galleryFragment = GalleryFragment.newInstance()
                }
                return galleryFragment
            }

        }
        return null
    }

    override internal fun bindPresenter(): MainContract.Presenter {
        return MainPresenter(this)
    }

    override fun onClick(view: View?) {
        closeMenu()
        when (view?.id) {
            R.id.ivMenu -> openMenu()
            R.id.rlBuild -> {
                if (supportFragmentManager.findFragmentByTag(CameraFragment.TAG) != null &&
                        supportFragmentManager.findFragmentByTag(CameraFragment.TAG).isVisible) {
                    return
                }
                changeMenuItemColor(rlBuild)

                getMyFragmentManager().hideFragment(GalleryFragment.TAG) // will consume device cpu but improve speed(compared to destroyFragment) when swithcing from fragments
                getMyFragmentManager().loadFragment(getFragment(CameraFragment.TAG), CameraFragment.TAG)

            }
            R.id.rlGallery -> {
                if (supportFragmentManager.findFragmentByTag(GalleryFragment.TAG) != null &&
                        supportFragmentManager.findFragmentByTag(GalleryFragment.TAG).isVisible) {
                    return
                }
                changeMenuItemColor(rlGallery)

                getMyFragmentManager().hideFragment(CameraFragment.TAG) // will consume device cpu but improve speed(compared to destroyFragment) when swithcing from fragments
                getMyFragmentManager().loadFragment(getFragment(GalleryFragment.TAG), GalleryFragment.TAG)
            }
        }
    }

    private fun changeMenuItemColor(viewItem: View) {
        var parentLayout = rlBuild.parent as LinearLayout
        for (i in 0..parentLayout.childCount) {
            if (parentLayout.getChildAt(i) is RelativeLayout) {
                parentLayout.getChildAt(i)?.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.white_20_alpha))
            }
        }
        viewItem.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.gray_light_20_alpha))

    }

    fun openMenu() {
        dlDrawer.openDrawer(GravityCompat.START)
    }

    fun closeMenu() {
        dlDrawer.closeDrawer(GravityCompat.START)
    }

    fun shouldRefreshGallery() = shouldRefreshGallery

    fun setShouldRefreshGallery(shouldRefreshGallery: Boolean) {
        this.shouldRefreshGallery = shouldRefreshGallery
    }


}