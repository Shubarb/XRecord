package com.module.config.service.service_floating.layout

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.module.config.R
import com.module.config.rx.RxBusHelper
import com.module.config.service.service_floating.base_window.BaseLayoutWindowManager
import com.module.config.utils.utils_controller.ScreenRecordHelper
import com.module.config.views.bases.BaseRecyclerAdapter.CallBackAdapter
import com.module.config.views.fragment.ColorAdapter
import com.raed.drawingview.BrushView
import com.raed.drawingview.DrawingView
import com.raed.drawingview.brushes.BrushSettings
import com.raed.drawingview.brushes.Brushes

class LayoutBrushManager(context: Context?) :
    BaseLayoutWindowManager(context!!) {
    private var drawingView: DrawingView? = null
    private var colorAdapter: ColorAdapter? = null
    private fun initParams() {
        mParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        mParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun getRootViewId()= R.layout.layout_main_brush

    override fun initLayout() {
        drawingView = rootView?.findViewById(R.id.drawview)
        val rcv: RecyclerView? = rootView?.findViewById(R.id.rcv)
        val containerColor: ConstraintLayout? = rootView?.findViewById(R.id.container_color)
        val layoutBrush: LinearLayout? = rootView?.findViewById(R.id.layout_brush)
        val imgClose: ImageView? = rootView?.findViewById(R.id.imgClose)
        val imvClose: ImageView? = rootView?.findViewById(R.id.imv_close)
        val imgCamera: ImageView? = rootView?.findViewById(R.id.imgCamera)
        val imgPaint: ImageView? = rootView?.findViewById(R.id.imgPaint)
        val imgEraser: CheckBox? = rootView?.findViewById(R.id.imgEraser)
        val imgBrush: CheckBox? = rootView?.findViewById(R.id.imgBrush)
        val mSizeSeekBar: SeekBar? = rootView?.findViewById(R.id.size_seek_bar)

        // drawview
        val brushView: BrushView? = rootView?.findViewById(R.id.brush_view)
        brushView?.setDrawingView(drawingView)
        val brushSettings: BrushSettings? = drawingView?.brushSettings
        drawingView?.setUndoAndRedoEnable(true)
        brushSettings?.color = colorTransparent
        imgEraser?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                imgBrush?.isChecked = false
                brushSettings?.selectedBrush = Brushes.ERASER
            } else {
                brushSettings?.color = colorTransparent
                brushSettings?.selectedBrush = Brushes.PEN
            }
        }
        imgBrush?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                imgEraser?.isChecked = false
                brushSettings?.selectedBrush = Brushes.PEN
                if (colorAdapter != null) {
                    colorAdapter?.itemCount?.let { brushSettings?.setColor(it) }
                }
            } else {
                brushSettings?.color = colorTransparent
                brushSettings?.selectedBrush = Brushes.PEN
            }
        }
        imgPaint?.setOnClickListener { v: View? ->
            containerColor?.visibility = View.VISIBLE
            layoutBrush?.visibility = View.GONE
        }
        imgCamera?.setOnClickListener { v: View? ->
            if (ScreenRecordHelper.STATE !== ScreenRecordHelper.State.RECORDING) {
                layoutBrush?.visibility = View.GONE
                RxBusHelper.sendClickBrushScreenShot()
            }
        }
        mSizeSeekBar?.max = 100
        mSizeSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                brushSettings?.selectedBrushSize = i / 100f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                brushView?.visibility = View.VISIBLE
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                brushView?.visibility = View.GONE
            }
        })
        colorAdapter = ColorAdapter(initColors(), context)
        brushSettings!!::setColor.let { colorAdapter!!.setCallBackAdapter(it) }
        rcv?.adapter = colorAdapter
        rcv?.visibility = View.VISIBLE
        imgClose?.setOnClickListener { v: View? -> removeLayout() }
        imvClose?.setOnClickListener { v: View? ->
            containerColor?.visibility = View.GONE
            layoutBrush?.visibility = View.VISIBLE
        }
    }

    private fun initColors(): ArrayList<Int> {
        val datas = ArrayList<Int>()
        datas.add(Color.parseColor("#ffffff"))
        datas.add(Color.parseColor("#9E9E9E"))
        datas.add(Color.parseColor("#000000"))
        datas.add(Color.parseColor("#972929"))
        datas.add(Color.parseColor("#F82C1F"))
        datas.add(Color.parseColor("#F86D1F"))
        datas.add(Color.parseColor("#F8C81F"))
        datas.add(Color.parseColor("#53CB2C"))
        datas.add(Color.parseColor("#31D2BF"))
        datas.add(Color.parseColor("#3198D2"))
        datas.add(Color.parseColor("#2158E7"))
        datas.add(Color.parseColor("#8F21E7"))
        return datas
    }

    private val colorTransparent = Color.parseColor("#00FFFFFF")

    init {
        initParams()
        addLayout()
    }
}
