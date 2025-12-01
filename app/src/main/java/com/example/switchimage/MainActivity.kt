package com.example.switchimage

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    // Declare all views
    private lateinit var imageView: ImageView
    private lateinit var btnBack: Button
    private lateinit var btnNext: Button
    private lateinit var tvImageCounter: TextView
    private lateinit var tvImageTitle: TextView

    // List of images and titles - has 3 images
    private val imageList = listOf(
        ImageItem(R.drawable.natural1, "Waterfall"),
        ImageItem(R.drawable.bridge, "Bridge"),
        ImageItem(R.drawable.natural2, "Snowy Mountains")
    )

    // Current image position
    private var currentPosition = 0
    private var backPressTime: Long = 0

    // Data class to manage image and title
    data class ImageItem(val imageRes: Int, val title: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupClickListeners()
        setupBackPressed()
        updateUI()
    }

    private fun initViews() {
        imageView = findViewById(R.id.imageView)
        btnBack = findViewById(R.id.btnBack)
        btnNext = findViewById(R.id.btnNext)
        tvImageCounter = findViewById(R.id.tvImageCounter)
        tvImageTitle = findViewById(R.id.tvImageTitle)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            showPreviousImage()
            animateButtonClick(btnBack)
        }

        btnNext.setOnClickListener {
            showNextImage()
            animateButtonClick(btnNext)
        }

        imageView.setOnClickListener {
            showNextImage()
        }

        // Long press on the image to return to the first image
        imageView.setOnLongClickListener {
            resetToFirstImage()
            true
        }
    }

    /**
     * Handle Back button with exit confirmation
     */
    private fun setupBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            handleBackPressWithConfirmation()
        }
    }

    /**
     * Handle back press with double tap to exit
     */
    private fun handleBackPressWithConfirmation() {
        if (currentPosition != 0) {
            showPreviousImage()
        } else {
            if (System.currentTimeMillis() - backPressTime > 2000) {
                showToast("Press Back again to exit")
                backPressTime = System.currentTimeMillis()
            } else {
                finish()
            }
        }
    }

    /**
     * Show previous image
     */
    private fun showPreviousImage() {
        if (imageList.isNotEmpty()) {
            currentPosition = if (currentPosition > 0) currentPosition - 1 else imageList.size - 1
            updateUI()
            animateImageTransition(false)
        }
    }

    /**
     * Show next image
     */
    private fun showNextImage() {
        if (imageList.isNotEmpty()) {
            currentPosition = if (currentPosition < imageList.size - 1) currentPosition + 1 else 0
            updateUI()
            animateImageTransition(true)
        }
    }

    /**
     * Reset to the first image
     */
    private fun resetToFirstImage() {
        if (currentPosition != 0) {
            currentPosition = 0
            updateUI()
            animateImageTransition(false)
            showToast("Returned to the first image")
        } else {
            showToast("Already at the first image")
        }
    }

    /**
     * Update UI
     */
    private fun updateUI() {
        val currentImage = imageList[currentPosition]

        imageView.setImageResource(currentImage.imageRes)
        tvImageCounter.text = "${currentPosition + 1}/${imageList.size}"
        tvImageTitle.text = currentImage.title

        updateButtonStates()
    }

    /**
     * Update button states
     */
    private fun updateButtonStates() {
        // Enable buttons only if there is more than 1 image
        val hasMultipleImages = imageList.size > 1
        btnBack.isEnabled = hasMultipleImages
        btnNext.isEnabled = hasMultipleImages

        btnBack.alpha = if (hasMultipleImages) 1.0f else 0.5f
        btnNext.alpha = if (hasMultipleImages) 1.0f else 0.5f
    }

    /**
     * Image transition animation
     */
    private fun animateImageTransition(isNext: Boolean) {
        val slideOut = AnimationUtils.loadAnimation(this,
            if (isNext) R.anim.slide_out_left else R.anim.slide_out_right
        )
        val slideIn = AnimationUtils.loadAnimation(this,
            if (isNext) R.anim.slide_in_right else R.anim.slide_in_left
        )

        imageView.startAnimation(slideOut)

        slideOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}

            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                imageView.startAnimation(slideIn)
            }

            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })
    }

    /**
     * Button click animation
     */
    private fun animateButtonClick(button: Button) {
        button.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .withEndAction {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    /**
     * Show toast message
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Get current image
     */
    fun getCurrentImage(): ImageItem {
        return imageList[currentPosition]
    }

    /**
     * Go to a specific image
     */
    fun goToImage(position: Int) {
        if (position in imageList.indices) {
            currentPosition = position
            updateUI()
            animateImageTransition(true)
        }
    }
}
