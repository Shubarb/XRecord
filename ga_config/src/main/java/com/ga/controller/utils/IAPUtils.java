package com.ga.controller.utils;

import android.app.Activity;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.anjlab.android.iab.v3.SkuDetails;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;

public class IAPUtils {

    private static IAPUtils INSTANCE;

    public static IAPUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new IAPUtils();
        }
        return INSTANCE;
    }

    private BillingProcessor billingProcessor;

    public void initPurchase(Activity activity, onResult mOnResult) {
        billingProcessor = new BillingProcessor(activity, PurchaseUtils.LICENSE_KEY, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, @org.jetbrains.annotations.Nullable PurchaseInfo details) {
                PurchaseUtils.setNoAds(activity, true);
                if (mOnResult != null) {
                    mOnResult.onSuccess();
                }
            }

            @Override
            public void onPurchaseHistoryRestored() {
                if (mOnResult != null) {
                    mOnResult.onHistory();
                }
            }

            @Override
            public void onBillingError(int errorCode, @org.jetbrains.annotations.Nullable Throwable error) {
                if (mOnResult != null) {
                    if (error != null) {
                        mOnResult.onFail(error.getMessage());
                    }
                }
            }

            @Override
            public void onBillingInitialized() {
                if (billingProcessor != null) {
                    initPrice(mOnResult);
                    if (billingProcessor.isPurchased(PurchaseUtils.getIdOneTime())) {
                        PurchaseUtils.setNoAds(activity, true);
                    } else {
                        PurchaseUtils.setNoAds(activity, false);
                    }
                }
            }
        });
        billingProcessor.initialize();
    }

    private void initPrice(onResult mOnResult) {
        try {
            NumberFormat numberFormat = NumberFormat.getInstance();
            DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
            decimalFormatSymbols.setDecimalSeparator('.');
            decimalFormatSymbols.setGroupingSeparator(' ');
            decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

            billingProcessor.getSubscriptionListingDetailsAsync(PurchaseUtils.getIdMonth(), new BillingProcessor.ISkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@org.jetbrains.annotations.Nullable List<SkuDetails> products) {
                    if (products != null && products.size() > 0) {
                        for (int i = 0; i < products.size(); i++) {
                            if (mOnResult != null) {
                                mOnResult.onPriceMonth(numberFormat.format(products.get(i).priceValue).concat(products.get(i).currency));
                            }
                        }
                    }
                }

                @Override
                public void onSkuDetailsError(String error) {
                    if (mOnResult != null) {
                        mOnResult.onPriceMonth("");
                    }
                }
            });

            billingProcessor.getSubscriptionListingDetailsAsync(PurchaseUtils.getIdTrialYear(), new BillingProcessor.ISkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@org.jetbrains.annotations.Nullable List<SkuDetails> products) {
                    if (products != null && products.size() > 0) {
                        for (int i = 0; i < products.size(); i++) {
                            if (mOnResult != null) {
                                mOnResult.onPriceYearTrial(numberFormat.format(products.get(i).priceValue).concat(products.get(i).currency));
                            }
                        }
                    }
                }

                @Override
                public void onSkuDetailsError(String error) {
                    if (mOnResult != null) {
                        mOnResult.onPriceYearTrial("");
                    }
                }
            });

            billingProcessor.getPurchaseListingDetailsAsync(PurchaseUtils.getIdOneTime(), new BillingProcessor.ISkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@org.jetbrains.annotations.Nullable List<SkuDetails> products) {
                    if (products != null && products.size() > 0) {
                        for (int i = 0; i < products.size(); i++) {
                            if (mOnResult != null) {
                                mOnResult.onPriceOneTime(numberFormat.format(products.get(i).priceValue).concat(products.get(i).currency));
                            }
                        }
                    }
                }

                @Override
                public void onSkuDetailsError(String error) {
                    if (mOnResult != null) {
                        mOnResult.onPriceOneTime("");
                    }
                }
            });

            billingProcessor.getPurchaseListingDetailsAsync(PurchaseUtils.getIdYear(), new BillingProcessor.ISkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@org.jetbrains.annotations.Nullable List<SkuDetails> products) {
                    if (products != null && products.size() > 0) {
                        for (int i = 0; i < products.size(); i++) {
                            if (mOnResult != null) {
                                mOnResult.onPriceYear(numberFormat.format(products.get(i).priceValue).concat(products.get(i).currency));
                            }
                        }
                    }
                }

                @Override
                public void onSkuDetailsError(String error) {
                    if (mOnResult != null) {
                        mOnResult.onPriceYear("");
                    }
                }
            });

            billingProcessor.getPurchaseListingDetailsAsync(PurchaseUtils.getIdRemoveAds(), new BillingProcessor.ISkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@org.jetbrains.annotations.Nullable List<SkuDetails> products) {
                    if (products != null && products.size() > 0) {
                        for (int i = 0; i < products.size(); i++) {
                            if (mOnResult != null) {
                                mOnResult.onRemoveAds(numberFormat.format(products.get(i).priceValue).concat(products.get(i).currency));
                            }
                        }
                    }
                }

                @Override
                public void onSkuDetailsError(String error) {
                    if (mOnResult != null) {
                        mOnResult.onRemoveAds("");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Show Process call Purchase
    public void callPurchase(Activity activity, String id) {
        if (billingProcessor != null) {
            billingProcessor.purchase(activity, id);
        }
    }

    // Show Process call Sub
    public void callSub(Activity activity, String id) {
        if (billingProcessor != null) {
            billingProcessor.subscribe(activity, id);
        }
    }

    // Call ở hàm onDestroy của activity gọi ra
    public void onDestroyIAP() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
    }

    public interface onResult {
        void onSuccess();

        void onHistory();

        void onFail(String error);

        void onPriceMonth(String price);

        void onPriceYear(String price);

        void onPriceYearTrial(String price);

        void onPriceOneTime(String price);

        void onRemoveAds(String price);
    }
}
