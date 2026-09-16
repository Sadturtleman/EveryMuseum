package com.sadturtleman.androidsampleproject.common.datastore

import javax.inject.Qualifier

/**
 * 피처별 저장 파일 한정자.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StorePreferences

/**
 * 기기 정보 저장 파일 한정자.
 *
 * 보관함과 파일을 나누는 이유는 수명이 다르기 때문이다 — 설치 ID 는 앱이 깔려 있는 동안
 * 한 번만 만들어져 끝까지 같아야 하고, 사용자 데이터를 비우는 자리에서 함께 지워지면 안 된다.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DevicePreferences
