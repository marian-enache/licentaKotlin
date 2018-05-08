package com.marian.licenta.managers

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.marian.licenta.R

/**
 * Created by Marian on 24.03.2018.
 */


class MyFragmentManager(private val fragmentManager: FragmentManager) {


    fun loadFragment(fragment: Fragment?, tag: String) {
        fragment?.let {
            if (fragmentManager.findFragmentByTag(fragment.tag) == null) {
                fragmentManager.beginTransaction()
                        .add(R.id.flContainer, fragment, tag)
                        .commitNow()
            } else {
                fragmentManager.beginTransaction()
                        .show(fragment)
                        .commitNow()
            }
        }
    }

    fun hideFragment(tag: String?) {
        tag?.let {
            val frag = fragmentManager.findFragmentByTag(tag)
            frag?.let {
                    fragmentManager.beginTransaction()
                            .hide(frag)
                            .commitNow()
            }
        }
    }

    fun destroyFragment(tag: String?) {
        tag?.let {
            val frag = fragmentManager.findFragmentByTag(tag)
            frag?.let {
                fragmentManager.beginTransaction()
                        .remove(frag)
                        .commitNow()
            }
        }
    }
}
