package com.example.yoringmain

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.v2.all.BuildConfig

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들

        // Kakao SDK 초기화
        KakaoSdk.init(this, com.example.yoringmain.BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}