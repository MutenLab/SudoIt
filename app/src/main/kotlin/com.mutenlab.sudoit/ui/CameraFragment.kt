package com.mutenlab.sudoit.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import com.mutenlab.sudoit.R
import com.mutenlab.sudoit.common.PuzzleScanner
import kotlinx.android.synthetic.main.container_camera_mask.*
import kotlinx.android.synthetic.main.fragment_camera.*
import java.util.*
import kotlin.math.roundToInt

class CameraFragment : Fragment() {

    companion object {
        private val TAG = CameraFragment::class.qualifiedName
        private const val MAX_PREVIEW_WIDTH = 1280
        private const val MAX_PREVIEW_HEIGHT = 720
        @JvmStatic
        fun newInstance() = CameraFragment()
    }

    private lateinit var captureSession: CameraCaptureSession

    private lateinit var captureRequestBuilder: CaptureRequest.Builder

    private lateinit var cameraDevice: CameraDevice

    private lateinit var scannerAnimation: Animation

    private lateinit var backgroundThread: HandlerThread

    private lateinit var backgroundHandler: Handler

    private val deviceStateCallback = object: CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice?) {
            Log.d(TAG, "camera device opened")
            if (camera != null){
                cameraDevice = camera
                previewSession()
            }

        }

        override fun onDisconnected(camera: CameraDevice?) {
            Log.d(TAG, "camera device disconnected")
            camera?.close()
        }

        override fun onError(camera: CameraDevice?, error: Int) {
            Log.d(TAG, "camera device error")
            this@CameraFragment.activity?.finish()
        }
    }

    private val cameraManager by lazy {
        activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private val surfaceListener = object: TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) = Unit

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?) = true

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            Log.d(TAG, "textureSurface width: $width height: $height")
            connectCamera()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        takePhotoButton.setOnClickListener({
            ImageProcessingTask().execute()
        })
    }

    private fun startScannerBarAnimation() {
        val distance = scanner.layoutParams.height.toFloat() - scannerBar.layoutParams.height.toFloat()
        scannerAnimation = TranslateAnimation(0f, 0f, 0f, distance)
        scannerAnimation.duration = 2000
        scannerAnimation.repeatMode = Animation.REVERSE
        scannerAnimation.repeatCount = Animation.INFINITE
        scannerAnimation.fillAfter = true
        scannerAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                scannerBar.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        scannerBar.startAnimation(scannerAnimation)
    }

    private fun processSudokuImage(bitmap: Bitmap, context: Context?): Array<Array<Int>>? {
        try {
            val puzzleScanner = PuzzleScanner(bitmap, context)
            return puzzleScanner.puzzle
        } catch (ex: Exception) {
            Log.e(null, "Error extracting puzzle", ex)
        }

        return null
    }

    private fun startSolverActivity(unsolved: Array<Array<Int>>) {
        val bundle = Bundle()
        bundle.putSerializable(SolverActivity.PUZZLE_KEY, unsolved)

        val intent = Intent(activity, SolverActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtras(bundle)
        startActivity(intent)
        activity?.finish()
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (previewTextureView.isAvailable) {
            connectCamera()
        } else {
            previewTextureView.surfaceTextureListener = surfaceListener
        }
    }

    override fun onPause() {
        closeCamera()
        stopBackgroundThread()
        super.onPause()
    }

    private fun previewSession() {
        val surface = prepareSurface()

        startScannerBarAnimation()

        captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequestBuilder.addTarget(surface)

        cameraDevice.createCaptureSession(Arrays.asList(surface),
                object: CameraCaptureSession.StateCallback(){
                    override fun onConfigureFailed(session: CameraCaptureSession?) {
                        Log.e(TAG, "creating capture session failded!")
                    }

                    override fun onConfigured(session: CameraCaptureSession?) {
                        if (session != null) {
                            captureSession = session
                            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                            captureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null)
                        }
                    }

                }, null)
    }

    private fun prepareSurface() : Surface {
        val surfaceTexture = previewTextureView.surfaceTexture
        val display = activity?.windowManager?.defaultDisplay
        val point = Point()
        display?.getSize(point)
        val width = point.x * 0.75
        scanner.layoutParams.width = width.roundToInt()
        scanner.layoutParams.height = width.roundToInt()
        surfaceTexture.setDefaultBufferSize(MAX_PREVIEW_WIDTH, MAX_PREVIEW_HEIGHT)
        return Surface(surfaceTexture)
    }

    private fun closeCamera() {
        if (this::captureSession.isInitialized)
            captureSession.close()
        if (this::cameraDevice.isInitialized)
            cameraDevice.close()
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("Camara2 Kotlin").also { it.start() }
        backgroundHandler = Handler(backgroundThread.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread.quitSafely()
        try {
            backgroundThread.join()
        } catch (e: InterruptedException) {
            Log.e(TAG, e.toString())
        }
    }

    private fun <T> cameraCharacteristics(cameraId: String, key: CameraCharacteristics.Key<T>) :T {
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        return when (key) {
            CameraCharacteristics.LENS_FACING -> characteristics.get(key)
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP -> characteristics.get(key)
            else -> throw  IllegalArgumentException("Key not recognized")
        }
    }

    private fun cameraId(lens: Int) : String {
        var deviceId = listOf<String>()
        try {
            val cameraIdList = cameraManager.cameraIdList
            deviceId = cameraIdList.filter { lens == cameraCharacteristics(it, CameraCharacteristics.LENS_FACING) }
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }
        return deviceId[0]
    }

    @SuppressLint("MissingPermission")
    private fun connectCamera() {
        val deviceId = cameraId(CameraCharacteristics.LENS_FACING_BACK)
        Log.d(TAG, "deviceId: $deviceId")
        try {
            cameraManager.openCamera(deviceId, deviceStateCallback, backgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        } catch (e: InterruptedException) {
            Log.e(TAG, "Open camera device interrupted while opened")
        }
    }

    inner class ImageProcessingTask: AsyncTask<Array<Array<Int>>?, Array<Array<Int>>?, Array<Array<Int>>?>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg p0: Array<Array<Int>>?): Array<Array<Int>>? {
            val bitmapTextureView = previewTextureView.bitmap
            val intArray = IntArray(2)
            scanner.getLocationOnScreen(intArray)
            val scannerLayoutParams = scanner.layoutParams

            val display = activity?.windowManager?.defaultDisplay
            val point = Point()
            display?.getSize(point)

            val cropBitmap = Bitmap.createBitmap(bitmapTextureView, intArray[0], intArray[1],
                    scannerLayoutParams.width, scannerLayoutParams.height)

            return processSudokuImage(cropBitmap, context)
        }

        override fun onPostExecute(unsolved: Array<Array<Int>>?) {
            super.onPostExecute(unsolved)

            if (unsolved != null) {
                startSolverActivity(unsolved)
            } else {
                progressBar.visibility = View.GONE
                Toast.makeText(context, R.string.board_not_found, Toast.LENGTH_LONG).show()
            }
        }
    }
}