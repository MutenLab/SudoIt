package com.mutenlab.sudoit.ui

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import butterknife.ButterKnife
import com.mutenlab.sudoit.R
import kotlinx.android.synthetic.main.activity_camera.*
import org.opencv.android.OpenCVLoader



/**
 * @author Ivan Cerrate.
 */
class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        ButterKnife.bind(this)

        toolbar.title = ""
        val backArrow = resources.getDrawable(R.drawable.abc_ic_ab_back_material)
        backArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(backArrow)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        OpenCVLoader.initDebug()
        addFragment(CameraFragment.newInstance())
    }

    private fun addFragment(fragment: Fragment?) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}