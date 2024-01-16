package com.module.config.views.dialogs

import android.app.Activity
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import com.module.config.R
import com.module.config.app.AppConstants
import com.module.config.databinding.DialogRateAppBinding
import com.module.config.views.bases.BaseDialog
import com.orhanobut.hawk.Hawk
import com.willy.ratingbar.BaseRatingBar

class RateAppDialog(private val activity: Activity) : BaseDialog<DialogRateAppBinding>(activity) {

    override fun getLayoutDialog() = R.layout.dialog_rate_app

    override fun initViews() {
        super.initViews()
        mBinding.simpleRatingBar.rating = 5f

        mBinding.simpleRatingBar.setOnRatingChangeListener(object :
            BaseRatingBar.OnRatingChangeListener {
            override fun onRatingChange(
                ratingBar: BaseRatingBar?,
                rating: Float,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    when (rating) {
                        1f -> {
                            mBinding.ivStart.setImageResource(R.drawable.img_star_1)
                        }

                        2f -> {
                            mBinding.ivStart.setImageResource(R.drawable.img_star_2)
                        }

                        3f -> {
                            mBinding.ivStart.setImageResource(R.drawable.img_star_3)
                        }

                        4f -> {
                            mBinding.ivStart.setImageResource(R.drawable.img_star_4)
                        }

                        5f -> {
                            mBinding.ivStart.setImageResource(R.drawable.img_star_5)
                        }
                    }
                }
            }
        })
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.tvRateNow.setOnClickListener {
            dismiss()
            val star = mBinding.simpleRatingBar.rating
            if (star < 4) {
                val dialogFeedback = FeedbackDialog(context)
                dialogFeedback.show()
            } else {
                rateInApp()
            }
        }

        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    private fun rateInApp() {
        val manager: ReviewManager = ReviewManagerFactory.create(activity)
        val request: Task<ReviewInfo> = manager.requestReviewFlow() as Task<ReviewInfo>
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo: ReviewInfo = task.result
                val flow: Task<Void> = manager.launchReviewFlow(activity, reviewInfo) as Task<Void>
                flow.addOnSuccessListener {
                    Hawk.put(AppConstants.KEY_RATING_APP, true)
                }
            } else {
                Hawk.put(AppConstants.KEY_RATING_APP, false)
            }
        }
    }
}